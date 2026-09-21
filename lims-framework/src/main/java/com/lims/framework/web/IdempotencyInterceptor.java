package com.lims.framework.web;

import cn.hutool.core.util.StrUtil;
import com.lims.common.constant.SystemConstants;
import com.lims.common.exception.BizException;
import com.lims.common.result.LimsBizErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

/**
 * 接口幂等防重拦截器
 * 基于请求头 Idempotency-Key，通过 Redis SETNX 保持 5 分钟，防止重复提交
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String IDEMPOTENCY_KEY_PREFIX = "lims:idempotency:";
    private static final Duration LOCK_EXPIRE = Duration.ofMinutes(5);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        // 仅对写接口 (POST, PUT, DELETE) 校验幂等性
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            String idempotencyKey = request.getHeader(SystemConstants.IDEMPOTENCY_HEADER);
            if (StrUtil.isNotBlank(idempotencyKey)) {
                String redisKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
                Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "1", LOCK_EXPIRE);
                if (Boolean.FALSE.equals(success)) {
                    log.warn("检测到重复请求拦截: uri={}, idempotencyKey={}", request.getRequestURI(), idempotencyKey);
                    throw new BizException(LimsBizErrorCode.IDEMPOTENCY_VIOLATION);
                }
            }
        }
        return true;
    }
}
