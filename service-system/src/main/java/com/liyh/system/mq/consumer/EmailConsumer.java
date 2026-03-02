package com.liyh.system.mq.consumer;

import com.liyh.model.vo.message.EmailMessage;
import com.liyh.system.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 邮件消息消费者
 * 异步处理邮件发送任务
 *
 * @Author LiYH
 */
@Component
@Slf4j
public class EmailConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 监听邮件队列，异步发送邮件
     *
     * @param emailMessage 邮件消息
     * @param message      原始消息（用于获取 deliveryTag）
     * @param channel      通道（用于手动确认）
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailMessage(EmailMessage emailMessage, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("收到邮件消息 - 收件人: {}, 类型: {}", emailMessage.getTo(), emailMessage.getType());
            
            // 发送邮件
            doSendEmail(emailMessage);
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("邮件发送成功 - 收件人: {}", emailMessage.getTo());
            
        } catch (Exception e) {
            log.error("邮件发送失败 - 收件人: {}, 错误: {}", emailMessage.getTo(), e.getMessage());
            try {
                // 拒绝消息，重新入队（第三个参数 true 表示重新入队）
                // 注意：如果消息反复失败，会造成死循环，生产环境建议配合死信队列
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息拒绝失败", ioException);
            }
        }
    }

    /**
     * 执行邮件发送
     */
    private void doSendEmail(EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(emailMessage.getTo());
        message.setSubject(emailMessage.getSubject());
        message.setText(emailMessage.getContent());
        mailSender.send(message);
    }
}
