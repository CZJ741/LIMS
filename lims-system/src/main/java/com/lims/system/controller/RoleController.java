package com.lims.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.result.Result;
import com.lims.system.audit.AuditLog;
import com.lims.system.dto.AssignPermissionReq;
import com.lims.system.entity.SysRole;
import com.lims.system.service.IRoleService;
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

@Tag(name = "角色管理", description = "角色增删改查与权限矩阵配置")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @SaCheckPermission("system:role:view")
    @GetMapping("/page")
    public Result<PageResp<SysRole>> pageRoles(
            PageReq pageReq,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode) {
        return Result.ok(roleService.pageRoles(pageReq, roleName, roleCode));
    }

    @Operation(summary = "新增角色")
    @SaCheckPermission("system:role:add")
    @AuditLog(module = "角色管理", operation = "INSERT", description = "新增系统角色")
    @PostMapping
    public Result<Long> createRole(@RequestBody SysRole role) {
        return Result.ok(roleService.createRole(role));
    }

    @Operation(summary = "修改角色")
    @SaCheckPermission("system:role:edit")
    @AuditLog(module = "角色管理", operation = "UPDATE", description = "修改系统角色")
    @PutMapping("/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        roleService.updateRole(role);
        return Result.ok();
    }

    @Operation(summary = "删除角色")
    @SaCheckPermission("system:role:delete")
    @AuditLog(module = "角色管理", operation = "DELETE", description = "删除系统角色")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.ok();
    }

    @Operation(summary = "分配权限与数据范围矩阵")
    @SaCheckPermission("system:role:assignPerm")
    @AuditLog(module = "角色管理", operation = "UPDATE", description = "配置角色菜单权限及数据范围")
    @PostMapping("/{id}/assign-permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody AssignPermissionReq req) {
        roleService.assignPermissions(id, req.getMenuIds(), req.getDataScopeMap());
        return Result.ok();
    }

    @Operation(summary = "复制角色权限")
    @SaCheckPermission("system:role:copyPerm")
    @AuditLog(module = "角色管理", operation = "UPDATE", description = "从现有角色快速复制权限")
    @PostMapping("/{id}/copy-permissions")
    public Result<Void> copyPermissions(@PathVariable Long id, @RequestParam Long sourceRoleId) {
        roleService.copyPermissions(sourceRoleId, id);
        return Result.ok();
    }
}
