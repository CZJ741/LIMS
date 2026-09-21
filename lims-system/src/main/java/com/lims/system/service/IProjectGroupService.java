package com.lims.system.service;

import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.system.entity.SysProjectGroup;
import com.lims.system.entity.SysUser;

import java.util.List;

public interface IProjectGroupService {

    PageResp<SysProjectGroup> pageGroups(PageReq pageReq, String groupName, String groupCode);

    Long createGroup(SysProjectGroup group);

    void updateGroup(SysProjectGroup group);

    void deleteGroup(Long id);

    List<SysUser> getGroupMembers(Long groupId);

    void addGroupMember(Long groupId, Long userId);

    void removeGroupMember(Long groupId, Long userId);
}
