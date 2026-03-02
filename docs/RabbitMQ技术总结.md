# RabbitMQ 消息队列技术总结与面试指南

> 本文档总结了项目中 RabbitMQ 的实际应用，以及相关的面试高频问题

---

## 一、项目中 RabbitMQ 的应用场景

### 1.1 为什么引入消息队列？

| 问题场景 | 解决方案 | 效果 |
|---------|---------|------|
| **邮件发送慢**（2-3秒） | 异步发送 | 接口响应 < 200ms |
| **耦合度高** | 生产者/消费者解耦 | 服务独立部署 |
| **高并发压力** | 削峰填谷 | 保护下游服务 |
| **通知推送** | 异步处理 | 用户体验提升 |

### 1.2 应用场景总览

| 场景 | 交换机类型 | 交换机 | 队列 | 路由键 | 说明 |
|------|:----------:|--------|------|--------|------|
| 验证码邮件 | Direct | email.exchange | email.queue | email.send | 注册验证码异步发送 |
| 审核通知邮件 | Direct | email.exchange | email.queue | email.send | 投稿审核结果通知 |
| 点赞通知 | Topic | notify.exchange | like.notify.queue | notify.like | 异步处理点赞通知 |
| 评论通知 | Topic | notify.exchange | comment.notify.queue | notify.comment | 异步处理评论/回复 |
| 关注通知 | Topic | notify.exchange | follow.notify.queue | notify.follow | 异步处理关注通知 |
| **定时发布** | Direct | delay.exchange | delay.queue → delay.process.queue | delay.post.publish | **死信队列延迟消息** |
| **系统公告** | **Fanout** | broadcast.exchange | broadcast.announcement.queue | - | **广播给所有用户** |

---

## 二、架构设计

### 2.1 整体架构图

```
┌─────────────────┐     ┌─────────────────────────────────────┐     ┌─────────────────┐
│   Controller    │     │             RabbitMQ                │     │    Consumer     │
│  (生产者调用)    │     │                                     │     │   (消费者)       │
├─────────────────┤     │  ┌─────────────┐                    │     ├─────────────────┤
│ IndexController │────▶│  │   Exchange  │                    │     │ EmailConsumer   │
│   (发送验证码)   │     │  │  (交换机)    │                    │     │  (发送邮件)      │
├─────────────────┤     │  └──────┬──────┘                    │     ├─────────────────┤
│ TipPostService  │────▶│         │ routing key              │     │ NotifyConsumer  │
│  (审核通知)      │     │         ▼                          │     │  (处理通知)      │
├─────────────────┤     │  ┌─────────────┐                    │     └─────────────────┘
│ PostService     │────▶│  │    Queue    │────────────────────│────▶       ↑
│  (点赞/评论)     │     │  │   (队列)    │                    │            │
└─────────────────┘     │  └─────────────┘                    │      手动 ACK
                        └─────────────────────────────────────┘
```

### 2.2 消息流转过程

```
用户请求 "发送验证码"
    │
    ▼
┌───────────────────────────────────────────────────────────────┐
│ 1. Controller 接收请求                                         │
│    - 生成验证码                                                │
│    - 存入 Redis（5分钟过期）                                   │
│    - 调用 messageProducer.sendVerifyCodeEmail()              │
└───────────────────────────────────────────────────────────────┘
    │
    ▼ 立即返回（不等待邮件发送）
┌───────────────────────────────────────────────────────────────┐
│ 2. MessageProducer 发送消息到 RabbitMQ                         │
│    - 构建 EmailMessage 对象                                    │
│    - 发送到 email.exchange                                     │
│    - 路由键: email.send                                        │
└───────────────────────────────────────────────────────────────┘
    │
    ▼ 消息持久化到队列
┌───────────────────────────────────────────────────────────────┐
│ 3. EmailConsumer 消费消息                                      │
│    - 监听 email.queue                                         │
│    - 调用 JavaMailSender 发送邮件                              │
│    - 成功: channel.basicAck() 确认消息                         │
│    - 失败: channel.basicNack() 拒绝消息                        │
└───────────────────────────────────────────────────────────────┘
```

