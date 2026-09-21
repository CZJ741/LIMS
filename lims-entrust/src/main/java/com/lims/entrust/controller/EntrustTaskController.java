package com.lims.entrust.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.entrust.service.EntrustService;
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
 * 委托流程任务处理控制器
 */
@Tag(name = "阶段二：委托流程审批管理", description = "委托下单完成、取消、驳回与回退")
@RestController
@RequestMapping("/api/entrust/task")
@RequiredArgsConstructor
public class EntrustTaskController {

    private final EntrustService entrustService;

    @Operation(summary = "完成委托任务（通过）")
    @SaCheckPermission("entrust:order")
    @AuditLog(module = "委托管理", operation = "COMPLETE_TASK", description = "委托下单完成", bizKey = "#taskId")
    @PostMapping("/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        entrustService.complete(taskId, req != null ? req.getVariables() : null);
        return Result.ok();
    }

    @Operation(summary = "驳回委托任务（至上一节点，强制>=10字）")
    @SaCheckPermission("entrust:reject")
    @AuditLog(module = "委托管理", operation = "REJECT_TASK", description = "委托任务驳回", bizKey = "#taskId")
    @PostMapping("/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        entrustService.reject(taskId, req.getReason());
        return Result.ok();
    }

    @Operation(summary = "回退委托任务（至指定上游节点，强制>=10字）")
    @SaCheckPermission("entrust:rollback")
    @AuditLog(module = "委托管理", operation = "ROLLBACK_TASK", description = "委托任务回退", bizKey = "#taskId")
    @PostMapping("/{taskId}/rollback")
    public Result<Void> rollbackTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        entrustService.rollback(taskId, req.getTargetActivityId(), req.getReason());
        return Result.ok();
    }
}
