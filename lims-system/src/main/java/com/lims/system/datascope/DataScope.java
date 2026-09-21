package com.lims.system.datascope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据范围过滤注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 数据范围类型
     */
    DataScopeType scopeType() default DataScopeType.DEPARTMENT;

    /**
     * 目标表主别名
     */
    String tableAlias() default "";

    /**
     * 部门ID关联字段名
     */
    String orgIdColumn() default "org_id";

    /**
     * 用户ID关联字段名
     */
    String userIdColumn() default "create_by";

    /**
     * 项目组ID关联字段名
     */
    String groupIdColumn() default "project_group_id";
}