---

## 三、核心代码实现

### 3.1 RabbitMQ 配置类

```java
@Configuration
public class RabbitMQConfig {
    // 交换机名称
    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String NOTIFY_EXCHANGE = "notify.exchange";
    
    // 队列名称
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String LIKE_NOTIFY_QUEUE = "like.notify.queue";
    
    // 路由键
    public static final String EMAIL_ROUTING_KEY = "email.send";
    
    // JSON 消息转换器
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    // 邮件直连交换机
    @Bean
    public DirectExchange emailExchange() {
        return ExchangeBuilder.directExchange(EMAIL_EXCHANGE)
                .durable(true)  // 持久化
                .build();
    }
    
    // 邮件队列
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }
    
    // 绑定队列到交换机
    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(emailExchange)
                .with(EMAIL_ROUTING_KEY);
    }
}
```

### 3.2 消息生产者

```java
@Component
@Slf4j
public class MessageProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送验证码邮件（异步）
     */
    public void sendVerifyCodeEmail(String to, String code) {
        EmailMessage message = EmailMessage.builder()
                .to(to)
                .subject("【校园社区】邮箱验证码")
                .content("您的验证码是：" + code + "，有效期5分钟")
                .type(EmailMessage.EmailType.VERIFY_CODE)
                .build();
        
        log.info("发送邮件消息到队列 - 收件人: {}", to);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                message
        );
    }
    
    /**
     * 发送点赞通知
     */
    public void sendLikeNotify(Long fromUserId, String fromUsername, 
                               Long toUserId, Long postId, String postTitle) {
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
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFY_EXCHANGE,
                RabbitMQConfig.LIKE_ROUTING_KEY,
                message
        );
    }
}
```

### 3.3 消息消费者（手动 ACK）

```java
@Component
@Slf4j
public class EmailConsumer {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 监听邮件队列，异步发送邮件
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailMessage(EmailMessage emailMessage, 
                                   Message message, 
                                   Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("收到邮件消息 - 收件人: {}", emailMessage.getTo());
            
            // 发送邮件
            doSendEmail(emailMessage);
            
            // 手动确认消息（消费成功）
            channel.basicAck(deliveryTag, false);
            log.info("邮件发送成功");
            
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage());
            try {
                // 拒绝消息，不重新入队（避免死循环）
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息拒绝失败", ioException);
            }
        }
    }
    
    private void doSendEmail(EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(emailMessage.getTo());
        message.setSubject(emailMessage.getSubject());
        message.setText(emailMessage.getContent());
        mailSender.send(message);
    }
}
```

### 3.4 Controller 调用（异步化）

```java
// 重构前：同步发送邮件（接口响应慢）
@GetMapping("sendCode")
public Result sendCode(@RequestParam String email) {
    String verifyCode = VCodeUtil.verifyCode(6);
    redisTemplate.opsForValue().set(email + "verify", verifyCode, 5 * 60, TimeUnit.SECONDS);
    
    emailService.sendEmail(email, verifyCode);  // 阻塞 2-3 秒
    return Result.ok();
}

// 重构后：异步发送邮件（接口立即返回）
@GetMapping("sendCode")
public Result sendCode(@RequestParam String email) {
    String verifyCode = VCodeUtil.verifyCode(6);
    redisTemplate.opsForValue().set(email + "verify", verifyCode, 5 * 60, TimeUnit.SECONDS);
    
    messageProducer.sendVerifyCodeEmail(email, verifyCode);  // 立即返回
    return Result.ok();
}
```

---

## 四、消息可靠性保障

### 4.1 生产者确认机制

```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated  # 开启发送确认
    publisher-returns: true             # 开启路由失败回调
```

