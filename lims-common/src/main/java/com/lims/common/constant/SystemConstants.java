package com.lims.common.constant;

/**
 * 系统级常量
 */
public final class SystemConstants {

    private SystemConstants() {
    }

    /**
     * 系统默认时区
     */
    public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";

    /**
     * 系统默认编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 500;

    /**
     * 幂等防重 Key 请求头
     */
    public static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    /**
     * 跟踪号请求头
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 超级管理员角色标识
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * 超级管理员用户 ID
     */
    public static final Long SUPER_ADMIN_USER_ID = 1L;

    /**
     * 逻辑删除状态：0 未删除，1 已删除
     */
    public static final Integer DEL_FLAG_NORMAL = 0;
    public static final Integer DEL_FLAG_DELETED = 1;

    /**
     * 状态：0 禁用，1 启用
     */
    public static final Integer STATUS_DISABLE = 0;
    public static final Integer STATUS_ENABLE = 1;
}
