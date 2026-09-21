package com.lims.contract.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.contract.service.ContractService;
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
 * 合同流程任务审批控制器
 */
@Tag(name = "阶段一：合同流程审批管理", description = "合同审批通过、驳回上一节点、任意上游回退")
@RestController
@RequestMapping("/api/contract/task")
@RequiredArgsConstructor
public class ContractTaskController {

    private final ContractService contractService;

    @Operation(summary = "完成审批任务（通过）")
    @SaCheckPermission("contract:audit")
    @AuditLog(module = "合同管理", operation = "COMPLETE_TASK", description = "合同审核通过", bizKey = "#taskId")
    @PostMapping("/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "审核通过";
        contractService.approve(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "驳回任务（至上一节点，强制>=10字）")
    @SaCheckPermission("contract:reject")
    @AuditLog(module = "合同管理", operation = "REJECT_TASK", description = "合同审核驳回", bizKey = "#taskId")
    @PostMapping("/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        contractService.reject(taskId, req.getReason());
        return Result.ok();
    }

    @Operation(summary = "回退任务（至指定上游节点，强制>=10字）")
    @SaCheckPermission("contract:rollback")
    @AuditLog(module = "合同管理", operation = "ROLLBACK_TASK", description = "合同节点回退", bizKey = "#taskId")
    @PostMapping("/{taskId}/rollback")
    public Result<Void> rollbackTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        contractService.rollback(taskId, req.getTargetActivityId(), req.getReason());
        return Result.ok();
    }
}
