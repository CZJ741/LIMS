package com.lims.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.result.Result;
import com.lims.system.audit.AuditLog;
import com.lims.system.entity.SysMenu;
import com.lims.system.mapper.SysMenuMapper;
import com.lims.system.service.IPermissionService;
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
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "菜单管理", description = "系统菜单与路由树维护")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final SysMenuMapper menuMapper;
    private final IPermissionService permissionService;

    @Operation(summary = "获取全量菜单树")
    @SaCheckPermission("system:menu:view")
    @GetMapping("/tree")
    public Result<List<SysMenu>> getMenuTree() {
        return Result.ok(permissionService.getAllMenuTree());
    }

    @Operation(summary = "新增菜单")
    @SaCheckPermission("system:menu:add")
    @AuditLog(module = "菜单管理", operation = "INSERT", description = "创建新菜单")
    @PostMapping
    public Result<Long> createMenu(@RequestBody SysMenu menu) {
        menu.setCreateTime(LocalDateTime.now());
        menuMapper.insert(menu);
        return Result.ok(menu.getId());
    }

    @Operation(summary = "修改菜单")
    @SaCheckPermission("system:menu:edit")
    @AuditLog(module = "菜单管理", operation = "UPDATE", description = "更新菜单信息")
    @PutMapping
    public Result<Void> updateMenu(@RequestBody SysMenu menu) {
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(menu);
        return Result.ok();
    }

    @Operation(summary = "删除菜单")
    @SaCheckPermission("system:menu:delete")
    @AuditLog(module = "菜单管理", operation = "DELETE", description = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuMapper.deleteById(id);
        return Result.ok();
    }
}
