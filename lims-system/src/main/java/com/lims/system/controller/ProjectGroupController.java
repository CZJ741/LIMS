package com.lims.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.result.Result;
import com.lims.system.audit.AuditLog;
import com.lims.system.entity.SysProjectGroup;
import com.lims.system.entity.SysUser;
import com.lims.system.service.IProjectGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "项目组管理", description = "班组/项目组多维度业务协同与成员维护")
@RestController
@RequestMapping("/api/system/project-group")
@RequiredArgsConstructor
public class ProjectGroupController {

    private final IProjectGroupService projectGroupService;

    @Operation(summary = "分页查询项目组")
    @SaCheckPermission("system:group:view")
    @GetMapping("/page")
    public Result<PageResp<SysProjectGroup>> pageGroups(
            PageReq pageReq,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String groupCode) {
        return Result.ok(projectGroupService.pageGroups(pageReq, groupName, groupCode));
    }

    @Operation(summary = "新增项目组")
    @SaCheckPermission("system:group:add")
    @AuditLog(module = "项目组管理", operation = "INSERT", description = "创建新项目组")
    @PostMapping
    public Result<Long> createGroup(@RequestBody SysProjectGroup group) {
        return Result.ok(projectGroupService.createGroup(group));
    }

    @Operation(summary = "修改项目组")
    @SaCheckPermission("system:group:edit")
    @AuditLog(module = "项目组管理", operation = "UPDATE", description = "修改项目组信息")
    @PutMapping("/{id}")
    public Result<Void> updateGroup(@PathVariable Long id, @RequestBody SysProjectGroup group) {
        group.setId(id);
        projectGroupService.updateGroup(group);
        return Result.ok();
    }

    @Operation(summary = "删除项目组")
    @SaCheckPermission("system:group:delete")
    @AuditLog(module = "项目组管理", operation = "DELETE", description = "删除项目组")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGroup(@PathVariable Long id) {
        projectGroupService.deleteGroup(id);
        return Result.ok();
    }

    @Operation(summary = "获取项目组成员列表")
    @SaCheckPermission("system:group:view")
    @GetMapping("/{id}/members")
    public Result<List<SysUser>> getGroupMembers(@PathVariable Long id) {
        return Result.ok(projectGroupService.getGroupMembers(id));
    }

    @Operation(summary = "添加项目组成员")
    @SaCheckPermission("system:group:edit")
    @AuditLog(module = "项目组管理", operation = "UPDATE", description = "添加项目组成员")
    @PostMapping("/{id}/members/{userId}")
    public Result<Void> addGroupMember(@PathVariable Long id, @PathVariable Long userId) {
        projectGroupService.addGroupMember(id, userId);
        return Result.ok();
    }

    @Operation(summary = "移除项目组成员")
    @SaCheckPermission("system:group:edit")
    @AuditLog(module = "项目组管理", operation = "UPDATE", description = "移除项目组成员")
    @DeleteMapping("/{id}/members/{userId}")
    public Result<Void> removeGroupMember(@PathVariable Long id, @PathVariable Long userId) {
        projectGroupService.removeGroupMember(id, userId);
        return Result.ok();
    }
}
