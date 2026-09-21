package com.lims.system.context;

/**
 * 用户上下文 ThreadLocal 管理器
 */
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT_HOLDER = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(UserContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static UserContext get() {
        return CONTEXT_HOLDER.get();
    }

    public static Long getUserId() {
        UserContext ctx = get();
        return ctx != null ? ctx.getUserId() : null;
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
