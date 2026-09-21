package com.lims.detection.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.detection.service.DetectionService;
import com.lims.system.audit.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 检测流程审批任务控制器
 */
@Tag(name = "阶段四：检测流程审批管理", description = "检测任务流转、数据复审、驳回重测与回退")
@RestController
@RequestMapping("/api/detection/task")
@RequiredArgsConstructor
public class DetectionTaskController {

    private final DetectionService detectionService;

    @Operation(summary = "完成检测任务（通过）")
    @SaCheckPermission("detection:audit")
    @AuditLog(module = "检测管理", operation = "COMPLETE_TASK", description = "检测数据复审通过", bizKey = "#taskId")
    @PostMapping("/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "数据复审合格";
        detectionService.recheckResult(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "驳回检测任务（至上一节点，强制>=10字）")
    @SaCheckPermission("detection:reject")
    @AuditLog(module = "检测管理", operation = "REJECT_TASK", description = "检测数据复审驳回重测", bizKey = "#taskId")
    @PostMapping("/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        detectionService.rejectResult(taskId, req.getReason());
        return Result.ok();
    }

    @Operation(summary = "回退检测任务（至指定上游节点，强制>=10字）")
    @SaCheckPermission("detection:rollback")
    @AuditLog(module = "检测管理", operation = "ROLLBACK_TASK", description = "检测任务节点回退", bizKey = "#taskId")
    @PostMapping("/{taskId}/rollback")
    public Result<Void> rollbackTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        detectionService.rollback(taskId, req.getTargetActivityId(), req.getReason());
        return Result.ok();
    }
}
