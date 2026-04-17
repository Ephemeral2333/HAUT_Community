package com.liyh.system.service.serviceImpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.ai.SearchResultVo;
import com.liyh.system.config.AiProperties;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.service.AiService;
import com.liyh.system.service.EsPostService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Elasticsearch 帖子搜索服务实现（Chunk 版）
 * <p>
 * 核心改造：
 * 1. 每篇帖子按句子边界切分为多个 chunk，每个 chunk 单独向量化并写入 ES
 * 2. 搜索时通过 collapse 按 postId 去重，返回每篇帖子最相关的 chunk
 * 3. 删除帖子时通过 DeleteByQuery 删除该帖子的所有 chunk
 * <p>
 * 支持 BM25 关键词搜索 + 向量语义搜索 + RRF 混合排序
 */
@Service
@Slf4j
public class EsPostServiceImpl implements EsPostService {

    // ==================== 常量 ====================

    /** 索引名（chunk 级别，每个帖子对应多条文档） */
    private static final String INDEX_NAME = "community_post_chunks";

    /** 每个 chunk 的目标字符数 */
    private static final int CHUNK_SIZE = 300;

    /** chunk 之间的重叠字符数（保持上下文连贯） */
    private static final int CHUNK_OVERLAP = 50;

    /** 向量化时内容最大字符数（标题 + chunk 正文） */
    private static final int EMBED_TEXT_MAX_LEN = 512;

    // ==================== 依赖 ====================

    @Autowired(required = false)
    private ElasticsearchClient esClient;

    @Autowired
    private AiService aiService;

    @Autowired
    private AiProperties aiProperties;

    @Autowired
    private PostMapper postMapper;

    // ==================== 生命周期 ====================

    @Override
    public boolean isAvailable() {
        if (esClient == null) return false;
        try {
            return esClient.ping().value();
        } catch (Exception e) {
            return false;
        }
    }

    @PostConstruct
    public void init() {
        try {
            if (isAvailable()) {
                initIndex();
                log.info("Elasticsearch 连接成功，chunk 索引就绪");
            } else {
                log.warn("Elasticsearch 不可用，搜索功能降级为 MySQL LIKE 查询");
            }
        } catch (Exception e) {
            log.warn("Elasticsearch 初始化失败: {}", e.getMessage());
        }
    }

    @Override
    public void initIndex() {
        try {
            boolean exists = esClient.indices().exists(r -> r.index(INDEX_NAME)).value();
            if (exists) {
                log.info("ES 索引 {} 已存在，跳过创建", INDEX_NAME);
                return;
            }

            int dims = aiProperties.getEmbedding().getDimensions();

            String mapping = """
                    {
                      "settings": {
                        "number_of_shards": 1,
                        "number_of_replicas": 0,
                        "analysis": {
                          "analyzer": {
                            "ik_smart_analyzer": { "type": "custom", "tokenizer": "ik_smart" },
                            "ik_max_analyzer":   { "type": "custom", "tokenizer": "ik_max_word" }
                          }
                        }
                      },
                      "mappings": {
                        "properties": {
                          "chunkId":    { "type": "keyword" },
                          "postId":     { "type": "long" },
                          "postTitle":  { "type": "text", "analyzer": "ik_max_word", "search_analyzer": "ik_smart",
                                          "fields": { "keyword": { "type": "keyword" } } },
                          "chunkIndex": { "type": "integer" },
                          "chunkText":  { "type": "text", "analyzer": "ik_max_word", "search_analyzer": "ik_smart" },
                          "userId":     { "type": "long" },
                          "tags":       { "type": "keyword" },
                          "status":     { "type": "integer" },
                          "createTime": { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||epoch_millis" },
                          "chunkVector":{ "type": "dense_vector", "dims": %d }
                        }
                      }
                    }
                    """.formatted(dims);

            esClient.indices().create(r -> r.index(INDEX_NAME).withJson(new StringReader(mapping)));
            log.info("ES chunk 索引 {} 创建成功，向量维度: {}", INDEX_NAME, dims);

        } catch (Exception e) {
            log.error("ES 索引初始化失败: {}", e.getMessage(), e);
        }
    }

    // ==================== 写操作 ====================