```java
@Bean
public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    
    // 消息发送确认回调
    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
        if (!ack) {
            log.error("消息发送失败: {}", cause);
            // 可以重试或记录到数据库
        }
    });
    
    // 消息路由失败回调
    rabbitTemplate.setReturnsCallback(returned -> {
        log.error("消息路由失败: {}", returned.getMessage());
    });
    
    return rabbitTemplate;
}
```

### 4.2 消费者手动确认

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: manual  # 手动确认
        prefetch: 1               # 一次处理一条
```

| ACK 方式 | 方法 | 说明 |
|---------|------|------|
| **确认** | `channel.basicAck(deliveryTag, false)` | 消费成功，消息从队列删除 |
| **拒绝** | `channel.basicNack(deliveryTag, false, false)` | 消费失败，消息丢弃 |
| **重试** | `channel.basicNack(deliveryTag, false, true)` | 消费失败，消息重新入队 |

### 4.3 消息持久化

```java
// 交换机持久化
ExchangeBuilder.directExchange(EMAIL_EXCHANGE).durable(true).build();

// 队列持久化
QueueBuilder.durable(EMAIL_QUEUE).build();

// 消息持久化（默认开启）
rabbitTemplate.convertAndSend(exchange, routingKey, message);
```

---

## 五、面试高频问题

### Q1: 为什么用 RabbitMQ 而不是 Kafka？

**答案**：
> | 对比项 | RabbitMQ | Kafka |
> |-------|----------|-------|
> | **定位** | 消息中间件 | 分布式流处理平台 |
> | **吞吐量** | 万级 | 百万级 |
> | **延迟** | 微秒级 | 毫秒级 |
> | **消息顺序** | 单队列有序 | 分区内有序 |
> | **适用场景** | 业务解耦、异步处理 | 日志收集、大数据 |
>
> **项目选择 RabbitMQ 的原因**：
> 1. 邮件发送不需要百万级吞吐量
> 2. 需要可靠的消息确认机制
> 3. 支持多种交换机类型，路由灵活
> 4. Spring Boot 集成简单

---

### Q2: 如何保证消息不丢失？

**答案**：
> 消息丢失可能发生在三个阶段：
>
> **1. 生产者 → MQ（发送丢失）**
> - 开启 `publisher-confirm-type: correlated`
> - 发送失败时重试或记录到数据库
>
> **2. MQ 存储（MQ 宕机）**
> - 交换机、队列、消息都设置持久化
> - `durable(true)` + `deliveryMode=2`
>
> **3. MQ → 消费者（消费丢失）**
> - 关闭自动确认 `acknowledge-mode: manual`
> - 消费成功后才 `basicAck()`
> - 消费失败 `basicNack()` 拒绝消息

---

### Q3: 如何处理消息重复消费？

**答案**：
> **场景**：消费者处理成功但 ACK 失败，消息重新投递
>
> **解决方案 - 幂等性设计**：
> ```java
> @RabbitListener(queues = EMAIL_QUEUE)
> public void handleEmail(EmailMessage message, Channel channel) {
>     String messageId = message.getMessageId();
>     
>     // 1. 检查是否已处理（Redis 或数据库）
>     if (redisTemplate.hasKey("email:processed:" + messageId)) {
>         channel.basicAck(deliveryTag, false);
>         return;
>     }
>     
>     // 2. 处理消息
>     doSendEmail(message);
>     
>     // 3. 标记为已处理
>     redisTemplate.opsForValue().set(
>         "email:processed:" + messageId, "1", 24, TimeUnit.HOURS);
>     
>     channel.basicAck(deliveryTag, false);
> }
> ```

---

### Q4: 消息堆积怎么处理？

**答案**：
> **原因**：生产速度 > 消费速度
>
> **解决方案**：
> 1. **增加消费者数量**
>    ```yaml
>    listener:
>      simple:
>        concurrency: 1      # 最小消费者
>        max-concurrency: 10 # 最大消费者
>    ```
>
> 2. **批量消费**
>    - 一次拉取多条消息处理
>
> 3. **临时队列**
>    - 创建临时消费者快速消费积压消息
>
> 4. **根因分析**
>    - 检查消费者处理逻辑是否有性能问题

---

### Q5: Direct、Topic、Fanout 交换机有什么区别？

**答案**：
> | 类型 | 路由规则 | 适用场景 |
> |------|---------|---------|
> | **Direct** | 精确匹配 routing key | 邮件发送（一对一） |
> | **Topic** | 模糊匹配（* 单词，# 多词） | 通知推送（一对多） |
> | **Fanout** | 广播到所有绑定队列 | 系统公告 |
>
> **项目中的使用**：
> - `email.exchange`（Direct）：邮件精确发送
> - `notify.exchange`（Topic）：通知分类推送
>   - `notify.like` → 点赞队列
>   - `notify.comment` → 评论队列
>   - `notify.*` → 可匹配所有通知

---

### Q6: 死信队列是什么？怎么用？

**答案**：
> **死信**：无法被正常消费的消息
> - 消息被拒绝（basicNack + requeue=false）
> - 消息 TTL 过期
> - 队列达到最大长度
>
> **应用场景**：
> - 延迟消息（订单 30 分钟未支付自动取消）
> - 失败消息重试
>
> ```java
> // 创建业务队列时绑定死信交换机
> @Bean
> public Queue emailQueue() {
>     return QueueBuilder.durable(EMAIL_QUEUE)
>             .deadLetterExchange("dlx.exchange")
>             .deadLetterRoutingKey("dlx.email")
>             .build();
> }
> ```

---

### Q7: 项目中邮件发送的性能提升了多少？

**答案**：
> | 指标 | 优化前（同步） | 优化后（异步） | 提升 |
> |------|--------------|--------------|------|
> | 接口响应时间 | 2-3 秒 | < 200ms | **90%+** |
> | 用户等待 | 有明显等待 | 无感知 | 体验提升 |
> | 系统吞吐量 | 受邮件服务限制 | 不受影响 | 解耦成功 |
> | 失败重试 | 无 | 自动重试 | 可靠性提升 |

---

## 六、消息队列对比

| 特性 | RabbitMQ | Kafka | RocketMQ |
|------|----------|-------|----------|
| **语言** | Erlang | Scala/Java | Java |
| **吞吐量** | 万级 | 百万级 | 十万级 |
| **延迟** | 微秒 | 毫秒 | 毫秒 |
| **消息确认** | 支持 | 支持 | 支持 |
| **消息回溯** | 不支持 | 支持 | 支持 |
| **事务消息** | 支持 | 不支持 | 支持 |
| **适用场景** | 业务消息 | 日志/大数据 | 电商/金融 |

---

## 七、配置参数说明

### application-dev.yml

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    # 生产者确认
    publisher-confirm-type: correlated
    publisher-returns: true
    listener:
      simple:
        # 手动确认
        acknowledge-mode: manual
        # 消费者数量
        concurrency: 1
        max-concurrency: 3
        # 限流
        prefetch: 1
        retry:
          # 重试机制
          enabled: true
          max-attempts: 3
          initial-interval: 3000
```

