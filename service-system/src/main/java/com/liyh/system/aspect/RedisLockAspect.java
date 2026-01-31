package com.liyh.system.aspect;

import com.liyh.system.annotation.RedisLock;
import com.liyh.system.exception.BusinessException;
import com.liyh.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Redis 分布式锁切面
 *
 * @Author LiYH
 */
@Aspect
@Component
@Slf4j
public class RedisLockAspect {

    @Autowired
    private RedisUtil redisUtil;

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        // 1. 解析锁的 Key
        String lockKey = redisLock.prefix() + parseKey(redisLock.key(), joinPoint);
        // 2. 生成请求唯一标识
        String requestId = UUID.randomUUID().toString();
        // 3. 锁过期时间
        long expireTime = redisLock.expireTime();

        log.debug("尝试获取分布式锁, key: {}, requestId: {}", lockKey, requestId);

        // 4. 尝试获取锁
        Boolean locked = redisUtil.tryLock(lockKey, requestId, expireTime);

        if (!Boolean.TRUE.equals(locked)) {
            log.warn("获取分布式锁失败, key: {}", lockKey);
            throw new BusinessException(redisLock.message());
        }

        try {
            log.debug("获取分布式锁成功, key: {}", lockKey);
            // 5. 执行业务逻辑
            return joinPoint.proceed();
        } finally {
            // 6. 释放锁
            redisUtil.releaseLock(lockKey, requestId);
            log.debug("释放分布式锁, key: {}", lockKey);
        }
    }

    /**
     * 解析 SpEL 表达式获取 Key
     */
    private String parseKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        // 如果不是 SpEL 表达式，直接返回
        if (!keyExpression.contains("#")) {
            return keyExpression;
        }

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取参数名
        String[] parameterNames = nameDiscoverer.getParameterNames(method);
        // 获取参数值
        Object[] args = joinPoint.getArgs();

        // 创建 SpEL 上下文
        EvaluationContext context = new StandardEvaluationContext();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // 解析表达式
        Expression expression = parser.parseExpression(keyExpression);
        Object value = expression.getValue(context);
        return value == null ? "" : value.toString();
    }
}
