package com.liyh.system.mq.consumer;

import com.liyh.model.entity.Notification;
import com.liyh.model.vo.message.BroadcastMessage;
import com.liyh.system.config.RabbitMQConfig;
import com.liyh.system.service.NotificationService;
import com.liyh.system.service.SysUserService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 广播消息消费者
 * 处理系统公告等全站广播消息
 *
 * @Author LiYH
 */
@Component
@Slf4j
public class BroadcastConsumer {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 处理广播消息（系统公告）
     */
    @RabbitListener(queues = RabbitMQConfig.BROADCAST_ANNOUNCEMENT_QUEUE)
    public void handleBroadcast(BroadcastMessage message, Message msg, Channel channel) {
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();

        try {
            log.info("收到广播消息 - type: {}, title: {}, messageId: {}",
                    message.getType(), message.getTitle(), message.getMessageId());

            // 根据广播类型处理
            switch (message.getType()) {
                case ANNOUNCEMENT -> handleAnnouncement(message);
                case MAINTENANCE -> handleMaintenance(message);
                case ACTIVITY -> handleActivity(message);
                case URGENT -> handleUrgent(message);
                default -> log.warn("未知的广播类型: {}", message.getType());
            }

            // 消息确认
            channel.basicAck(deliveryTag, false);
            log.info("广播消息处理完成 - messageId: {}", message.getMessageId());

        } catch (Exception e) {
            log.error("处理广播消息失败: messageId={}, error={}",
                    message.getMessageId(), e.getMessage());
            handleMessageError(channel, deliveryTag);
        }
    }

    /**
     * 处理系统公告
     * 给所有用户发送通知
     */
    private void handleAnnouncement(BroadcastMessage message) {
        log.info("处理系统公告: {}", message.getTitle());

        // 获取所有活跃用户ID
        List<Long> userIds = sysUserService.getAllActiveUserIds();
        
        int successCount = 0;
        int failCount = 0;

        // 批量创建通知（可以考虑分批处理，避免一次性处理过多）
        for (Long userId : userIds) {
            try {
                Notification notification = Notification.builder()
                        .fromUserId(message.getSenderId())
                        .toUserId(userId)
                        .type(6) // 系统通知类型
                        .targetId(message.getTargetId())
                        .targetTitle(message.getTitle())
                        .content(message.getContent())
                        .isRead(0)
                        .build();
                
                notificationService.save(notification);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.warn("给用户{}发送公告通知失败: {}", userId, e.getMessage());
            }
        }

        log.info("系统公告发送完成 - 成功: {}, 失败: {}, 总用户数: {}",
                successCount, failCount, userIds.size());
    }

    /**
     * 处理系统维护通知
     */
    private void handleMaintenance(BroadcastMessage message) {
        log.info("处理系统维护通知: {}", message.getTitle());
        
        // 维护通知可能只需要通知在线用户或者存储到特定位置
        // 这里简化处理，同样发送给所有用户
        handleAnnouncement(message);
    }

    /**
     * 处理活动通知
     */
    private void handleActivity(BroadcastMessage message) {
        log.info("处理活动通知: {}", message.getTitle());
        handleAnnouncement(message);
    }

    /**
     * 处理紧急通知
     */
    private void handleUrgent(BroadcastMessage message) {
        log.info("处理紧急通知: {}", message.getTitle());
        
        // 紧急通知可以考虑额外的处理，比如发送邮件或短信
        handleAnnouncement(message);
        
        // TODO: 可扩展发送紧急邮件通知
        // messageProducer.sendUrgentEmail(...)
    }

    /**
     * 统一处理消息错误
     */
    private void handleMessageError(Channel channel, long deliveryTag) {
        try {
            // 拒绝消息，不重新入队
            channel.basicNack(deliveryTag, false, false);
        } catch (IOException e) {
            log.error("消息拒绝失败", e);
        }
    }
}