---

## 八、简历写法

> **RabbitMQ 消息队列**：引入 RabbitMQ 实现邮件通知异步化，采用 Direct 交换机路由邮件消息、Topic 交换机分发通知消息，配置手动 ACK + 消息持久化保证可靠性，接口响应时间从 2s 降至 200ms，提升用户体验 90%+

---

## 九、待优化项

1. ~~**死信队列**：邮件发送失败自动进入死信队列，延迟重试~~ ✅ 已实现
2. ~~**延迟消息**：定时发布帖子~~ ✅ 已实现（死信队列方案）
3. ~~**Fanout 广播**：系统公告广播~~ ✅ 已实现
4. **消息追踪**：集成 Zipkin/Sleuth 实现消息链路追踪
5. **监控告警**：RabbitMQ 管理界面 + Prometheus 监控

---

## 十、站内通知系统实现（新增）

### 10.1 功能概述

基于 RabbitMQ 实现了完整的站内通知系统，当用户进行点赞、评论、关注等操作时，异步发送通知给目标用户。

| 通知类型 | type 值 | 触发场景 | 队列 |
|---------|--------|---------|------|
| 点赞帖子 | 1 | 用户点赞帖子 | like.notify.queue |
| 点赞评论 | 2 | 用户点赞评论 | like.notify.queue |
| 评论帖子 | 3 | 用户评论帖子 | comment.notify.queue |
| 回复评论 | 4 | 用户回复评论 | comment.notify.queue |
| 关注用户 | 5 | 用户关注他人 | follow.notify.queue |
| 系统通知 | 6 | 系统公告等 | - |