    @Override
    public void indexPost(Post post) {
        if (!isAvailable()) return;

        try {
            List<String> chunks = splitIntoChunks(
                    post.getTitle(),
                    post.getContent() != null ? post.getContent() : "");

            log.info("帖子 {} 切分为 {} 个 chunk", post.getId(), chunks.size());

            List<String> tagNames = Collections.emptyList();
            if (post.getTags() != null) {
                tagNames = post.getTags().stream()
                        .map(t -> t.getName())
                        .collect(Collectors.toList());
            }

            long createTimeMs = post.getCreateTime() != null
                    ? post.getCreateTime().getTime()
                    : System.currentTimeMillis();

            List<float[]> vectors = null;
            if (aiService.isAvailable()) {
                List<String> textsToEmbed = chunks.stream()
                        .map(chunk -> truncate(chunk, EMBED_TEXT_MAX_LEN))
                        .collect(Collectors.toList());
                vectors = aiService.embedBatch(textsToEmbed);
                if (vectors == null || vectors.isEmpty()) {
                    log.warn("帖子 {} 的 Embedding 生成失败，该帖子将缺少向量字段", post.getId());
                }
            }

            List<BulkOperation> ops = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                String chunkId = post.getId() + "_" + i;
                String chunkText = chunks.get(i);

                Map<String, Object> doc = new HashMap<>();
                doc.put("chunkId", chunkId);
                doc.put("postId", post.getId());
                doc.put("postTitle", post.getTitle());
                doc.put("chunkIndex", i);
                doc.put("chunkText", chunkText);
                doc.put("userId", post.getUserId());
                doc.put("tags", tagNames);
                doc.put("status", post.getStatus() != null ? post.getStatus() : 1);
                doc.put("createTime", createTimeMs);

                if (vectors != null && i < vectors.size() && vectors.get(i) != null) {
                    doc.put("chunkVector", toFloatList(vectors.get(i)));
                }

                String docJson = JSON.toJSONString(doc);
                ops.add(BulkOperation.of(b -> b.index(
                        idx -> idx.index(INDEX_NAME).id(chunkId).withJson(new StringReader(docJson)))));
            }

            BulkResponse bulkResponse = esClient.bulk(BulkRequest.of(r -> r.operations(ops)));
            if (bulkResponse.errors()) {
                log.warn("帖子 {} 部分 chunk 写入失败", post.getId());
            } else {
                log.info("帖子 {} 的 {} 个 chunk 写入 ES 成功", post.getId(), chunks.size());
            }

        } catch (Exception e) {
            log.error("帖子索引到 ES 失败 - postId: {}, error: {}", post.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void deletePost(Long postId) {
        if (!isAvailable()) return;
        try {
            esClient.deleteByQuery(r -> r
                    .index(INDEX_NAME)
                    .query(q -> q.term(t -> t.field("postId").value(postId))));
            log.info("从 ES 删除帖子所有 chunk - postId: {}", postId);
        } catch (Exception e) {
            log.error("从 ES 删除帖子失败 - postId: {}", postId, e);
        }
    }

    // ==================== 搜索 ====================

    @Override
    public List<SearchResultVo> searchByKeyword(String keyword, int page, int size) {
        if (!isAvailable()) return Collections.emptyList();

        try {
            SearchResponse<Map> response = esClient.search(s -> s
                    .index(INDEX_NAME)
                    .from(page * size)
                    .size(size)
                    .query(q -> q.bool(b -> b
                            .must(m -> m.multiMatch(mm -> mm
                                    .query(keyword)
                                    .fields("postTitle^3", "chunkText^1")))
                            .filter(f -> f.term(t -> t.field("status").value(1)))))
                    .collapse(c -> c.field("postId"))
                    .highlight(h -> h
                            .preTags("<em>").postTags("</em>")
                            .fragmentSize(200).numberOfFragments(1)
                            .fields("postTitle", hf -> hf)
                            .fields("chunkText", hf -> hf)),
                    Map.class);

            return parseSearchHits(response);

        } catch (Exception e) {
            log.error("ES 关键词搜索失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<SearchResultVo> searchByVector(String query, int topK) {
        if (!isAvailable() || !aiService.isAvailable()) return Collections.emptyList();

        try {
            float[] queryVector = aiService.embed(query);
            if (queryVector == null) return Collections.emptyList();

            Map<String, JsonData> params = Map.of(
                    "query_vector", JsonData.of(toFloatList(queryVector)));

            Query innerFilter = Query.of(q -> q.bool(b -> b
                    .must(m -> m.exists(e -> e.field("chunkVector")))
                    .filter(f -> f.term(t -> t.field("status").value(1)))));

            Script script = Script.of(sc -> sc.inline(i -> i
                    .source("cosineSimilarity(params.query_vector, 'chunkVector') + 1.0")
                    .params(params)));

            SearchResponse<Map> response = esClient.search(s -> s
                    .index(INDEX_NAME)
                    .size(topK)
                    .query(q -> q.scriptScore(ss -> ss.query(innerFilter).script(script)))
                    .collapse(c -> c.field("postId")),
                    Map.class);

            return parseSearchHits(response);

        } catch (Exception e) {
            log.error("ES 向量搜索失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<SearchResultVo> hybridSearch(String query, int page, int size) {
        int fetchSize = size * 3;
        List<SearchResultVo> bm25Results = searchByKeyword(query, 0, fetchSize);
        List<SearchResultVo> vectorResults = searchByVector(query, fetchSize);

        if (bm25Results.isEmpty())
            return vectorResults.isEmpty() ? Collections.emptyList() : paginate(vectorResults, page, size);
        if (vectorResults.isEmpty())
            return paginate(bm25Results, page, size);

        return rrfMerge(bm25Results, vectorResults, page, size);
    }

    // ==================== RRF 混合排序 ====================

    private List<SearchResultVo> rrfMerge(List<SearchResultVo> listA, List<SearchResultVo> listB,
                                           int page, int size) {
        final int k = 60;
        Map<Long, Double> scoreMap = new LinkedHashMap<>();
        Map<Long, SearchResultVo> resultMap = new HashMap<>();

        for (int i = 0; i < listA.size(); i++) {
            SearchResultVo r = listA.get(i);
            scoreMap.merge(r.getPostId(), 1.0 / (k + i), Double::sum);
            resultMap.putIfAbsent(r.getPostId(), r);
        }
        for (int i = 0; i < listB.size(); i++) {
            SearchResultVo r = listB.get(i);
            scoreMap.merge(r.getPostId(), 1.0 / (k + i), Double::sum);
            resultMap.putIfAbsent(r.getPostId(), r);
        }

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .skip((long) page * size)
                .limit(size)
                .map(e -> {
                    SearchResultVo vo = resultMap.get(e.getKey());
                    vo.setScore(e.getValue());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    // ==================== 全量重建 ====================

    private static final int REINDEX_BATCH_SIZE = 100;

    @Override
    public int reindexAll() {
        if (!isAvailable()) {
            log.warn("ES 不可用，无法执行全量索引");
            return 0;
        }

        log.info("开始全量 chunk 索引（分页批处理, 每批 {} 条）...", REINDEX_BATCH_SIZE);

        int success = 0, total = 0;
        long currentPage = 1;

        while (true) {
            QueryWrapper<Post> wrapper = new QueryWrapper<>();
            wrapper.eq("is_deleted", 0).eq("status", 1);
            IPage<Post> page = postMapper.selectPage(
                    new Page<>(currentPage, REINDEX_BATCH_SIZE), wrapper);
            List<Post> posts = page.getRecords();

            if (posts.isEmpty()) break;
            total += posts.size();
            for (Post post : posts) {
                try {
                    indexPost(post);
                    success++;
                } catch (Exception e) {
                    log.warn("索引帖子失败 - postId: {}, error: {}", post.getId(), e.getMessage());
                }
            }
            log.info("全量索引进度: 已处理 {}/{}, 成功 {}", total, page.getTotal(), success);
            if (currentPage >= page.getPages()) break;
            currentPage++;
        }

        log.info("全量 chunk 索引完成: 成功 {}/{}", success, total);
        return success;
    }

    // ==================== 私有工具方法 ====================

    private List<SearchResultVo> parseSearchHits(SearchResponse<Map> response) {
        List<SearchResultVo> results = new ArrayList<>();
        for (Hit<Map> hit : response.hits().hits()) {
            Map<String, Object> source = hit.source();
            if (source == null) continue;

            SearchResultVo vo = SearchResultVo.builder()
                    .postId(toLong(source.get("postId")))
                    .title((String) source.get("postTitle"))
                    .content(truncate((String) source.get("chunkText"), 300))
                    .userId(toLong(source.get("userId")))
                    .score(hit.score() != null ? hit.score() : 0.0)
                    .build();

            if (source.get("tags") instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) source.get("tags");
                vo.setTags(tags);
            }
            if (source.get("createTime") instanceof Number) {
                vo.setCreateTime(new Date(((Number) source.get("createTime")).longValue()));
            }

            Map<String, List<String>> highlights = hit.highlight();
            if (highlights.containsKey("postTitle")) {
                vo.setHighlightTitle(String.join("", highlights.get("postTitle")));
            }
            if (highlights.containsKey("chunkText")) {
                vo.setHighlightContent(String.join("", highlights.get("chunkText")));
            }

            results.add(vo);
        }
        return results;
    }

    private List<String> splitIntoChunks(String title, String content) {
        if (content == null || content.isBlank()) {
            return List.of("【" + title + "】");
        }

        String[] sentences = content.split("(?<=[。！？!?\\n])");
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        String overlapBuffer = "";

        for (String raw : sentences) {
            String sentence = raw.trim();
            if (sentence.isEmpty()) continue;

            if (current.length() + sentence.length() > CHUNK_SIZE && current.length() > 0) {
                chunks.add("【" + title + "】\n" + current.toString().trim());
                String built = current.toString();
                overlapBuffer = built.length() > CHUNK_OVERLAP
                        ? built.substring(built.length() - CHUNK_OVERLAP)
                        : built;
                current = new StringBuilder(overlapBuffer);
            }
            current.append(sentence);
        }

        if (current.length() > 0) {
            chunks.add("【" + title + "】\n" + current.toString().trim());
        }

        return chunks.isEmpty() ? List.of("【" + title + "】\n" + truncate(content, CHUNK_SIZE)) : chunks;
    }

    private <T> List<T> paginate(List<T> list, int page, int size) {
        int from = page * size;
        if (from >= list.size()) return Collections.emptyList();
        return list.subList(from, Math.min(from + size, list.size()));
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        return Long.parseLong(obj.toString());
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }

    private List<Float> toFloatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) list.add(f);
        return list;
    }
}
