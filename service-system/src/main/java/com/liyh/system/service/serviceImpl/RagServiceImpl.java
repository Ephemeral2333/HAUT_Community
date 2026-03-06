package com.liyh.system.service.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.liyh.model.vo.ai.RagResponse;
import com.liyh.model.vo.ai.SearchResultVo;
import com.liyh.system.config.AiProperties;
import com.liyh.system.service.AiService;
import com.liyh.system.service.EsPostService;
import com.liyh.system.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RagServiceImpl implements RagService {

    @Autowired
    private AiService aiService;

    @Autowired
    private EsPostService esPostService;

    @Autowired
    private AiProperties aiProperties;

    private static final int RETRIEVE_PER_QUERY = 10;
    private static final int FINAL_TOP_K = 5;
    private static final int CONTEXT_CHUNK_MAX_LEN = 300;
    private static final int RAG_ANSWER_MAX_TOKENS = 512;

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
            6. 返回格式符合网页显示要求，不要包含任何 Markdown 语法或特殊格式
            """;

    @Override
    public boolean isAvailable() {
        return aiService.isAvailable() && esPostService.isAvailable();
    }

    // ==================== 同步问答 ====================

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

        RetrievalResult retrieval = doRetrieval(question, topK);
        if (retrieval.isEmpty()) {
            return RagResponse.builder()
                    .answer("根据社区现有内容暂时无法回答该问题，试试换个问法？")
                    .sources(List.of())
                    .build();
        }

        String userMessage = buildUserMessage(question, retrieval.ranked);
        String answer = aiService.chat(RAG_SYSTEM_PROMPT, userMessage, RAG_ANSWER_MAX_TOKENS);
        if (answer == null || answer.isBlank()) {
            answer = "AI 服务暂时无法生成回答，以下是相关帖子供参考。";
        }

        List<RagResponse.SourcePost> sources = toSourcePosts(retrieval.ranked);

        RagResponse response = RagResponse.builder().answer(answer).sources(sources).build();

        log.info("RAG 问答完成 - 引用 {} 篇帖子, 耗时 {}ms", sources.size(), System.currentTimeMillis() - startTime);
        return response;
    }

    // ==================== 流式问答 (SSE) ====================

    @Override
    public void askStream(String question, int topK, SseEmitter emitter) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!isAvailable()) {
                    sendSseEvent(emitter, "error", "智能问答服务暂不可用");
                    emitter.complete();
                    return;
                }

                sendSseEvent(emitter, "status", "正在检索相关内容...");

                long startTime = System.currentTimeMillis();
                RetrievalResult retrieval = doRetrieval(question, topK);

                if (retrieval.isEmpty()) {
                    sendSseEvent(emitter, "answer", "根据社区现有内容暂时无法回答该问题，试试换个问法？");
                    sendSseEvent(emitter, "sources", "[]");
                    sendSseEvent(emitter, "done", "");
                    emitter.complete();
                    return;
                }

                List<RagResponse.SourcePost> sources = toSourcePosts(retrieval.ranked);
                sendSseEvent(emitter, "sources", JSON.toJSONString(sources));
                sendSseEvent(emitter, "status", "正在生成回答...");

                String userMessage = buildUserMessage(question, retrieval.ranked);

                aiService.chatStream(RAG_SYSTEM_PROMPT, userMessage, RAG_ANSWER_MAX_TOKENS,
                        token -> sendSseEvent(emitter, "token", token),
                        fullAnswer -> {
                            sendSseEvent(emitter, "done", "");
                            emitter.complete();
                            log.info("RAG 流式问答完成 - 耗时 {}ms", System.currentTimeMillis() - startTime);
                        },
                        error -> {
                            log.error("RAG 流式生成失败: {}", error.getMessage());
                            sendSseEvent(emitter, "error", "AI 生成失败，请重试");
                            emitter.complete();
                        });
            } catch (Exception e) {
                log.error("RAG 流式问答异常: {}", e.getMessage(), e);
                sendSseEvent(emitter, "error", "服务异常，请重试");
                emitter.completeWithError(e);
            }
        }, RETRIEVAL_POOL);
    }

    // ==================== 检索核心逻辑（同步/流式共用） ====================

    private RetrievalResult doRetrieval(String question, int topK) {
        AiProperties.Rag ragConfig = aiProperties.getRag();
        int effectiveTopK = topK > 0 ? topK : FINAL_TOP_K;

        // 并行执行：查询改写 + 原始问题向量检索
        CompletableFuture<List<String>> rewriteFuture;
        if (ragConfig.isQueryRewriteEnabled()) {
            rewriteFuture = CompletableFuture.supplyAsync(
                    () -> aiService.rewriteQuery(question), RETRIEVAL_POOL);
        } else {
            rewriteFuture = CompletableFuture.completedFuture(Collections.emptyList());
        }

        CompletableFuture<List<SearchResultVo>> originalSearchFuture =
                CompletableFuture.supplyAsync(
                        () -> esPostService.searchByVector(question, RETRIEVE_PER_QUERY), RETRIEVAL_POOL);

        // 等待查询改写完成，对改写后的查询发起额外检索
        List<String> rewritten = rewriteFuture.join();
        List<SearchResultVo> originalResults = originalSearchFuture.join();

        Map<Long, SearchResultVo> candidatesMap = new ConcurrentHashMap<>();
        double threshold = ragConfig.getVectorScoreThreshold();
        mergeResults(candidatesMap, originalResults, threshold);

        if (!rewritten.isEmpty()) {
            log.info("Query 改写扩展: {} 条子查询", rewritten.size());
            List<CompletableFuture<List<SearchResultVo>>> extraFutures = rewritten.stream()
                    .map(q -> CompletableFuture.supplyAsync(
                            () -> esPostService.searchByVector(q, RETRIEVE_PER_QUERY), RETRIEVAL_POOL)
                            .exceptionally(ex -> {
                                log.warn("改写查询检索异常: {}", ex.getMessage());
                                return Collections.emptyList();
                            }))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(extraFutures.toArray(new CompletableFuture[0])).join();
            for (CompletableFuture<List<SearchResultVo>> f : extraFutures) {
                mergeResults(candidatesMap, f.join(), threshold);
            }
        }

        if (candidatesMap.isEmpty()) {
            log.info("向量检索无结果，降级为关键词搜索");
            List<SearchResultVo> kwResults = esPostService.searchByKeyword(question, 0, RETRIEVE_PER_QUERY);
            kwResults.forEach(r -> candidatesMap.putIfAbsent(r.getPostId(), r));
        }

        if (candidatesMap.isEmpty()) {
            return RetrievalResult.EMPTY;
        }

        List<SearchResultVo> candidates = new ArrayList<>(candidatesMap.values());
        log.info("检索合并后候选帖子: {} 篇", candidates.size());

        List<SearchResultVo> ranked = rerank(question, candidates, effectiveTopK);
        return new RetrievalResult(ranked);
    }

    private void mergeResults(Map<Long, SearchResultVo> map, List<SearchResultVo> results, double threshold) {
        for (SearchResultVo r : results) {
            if (r.getScore() < threshold) continue;
            map.merge(r.getPostId(), r,
                    (existing, incoming) -> existing.getScore() >= incoming.getScore() ? existing : incoming);
        }
    }

    private List<SearchResultVo> rerank(String question, List<SearchResultVo> candidates, int topK) {
        if (candidates.size() <= topK || !aiProperties.getRag().isRerankEnabled()) {
            return candidates.stream()
                    .sorted(Comparator.comparingDouble(SearchResultVo::getScore).reversed())
                    .limit(topK)
                    .collect(Collectors.toList());
        }

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

    // ==================== 缓存 ====================

    // ==================== 工具方法 ====================

    private String buildUserMessage(String question, List<SearchResultVo> ranked) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < ranked.size(); i++) {
            SearchResultVo post = ranked.get(i);
            context.append("--- 帖子").append(i + 1).append(" ---\n");
            context.append("标题: ").append(post.getTitle()).append("\n");
            context.append("内容: ").append(truncateAtSentence(post.getContent(), CONTEXT_CHUNK_MAX_LEN)).append("\n\n");
        }
        return "【参考帖子内容】\n" + context + "\n【用户问题】\n" + question;
    }

    private List<RagResponse.SourcePost> toSourcePosts(List<SearchResultVo> ranked) {
        return ranked.stream()
                .map(p -> RagResponse.SourcePost.builder()
                        .postId(p.getPostId())
                        .title(p.getTitle())
                        .score(p.getScore())
                        .build())
                .collect(Collectors.toList());
    }

    private void sendSseEvent(SseEmitter emitter, String name, String data) {
        try {
            emitter.send(SseEmitter.event().name(name).data(data));
        } catch (IOException e) {
            log.warn("SSE 发送失败({}): {}", name, e.getMessage());
        }
    }

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

    private static class RetrievalResult {
        static final RetrievalResult EMPTY = new RetrievalResult(Collections.emptyList());
        final List<SearchResultVo> ranked;

        RetrievalResult(List<SearchResultVo> ranked) {
            this.ranked = ranked;
        }

        boolean isEmpty() {
            return ranked == null || ranked.isEmpty();
        }
    }
}
