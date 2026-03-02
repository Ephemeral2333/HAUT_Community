package com.liyh.system.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置类
 * 定义交换机、队列、绑定关系
 *
 * @Author LiYH
 */
@Configuration
public class RabbitMQConfig {

    // ==================== 交换机名称 ====================
    /**
     * 邮件通知交换机
     */
    public static final String EMAIL_EXCHANGE = "email.exchange";

    /**
     * 系统通知交换机
     */
    public static final String NOTIFY_EXCHANGE = "notify.exchange";

    /**
     * 延迟消息交换机（使用 rabbitmq_delayed_message_exchange 插件）
     */
    public static final String DELAY_EXCHANGE = "delay.exchange";

    /**
     * 广播交换机（Fanout）- 系统公告
     */
    public static final String BROADCAST_EXCHANGE = "broadcast.exchange";

    // ==================== 队列名称 ====================
    /**
     * 邮件发送队列
     */
    public static final String EMAIL_QUEUE = "email.queue";

    /**
     * 点赞通知队列
     */
    public static final String LIKE_NOTIFY_QUEUE = "like.notify.queue";

    /**
     * 评论通知队列
     */
    public static final String COMMENT_NOTIFY_QUEUE = "comment.notify.queue";

    /**
     * 关注通知队列
     */
    public static final String FOLLOW_NOTIFY_QUEUE = "follow.notify.queue";

    /**
     * 延迟消息队列（插件方案，直接消费）
     */
    public static final String DELAY_QUEUE = "delay.queue";

    /**
     * 广播队列 - 公告通知
     */
    public static final String BROADCAST_ANNOUNCEMENT_QUEUE = "broadcast.announcement.queue";

    // ==================== 路由键 ====================
    public static final String EMAIL_ROUTING_KEY = "email.send";
    public static final String LIKE_ROUTING_KEY = "notify.like";
    public static final String COMMENT_ROUTING_KEY = "notify.comment";
    public static final String FOLLOW_ROUTING_KEY = "notify.follow";
    public static final String DELAY_ROUTING_KEY = "delay.post.publish";

    // ==================== 消息转换器 ====================
    /**
     * 使用 JSON 序列化消息
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置 RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        
        // 消息发送确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.err.println("消息发送失败: " + cause);
            }
        });
        
        // 消息返回回调（路由失败时触发）
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("消息路由失败: " + returned.getMessage());
        });
        
        return rabbitTemplate;
    }

    // ==================== 邮件交换机和队列 ====================
    
    /**
     * 邮件直连交换机
     */
    @Bean
    public DirectExchange emailExchange() {
        return ExchangeBuilder.directExchange(EMAIL_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .build();
    }

    /**
     * 绑定邮件队列到交换机
     */
    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(emailExchange)
                .with(EMAIL_ROUTING_KEY);
    }

    // ==================== 通知交换机和队列 ====================

    /**
     * 通知主题交换机
     */
    @Bean
    public TopicExchange notifyExchange() {
        return ExchangeBuilder.topicExchange(NOTIFY_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 点赞通知队列
     */
    @Bean
    public Queue likeNotifyQueue() {
        return QueueBuilder.durable(LIKE_NOTIFY_QUEUE).build();
    }

    /**
     * 评论通知队列
     */
    @Bean
    public Queue commentNotifyQueue() {
        return QueueBuilder.durable(COMMENT_NOTIFY_QUEUE).build();
    }

    /**
     * 关注通知队列
     */
    @Bean
    public Queue followNotifyQueue() {
        return QueueBuilder.durable(FOLLOW_NOTIFY_QUEUE).build();
    }

    /**
     * 绑定点赞通知队列
     */
    @Bean
    public Binding likeNotifyBinding(Queue likeNotifyQueue, TopicExchange notifyExchange) {
        return BindingBuilder.bind(likeNotifyQueue)
                .to(notifyExchange)
                .with(LIKE_ROUTING_KEY);
    }

    /**
     * 绑定评论通知队列
     */
    @Bean
    public Binding commentNotifyBinding(Queue commentNotifyQueue, TopicExchange notifyExchange) {
        return BindingBuilder.bind(commentNotifyQueue)
                .to(notifyExchange)
                .with(COMMENT_ROUTING_KEY);
    }

    /**
     * 绑定关注通知队列
     */
    @Bean
    public Binding followNotifyBinding(Queue followNotifyQueue, TopicExchange notifyExchange) {
        return BindingBuilder.bind(followNotifyQueue)
                .to(notifyExchange)
                .with(FOLLOW_ROUTING_KEY);
    }

    // ==================== 延迟消息（rabbitmq_delayed_message_exchange 插件） ====================

    /**
     * 延迟消息交换机（x-delayed-message 类型）
     * 需要安装 rabbitmq_delayed_message_exchange 插件
     */
    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");  // 延迟后按 direct 方式路由
        return new CustomExchange(DELAY_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**
     * 延迟消息队列
     */
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE).build();
    }

    /**
     * 绑定延迟队列到延迟交换机
     */
    @Bean
    public Binding delayBinding(Queue delayQueue, CustomExchange delayExchange) {
        return BindingBuilder.bind(delayQueue)
                .to(delayExchange)
                .with(DELAY_ROUTING_KEY)
                .noargs();
    }

    // ==================== 广播交换机（Fanout）- 系统公告 ====================

    /**
     * 广播交换机（Fanout类型，消息会发送到所有绑定的队列）
     */
    @Bean
    public FanoutExchange broadcastExchange() {
        return ExchangeBuilder.fanoutExchange(BROADCAST_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 公告通知队列
     */
    @Bean
    public Queue broadcastAnnouncementQueue() {
        return QueueBuilder.durable(BROADCAST_ANNOUNCEMENT_QUEUE).build();
    }

    /**
     * 绑定公告队列到广播交换机
     * Fanout交换机不需要路由键，会广播到所有绑定的队列
     */
    @Bean
    public Binding broadcastAnnouncementBinding(Queue broadcastAnnouncementQueue, FanoutExchange broadcastExchange) {
        return BindingBuilder.bind(broadcastAnnouncementQueue)
                .to(broadcastExchange);
    }
}
