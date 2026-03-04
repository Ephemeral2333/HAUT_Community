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
import java.util.stream.Collectors;

/**
 * RAG 智能问答服务实现（生产级三阶段架构）
 * <p>
 * 流程：
 * 1. [Query 改写] 用 DeepSeek 生成 2 条扩展子查询，多路检索提高召回率
 * 2. [多路向量检索 + 合并去重] 对原始问题及改写问题各自检索，按 postId 合并
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

    /** 每路检索的候选帖子数（多路合并后再精排） */
    private static final int RETRIEVE_PER_QUERY = 10;

    /** 精排后送入 LLM 的最终帖子数 */
    private static final int FINAL_TOP_K = 5;

    /** RAG 生成时每篇帖子内容的最大字符数 */
    private static final int CONTEXT_CHUNK_MAX_LEN = 500;

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

        log.info("RAG 问答开始 - 问题: {}", question);

        // ========== Step 1: Query 改写 ==========
        List<String> queries = new ArrayList<>();
        queries.add(question); // 保留原始问题

        List<String> rewritten = aiService.rewriteQuery(question);
        if (!rewritten.isEmpty()) {
            queries.addAll(rewritten);
            log.info("Query 改写扩展: {} 条子查询", rewritten.size());
        }

        // ========== Step 2: 多路检索 + 合并去重 ==========
        // 使用 LinkedHashMap 按 postId 去重，优先保留得分更高的结果
        Map<Long, SearchResultVo> candidatesMap = new LinkedHashMap<>();

        for (String q : queries) {
            List<SearchResultVo> results = esPostService.searchByVector(q, RETRIEVE_PER_QUERY);
            for (SearchResultVo r : results) {
                candidatesMap.merge(r.getPostId(), r,
                        (existing, incoming) -> existing.getScore() >= incoming.getScore() ? existing : incoming);
            }
        }

        // 向量检索无结果时，降级为关键词检索
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
            String content = post.getContent();
            if (content != null && content.length() > CONTEXT_CHUNK_MAX_LEN) {
                content = content.substring(0, CONTEXT_CHUNK_MAX_LEN) + "...";
            }
            context.append("内容: ").append(content).append("\n\n");
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

        log.info("RAG 问答完成 - 引用 {} 篇帖子", sources.size());
        return RagResponse.builder().answer(answer).sources(sources).build();
    }

    /**
     * 使用 LLM Reranker 对候选帖子精排，返回 Top-K 结果
     * 若 Reranker 调用失败则直接按向量相似度截取 Top-K（降级）
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
            log.info("Reranker 不可用，降级为向量得分截取 Top-{}", topK);
            return candidates.stream().limit(topK).collect(Collectors.toList());
        }

        // 过滤有效索引，按重排顺序取 Top-K
        return indices.stream()
                .filter(i -> i >= 0 && i < candidates.size())
                .distinct()
                .limit(topK)
                .map(candidates::get)
                .collect(Collectors.toList());
    }
}
