package com.lims.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.result.Result;
import com.lims.system.audit.AuditLog;
import com.lims.system.entity.SysUser;
import com.lims.system.service.IUserService;
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

@Tag(name = "用户管理", description = "用户账号维护与三维度权限分配")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(summary = "分页查询用户列表")
    @SaCheckPermission("system:user:view")
    @GetMapping("/page")
    public Result<PageResp<SysUser>> pageUsers(
            PageReq pageReq,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Long orgId) {
        return Result.ok(userService.pageUsers(pageReq, username, realName, orgId));
    }

    @Operation(summary = "新增用户")
    @SaCheckPermission("system:user:add")
    @AuditLog(module = "用户管理", operation = "INSERT", description = "创建新用户账号")
    @PostMapping
    public Result<Long> createUser(@RequestBody SysUser user) {
        return Result.ok(userService.createUser(user));
    }

    @Operation(summary = "修改用户")
    @SaCheckPermission("system:user:edit")
    @AuditLog(module = "用户管理", operation = "UPDATE", description = "修改用户基础信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.ok();
    }

    @Operation(summary = "删除用户")
    @SaCheckPermission("system:user:delete")
    @AuditLog(module = "用户管理", operation = "DELETE", description = "软删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @Operation(summary = "重置密码")
    @SaCheckPermission("system:user:resetPwd")
    @AuditLog(module = "用户管理", operation = "UPDATE", description = "重置用户密码")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam(defaultValue = "123456") String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.ok();
    }

    @Operation(summary = "分配角色")
    @SaCheckPermission("system:user:assignRole")
    @AuditLog(module = "用户管理", operation = "UPDATE", description = "为用户分配角色")
    @PostMapping("/{id}/assign-roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.ok();
    }

    @Operation(summary = "分配岗位")
    @SaCheckPermission("system:user:assignPosition")
    @AuditLog(module = "用户管理", operation = "UPDATE", description = "为用户分配岗位身份")
    @PostMapping("/{id}/assign-positions")
    public Result<Void> assignPositions(@PathVariable Long id, @RequestBody List<Long> positionIds) {
        userService.assignPositions(id, positionIds);
        return Result.ok();
    }

    @Operation(summary = "分配项目组")
    @SaCheckPermission("system:user:assignGroup")
    @AuditLog(module = "用户管理", operation = "UPDATE", description = "为用户分配项目组")
    @PostMapping("/{id}/assign-project-groups")
    public Result<Void> assignProjectGroups(@PathVariable Long id, @RequestBody List<Long> groupIds) {
        userService.assignProjectGroups(id, groupIds);
        return Result.ok();
    }
}
