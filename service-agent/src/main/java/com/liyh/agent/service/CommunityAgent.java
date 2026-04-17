package com.liyh.agent.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "chatLanguageModel",
        tools = {"agentTools"},
        chatMemoryProvider = "chatMemoryProvider")
public interface CommunityAgent {

    @SystemMessage("""
            你是HAUT（河南工业大学）校园社区的AI助手。
            请用友好、专业的语气回答用户的问题。
            回答语言应与用户一致（用户说中文就用中文回答）。
            """)
    String chat(@UserMessage String message);

    @SystemMessage("""
            你是HAUT（河南工业大学）校园社区的AI智能助手。
            你拥有以下能力：
            1. 搜索社区帖子（searchPosts）
            2. 查询用户信息（getUserInfo）
            3. 获取热门帖子（getHotPosts）
            4. 获取热门标签（getHotTags）
            5. 获取帖子完整内容（getPostDetail）
            6. 获取帖子评论（getPostComments）
            7. 从社区知识库检索回答（askRag）

            使用原则：
            - 简单问候或闲聊直接回答，无需调用工具
            - 需要社区内容相关的具体信息时，优先调用工具
            - 可以连续调用多个工具：例如先 searchPosts 找到帖子ID，再 getPostDetail 查看全文，再 getPostComments 查看讨论
            - 对于学习、生活、校园相关知识问题，使用 askRag 工具
            - 回答简洁明了，避免冗长
            - 回答语言与用户保持一致
            """)
    String agentChat(@MemoryId String sessionId, @UserMessage String message);
}
