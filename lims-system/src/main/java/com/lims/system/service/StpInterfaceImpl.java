package com.lims.system.service;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限与角色加载实现（系统管理模块）
 */
@Component
@Primary
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 超级管理员拥有所有权限
        if ("1".equals(String.valueOf(loginId)) || "admin".equals(String.valueOf(loginId))) {
            return List.of("*:*:*");
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if ("1".equals(String.valueOf(loginId)) || "admin".equals(String.valueOf(loginId))) {
            return List.of("SUPER_ADMIN");
        }
        return Collections.emptyList();
    }
}
