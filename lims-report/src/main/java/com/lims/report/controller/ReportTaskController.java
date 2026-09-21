package com.lims.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.report.service.ReportService;
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
 * 报告流程审批任务控制器
 */
@Tag(name = "阶段五：报告流程审批管理", description = "报告编制流转、技术/财务审核、驳回与回退")
@RestController
@RequestMapping("/api/report/task")
@RequiredArgsConstructor
public class ReportTaskController {

    private final ReportService reportService;

    @Operation(summary = "完成报告任务（通过）")
    @SaCheckPermission("report:audit")
    @AuditLog(module = "报告管理", operation = "COMPLETE_TASK", description = "报告审核通过", bizKey = "#taskId")
    @PostMapping("/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "报告审核合格";
        reportService.audit(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "驳回报告任务（至上一节点，强制>=10字）")
    @SaCheckPermission("report:reject")
    @AuditLog(module = "报告管理", operation = "REJECT_TASK", description = "报告审核驳回", bizKey = "#taskId")
    @PostMapping("/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        reportService.reject(taskId, req.getReason());
        return Result.ok();
    }

    @Operation(summary = "回退报告任务（至指定上游节点，强制>=10字）")
    @SaCheckPermission("report:rollback")
    @AuditLog(module = "报告管理", operation = "ROLLBACK_TASK", description = "报告任务节点回退", bizKey = "#taskId")
    @PostMapping("/{taskId}/rollback")
    public Result<Void> rollbackTask(
            @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        reportService.rollback(taskId, req.getTargetActivityId(), req.getReason());
        return Result.ok();
    }
}
