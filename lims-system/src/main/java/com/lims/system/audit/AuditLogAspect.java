package com.lims.system.audit;

import com.lims.common.util.JsonUtils;
import com.lims.system.context.UserContext;
import com.lims.system.context.UserContextHolder;
import com.lims.system.entity.SysAuditLog;
import com.lims.system.mapper.SysAuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 审计日志 AOP 切面实现
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final SysAuditLogMapper auditLogMapper;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(com.lims.system.audit.AuditLog)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditLog auditLog = method.getAnnotation(AuditLog.class);

        SysAuditLog logEntity = new SysAuditLog();
        logEntity.setCreateTime(LocalDateTime.now());
        logEntity.setModuleName(auditLog.module());
        logEntity.setOperationType(auditLog.operation());
        logEntity.setMethodName(method.getDeclaringClass().getName() + "." + method.getName());

        // 提取用户信息
        UserContext userContext = UserContextHolder.get();
        if (userContext != null) {
            logEntity.setUserId(userContext.getUserId());
            logEntity.setUsername(userContext.getUsername());
            logEntity.setRealName(userContext.getRealName());
        }

        // 提取请求上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logEntity.setRequestUri(request.getRequestURI());
            logEntity.setRequestIp(getClientIp(request));
        } else {
            logEntity.setRequestUri("/internal");
            logEntity.setRequestIp("127.0.0.1");
        }

        // 解析入参作为变更前参数
        try {
            logEntity.setParamBefore(JsonUtils.toJsonString(joinPoint.getArgs()));
        } catch (Exception e) {
            logEntity.setParamBefore("{}");
        }

        // 执行目标方法并记录变更结果与状态
        Object result = null;
        try {
            result = joinPoint.proceed();
            logEntity.setStatus(1);
            try {
                logEntity.setParamAfter(JsonUtils.toJsonString(result));
            } catch (Exception ignored) {
            }
            return result;
        } catch (Throwable t) {
            logEntity.setStatus(0);
            logEntity.setErrorMsg(t.getMessage());
            throw t;
        } finally {
            logEntity.setExecutionTime(System.currentTimeMillis() - startTime);
            try {
                auditLogMapper.insert(logEntity);
            } catch (Exception e) {
                log.error("写入审计日志失败", e);
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return (ip != null && ip.contains(",")) ? ip.split(",")[0].trim() : ip;
    }
}
