package com.lims.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.result.Result;
import com.lims.system.audit.AuditLog;
import com.lims.system.entity.SysPosition;
import com.lims.system.service.IPositionService;
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

import java.util.List;

@Tag(name = "职位管理", description = "19种标准职位身份维护")
@RestController
@RequestMapping("/api/system/position")
@RequiredArgsConstructor
public class PositionController {

    private final IPositionService positionService;

    @Operation(summary = "查询所有职位列表")
    @SaCheckPermission("system:position:view")
    @GetMapping("/list")
    public Result<List<SysPosition>> listPositions() {
        return Result.ok(positionService.listPositions());
    }

    @Operation(summary = "新增职位")
    @SaCheckPermission("system:position:add")
    @AuditLog(module = "职位管理", operation = "INSERT", description = "新增职位身份")
    @PostMapping
    public Result<Long> createPosition(@RequestBody SysPosition position) {
        return Result.ok(positionService.createPosition(position));
    }

    @Operation(summary = "修改职位")
    @SaCheckPermission("system:position:edit")
    @AuditLog(module = "职位管理", operation = "UPDATE", description = "修改职位信息")
    @PutMapping("/{id}")
    public Result<Void> updatePosition(@PathVariable Long id, @RequestBody SysPosition position) {
        position.setId(id);
        positionService.updatePosition(position);
        return Result.ok();
    }

    @Operation(summary = "删除职位")
    @SaCheckPermission("system:position:delete")
    @AuditLog(module = "职位管理", operation = "DELETE", description = "删除职位")
    @DeleteMapping("/{id}")
    public Result<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return Result.ok();
    }
}
