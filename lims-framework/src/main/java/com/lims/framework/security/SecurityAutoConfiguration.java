package com.lims.framework.security;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.lims.system.interceptor.UserContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 权限与拦截器配置
 */
@AutoConfiguration
@RequiredArgsConstructor
public class SecurityAutoConfiguration implements WebMvcConfigurer {

    private final UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Sa-Token 注解与路由鉴权拦截器
        registry.addInterceptor(new SaInterceptor(handle -> {
            SaRouter.match("/**")
                    .notMatch(
                            "/api/auth/login",
                            "/api/auth/captcha",
                            "/doc.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/webjars/**",
                            "/favicon.ico"
                    )
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");

        // 2. 当前用户上下文 ThreadLocal 拦截器
        registry.addInterceptor(userContextInterceptor).addPathPatterns("/**");
    }
}
