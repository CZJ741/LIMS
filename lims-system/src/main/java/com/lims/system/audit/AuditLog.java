package com.lims.system.audit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作审计日志注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /**
     * 业务模块名称
     */
    String module();

    /**
     * 操作类型 (如 INSERT, UPDATE, DELETE, AUDIT, EXPORT)
     */
    String operation();

    /**
     * 业务描述
     */
    String description() default "";

    /**
     * 提取主键 SpEL 表达式 (如 "#id" 或 "#user.id")
     */
    String bizKey() default "";
}
