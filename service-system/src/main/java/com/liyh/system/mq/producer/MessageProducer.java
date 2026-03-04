package com.liyh.system.mq.producer;

import com.liyh.model.vo.message.BroadcastMessage;
import com.liyh.model.vo.message.DelayedPostMessage;
import com.liyh.model.vo.message.EmailMessage;
import com.liyh.model.vo.message.NotifyMessage;
import com.liyh.system.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 消息生产者
 * 负责发送各类消息到 RabbitMQ
 *
 * @Author LiYH
 */
@Component
@Slf4j
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // ==================== 邮件消息 ====================

    /**
     * 发送邮件消息（异步）
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     * @param type    邮件类型
     */
    public void sendEmailMessage(String to, String subject, String content, EmailMessage.EmailType type) {
        EmailMessage message = EmailMessage.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .type(type)
                .build();
        
        log.info("发送邮件消息到队列 - 收件人: {}, 主题: {}", to, subject);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                message
        );
    }

    /**
     * 发送验证码邮件（异步）
     */
    public void sendVerifyCodeEmail(String to, String code) {
        String subject = "【校园社区】邮箱验证码";
        String content = "您的验证码是：" + code + "，有效期5分钟，请勿泄露给他人。";
        sendEmailMessage(to, subject, content, EmailMessage.EmailType.VERIFY_CODE);
    }

    /**
     * 发送审核结果邮件（异步）
     */
    public void sendApproveEmail(String to, String title, boolean approved, String reason) {
        String subject = "【校园社区】投稿审核结果通知";
        String status = approved ? "通过" : "未通过";
        String content = "您投稿的《" + title + "》审核" + status + "。" +
                (reason != null && !reason.isEmpty() ? "\n原因：" + reason : "");
        sendEmailMessage(to, subject, content, EmailMessage.EmailType.APPROVE);
    }

    // ==================== 通知消息 ====================

    /**
     * 发送点赞通知
     */
    public void sendLikeNotify(Long fromUserId, String fromUsername, Long toUserId, 
                               Long postId, String postTitle) {
        NotifyMessage message = NotifyMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .fromUserId(fromUserId)
                .fromUsername(fromUsername)
                .toUserId(toUserId)
                .type(NotifyMessage.NotifyType.LIKE_POST)
                .targetId(postId)
                .targetTitle(postTitle)
                .content(fromUsername + " 赞了你的帖子")
                .createTime(new Date())
                .build();

        log.info("发送点赞通知 - 从 {} 到 {}, 帖子: {}", fromUserId, toUserId, postId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFY_EXCHANGE,
                RabbitMQConfig.LIKE_ROUTING_KEY,
                message
        );
    }

    /**
     * 发送评论通知
     */
    public void sendCommentNotify(Long fromUserId, String fromUsername, Long toUserId,
                                   Long postId, String postTitle, String commentContent) {
        // 评论内容摘要（最多50字）
        String contentSummary = commentContent.length() > 50 
                ? commentContent.substring(0, 50) + "..." 
                : commentContent;

        NotifyMessage message = NotifyMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .fromUserId(fromUserId)
                .fromUsername(fromUsername)
                .toUserId(toUserId)
                .type(NotifyMessage.NotifyType.COMMENT_POST)
                .targetId(postId)
                .targetTitle(postTitle)
                .content(fromUsername + " 评论了你的帖子：" + contentSummary)
                .createTime(new Date())
                .build();

        log.info("发送评论通知 - 从 {} 到 {}, 帖子: {}", fromUserId, toUserId, postId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFY_EXCHANGE,
                RabbitMQConfig.COMMENT_ROUTING_KEY,
                message
        );
    }

    /**
     * 发送回复通知
     */
    public void sendReplyNotify(Long fromUserId, String fromUsername, Long toUserId,
                                 Long commentId, String replyContent) {
        String contentSummary = replyContent.length() > 50
                ? replyContent.substring(0, 50) + "..."
                : replyContent;

        NotifyMessage message = NotifyMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .fromUserId(fromUserId)
                .fromUsername(fromUsername)
                .toUserId(toUserId)
                .type(NotifyMessage.NotifyType.REPLY_COMMENT)
                .targetId(commentId)
                .content(fromUsername + " 回复了你：" + contentSummary)
                .createTime(new Date())
                .build();

        log.info("发送回复通知 - 从 {} 到 {}", fromUserId, toUserId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFY_EXCHANGE,
                RabbitMQConfig.COMMENT_ROUTING_KEY,
                message
        );
    }

    /**
     * 发送关注通知
     */
    public void sendFollowNotify(Long fromUserId, String fromUsername, Long toUserId) {
        NotifyMessage message = NotifyMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .fromUserId(fromUserId)
                .fromUsername(fromUsername)
                .toUserId(toUserId)
                .type(NotifyMessage.NotifyType.FOLLOW)
                .content(fromUsername + " 关注了你")
                .createTime(new Date())
                .build();

        log.info("发送关注通知 - {} 关注了 {}", fromUserId, toUserId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFY_EXCHANGE,
                RabbitMQConfig.FOLLOW_ROUTING_KEY,
                message
        );
    }

    // ==================== 延迟消息（定时发布，使用延迟插件） ====================

    /**
     * 发送延迟发布帖子消息
     * 使用 rabbitmq_delayed_message_exchange 插件实现
     *
     * @param postId      帖子ID（草稿状态）
     * @param userId      用户ID
     * @param title       帖子标题
     * @param content     帖子内容
     * @param tagIds      标签ID列表
     * @param anonymous   是否匿名
     * @param publishTime 计划发布时间
     */
    public void sendDelayedPostPublish(Long postId, Long userId, String title, String content,
                                        List<Long> tagIds, Boolean anonymous, Date publishTime) {
        // 计算延迟时间（毫秒）
        long delayMillis = publishTime.getTime() - System.currentTimeMillis();
        
        if (delayMillis <= 0) {
            log.warn("定时发布时间已过期，将立即发布: postId={}", postId);
            delayMillis = 1000; // 最小延迟1秒
        }

        // 延迟插件最大支持约 49 天（2^32 - 1 毫秒）
        long maxDelay = Integer.MAX_VALUE;
        if (delayMillis > maxDelay) {
            log.warn("延迟时间超过最大限制: postId={}", postId);
            delayMillis = maxDelay;
        }

        DelayedPostMessage message = DelayedPostMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .postId(postId)
                .userId(userId)
                .title(title)
                .content(content)
                .tagIds(tagIds)
                .anonymous(anonymous)
                .publishTime(publishTime)
                .createTime(new Date())
                .delayMillis(delayMillis)
                .build();

        // 使用 x-delay 头设置延迟时间（插件方案）
        final long finalDelayMillis = delayMillis;
        MessagePostProcessor messagePostProcessor = msg -> {
            msg.getMessageProperties().setDelay((int) finalDelayMillis);  // x-delay 头
            return msg;
        };

        log.info("发送定时发布消息 - postId: {}, 延迟: {}ms, 计划发布时间: {}", 
                postId, delayMillis, publishTime);
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELAY_EXCHANGE,
                RabbitMQConfig.DELAY_ROUTING_KEY,
                message,
                messagePostProcessor
        );
    }

    /**
     * 取消定时发布（通过更新帖子状态实现，消息到达后检查状态）
     * 注意：RabbitMQ不支持直接删除已发送的消息，需要在消费时检查状态
     */
    public void cancelDelayedPost(Long postId) {
        log.info("标记取消定时发布: postId={}", postId);
        // 实际取消逻辑在数据库层面实现，消费者处理时会检查帖子状态
    }

    // ==================== 广播消息（系统公告） ====================

    /**
     * 广播系统公告
     *
     * @param title      公告标题
     * @param content    公告内容
     * @param targetId   公告ID
     * @param senderId   发送者ID
     * @param senderName 发送者用户名
     */
    public void broadcastAnnouncement(String title, String content, Long targetId,
                                       Long senderId, String senderName) {
        BroadcastMessage message = BroadcastMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .type(BroadcastMessage.BroadcastType.ANNOUNCEMENT)
                .title(title)
                .content(content)
                .targetId(targetId)
                .senderId(senderId)
                .senderName(senderName)
                .createTime(new Date())
                .build();

        log.info("广播系统公告 - 标题: {}, 发送者: {}", title, senderName);
        
        // Fanout交换机不需要路由键，消息会发送到所有绑定的队列
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BROADCAST_EXCHANGE,
                "", // Fanout交换机忽略路由键
                message
        );
    }

    /**
     * 广播系统维护通知
     */
    public void broadcastMaintenance(String title, String content, Long senderId, String senderName) {
        BroadcastMessage message = BroadcastMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .type(BroadcastMessage.BroadcastType.MAINTENANCE)
                .title(title)
                .content(content)
                .senderId(senderId)
                .senderName(senderName)
                .createTime(new Date())
                .build();

        log.info("广播系统维护通知 - 标题: {}", title);
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BROADCAST_EXCHANGE,
                "",
                message
        );
    }

    // ==================== AI 内容审核消息 ====================

    /**
     * 发送内容审核消息（帖子发布后异步审核）
     *
     * @param postId 帖子 ID
     */
    public void sendAuditMessage(Long postId) {
        log.info("发送内容审核消息 - postId: {}", postId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.AUDIT_EXCHANGE,
                RabbitMQConfig.AUDIT_ROUTING_KEY,
                postId
        );
    }

    // ==================== ES 索引同步消息 ====================

    /**
     * 发送 ES 索引同步消息（帖子发布/更新后同步到 ES）
     *
     * @param postId 帖子 ID
     * @param action 操作类型: index / delete
     */
    public void sendEsIndexMessage(Long postId, String action) {
        java.util.Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("postId", postId);
        msg.put("action", action);
        log.info("发送 ES 索引同步消息 - postId: {}, action: {}", postId, action);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ES_INDEX_EXCHANGE,
                RabbitMQConfig.ES_INDEX_ROUTING_KEY,
                msg
        );
    }

    /**
     * 广播活动通知
     */
    public void broadcastActivity(String title, String content, Long targetId,
                                   Long senderId, String senderName) {
        BroadcastMessage message = BroadcastMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .type(BroadcastMessage.BroadcastType.ACTIVITY)
                .title(title)
                .content(content)
                .targetId(targetId)
                .senderId(senderId)
                .senderName(senderName)
                .createTime(new Date())
                .build();

        log.info("广播活动通知 - 标题: {}", title);
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BROADCAST_EXCHANGE,
                "",
                message
        );
    }
}
