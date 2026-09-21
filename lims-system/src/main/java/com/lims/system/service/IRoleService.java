package com.lims.system.service;

import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.system.entity.SysRole;

import java.util.List;
import java.util.Map;

public interface IRoleService {

    PageResp<SysRole> pageRoles(PageReq pageReq, String roleName, String roleCode);

    Long createRole(SysRole role);

    void updateRole(SysRole role);

    void deleteRole(Long id);

    void assignPermissions(Long roleId, List<Long> menuIds, Map<Long, String> dataScopeMap);

    void copyPermissions(Long sourceRoleId, Long targetRoleId);
}
