package com.liyh.system.mq.consumer;

import com.liyh.model.entity.Post;
import com.liyh.model.vo.message.DelayedPostMessage;
import com.liyh.system.config.RabbitMQConfig;
import com.liyh.system.service.PostService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 延迟帖子发布消费者
 * 处理定时发布的帖子
 *
 * @Author LiYH
 */
@Component
@Slf4j
public class DelayedPostConsumer {

    @Autowired
    private PostService postService;

    /**
     * 处理延迟发布的帖子消息
     * 使用延迟插件，消息延迟后直接投递到此队列
     */
    @RabbitListener(queues = RabbitMQConfig.DELAY_QUEUE)
    public void handleDelayedPost(DelayedPostMessage message, Message msg, Channel channel) {
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();

        try {
            log.info("收到定时发布消息 - postId: {}, messageId: {}", 
                    message.getPostId(), message.getMessageId());

            // 检查帖子是否存在且状态为待发布
            Post post = postService.getById(message.getPostId());
            
            if (post == null) {
                log.warn("帖子不存在，跳过发布: postId={}", message.getPostId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 检查帖子是否已被删除或已发布
            if (post.getIsDeleted() == 1) {
                log.info("帖子已删除，跳过发布: postId={}", message.getPostId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 执行发布操作：更新帖子状态为已发布
            postService.publishScheduledPost(message.getPostId());
            
            log.info("定时发布成功 - postId: {}, title: {}", 
                    message.getPostId(), message.getTitle());

            // 消息确认
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("处理定时发布消息失败: postId={}, error={}", 
                    message.getPostId(), e.getMessage());
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
