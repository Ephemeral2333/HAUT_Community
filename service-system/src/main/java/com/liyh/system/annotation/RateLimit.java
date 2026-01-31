package com.liyh.system.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 * 基于 Redis 滑动窗口算法实现
 *
 * @Author LiYH
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流 Key 前缀
     */
    String prefix() default "rate:limit:";

    /**
     * 限流 Key（支持 SpEL 表达式）
     * 例如：#email 表示取方法参数中的 email
     * 例如：#loginVo.username 表示取对象中的属性
     */
    String key() default "";

    /**
     * 限流时间窗口内允许的最大请求次数
     */
    int limit() default 5;

    /**
     * 时间窗口（秒）
     */
    int period() default 60;

    /**
     * 限流维度：IP / USER / CUSTOM
     * IP: 基于客户端 IP 限流
     * USER: 基于用户 ID 限流（需要登录）
     * CUSTOM: 基于自定义 key 限流
     */
    LimitType limitType() default LimitType.CUSTOM;

    /**
     * 超出限流时的提示信息
     */
    String message() default "请求太频繁，请稍后再试";

    /**
     * 限流维度枚举
     */
    enum LimitType {
        /**
         * 基于 IP 限流
         */
        IP,
        /**
         * 基于用户 ID 限流
         */
        USER,
        /**
         * 基于自定义 Key 限流
         */
        CUSTOM
    }
}
