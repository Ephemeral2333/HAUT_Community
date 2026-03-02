package com.liyh.system.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    // ==================== 路由键 ====================
    public static final String EMAIL_ROUTING_KEY = "email.send";
    public static final String LIKE_ROUTING_KEY = "notify.like";
    public static final String COMMENT_ROUTING_KEY = "notify.comment";
    public static final String FOLLOW_ROUTING_KEY = "notify.follow";

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
}
