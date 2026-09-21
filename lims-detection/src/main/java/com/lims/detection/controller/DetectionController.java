package com.lims.detection.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.detection.model.DetectionTaskCreateReq;
import com.lims.detection.model.DetectionTaskVO;
import com.lims.detection.service.DetectionService;
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
 * 检测管理控制器
 */
@Tag(name = "检测管理", description = "检测任务下发、分析员认领、数据复审与驳回")
@RestController
@RequestMapping("/api/detection")
@RequiredArgsConstructor
public class DetectionController {

    private final DetectionService detectionService;

    @Operation(summary = "创建并下发检测任务")
    @SaCheckPermission("detection:assign")
    @AuditLog(module = "检测管理", operation = "INSERT", description = "下发检测任务", bizKey = "#req.entrustId")
    @PostMapping
    public Result<IdResp> createDetectionTask(@Valid @RequestBody DetectionTaskCreateReq req) {
        return Result.ok(detectionService.createDetectionTask(req));
    }

    @Operation(summary = "分页查询检测任务列表")
    @SaCheckPermission("detection:query")
    @DataScope(scopeType = DataScopeType.DEPARTMENT, tableAlias = "detection_task")
    @GetMapping("/page")
    public Result<PageResp<DetectionTaskVO>> pageDetectionTasks(@Valid PageReq req) {
        return Result.ok(detectionService.pageDetectionTasks(req));
    }

    @Operation(summary = "查询检测任务详情")
    @SaCheckPermission("detection:query")
    @GetMapping("/{id}")
    public Result<DetectionTaskVO> getDetectionTaskById(@Parameter(description = "任务ID") @PathVariable Long id) {
        return Result.ok(detectionService.getDetectionTaskById(id));
    }

    @Operation(summary = "实验数据复审通过")
    @SaCheckPermission("detection:audit")
    @AuditLog(module = "检测管理", operation = "RECHECK", description = "检测数据复审通过", bizKey = "#taskId")
    @PostMapping("/task/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "数据复审合格";
        detectionService.recheckResult(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "实验数据复审驳回重测（强制≥10字）")
    @SaCheckPermission("detection:reject")
    @AuditLog(module = "检测管理", operation = "REJECT", description = "检测数据复审驳回", bizKey = "#taskId")
    @PostMapping("/task/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        detectionService.rejectResult(taskId, req.getReason());
        return Result.ok();
    }
}
