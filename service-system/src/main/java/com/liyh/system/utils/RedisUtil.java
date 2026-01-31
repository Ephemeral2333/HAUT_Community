package com.liyh.system.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类 - 提供通用的 Redis 操作方法
 *
 * @Author LiYH
 */
@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== String 操作 ====================

    /**
     * 设置值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值并设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取值并转换为Long
     */
    public Long getLong(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return Long.parseLong(value.toString());
    }

    /**
     * 自增
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增指定值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减
     */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 自增并设置过期时间（用于限流）
     * 如果 key 不存在，先自增再设置过期时间
     * 如果 key 已存在，只自增不修改过期时间
     *
     * @param key      键
     * @param timeout  过期时间
     * @param unit     时间单位
     * @return 自增后的值
     */
    public Long incrementWithExpire(String key, long timeout, TimeUnit unit) {
        Long count = redisTemplate.opsForValue().increment(key);
        // 如果是第一次访问（count == 1），设置过期时间
        if (count != null && count == 1) {
            redisTemplate.expire(key, timeout, unit);
        }
        return count;
    }

    /**
     * 删除键
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 判断键是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取剩余过期时间（秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // ==================== Set 操作 ====================

    /**
     * Set 添加成员
     */
    public Long setAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * Set 移除成员
     */
    public Long setRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * Set 判断是否是成员
     */
    public Boolean setIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * Set 获取所有成员
     */
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * Set 获取成员数量
     */
    public Long setSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    // ==================== ZSet 操作 ====================

    /**
     * ZSet 添加成员
     */
    public Boolean zsetAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * ZSet 增加分数
     */
    public Double zsetIncrementScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * ZSet 获取排名（降序，分数从高到低）
     */
    public Set<Object> zsetReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * ZSet 获取成员分数
     */
    public Double zsetScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * ZSet 移除成员
     */
    public Long zsetRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * ZSet 获取成员数量
     */
    public Long zsetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    // ==================== Hash 操作 ====================

    /**
     * Hash 设置值
     */
    public void hashPut(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * Hash 获取值
     */
    public Object hashGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * Hash 删除
     */
    public Long hashDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * Hash 判断是否存在
     */
    public Boolean hashHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Hash 自增
     */
    public Long hashIncrement(String key, String hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    // ==================== 分布式锁 ====================

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey    锁的 Key
     * @param requestId  请求标识（用于释放锁时验证）
     * @param expireTime 锁过期时间（秒）
     * @return 是否获取成功
     */
    public Boolean tryLock(String lockKey, String requestId, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁的 Key
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public Boolean releaseLock(String lockKey, String requestId) {
        Object currentValue = redisTemplate.opsForValue().get(lockKey);
        if (currentValue != null && currentValue.toString().equals(requestId)) {
            return redisTemplate.delete(lockKey);
        }
        return false;
    }

    /**
     * 尝试获取锁，带自旋重试
     *
     * @param lockKey    锁的 Key
     * @param requestId  请求标识
     * @param expireTime 锁过期时间（秒）
     * @param retryTimes 重试次数
     * @param sleepTime  重试间隔（毫秒）
     * @return 是否获取成功
     */
    public Boolean tryLockWithRetry(String lockKey, String requestId, long expireTime, int retryTimes, long sleepTime) {
        for (int i = 0; i < retryTimes; i++) {
            if (Boolean.TRUE.equals(tryLock(lockKey, requestId, expireTime))) {
                return true;
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}
