package com.lims.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lims.system.context.UserContext;
import com.lims.system.entity.SysMenu;
import com.lims.system.entity.SysUser;
import com.lims.system.mapper.SysMenuMapper;
import com.lims.system.mapper.SysUserMapper;
import com.lims.system.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

    private final SysUserMapper userMapper;
    private final SysMenuMapper menuMapper;

    @Override
    public UserContext loadUserContext(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        List<String> roles = userMapper.findRoleCodesByUserId(userId);
        List<String> positions = userMapper.findPositionCodesByUserId(userId);
        List<Long> groupIds = userMapper.findGroupIdsByUserId(userId);
        List<String> permissions = userMapper.findUserPermissions(userId);
        List<String> dataScopes = userMapper.findUserDataScopes(userId);

        boolean isSuperAdmin = (roles != null && roles.contains("SUPER_ADMIN")) || Long.valueOf(1L).equals(userId);
        if (isSuperAdmin) {
            permissions = List.of("*:*:*");
            dataScopes = List.of("GLOBAL");
        }

        return UserContext.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .orgId(user.getOrgId())
                .roles(roles)
                .positions(positions)
                .groupIds(groupIds)
                .permissions(permissions)
                .dataScopes(dataScopes)
                .build();
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        if (Long.valueOf(1L).equals(userId)) {
            return List.of("*:*:*");
        }
        List<String> roles = userMapper.findRoleCodesByUserId(userId);
        if (roles != null && roles.contains("SUPER_ADMIN")) {
            return List.of("*:*:*");
        }
        return userMapper.findUserPermissions(userId);
    }

    @Override
    public List<String> getRolesByUserId(Long userId) {
        if (Long.valueOf(1L).equals(userId)) {
            return List.of("SUPER_ADMIN");
        }
        return userMapper.findRoleCodesByUserId(userId);
    }

    @Override
    public List<SysMenu> getMenuTreeByUserId(Long userId) {
        List<SysMenu> menuList;
        if (Long.valueOf(1L).equals(userId)) {
            menuList = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getIsDeleted, 0)
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSortOrder));
        } else {
            menuList = menuMapper.findMenuByUserId(userId);
        }
        return buildTree(menuList, 0L);
    }

    @Override
    public List<SysMenu> getAllMenuTree() {
        List<SysMenu> menuList = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getIsDeleted, 0)
                .orderByAsc(SysMenu::getParentId, SysMenu::getSortOrder));
        return buildTree(menuList, 0L);
    }

    private List<SysMenu> buildTree(List<SysMenu> list, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : list) {
            if (parentId.equals(menu.getParentId())) {
                tree.add(menu);
            }
        }
        return tree;
    }
}
