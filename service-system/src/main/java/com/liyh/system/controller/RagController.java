package com.liyh.system.controller;

import com.liyh.common.result.Result;
import com.liyh.model.vo.ai.RagResponse;
import com.liyh.system.service.RagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * RAG 智能问答控制器
 */
@Api(tags = "智能问答")
@RestController
@RequestMapping("/front/rag")
@Slf4j
public class RagController {

    @Autowired
    private RagService ragService;

    @ApiOperation("智能问答 - 基于社区帖子内容回答用户问题")
    @PostMapping("/ask")
    public Result<?> ask(@RequestBody Map<String, Object> params) {
        String question = (String) params.get("question");
        Integer topK = (Integer) params.getOrDefault("topK", 5);

        if (question == null || question.trim().isEmpty()) {
            return Result.fail("问题不能为空");
        }

        if (!ragService.isAvailable()) {
            return Result.fail("智能问答服务暂不可用，请确认 AI 和搜索服务已配置");
        }

        RagResponse response = ragService.ask(question.trim(), topK);
        return Result.ok(response);
    }

    @ApiOperation("检查智能问答服务状态")
    @GetMapping("/status")
    public Result<Map<String, Boolean>> status() {
        return Result.ok(Map.of("available", ragService.isAvailable()));
    }
}
