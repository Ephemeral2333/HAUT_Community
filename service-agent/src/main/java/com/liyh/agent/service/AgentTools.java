package com.liyh.agent.service;

import com.liyh.model.vo.UserVo;
import com.liyh.model.vo.ai.RagResponse;
import com.liyh.model.vo.ai.SearchResultVo;
import com.liyh.system.service.EsPostService;
import com.liyh.system.service.RagService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentTools {

    private final EsPostService esPostService;
    private final RagService ragService;
    private final AgentCommunityQueryService agentCommunityQueryService;

    @Tool("搜索社区帖子，根据关键词返回匹配的帖子标题、作者和内容摘要列表")
    public String searchPosts(String keyword) {
        log.info("[AgentTool] searchPosts: {}", keyword);
        try {
            if (esPostService.isAvailable()) {
                List<SearchResultVo> results = esPostService.searchByKeyword(keyword, 0, 5);
                if (results.isEmpty()) {
                    return "未找到与 \"" + keyword + "\" 相关的帖子。";
                }
                return results.stream()
                        .map(r -> String.format("【帖子ID: %d】%s%n摘要: %s",
                                r.getPostId(), r.getTitle(), truncate(r.getContent(), 100)))
                        .collect(Collectors.joining("\n---\n"));
            }

            List<AgentCommunityQueryService.AgentPostSummary> posts =
                    agentCommunityQueryService.searchPosts(keyword, 5);
            if (posts.isEmpty()) {
                return "未找到与 \"" + keyword + "\" 相关的帖子。";
            }
            return posts.stream()
                    .map(p -> String.format("【帖子ID: %d】%s", p.getId(), p.getTitle()))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("[AgentTool] searchPosts error", e);
            return "搜索帖子时发生错误：" + e.getMessage();
        }
    }

    @Tool("根据用户ID查询用户的基本信息，包括昵称、简介、发帖数和粉丝数")
    public String getUserInfo(Long userId) {
        log.info("[AgentTool] getUserInfo: {}", userId);
        try {
            UserVo user = agentCommunityQueryService.getUserInfo(userId);
            if (user == null) {
                return "未找到 ID 为 " + userId + " 的用户。";
            }
            return String.format("用户信息:%n昵称: %s%n性别: %s%n简介: %s%n发帖数: %d%n粉丝数: %d%n注册时间: %s",
                    user.getNickname(),
                    user.getSex() == 1 ? "男" : "女",
                    user.getDescription() != null ? user.getDescription() : "暂无简介",
                    user.getTopicCount() != null ? user.getTopicCount() : 0,
                    user.getFollowerCount() != null ? user.getFollowerCount() : 0,
                    user.getCreateTime());
        } catch (Exception e) {
            log.error("[AgentTool] getUserInfo error", e);
            return "查询用户信息时发生错误：" + e.getMessage();
        }
    }

    @Tool("获取社区当前热门帖子列表，按热度排序")
    public String getHotPosts() {
        log.info("[AgentTool] getHotPosts");
        try {
            List<AgentCommunityQueryService.AgentPostSummary> posts = agentCommunityQueryService.getHotPosts(8);
            if (posts.isEmpty()) {
                return "暂无热门帖子。";
            }
            return posts.stream()
                    .map(p -> String.format("【帖子ID: %d】%s（浏览: %d | 点赞: %d）",
                            p.getId(), p.getTitle(), p.getView(), p.getFavor()))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("[AgentTool] getHotPosts error", e);
            return "获取热门帖子时发生错误：" + e.getMessage();
        }
    }

    @Tool("当用户就HAUT校园社区学生生活、学习、校园信息等进行知识性问题时，调用此工具从社区帖子中检索相关内容并回答")
    public String askRag(String question) {
        log.info("[AgentTool] askRag: {}", question);
        try {
            if (!ragService.isAvailable()) {
                return "RAG 服务暂时不可用，请稍后再试。";
            }
            RagResponse response = ragService.ask(question, 5);
            if (response == null || response.getAnswer() == null) {
                return "未能从知识库中检索到相关内容。";
            }
            StringBuilder sb = new StringBuilder(response.getAnswer());
            if (response.getSources() != null && !response.getSources().isEmpty()) {
                sb.append("\n\n参考帖子：");
                response.getSources().forEach(s ->
                        sb.append("\n- ").append(s.getTitle()).append(" (ID: ").append(s.getPostId()).append(")"));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("[AgentTool] askRag error", e);
            return "RAG 问答时发生错误：" + e.getMessage();
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