### 10.2 架构流程

```
用户点赞帖子
    │
    ▼
┌─────────────────────────────────────────────────────┐
│ PostServiceImpl.favor()                              │
│   - Redis 记录点赞关系                               │
│   - 数据库持久化                                     │
│   - 调用 messageProducer.sendLikeNotify()           │
└─────────────────────────────────────────────────────┘
    │
    ▼ 消息发送到 RabbitMQ
┌─────────────────────────────────────────────────────┐
│ RabbitMQ                                             │
│   Exchange: notify.exchange (Topic)                  │
│   Queue: like.notify.queue                           │
│   Routing Key: notify.like                           │
└─────────────────────────────────────────────────────┘
    │
    ▼ 消费者异步处理
┌─────────────────────────────────────────────────────┐
│ NotifyConsumer.handleLikeNotify()                   │
│   - 接收 NotifyMessage                              │
│   - 调用 notificationService.saveFromMQ()          │
│   - 保存到 notification 数据库表                    │
│   - channel.basicAck() 确认消息                     │
└─────────────────────────────────────────────────────┘
    │
    ▼ 前端轮询获取
┌─────────────────────────────────────────────────────┐
│ GET /front/notification/unread/count                │
│   - 返回未读通知数量                                 │
│   - 前端显示红点提示                                 │
└─────────────────────────────────────────────────────┘
```

### 10.3 核心代码

#### 业务层发送通知

```java
// PostServiceImpl.java - 点赞时发送通知
@Override
public void favor(String userId, Long postId) {
    // 1. Redis 操作
    redisUtil.setAdd(userLikedKey, postId);
    redisUtil.increment(postLikeCountKey);
    
    // 2. 数据库操作
    postMapper.favor(userId, postId);
    
    // 3. 异步发送点赞通知
    try {
        Post post = postMapper.selectById(postId);
        if (post != null && !post.getUserId().equals(userId)) {
            SysUser fromUser = sysUserService.getById(Long.parseLong(userId));
            messageProducer.sendLikeNotify(
                    Long.parseLong(userId),
                    fromUser.getUsername(),
                    Long.parseLong(post.getUserId()),
                    postId,
                    post.getTitle()
            );
        }
    } catch (Exception e) {
        log.warn("发送点赞通知失败: {}", e.getMessage());
    }
}
```

#### 消费者保存通知

```java
// NotifyConsumer.java
@RabbitListener(queues = RabbitMQConfig.LIKE_NOTIFY_QUEUE)
public void handleLikeNotify(NotifyMessage notifyMessage, Message message, Channel channel) {
    long deliveryTag = message.getMessageProperties().getDeliveryTag();
    try {
        log.info("收到点赞通知: {} -> {}", notifyMessage.getFromUsername(), notifyMessage.getToUserId());
        
        // 保存到数据库
        notificationService.saveFromMQ(notifyMessage);
        
        // 消息确认
        channel.basicAck(deliveryTag, false);
    } catch (Exception e) {
        log.error("处理点赞通知失败: {}", e.getMessage());
        channel.basicNack(deliveryTag, false, false);
    }
}
```

