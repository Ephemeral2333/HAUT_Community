package com.liyh.system.service.serviceImpl;

import com.liyh.model.vo.ai.RagResponse;
import com.liyh.model.vo.ai.SearchResultVo;
import com.liyh.system.service.AiService;
import com.liyh.system.service.EsPostService;
import com.liyh.system.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * RAG 智能问答服务实现（生产级三阶段架构）
 * <p>
 * 流程：
 * 1. [Query 改写] 用 DeepSeek 生成 2 条扩展子查询，多路检索提高召回率
 * 2. [多路向量检索 + 合并去重] 对原始问题及改写问题并行检索，按 postId 合并
 * 3. [Reranker] 用 DeepSeek LLM 对候选文档按相关性精排，取 Top-K
 * 4. [生成] 构建 Prompt，调用 LLM 生成最终回答
 */
@Service
@Slf4j
public class RagServiceImpl implements RagService {

    @Autowired
    private AiService aiService;

    @Autowired
    private EsPostService esPostService;

    private static final int RETRIEVE_PER_QUERY = 10;
    private static final int FINAL_TOP_K = 5;
    private static final int CONTEXT_CHUNK_MAX_LEN = 500;

    /** 向量搜索得分低于此阈值的结果视为不相关（cosineSimilarity + 1.0，范围 0~2） */
    private static final double VECTOR_SCORE_THRESHOLD = 1.3;

    private static final ExecutorService RETRIEVAL_POOL =
            new ThreadPoolExecutor(3, 6, 60, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(50),
                    r -> {
                        Thread t = new Thread(r, "rag-retrieval");
                        t.setDaemon(true);
                        return t;
                    },
                    new ThreadPoolExecutor.CallerRunsPolicy());

    private static final String RAG_SYSTEM_PROMPT = """
            你是「校园社区」的智能助手。你的任务是基于检索到的社区帖子内容来回答用户的问题。

            规则：
            1. 只根据提供的帖子内容来回答，不要编造信息
            2. 如果提供的帖子内容无法回答用户的问题，请如实说明"根据社区现有内容暂时无法回答该问题"
            3. 回答时请引用相关帖子的标题，格式为【帖子标题】
            4. 回答要简洁、有条理，控制在 300 字以内
            5. 如果多篇帖子有不同观点，请综合呈现
            """;

    @Override
    public boolean isAvailable() {
        return aiService.isAvailable() && esPostService.isAvailable();
    }

    @Override
    public RagResponse ask(String question, int topK) {
        if (!isAvailable()) {
            return RagResponse.builder()
                    .answer("智能问答服务暂不可用，请稍后再试")
                    .sources(List.of())
                    .build();
        }

        long startTime = System.currentTimeMillis();
        log.info("RAG 问答开始 - 问题: {}", question);

        // ========== Step 1: Query 改写 ==========
        List<String> queries = new ArrayList<>();
        queries.add(question);

        List<String> rewritten = aiService.rewriteQuery(question);
        if (!rewritten.isEmpty()) {
            queries.addAll(rewritten);
            log.info("Query 改写扩展: {} 条子查询", rewritten.size());
        }

        // ========== Step 2: 多路并行检索 + 合并去重 ==========
        Map<Long, SearchResultVo> candidatesMap = parallelRetrieve(queries);

        if (candidatesMap.isEmpty()) {
            log.info("向量检索无结果，降级为关键词搜索");
            List<SearchResultVo> kwResults = esPostService.searchByKeyword(question, 0, RETRIEVE_PER_QUERY);
            kwResults.forEach(r -> candidatesMap.putIfAbsent(r.getPostId(), r));
        }

        if (candidatesMap.isEmpty()) {
            return RagResponse.builder()
                    .answer("根据社区现有内容暂时无法回答该问题，试试换个问法？")
                    .sources(List.of())
                    .build();
        }

        List<SearchResultVo> candidates = new ArrayList<>(candidatesMap.values());
        log.info("多路检索合并后候选帖子: {} 篇", candidates.size());

        // ========== Step 3: Reranker 精排 ==========
        List<SearchResultVo> ranked = rerank(question, candidates, topK > 0 ? topK : FINAL_TOP_K);

        // ========== Step 4: 构建 Prompt 并生成回答 ==========
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < ranked.size(); i++) {
            SearchResultVo post = ranked.get(i);
            context.append("--- 帖子").append(i + 1).append(" ---\n");
            context.append("标题: ").append(post.getTitle()).append("\n");
            context.append("内容: ").append(truncateAtSentence(post.getContent(), CONTEXT_CHUNK_MAX_LEN)).append("\n\n");
        }

