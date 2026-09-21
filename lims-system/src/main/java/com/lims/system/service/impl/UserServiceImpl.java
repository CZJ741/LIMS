package com.lims.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.exception.BizException;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.system.entity.SysUser;
import com.lims.system.entity.SysUserPosition;
import com.lims.system.entity.SysUserProjectGroup;
import com.lims.system.entity.SysUserRole;
import com.lims.system.mapper.SysUserMapper;
import com.lims.system.mapper.SysUserPositionMapper;
import com.lims.system.mapper.SysUserProjectGroupMapper;
import com.lims.system.mapper.SysUserRoleMapper;
import com.lims.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysUserPositionMapper userPositionMapper;
    private final SysUserProjectGroupMapper userProjectGroupMapper;

    @Override
    public SysUser getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getIsDeleted, 0));
    }

    @Override
    public PageResp<SysUser> pageUsers(PageReq pageReq, String username, String realName, Long orgId) {
        Page<SysUser> page = new Page<>(pageReq.getCurrent(), pageReq.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getIsDeleted, 0)
                .like(username != null && !username.isEmpty(), SysUser::getUsername, username)
                .like(realName != null && !realName.isEmpty(), SysUser::getRealName, realName)
                .eq(orgId != null, SysUser::getOrgId, orgId)
                .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        // 脱敏密码
        result.getRecords().forEach(u -> u.setPassword(null));
        return PageResp.of(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(SysUser user) {
        SysUser exist = getByUsername(user.getUsername());
        if (exist != null) {
            throw new BizException("账号已存在: " + user.getUsername());
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(BCrypt.hashpw("123456"));
        } else {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        }
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUser user) {
        user.setPassword(null); // 禁止在此接口篡改密码
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        if (Long.valueOf(1L).equals(id)) {
            throw new BizException("超级管理员不可删除");
        }
        userMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, String newPassword) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(BCrypt.hashpw(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long rId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(rId);
                ur.setCreateTime(LocalDateTime.now());
                userRoleMapper.insert(ur);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPositions(Long userId, List<Long> positionIds) {
        userPositionMapper.delete(new LambdaQueryWrapper<SysUserPosition>().eq(SysUserPosition::getUserId, userId));
        if (positionIds != null && !positionIds.isEmpty()) {
            for (Long pId : positionIds) {
                SysUserPosition up = new SysUserPosition();
                up.setUserId(userId);
                up.setPositionId(pId);
                up.setCreateTime(LocalDateTime.now());
                userPositionMapper.insert(up);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignProjectGroups(Long userId, List<Long> groupIds) {
        userProjectGroupMapper.delete(new LambdaQueryWrapper<SysUserProjectGroup>().eq(SysUserProjectGroup::getUserId, userId));
        if (groupIds != null && !groupIds.isEmpty()) {
            for (Long gId : groupIds) {
                SysUserProjectGroup upg = new SysUserProjectGroup();
                upg.setUserId(userId);
                upg.setGroupId(gId);
                upg.setCreateTime(LocalDateTime.now());
                userProjectGroupMapper.insert(upg);
            }
        }
    }
}