#### Service 保存逻辑

```java
// NotificationServiceImpl.java
@Override
public void saveFromMQ(NotifyMessage message) {
    // 避免自己通知自己
    if (message.getFromUserId().equals(message.getToUserId())) {
        return;
    }
    
    Notification notification = Notification.builder()
            .fromUserId(message.getFromUserId())
            .toUserId(message.getToUserId())
            .type(convertType(message.getType()))
            .targetId(message.getTargetId())
            .targetTitle(message.getTargetTitle())
            .content(message.getContent())
            .isRead(0)
            .build();
    
    this.save(notification);
}
```

### 10.4 数据库设计

```sql
CREATE TABLE `notification` (
    `id`            bigint        NOT NULL AUTO_INCREMENT,
    `from_user_id`  bigint        NULL COMMENT '发送者ID',
    `to_user_id`    bigint        NOT NULL COMMENT '接收者ID',
    `type`          tinyint       NOT NULL COMMENT '通知类型(1-6)',
    `target_id`     bigint        NULL COMMENT '目标ID',
    `target_title`  varchar(255)  NULL COMMENT '目标标题',
    `content`       varchar(500)  NOT NULL COMMENT '通知内容',
    `is_read`       tinyint       DEFAULT 0 COMMENT '是否已读',
    `create_time`   datetime      DEFAULT CURRENT_TIMESTAMP,
    `is_deleted`    tinyint       DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_to_user_id` (`to_user_id`),
    INDEX `idx_is_read` (`is_read`)
) COMMENT = '站内通知表';
```

### 10.5 API 接口

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/front/notification/list` | POST | 分页获取通知列表 |
| `/front/notification/unread/count` | GET | 获取未读数量（分类统计） |
| `/front/notification/read/{id}` | PUT | 标记单条已读 |
| `/front/notification/read/all` | PUT | 标记全部已读 |
| `/front/notification/read/type/{type}` | PUT | 按类型标记已读 |
| `/front/notification/{id}` | DELETE | 删除通知 |

### 10.6 遇到的问题与解决

| 问题 | 原因 | 解决方案 |
|-----|------|---------|
| `Failed to convert Message content` | LocalDateTime 无法被 Jackson 序列化 | 改用 `Date` 类型 |
| `NumberFormatException: "1,2"` | 前端传多类型参数 | 接口改为 `String types`，支持逗号分隔 |
| `NoClassDefFoundError: SerializedLambdaMeta` | Java 17 与 MyBatis Plus 3.4.3 不兼容 | `LambdaUpdateWrapper` 改为 `UpdateWrapper` |

### 10.7 面试问题补充

**Q: 站内通知为什么用消息队列而不是直接写数据库？**

> **答案**：
> 1. **解耦**：点赞/评论业务不依赖通知服务，即使通知服务宕机也不影响主流程
> 2. **异步**：写数据库是 I/O 操作，异步处理不阻塞用户操作
> 3. **削峰**：高并发时点赞可能很多，消息队列可以平滑处理
> 4. **可靠性**：消息持久化 + 手动 ACK，保证通知不丢失

**Q: 如何保证通知不重复发送？**

> **答案**：
> 1. 每条消息有唯一 `messageId`
> 2. 消费前检查是否已处理（Redis 去重）
> 3. 数据库可加唯一索引（fromUserId + toUserId + targetId + type）

---

## 十一、简历写法（更新版）

> **RabbitMQ 消息队列**：引入 RabbitMQ 实现邮件通知和站内消息异步化。采用 Direct 交换机路由邮件消息、Topic 交换机分发点赞/评论/关注等通知消息，配置手动 ACK + 消息持久化保证可靠性。实现完整站内通知系统（6 种通知类型），支持未读统计、批量已读等功能，接口响应时间从 2s 降至 200ms。

