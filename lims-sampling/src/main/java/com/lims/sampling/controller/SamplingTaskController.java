package com.lims.sampling.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.sampling.service.SamplingService;
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
 * 采样流程审批任务控制器
 */
@Tag(name = "阶段三：采样流程审批管理", description = "采样准备完成、现场采样流转、采样审核、驳回与回退")
@RestController
@RequestMapping("/api/sampling/task")
@RequiredArgsConstructor
public class SamplingTaskController {

    private final SamplingService samplingService;

    @Operation(summary = "完成采样任务（通过）")
    @SaCheckPermission("sampling:audit")
    @AuditLog(module = "采样管理", operation = "COMPLETE_TASK", description = "采样节点审核通过", bizKey = "#taskId")
    @PostMapping("/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "采样审核合格";
        samplingService.auditSample(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "驳回采样任务（至上一节点，强制>=10字）")
    @SaCheckPermission("sampling:reject")
    @AuditLog(module = "采样管理", operation = "REJECT_TASK", description = "采样节点驳回重采", bizKey = "#taskId")
    @PostMapping("/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        samplingService.rejectSample(taskId, req.getReason());
        return Result.ok();
    }

    @Operation(summary = "回退采样任务（至指定上游节点，强制>=10字）")
    @SaCheckPermission("sampling:rollback")
    @AuditLog(module = "采样管理", operation = "ROLLBACK_TASK", description = "采样节点回退", bizKey = "#taskId")
    @PostMapping("/{taskId}/rollback")
    public Result<Void> rollbackTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        samplingService.rollback(taskId, req.getTargetActivityId(), req.getReason());
        return Result.ok();
    }
}