        String userMessage = "【参考帖子内容】\n" + context + "\n【用户问题】\n" + question;
        String answer = aiService.chat(RAG_SYSTEM_PROMPT, userMessage);

        if (answer == null || answer.isBlank()) {
            answer = "AI 服务暂时无法生成回答，以下是相关帖子供参考。";
        }

        List<RagResponse.SourcePost> sources = ranked.stream()
                .map(p -> RagResponse.SourcePost.builder()
                        .postId(p.getPostId())
                        .title(p.getTitle())
                        .score(p.getScore())
                        .build())
                .collect(Collectors.toList());

        log.info("RAG 问答完成 - 引用 {} 篇帖子, 耗时 {}ms", sources.size(), System.currentTimeMillis() - startTime);
        return RagResponse.builder().answer(answer).sources(sources).build();
    }

    /**
     * 多路并行向量检索，使用线程池同时发起多个查询，合并去重并过滤低分结果
     */
    private Map<Long, SearchResultVo> parallelRetrieve(List<String> queries) {
        List<CompletableFuture<List<SearchResultVo>>> futures = queries.stream()
                .map(q -> CompletableFuture.supplyAsync(
                        () -> esPostService.searchByVector(q, RETRIEVE_PER_QUERY), RETRIEVAL_POOL)
                        .exceptionally(ex -> {
                            log.warn("向量检索异常: query={}, error={}", q, ex.getMessage());
                            return Collections.emptyList();
                        }))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        Map<Long, SearchResultVo> candidatesMap = new ConcurrentHashMap<>();
        for (CompletableFuture<List<SearchResultVo>> future : futures) {
            List<SearchResultVo> results = future.join();
            for (SearchResultVo r : results) {
                if (r.getScore() < VECTOR_SCORE_THRESHOLD) {
                    continue;
                }
                candidatesMap.merge(r.getPostId(), r,
                        (existing, incoming) -> existing.getScore() >= incoming.getScore() ? existing : incoming);
            }
        }
        return candidatesMap;
    }

    /**
     * 使用 LLM Reranker 对候选帖子精排，返回 Top-K 结果。
     * 降级时按向量得分排序后取 Top-K。
     */
    private List<SearchResultVo> rerank(String question, List<SearchResultVo> candidates, int topK) {
        if (candidates.size() <= topK)
            return candidates;

        List<String> docTexts = candidates.stream()
                .map(c -> (c.getTitle() != null ? c.getTitle() : "") + "\n" +
                        (c.getContent() != null ? c.getContent() : ""))
                .collect(Collectors.toList());

        List<Integer> indices = aiService.rerankIndices(question, docTexts);

        if (indices == null || indices.isEmpty()) {
            log.info("Reranker 不可用，降级为向量得分排序取 Top-{}", topK);
            return candidates.stream()
                    .sorted(Comparator.comparingDouble(SearchResultVo::getScore).reversed())
                    .limit(topK)
                    .collect(Collectors.toList());
        }

        return indices.stream()
                .filter(i -> i >= 0 && i < candidates.size())
                .distinct()
                .limit(topK)
                .map(candidates::get)
                .collect(Collectors.toList());
    }

    /**
     * 在句子边界处截断文本，避免切断句子中间
     */
    private String truncateAtSentence(String text, int maxLen) {
        if (text == null || text.length() <= maxLen) {
            return text != null ? text : "";
        }
        String sub = text.substring(0, maxLen);
        int lastBreak = -1;
        for (int i = sub.length() - 1; i >= maxLen / 2; i--) {
            char c = sub.charAt(i);
            if (c == '。' || c == '！' || c == '？' || c == '!' || c == '?' || c == '\n') {
                lastBreak = i + 1;
                break;
            }
        }
        return (lastBreak > 0 ? sub.substring(0, lastBreak) : sub) + "...";
    }
}
