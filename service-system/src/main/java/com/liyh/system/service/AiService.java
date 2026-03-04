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
     * Query 改写：基于原始问题生成语义相关的扩展查询
     *
     * @param question 用户原始问题
     * @return 改写后的查询列表（2 条）
     */
    List<String> rewriteQuery(String question);

    /**
     * 重排序：对候选文档按照与查询的相关性进行排序
     *
     * @param query     用户查询
     * @param documents 候选文档文本列表
     * @return 按相关性从高到低排列的文档索引列表
     */
    List<Integer> rerankIndices(String query, List<String> documents);

    /**
     * AI 服务是否可用
     */
    boolean isAvailable();
}
