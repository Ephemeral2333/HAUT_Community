package com.liyh.system.mq.consumer;

import com.liyh.model.entity.Post;
import com.liyh.system.config.RabbitMQConfig;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.service.EsPostService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * ES 索引同步消费者
 * 帖子发布/更新/删除后异步同步到 Elasticsearch
 */
@Component
@Slf4j
public class EsIndexConsumer {

    @Autowired
    private EsPostService esPostService;

    @Autowired
    private PostMapper postMapper;

    @RabbitListener(queues = RabbitMQConfig.ES_INDEX_QUEUE)
    public void handleEsIndex(Map<String, Object> msg, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            Long postId = Long.parseLong(msg.get("postId").toString());
            String action = (String) msg.get("action");

            if (!esPostService.isAvailable()) {
                log.warn("ES 不可用，跳过索引同步 - postId: {}", postId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            switch (action) {
                case "index" -> {
                    Post post = postMapper.selectById(postId);
                    if (post != null && post.getIsDeleted() == 0) {
                        esPostService.indexPost(post);
                    }
                }
                case "delete" -> esPostService.deletePost(postId);
                default -> log.warn("未知的 ES 索引操作: {}", action);
            }

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("ES 索引同步失败: {}", e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息拒绝失败", ioException);
            }
        }
    }
}