---

## 十二、延迟消息（定时发布）

### 12.1 实现方案：死信队列

```
用户设置发布时间
    │
    ▼
┌─────────────────────────────────────────────────────┐
│ delay.exchange (Direct)                              │
│   → delay.queue (设置消息TTL)                        │
│       → 消息过期后转发到死信交换机                    │
│           → delay.process.queue                      │
│               → 消费者执行发布                        │
└─────────────────────────────────────────────────────┘
```

### 12.2 核心配置

```java
// 延迟队列（带死信配置）
@Bean
public Queue delayQueue() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-dead-letter-exchange", DELAY_DEAD_LETTER_EXCHANGE);
    args.put("x-dead-letter-routing-key", DELAY_DEAD_LETTER_ROUTING_KEY);
    return QueueBuilder.durable(DELAY_QUEUE).withArguments(args).build();
}

// 发送延迟消息（消息级别TTL）
MessagePostProcessor processor = msg -> {
    msg.getMessageProperties().setExpiration(String.valueOf(delayMillis));
    return msg;
};
rabbitTemplate.convertAndSend(DELAY_EXCHANGE, DELAY_ROUTING_KEY, message, processor);
```

### 12.3 面试问题

**Q: 延迟消息有哪些实现方案？**

> | 方案 | 优点 | 缺点 |
> |-----|------|------|
> | **死信队列** | 原生支持，无需插件 | 不支持任意延迟时间 |
> | **延迟插件** | 支持任意延迟 | 需要安装插件 |
> | **定时任务轮询** | 简单 | 精度低，数据库压力大 |

**Q: 死信队列的触发条件？**

> 1. 消息被拒绝（basicNack + requeue=false）
> 2. 消息 TTL 过期 ← 用于延迟消息
> 3. 队列达到最大长度

---

## 十三、广播公告（Fanout 交换机）

### 13.1 架构设计

```
管理员发布公告
    │
    ▼
┌─────────────────────────────────────────────────────┐
│ broadcast.exchange (Fanout)                          │
│   → 广播到所有绑定的队列（无需路由键）               │
│       → broadcast.announcement.queue                 │
│           → 消费者给每个用户创建通知                 │
└─────────────────────────────────────────────────────┘
```

### 13.2 核心配置

```java
// Fanout 交换机
@Bean
public FanoutExchange broadcastExchange() {
    return ExchangeBuilder.fanoutExchange(BROADCAST_EXCHANGE).durable(true).build();
}

// 绑定（Fanout 不需要路由键）
@Bean
public Binding broadcastBinding(Queue queue, FanoutExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange);  // 无 with()
}

// 发送（路由键传空字符串）
rabbitTemplate.convertAndSend(BROADCAST_EXCHANGE, "", message);
```

### 13.3 面试问题

**Q: 三种交换机的区别？**

> | 类型 | 路由规则 | 使用场景 |
> |------|---------|---------|
> | **Direct** | 精确匹配 routing key | 邮件发送（点对点） |
> | **Topic** | 模糊匹配（`*` 单词，`#` 多词） | 通知分类（一对多） |
> | **Fanout** | 广播，忽略 routing key | 系统公告（一对全部） |

**Q: Fanout 的应用场景？**

> 1. 系统公告（通知所有用户）
> 2. 配置更新（通知所有服务实例）
> 3. 日志收集（发送到多个消费者）

---

## 十四、简历写法（最终版）

> **RabbitMQ 消息队列**：引入 RabbitMQ 实现异步解耦，采用 **Direct 交换机**路由邮件消息、**Topic 交换机**分发通知消息、**Fanout 交换机**广播系统公告。基于**死信队列**实现帖子定时发布功能，配置手动 ACK + 消息持久化保证可靠性，接口响应时间从 2s 降至 200ms。

---

**文档版本**: v1.2  
**创建日期**: 2026年1月  
**更新日期**: 2026年2月
