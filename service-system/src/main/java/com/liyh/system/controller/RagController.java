package com.liyh.system.controller;

import com.liyh.common.result.Result;
import com.liyh.model.vo.ai.RagAskRequest;
import com.liyh.model.vo.ai.RagResponse;
import com.liyh.system.config.AiProperties;
import com.liyh.system.service.RagService;
import com.liyh.system.utils.RedisUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "智能问答")
@RestController
@RequestMapping("/front/rag")
@Slf4j
public class RagController {

    @Autowired
    private RagService ragService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AiProperties aiProperties;

    private static final String RATE_LIMIT_KEY_PREFIX = "rag:rate:";

    @Operation(summary = "智能问答 - 基于社区帖子内容回答用户问题")
    @PostMapping("/ask")
    public Result<?> ask(@Validated @RequestBody RagAskRequest request,
                         HttpServletRequest httpRequest) {
        Result<?> rateLimitResult = checkRateLimit(httpRequest);
        if (rateLimitResult != null) {
            return rateLimitResult;
        }

        if (!ragService.isAvailable()) {
            return Result.fail("智能问答服务暂不可用，请确认 AI 和搜索服务已配置");
        }

        RagResponse response = ragService.ask(request.getQuestion().trim(), request.getTopK());
        return Result.ok(response);
    }

    @Operation(summary = "智能问答（流式） - SSE 逐字输出回答")
    @GetMapping("/ask/stream")
    public SseEmitter askStream(@RequestParam String question,
                                @RequestParam(defaultValue = "5") Integer topK,
                                HttpServletRequest httpRequest) {
        SseEmitter emitter = new SseEmitter(120_000L);

        if (question == null || question.isBlank()
                || question.length() > aiProperties.getRag().getMaxQuestionLength()) {
            sendErrorAndComplete(emitter, "问题为空或超过长度限制");
            return emitter;
        }

        Result<?> rateLimitResult = checkRateLimit(httpRequest);
        if (rateLimitResult != null) {
            sendErrorAndComplete(emitter, rateLimitResult.getMessage());
            return emitter;
        }

        if (!ragService.isAvailable()) {
            sendErrorAndComplete(emitter, "智能问答服务暂不可用");
            return emitter;
        }

        ragService.askStream(question.trim(), topK, emitter);
        return emitter;
    }

    @Operation(summary = "检查智能问答服务状态")
    @GetMapping("/status")
    public Result<Map<String, Boolean>> status() {
        return Result.ok(Map.of("available", ragService.isAvailable()));
    }

    private Result<?> checkRateLimit(HttpServletRequest request) {
        int limit = aiProperties.getRag().getRateLimitPerMinute();
        if (limit <= 0) {
            return null;
        }

        String ip = getClientIp(request);
        String key = RATE_LIMIT_KEY_PREFIX + ip;
        Long count = redisUtil.incrementWithExpire(key, 1, TimeUnit.MINUTES);

        if (count != null && count > limit) {
            log.warn("IP {} 触发 RAG 限流，当前: {}/{}/min", ip, count, limit);
            return Result.fail("请求过于频繁，请稍后再试（每分钟最多 " + limit + " 次）");
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    private void sendErrorAndComplete(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().name("error").data(message));
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }
}
