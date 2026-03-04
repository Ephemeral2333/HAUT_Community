package com.liyh.system.service.serviceImpl;

import com.liyh.model.vo.ai.RagResponse;
import com.liyh.model.vo.ai.SearchResultVo;
import com.liyh.system.service.AiService;
import com.liyh.system.service.EsPostService;
import com.liyh.system.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG 智能问答服务实现
 * 流程：向量检索相关帖子 → 构建 Prompt → 调用 LLM 生成回答
 */
@Service
@Slf4j
public class RagServiceImpl implements RagService {

    @Autowired
    private AiService aiService;

    @Autowired
    private EsPostService esPostService;

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

        log.info("RAG 问答 - 问题: {}", question);

        // 1. 向量检索相关帖子
        List<SearchResultVo> relatedPosts = esPostService.searchByVector(question, topK);

        if (relatedPosts.isEmpty()) {
            // 降级为关键词搜索
            relatedPosts = esPostService.searchByKeyword(question, 0, topK);
        }

        if (relatedPosts.isEmpty()) {
            return RagResponse.builder()
                    .answer("根据社区现有内容暂时无法回答该问题，试试换个问法？")
                    .sources(List.of())
                    .build();
        }

        // 2. 构建上下文
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < relatedPosts.size(); i++) {
            SearchResultVo post = relatedPosts.get(i);
            context.append("--- 帖子").append(i + 1).append(" ---\n");
            context.append("标题: ").append(post.getTitle()).append("\n");
            String content = post.getContent();
            if (content != null && content.length() > 500) {
                content = content.substring(0, 500) + "...";
            }
            context.append("内容: ").append(content).append("\n\n");
        }

        // 3. 构建用户消息
        String userMessage = "【参考帖子内容】\n" + context + "\n【用户问题】\n" + question;

        // 4. 调用 LLM 生成回答
        String answer = aiService.chat(RAG_SYSTEM_PROMPT, userMessage);

        if (answer == null || answer.isBlank()) {
            answer = "AI 服务暂时无法生成回答，以下是相关帖子供参考。";
        }

        // 5. 构建来源列表
        List<RagResponse.SourcePost> sources = relatedPosts.stream()
                .map(p -> RagResponse.SourcePost.builder()
                        .postId(p.getPostId())
                        .title(p.getTitle())
                        .score(p.getScore())
                        .build())
                .collect(Collectors.toList());

        log.info("RAG 问答完成 - 引用 {} 篇帖子", sources.size());

        return RagResponse.builder()
                .answer(answer)
                .sources(sources)
                .build();
    }
}
