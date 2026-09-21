package com.lims.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.exception.BizException;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.system.entity.SysRole;
import com.lims.system.entity.SysRoleMenu;
import com.lims.system.mapper.SysRoleMapper;
import com.lims.system.mapper.SysRoleMenuMapper;
import com.lims.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public PageResp<SysRole> pageRoles(PageReq pageReq, String roleName, String roleCode) {
        Page<SysRole> page = new Page<>(pageReq.getCurrent(), pageReq.getSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getIsDeleted, 0)
                .like(roleName != null && !roleName.isEmpty(), SysRole::getRoleName, roleName)
                .like(roleCode != null && !roleCode.isEmpty(), SysRole::getRoleCode, roleCode)
                .orderByAsc(SysRole::getSortOrder);

        Page<SysRole> result = roleMapper.selectPage(page, wrapper);
        return PageResp.of(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(SysRole role) {
        SysRole exist = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, role.getRoleCode())
                .eq(SysRole::getIsDeleted, 0));
        if (exist != null) {
            throw new BizException("角色编码已存在: " + role.getRoleCode());
        }
        role.setCreateTime(LocalDateTime.now());
        roleMapper.insert(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role) {
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        if (Long.valueOf(1L).equals(id)) {
            throw new BizException("超级管理员角色不可删除");
        }
        roleMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> menuIds, Map<Long, String> dataScopeMap) {
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                String scope = dataScopeMap != null ? dataScopeMap.getOrDefault(menuId, "GLOBAL") : "GLOBAL";
                rm.setDataScope(scope);
                rm.setCreateTime(LocalDateTime.now());
                roleMenuMapper.insert(rm);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyPermissions(Long sourceRoleId, Long targetRoleId) {
        List<SysRoleMenu> sourceList = roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, sourceRoleId)
                .eq(SysRoleMenu::getIsDeleted, 0));

        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, targetRoleId));
        for (SysRoleMenu item : sourceList) {
            SysRoleMenu newItem = new SysRoleMenu();
            newItem.setRoleId(targetRoleId);
            newItem.setMenuId(item.getMenuId());
            newItem.setDataScope(item.getDataScope());
            newItem.setCreateTime(LocalDateTime.now());
            roleMenuMapper.insert(newItem);
        }
    }
}
