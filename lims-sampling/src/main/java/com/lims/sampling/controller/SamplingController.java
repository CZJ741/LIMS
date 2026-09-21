package com.lims.sampling.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.sampling.model.SamplingTaskCreateReq;
import com.lims.sampling.model.SamplingTaskVO;
import com.lims.sampling.service.SamplingService;
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
 * 采样管理控制器
 */
@Tag(name = "采样管理", description = "采样任务创建、现场采集填报、样品记录审核与回退")
@RestController
@RequestMapping("/api/sampling")
@RequiredArgsConstructor
public class SamplingController {

    private final SamplingService samplingService;

    @Operation(summary = "创建采样任务")
    @SaCheckPermission("sampling:create")
    @AuditLog(module = "采样管理", operation = "INSERT", description = "创建采样任务", bizKey = "#req.samplingSite")
    @PostMapping
    public Result<IdResp> createSamplingTask(@Valid @RequestBody SamplingTaskCreateReq req) {
        return Result.ok(samplingService.createSamplingTask(req));
    }

    @Operation(summary = "分页查询采样任务列表")
    @SaCheckPermission("sampling:query")
    @DataScope(scopeType = DataScopeType.DEPARTMENT, tableAlias = "sampling_task")
    @GetMapping("/page")
    public Result<PageResp<SamplingTaskVO>> pageSamplingTasks(@Valid PageReq req) {
        return Result.ok(samplingService.pageSamplingTasks(req));
    }

    @Operation(summary = "查询采样任务详情")
    @SaCheckPermission("sampling:query")
    @GetMapping("/{id}")
    public Result<SamplingTaskVO> getSamplingTaskById(@Parameter(description = "采样任务ID") @PathVariable Long id) {
        return Result.ok(samplingService.getSamplingTaskById(id));
    }

    @Operation(summary = "启动采样准备流程")
    @SaCheckPermission("sampling:start")
    @AuditLog(module = "采样管理", operation = "START_PROCESS", description = "启动采样准备流程", bizKey = "#samplingNo")
    @PostMapping("/{samplingNo}/start")
    public Result<String> startPrepare(@PathVariable String samplingNo) {
        return Result.ok("采样流程已启动: " + samplingService.startPrepare(samplingNo, null));
    }

    @Operation(summary = "完成采样审核（通过）")
    @SaCheckPermission("sampling:audit")
    @AuditLog(module = "采样管理", operation = "AUDIT", description = "采样审核通过", bizKey = "#taskId")
    @PostMapping("/task/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "采样合格";
        samplingService.auditSample(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "驳回采样重采（强制≥10字）")
    @SaCheckPermission("sampling:reject")
    @AuditLog(module = "采样管理", operation = "REJECT", description = "采样驳回重采", bizKey = "#taskId")
    @PostMapping("/task/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        samplingService.rejectSample(taskId, req.getReason());
        return Result.ok();
    }
}
