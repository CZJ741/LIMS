package com.lims.system.service;

import com.lims.system.context.UserContext;
import com.lims.system.entity.SysMenu;

import java.util.List;

public interface IPermissionService {

    UserContext loadUserContext(Long userId);

    List<String> getPermissionsByUserId(Long userId);

    List<String> getRolesByUserId(Long userId);

    List<SysMenu> getMenuTreeByUserId(Long userId);

    List<SysMenu> getAllMenuTree();
}
