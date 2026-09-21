package com.lims.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.exception.BizException;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.system.entity.SysProjectGroup;
import com.lims.system.entity.SysUser;
import com.lims.system.entity.SysUserProjectGroup;
import com.lims.system.mapper.SysProjectGroupMapper;
import com.lims.system.mapper.SysUserMapper;
import com.lims.system.mapper.SysUserProjectGroupMapper;
import com.lims.system.service.IProjectGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectGroupServiceImpl implements IProjectGroupService {

    private final SysProjectGroupMapper groupMapper;
    private final SysUserProjectGroupMapper userGroupMapper;
    private final SysUserMapper userMapper;

    @Override
    public PageResp<SysProjectGroup> pageGroups(PageReq pageReq, String groupName, String groupCode) {
        Page<SysProjectGroup> page = new Page<>(pageReq.getCurrent(), pageReq.getSize());
        LambdaQueryWrapper<SysProjectGroup> wrapper = new LambdaQueryWrapper<SysProjectGroup>()
                .eq(SysProjectGroup::getIsDeleted, 0)
                .like(groupName != null && !groupName.isEmpty(), SysProjectGroup::getGroupName, groupName)
                .like(groupCode != null && !groupCode.isEmpty(), SysProjectGroup::getGroupCode, groupCode)
                .orderByDesc(SysProjectGroup::getCreateTime);

        Page<SysProjectGroup> result = groupMapper.selectPage(page, wrapper);
        return PageResp.of(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGroup(SysProjectGroup group) {
        SysProjectGroup exist = groupMapper.selectOne(new LambdaQueryWrapper<SysProjectGroup>()
                .eq(SysProjectGroup::getGroupCode, group.getGroupCode())
                .eq(SysProjectGroup::getIsDeleted, 0));
        if (exist != null) {
            throw new BizException("项目组编码已存在: " + group.getGroupCode());
        }
        group.setCreateTime(LocalDateTime.now());
        groupMapper.insert(group);

        // 默认将组长加入该项目组
        if (group.getLeaderId() != null) {
            addGroupMember(group.getId(), group.getLeaderId());
        }
        return group.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroup(SysProjectGroup group) {
        group.setUpdateTime(LocalDateTime.now());
        groupMapper.updateById(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(Long id) {
        groupMapper.deleteById(id);
        userGroupMapper.delete(new LambdaQueryWrapper<SysUserProjectGroup>()
                .eq(SysUserProjectGroup::getGroupId, id));
    }

    @Override
    public List<SysUser> getGroupMembers(Long groupId) {
        List<SysUserProjectGroup> list = userGroupMapper.selectList(new LambdaQueryWrapper<SysUserProjectGroup>()
                .eq(SysUserProjectGroup::getGroupId, groupId)
                .eq(SysUserProjectGroup::getIsDeleted, 0));
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> userIds = list.stream().map(SysUserProjectGroup::getUserId).collect(Collectors.toList());
        return userMapper.selectBatchIds(userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGroupMember(Long groupId, Long userId) {
        SysUserProjectGroup exist = userGroupMapper.selectOne(new LambdaQueryWrapper<SysUserProjectGroup>()
                .eq(SysUserProjectGroup::getGroupId, groupId)
                .eq(SysUserProjectGroup::getUserId, userId));
        if (exist == null) {
            SysUserProjectGroup record = new SysUserProjectGroup();
            record.setGroupId(groupId);
            record.setUserId(userId);
            record.setCreateTime(LocalDateTime.now());
            userGroupMapper.insert(record);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGroupMember(Long groupId, Long userId) {
        userGroupMapper.delete(new LambdaQueryWrapper<SysUserProjectGroup>()
                .eq(SysUserProjectGroup::getGroupId, groupId)
                .eq(SysUserProjectGroup::getUserId, userId));
    }
}
