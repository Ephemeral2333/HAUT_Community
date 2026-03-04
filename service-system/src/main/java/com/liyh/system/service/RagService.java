package com.liyh.system.service;

import com.liyh.model.vo.ai.RagResponse;

/**
 * RAG（检索增强生成）智能问答服务
 */
public interface RagService {

    /**
     * 基于社区帖子内容回答用户问题
     *
     * @param question 用户问题
     * @param topK     检索的帖子数量
     * @return RAG 回答（含引用来源）
     */
    RagResponse ask(String question, int topK);

    /**
     * RAG 服务是否可用（需要 AI + ES 同时可用）
     */
    boolean isAvailable();
}
