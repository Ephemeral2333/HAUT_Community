package com.liyh.system.mq.consumer;

import com.liyh.model.vo.message.NotifyMessage;
import com.liyh.system.config.RabbitMQConfig;
import com.liyh.system.service.NotificationService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 通知消息消费者
 * 异步处理点赞、评论、关注等通知
 *
 * @Author LiYH
 */
@Component
@Slf4j
public class NotifyConsumer {

    @Autowired
    private NotificationService notificationService;

    /**
     * 处理点赞通知
     */
    @RabbitListener(queues = RabbitMQConfig.LIKE_NOTIFY_QUEUE)
    public void handleLikeNotify(NotifyMessage notifyMessage, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("收到点赞通知 - {} 点赞了 {} 的帖子 {}", 
                    notifyMessage.getFromUsername(), 
                    notifyMessage.getToUserId(),
                    notifyMessage.getTargetId());
            
            // 保存通知到数据库
            notificationService.saveFromMQ(notifyMessage);
            
            // 消息确认
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理点赞通知失败: {}", e.getMessage());
            handleMessageError(channel, deliveryTag);
        }
    }

    /**
     * 处理评论通知
     */
    @RabbitListener(queues = RabbitMQConfig.COMMENT_NOTIFY_QUEUE)
    public void handleCommentNotify(NotifyMessage notifyMessage, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("收到评论通知 - {} 评论了 {} 的内容", 
                    notifyMessage.getFromUsername(), 
                    notifyMessage.getToUserId());
            
            // 保存通知到数据库
            notificationService.saveFromMQ(notifyMessage);
            
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理评论通知失败: {}", e.getMessage());
            handleMessageError(channel, deliveryTag);
        }
    }

    /**
     * 处理关注通知
     */
    @RabbitListener(queues = RabbitMQConfig.FOLLOW_NOTIFY_QUEUE)
    public void handleFollowNotify(NotifyMessage notifyMessage, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("收到关注通知 - {} 关注了 {}", 
                    notifyMessage.getFromUsername(), 
                    notifyMessage.getToUserId());
            
            // 保存通知到数据库
            notificationService.saveFromMQ(notifyMessage);
            
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理关注通知失败: {}", e.getMessage());
            handleMessageError(channel, deliveryTag);
        }
    }

    /**
     * 统一处理消息错误
     */
    private void handleMessageError(Channel channel, long deliveryTag) {
        try {
            // 拒绝消息，不重新入队（避免死循环）
            channel.basicNack(deliveryTag, false, false);
        } catch (IOException e) {
            log.error("消息拒绝失败", e);
        }
    }
}
