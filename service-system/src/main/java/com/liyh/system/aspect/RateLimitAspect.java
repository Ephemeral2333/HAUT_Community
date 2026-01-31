package com.liyh.system.aspect;

import com.liyh.common.utils.JwtHelper;
import com.liyh.system.annotation.RateLimit;
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
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流切面
 * 基于 Redis 实现滑动窗口限流算法
 *
 * @Author LiYH
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private RedisUtil redisUtil;

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 1. 获取限流 Key
        String limitKey = buildLimitKey(rateLimit, joinPoint);
        
        // 2. 获取限流参数
        int limit = rateLimit.limit();
        int period = rateLimit.period();

        // 3. 执行限流检查
        long currentCount = redisUtil.incrementWithExpire(limitKey, period, TimeUnit.SECONDS);

        log.debug("限流检查 - Key: {}, 当前请求数: {}, 限制: {}/{} 秒", 
                  limitKey, currentCount, limit, period);

        // 4. 判断是否超过限制
        if (currentCount > limit) {
            log.warn("触发限流 - Key: {}, 当前请求数: {}, 限制: {}", limitKey, currentCount, limit);
            throw new BusinessException(rateLimit.message());
        }

        // 5. 未超过限制，执行业务逻辑
        return joinPoint.proceed();
    }

    /**
     * 构建限流 Key
     */
    private String buildLimitKey(RateLimit rateLimit, ProceedingJoinPoint joinPoint) {
        StringBuilder keyBuilder = new StringBuilder(rateLimit.prefix());
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 添加类名和方法名作为 Key 的一部分
        keyBuilder.append(method.getDeclaringClass().getSimpleName())
                  .append(":")
                  .append(method.getName())
                  .append(":");

        // 根据限流类型构建 Key
        switch (rateLimit.limitType()) {
            case IP:
                keyBuilder.append(getClientIp());
                break;
            case USER:
                keyBuilder.append(getCurrentUserId());
                break;
            case CUSTOM:
            default:
                // 自定义 Key，解析 SpEL 表达式
                String customKey = rateLimit.key();
                if (StringUtils.hasText(customKey)) {
                    keyBuilder.append(parseKey(customKey, joinPoint));
                } else {
                    // 没有自定义 Key，使用 IP 作为默认
                    keyBuilder.append(getClientIp());
                }
                break;
        }

        return keyBuilder.toString();
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
        try {
            Expression expression = parser.parseExpression(keyExpression);
            Object value = expression.getValue(context);
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            log.warn("SpEL 表达式解析失败: {}, 使用原值", keyExpression);
            return keyExpression;
        }
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 优先从代理头获取真实 IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 多个代理时，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    /**
     * 获取当前用户 ID
     */
    private String getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "anonymous";
        }
        
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        
        if (StringUtils.hasText(token)) {
            String userId = JwtHelper.getUserId(token);
            return userId != null ? userId : "anonymous";
        }
        
        return "anonymous";
    }
}
