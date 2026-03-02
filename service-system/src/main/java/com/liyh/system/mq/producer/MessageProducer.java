package com.liyh.system.mq.producer;

import com.liyh.model.vo.message.EmailMessage;
import com.liyh.model.vo.message.NotifyMessage;
import com.liyh.system.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
                .createTime(LocalDateTime.now())
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
                .createTime(LocalDateTime.now())
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
                .createTime(LocalDateTime.now())
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
                .createTime(LocalDateTime.now())
                .build();

        log.info("发送关注通知 - {} 关注了 {}", fromUserId, toUserId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFY_EXCHANGE,
                RabbitMQConfig.FOLLOW_ROUTING_KEY,
                message
        );
    }
}
