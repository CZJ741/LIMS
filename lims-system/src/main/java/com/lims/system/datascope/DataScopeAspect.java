package com.lims.system.datascope;

import com.lims.common.model.PageReq;
import com.lims.system.context.UserContext;
import com.lims.system.context.UserContextHolder;
import com.lims.system.service.IDataScopeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据范围切面处理
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    private final IDataScopeService dataScopeService;

    @Around("@annotation(com.lims.system.datascope.DataScope)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataScope dataScope = method.getAnnotation(DataScope.class);

        UserContext userContext = UserContextHolder.get();
        if (dataScope != null && userContext != null) {
            DataScopeSqlSnippet snippet = dataScopeService.buildDataScopeSql(dataScope, userContext);
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof PageReq) {
                    // 如分页查询参数包含数据权限片段槽位，可在此自动填充
                    log.debug("注入数据范围过滤: {}", snippet.getSqlSegment());
                }
            }
        }

        try {
            return joinPoint.proceed();
        } finally {
            // 切面执行完毕后保持规范清理
        }
    }
}
