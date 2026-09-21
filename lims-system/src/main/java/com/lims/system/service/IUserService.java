package com.lims.system.service;

import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.system.entity.SysUser;

import java.util.List;

public interface IUserService {

    SysUser getById(Long id);

    SysUser getByUsername(String username);

    PageResp<SysUser> pageUsers(PageReq pageReq, String username, String realName, Long orgId);

    Long createUser(SysUser user);

    void updateUser(SysUser user);

    void deleteUser(Long id);

    void resetPassword(Long id, String newPassword);

    void assignRoles(Long userId, List<Long> roleIds);

    void assignPositions(Long userId, List<Long> positionIds);

    void assignProjectGroups(Long userId, List<Long> groupIds);
}
