package com.lims.finance.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.finance.service.SettlementService;
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
 * 财务结算流程审批任务控制器
 */
@Tag(name = "阶段六：财务结算流程审批管理", description = "结算单核销到账、金额异议驳回与回退")
@RestController
@RequestMapping("/api/finance/task")
@RequiredArgsConstructor
public class SettlementTaskController {

    private final SettlementService settlementService;

    @Operation(summary = "完成结算任务（通过）")
    @SaCheckPermission("finance:audit")
    @AuditLog(module = "财务管理", operation = "COMPLETE_TASK", description = "结算单核销到账完成", bizKey = "#taskId")
    @PostMapping("/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "款项到账核销完成";
        settlementService.confirm(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "驳回结算任务（至上一节点，强制>=10字）")
    @SaCheckPermission("finance:reject")
    @AuditLog(module = "财务管理", operation = "REJECT_TASK", description = "结算单驳回修改", bizKey = "#taskId")
    @PostMapping("/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        settlementService.reject(taskId, req.getReason());
        return Result.ok();
    }

    @Operation(summary = "回退结算任务（至指定上游节点，强制>=10字）")
    @SaCheckPermission("finance:rollback")
    @AuditLog(module = "财务管理", operation = "ROLLBACK_TASK", description = "结算单节点回退", bizKey = "#taskId")
    @PostMapping("/{taskId}/rollback")
    public Result<Void> rollbackTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        settlementService.rollback(taskId, req.getTargetActivityId(), req.getReason());
        return Result.ok();
    }
}
