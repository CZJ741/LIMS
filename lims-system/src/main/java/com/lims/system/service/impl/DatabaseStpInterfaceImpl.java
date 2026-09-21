package com.lims.system.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.lims.system.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 接入真实数据库的 Sa-Token 权限和角色认证实现
 */
@Component
@Primary
@RequiredArgsConstructor
public class DatabaseStpInterfaceImpl implements StpInterface {

    private final IPermissionService permissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.valueOf(String.valueOf(loginId));
        return permissionService.getPermissionsByUserId(userId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.valueOf(String.valueOf(loginId));
        return permissionService.getRolesByUserId(userId);
    }
}
