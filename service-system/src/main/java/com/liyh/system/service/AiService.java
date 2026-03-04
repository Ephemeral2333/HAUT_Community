package com.liyh.system.service;

import java.util.List;

/**
 * AI 服务接口 - 封装 LLM 对话与 Embedding 向量化能力
 */
public interface AiService {

    /**
     * 调用大模型进行对话
     *
     * @param systemPrompt 系统提示词
     * @param userMessage  用户消息
     * @return 模型回复内容
     */
    String chat(String systemPrompt, String userMessage);

    /**
     * 将文本向量化
     *
     * @param text 输入文本
     * @return 向量数组
     */
    float[] embed(String text);

    /**
     * 批量向量化
     *
     * @param texts 输入文本列表
     * @return 向量数组列表
     */
    List<float[]> embedBatch(List<String> texts);

    /**
     * AI 服务是否可用
     */
    boolean isAvailable();
}
