package com.liyh.system.service.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.ai.SearchResultVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liyh.system.config.AiProperties;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.service.AiService;
import com.liyh.system.service.EsPostService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Elasticsearch 帖子搜索服务实现
 * 支持 BM25 关键词搜索 + 向量语义搜索 + RRF 混合排序
 */
@Service
@Slf4j
public class EsPostServiceImpl implements EsPostService {

    private static final String INDEX_NAME = "community_posts";

    @Autowired(required = false)
    private RestHighLevelClient esClient;

    @Autowired
    private AiService aiService;

    @Autowired
    private AiProperties aiProperties;

    @Autowired
    private PostMapper postMapper;

    @Override
    public boolean isAvailable() {
        if (esClient == null) return false;
        try {
            return esClient.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {
            return false;
        }
    }

    @PostConstruct
    public void init() {
        try {
            if (isAvailable()) {
                initIndex();
                log.info("Elasticsearch 连接成功，索引就绪");
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
            boolean exists = esClient.indices().exists(
                    new GetIndexRequest(INDEX_NAME), RequestOptions.DEFAULT);
            if (exists) {
                log.info("ES 索引 {} 已存在", INDEX_NAME);
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
                          "postId":     { "type": "long" },
                          "title":      { "type": "text", "analyzer": "ik_max_word", "search_analyzer": "ik_smart" },
                          "content":    { "type": "text", "analyzer": "ik_max_word", "search_analyzer": "ik_smart" },
                          "userId":     { "type": "long" },
                          "tags":       { "type": "keyword" },
                          "status":     { "type": "integer" },
                          "createTime": { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||epoch_millis" },
                          "contentVector": { "type": "dense_vector", "dims": %d }
                        }
                      }
                    }
                    """.formatted(dims);

            CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
            request.source(mapping, XContentType.JSON);
            esClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("ES 索引 {} 创建成功, 向量维度: {}", INDEX_NAME, dims);

        } catch (Exception e) {
            log.error("ES 索引初始化失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void indexPost(Post post) {
        if (!isAvailable()) return;

        try {
            Map<String, Object> doc = new HashMap<>();
            doc.put("postId", post.getId());
            doc.put("title", post.getTitle());
            doc.put("content", post.getContent());
            doc.put("userId", post.getUserId());
            doc.put("status", post.getStatus() != null ? post.getStatus() : 1);
            doc.put("createTime", post.getCreateTime() != null ?
                    post.getCreateTime().getTime() : System.currentTimeMillis());

            if (post.getTags() != null) {
                doc.put("tags", post.getTags().stream()
                        .map(t -> t.getName()).collect(Collectors.toList()));
            }

            // 向量化（标题 + 内容）
            if (aiService.isAvailable()) {
                String text = post.getTitle() + "\n" + truncate(post.getContent(), 1000);
                float[] vector = aiService.embed(text);
                if (vector != null) {
                    List<Float> vectorList = new ArrayList<>();
                    for (float v : vector) vectorList.add(v);
                    doc.put("contentVector", vectorList);
                }
            }

            IndexRequest request = new IndexRequest(INDEX_NAME)
                    .id(String.valueOf(post.getId()))
                    .source(JSON.toJSONString(doc), XContentType.JSON);
            esClient.index(request, RequestOptions.DEFAULT);
            log.info("帖子索引到 ES - postId: {}", post.getId());

        } catch (Exception e) {
            log.error("帖子索引到 ES 失败 - postId: {}, error: {}", post.getId(), e.getMessage());
        }
    }

    @Override
    public void deletePost(Long postId) {
        if (!isAvailable()) return;
        try {
            esClient.delete(new DeleteRequest(INDEX_NAME, String.valueOf(postId)), RequestOptions.DEFAULT);
            log.info("从 ES 删除帖子 - postId: {}", postId);
        } catch (Exception e) {
            log.error("从 ES 删除帖子失败 - postId: {}", postId, e);
        }
    }

    @Override
    public List<SearchResultVo> searchByKeyword(String keyword, int page, int size) {
        if (!isAvailable()) return Collections.emptyList();

        try {
            BoolQueryBuilder query = QueryBuilders.boolQuery()
                    .must(QueryBuilders.multiMatchQuery(keyword, "title", "content")
                            .field("title", 3.0f)
                            .field("content", 1.0f))
                    .filter(QueryBuilders.boolQuery()
                            .should(QueryBuilders.termQuery("status", 1))
                            .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("status"))));

            SearchSourceBuilder source = new SearchSourceBuilder()
                    .query(query)
                    .from(page * size)
                    .size(size)
                    .highlighter(new HighlightBuilder()
                            .field("title").field("content")
                            .preTags("<em>").postTags("</em>")
                            .fragmentSize(200).numOfFragments(1));

            SearchRequest request = new SearchRequest(INDEX_NAME).source(source);
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

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

            Map<String, Object> params = new HashMap<>();
            params.put("query_vector", toFloatList(queryVector));

            Script script = new Script(
                    ScriptType.INLINE, "painless",
                    "cosineSimilarity(params.query_vector, 'contentVector') + 1.0",
                    params);

            BoolQueryBuilder filter = QueryBuilders.boolQuery()
                    .must(QueryBuilders.existsQuery("contentVector"))
                    .filter(QueryBuilders.boolQuery()
                            .should(QueryBuilders.termQuery("status", 1))
                            .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("status"))));

            SearchSourceBuilder source = new SearchSourceBuilder()
                    .query(QueryBuilders.scriptScoreQuery(filter, script))
                    .size(topK);

            SearchRequest request = new SearchRequest(INDEX_NAME).source(source);
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

            return parseSearchHits(response);
        } catch (Exception e) {
            log.error("ES 向量搜索失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<SearchResultVo> hybridSearch(String query, int page, int size) {
        int fetchSize = size * 3;

        // BM25 关键词搜索
        List<SearchResultVo> bm25Results = searchByKeyword(query, 0, fetchSize);

        // 向量语义搜索
        List<SearchResultVo> vectorResults = searchByVector(query, fetchSize);

        if (bm25Results.isEmpty()) return vectorResults.isEmpty() ?
                Collections.emptyList() : paginate(vectorResults, page, size);
        if (vectorResults.isEmpty()) return paginate(bm25Results, page, size);

        // RRF (Reciprocal Rank Fusion) 融合排序
        return rrfMerge(bm25Results, vectorResults, page, size);
    }

    /**
     * RRF 融合排序: score = Σ 1/(k + rank_i), k=60
     */
    private List<SearchResultVo> rrfMerge(List<SearchResultVo> listA,
                                           List<SearchResultVo> listB,
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

    private List<SearchResultVo> parseSearchHits(SearchResponse response) {
        List<SearchResultVo> results = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> source = hit.getSourceAsMap();

            SearchResultVo vo = SearchResultVo.builder()
                    .postId(toLong(source.get("postId")))
                    .title((String) source.get("title"))
                    .content(truncate((String) source.get("content"), 200))
                    .userId(toLong(source.get("userId")))
                    .score(hit.getScore())
                    .build();

            if (source.get("tags") instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) source.get("tags");
                vo.setTags(tags);
            }
            if (source.get("createTime") instanceof Number) {
                vo.setCreateTime(new Date(((Number) source.get("createTime")).longValue()));
            }

            Map<String, HighlightField> highlights = hit.getHighlightFields();
            if (highlights.containsKey("title")) {
                vo.setHighlightTitle(fragments(highlights.get("title").getFragments()));
            }
            if (highlights.containsKey("content")) {
                vo.setHighlightContent(fragments(highlights.get("content").getFragments()));
            }

            results.add(vo);
        }
        return results;
    }

    private String fragments(Text[] texts) {
        if (texts == null || texts.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (Text t : texts) sb.append(t.string());
        return sb.toString();
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

    @Override
    public int reindexAll() {
        if (!isAvailable()) {
            log.warn("ES 不可用，无法执行全量索引");
            return 0;
        }

        log.info("开始全量索引：从数据库导入所有帖子到 ES...");

        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        wrapper.and(w -> w.isNull("status").or().eq("status", 1));
        List<Post> posts = postMapper.selectList(wrapper);

        int success = 0;
        int total = posts.size();

        for (int i = 0; i < total; i++) {
            try {
                indexPost(posts.get(i));
                success++;
                if ((i + 1) % 50 == 0) {
                    log.info("全量索引进度: {}/{}", i + 1, total);
                }
            } catch (Exception e) {
                log.warn("索引帖子失败 - postId: {}, error: {}", posts.get(i).getId(), e.getMessage());
            }
        }

        log.info("全量索引完成: 成功 {}/{}", success, total);
        return success;
    }
}
