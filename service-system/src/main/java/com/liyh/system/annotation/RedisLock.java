package com.liyh.system.annotation;

import java.lang.annotation.*;

/**
 * Redis 分布式锁注解
 * 用于防止接口重复提交
 *
 * @Author LiYH
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /**
     * 锁的 Key 前缀
     */
    String prefix() default "lock:";

    /**
     * 锁的 Key（支持 SpEL 表达式）
     * 例如：#userId 表示取方法参数中的 userId
     */
    String key();

    /**
     * 锁过期时间（秒），默认 10 秒
     */
    long expireTime() default 10;

    /**
     * 获取锁失败时的提示信息
     */
    String message() default "操作太频繁，请稍后再试";
}
