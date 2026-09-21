package com.lims.entrust.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.entrust.model.EntrustCreateReq;
import com.lims.entrust.model.EntrustVO;
import com.lims.entrust.service.EntrustService;
import com.lims.system.audit.AuditLog;
import com.lims.system.datascope.DataScope;
import com.lims.system.datascope.DataScopeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 委托单管理控制器
 */
@Tag(name = "委托单管理", description = "委托下单、取消、分页查询及委托单明细")
@RestController
@RequestMapping("/api/entrust")
@RequiredArgsConstructor
public class EntrustController {

    private final EntrustService entrustService;

    @Operation(summary = "新增/登记委托单")
    @SaCheckPermission("entrust:create")
    @AuditLog(module = "委托管理", operation = "INSERT", description = "开具委托单", bizKey = "#req.clientCompany")
    @PostMapping
    public Result<IdResp> createEntrust(@Valid @RequestBody EntrustCreateReq req) {
        return Result.ok(entrustService.createEntrust(req));
    }

    @Operation(summary = "分页查询委托单列表")
    @SaCheckPermission("entrust:query")
    @DataScope(scopeType = DataScopeType.DEPARTMENT, tableAlias = "entrust_order")
    @GetMapping("/page")
    public Result<PageResp<EntrustVO>> pageEntrusts(@Valid PageReq req) {
        return Result.ok(entrustService.pageEntrusts(req));
    }

    @Operation(summary = "查询委托单详情")
    @SaCheckPermission("entrust:query")
    @GetMapping("/{id}")
    public Result<EntrustVO> getEntrustById(@Parameter(description = "委托单ID") @PathVariable Long id) {
        return Result.ok(entrustService.getEntrustById(id));
    }

    @Operation(summary = "确认下单（启动并流转流程）")
    @SaCheckPermission("entrust:order")
    @AuditLog(module = "委托管理", operation = "ORDER", description = "确认委托下单", bizKey = "#taskId")
    @PostMapping("/task/{taskId}/complete")
    public Result<Void> completeTask(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        entrustService.complete(taskId, req != null ? req.getVariables() : null);
        return Result.ok();
    }

    @Operation(summary = "取消委托单（需填写原因）")
    @SaCheckPermission("entrust:cancel")
    @AuditLog(module = "委托管理", operation = "CANCEL", description = "取消委托单", bizKey = "#taskId")
    @PostMapping("/task/{taskId}/cancel")
    public Result<Void> cancelTask(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        entrustService.cancel(taskId, req.getReason());
        return Result.ok();
    }
}
