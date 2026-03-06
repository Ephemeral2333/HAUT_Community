package com.liyh.system.service;

import com.liyh.model.vo.ai.RagResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface RagService {

    RagResponse ask(String question, int topK);

    /**
     * 流式问答：检索阶段同步执行，生成阶段逐 token 推送给前端
     */
    void askStream(String question, int topK, SseEmitter emitter);

    boolean isAvailable();
}
