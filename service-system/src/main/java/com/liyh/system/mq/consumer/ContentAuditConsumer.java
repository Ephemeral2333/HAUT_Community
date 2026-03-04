package com.liyh.system.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.ai.AuditResult;
import com.liyh.system.config.RabbitMQConfig;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.service.AiService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * AI 内容审核消费者
 * 帖子发布后异步进行 AI 内容安全审核
 * 审核策略：合规放行 / 疑似人工复审 / 违规拦截
 */
@Component
@Slf4j
public class ContentAuditConsumer {

    @Autowired
    private AiService aiService;

    @Autowired
    private PostMapper postMapper;

    private static final String AUDIT_SYSTEM_PROMPT = """
            你是内容安全审核员。请分析以下校园社区用户发布的内容，判断是否存在违规。
            审核维度：色情低俗、暴力血腥、政治敏感、广告引流、人身攻击、虚假信息。
            
            请严格按照以下 JSON 格式返回审核结果（不要包含其他文字）：
            {"verdict":"PASS","reason":"内容正常","category":"无"}
            
            verdict 取值说明：
            - PASS: 内容合规，可以正常发布
            - SUSPECT: 疑似违规，需要人工复审
            - REJECT: 明确违规，应当拦截
            
            category 取值：色情低俗/暴力血腥/政治敏感/广告引流/人身攻击/虚假信息/无
            """;

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void handleAudit(Long postId, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("[审核] 1. 开始 - postId: {}, postId类型: {}", postId, postId == null ? "null" : postId.getClass().getName());

            log.info("[审核] 2. 查询帖子...");
            Post post = postMapper.selectById(postId);
            log.info("[审核] 3. 查询结果 - post: {}", post == null ? "null" : "id=" + post.getId() + ", title=" + post.getTitle());

            if (post == null || post.getIsDeleted() == 1) {
                log.info("[审核] 4. 帖子不存在或已删除，跳过");
                channel.basicAck(deliveryTag, false);
                return;
            }

            log.info("[审核] 4. 检查 AI 服务可用性...");
            boolean available = aiService.isAvailable();
            log.info("[审核] 5. AI 服务可用: {}", available);

            if (!available) {
                log.warn("[审核] AI 服务不可用，跳过审核 - postId: {}", postId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            log.info("[审核] 6. 开始调用 AI 审核...");
            AuditResult result = doAudit(post.getTitle(), post.getContent());
            log.info("[审核] 7. AI 审核完成 - result: {}", result);

            if (result == null) {
                log.warn("[审核] AI 审核返回空结果，降级为人工审核 - postId: {}", postId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            switch (result.getVerdict()) {
                case PASS -> {
                    log.info("[审核] 结果: PASS - postId: {}, reason: {}, category: {}",
                            postId, result.getReason(), result.getCategory());
                }
                case SUSPECT -> {
                    post.setStatus(2);
                    postMapper.updateById(post);
                    log.warn("[审核] 结果: SUSPECT - postId: {}, reason: {}, category: {}",
                            postId, result.getReason(), result.getCategory());
                }
                case REJECT -> {
                    post.setStatus(3);
                    postMapper.updateById(post);
                    log.warn("[审核] 结果: REJECT - postId: {}, reason: {}, category: {}",
                            postId, result.getReason(), result.getCategory());
                }
            }

            channel.basicAck(deliveryTag, false);
        } catch (Throwable e) {
            log.error("[审核] 异常!!! postId: {}, 异常类型: {}, 消息: {}",
                    postId, e.getClass().getName(), e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("[审核] 消息拒绝失败", ioException);
            }
        }
    }

    private AuditResult doAudit(String title, String content) {
        String userMessage = "【标题】" + title + "\n【内容】" + content;
        String response = aiService.chat(AUDIT_SYSTEM_PROMPT, userMessage);

        if (response == null || response.isBlank()) {
            log.warn("AI 审核返回为空 - title: {}", title);
            return null;
        }

        log.info("AI 审核原始返回: {}", response);

        try {
            String jsonStr = response.trim();
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");
            }
            return JSON.parseObject(jsonStr, AuditResult.class);
        } catch (Exception e) {
            log.warn("AI 审核结果解析失败: {}", response, e);
            return null;
        }
    }
}
