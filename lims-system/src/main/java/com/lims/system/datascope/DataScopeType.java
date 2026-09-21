package com.lims.system.datascope;

/**
 * 数据范围类型枚举
 */
public enum DataScopeType {

    /**
     * 全局数据权限（无限制）
     */
    GLOBAL,

    /**
     * 本部门及下属部门数据
     */
    DEPARTMENT,

    /**
     * 仅指定项目组数据
     */
    PROJECT_GROUP,

    /**
     * 仅本人创建/经办数据
     */
    PERSONAL
}
