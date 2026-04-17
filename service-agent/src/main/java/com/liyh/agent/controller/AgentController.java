package com.liyh.agent.controller;

import com.liyh.agent.service.CommunityAgent;
import com.liyh.common.result.Result;
import com.liyh.system.config.AiProperties;
import com.liyh.system.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "AI Agent")
@RestController
@RequestMapping("/front/agent")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AgentController {

    private final CommunityAgent communityAgent;
    private final RedisUtil redisUtil;
    private final AiProperties aiProperties;

    private static final String RATE_KEY = "agent:rate:";

    @Operation(summary = "普通对话 - 直接由 LLM 回答，不调用工具")
    @PostMapping("/chat")
    public Result<String> chat(@RequestParam @NotBlank @Size(max = 500) String message,
                               HttpServletRequest request) {
        if (!aiProperties.isEnabled()) return Result.fail("AI Agent 服务未启用");
        Result<?> limit = checkRateLimit(request);
        if (limit != null) return Result.fail(limit.getMessage());
        return Result.ok(communityAgent.chat(message.trim()));
    }

    @Operation(summary = "智能 Agent - 可自动调用搜索/RAG/用户查询等工具")
    @PostMapping("/smart")
    public Result<String> smart(@RequestParam @NotBlank @Size(max = 500) String message,
                                HttpServletRequest request) {
        if (!aiProperties.isEnabled()) return Result.fail("AI Agent 服务未启用");
        Result<?> limit = checkRateLimit(request);
        if (limit != null) return Result.fail(limit.getMessage());
        log.info("[Agent] smart chat: {}", message);
        return Result.ok(communityAgent.agentChat(message.trim()));
    }

    @Operation(summary = "Agent 服务状态")
    @GetMapping("/status")
    public Result<Map<String, Object>> status() {
        return Result.ok(Map.of(
                "enabled", aiProperties.isEnabled(),
                "model", aiProperties.getChat().getModel()
        ));
    }

    private Result<?> checkRateLimit(HttpServletRequest request) {
        int limit = aiProperties.getRag().getRateLimitPerMinute();
        if (limit <= 0) return null;
        String key = RATE_KEY + getClientIp(request);
        Long count = redisUtil.incrementWithExpire(key, 1, TimeUnit.MINUTES);
        if (count != null && count > limit) {
            return Result.fail("请求过于频繁，每分钟最多 " + limit + " 次");
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) return ip.split(",")[0].trim();
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) return ip;
        return request.getRemoteAddr();
    }
}
