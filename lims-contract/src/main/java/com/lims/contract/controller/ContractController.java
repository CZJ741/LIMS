package com.lims.contract.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.contract.model.ContractCreateReq;
import com.lims.contract.model.ContractUpdateReq;
import com.lims.contract.model.ContractVO;
import com.lims.contract.service.ContractService;
import com.lims.system.audit.AuditLog;
import com.lims.system.datascope.DataScope;
import com.lims.system.datascope.DataScopeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 合同管理控制器
 */
@Tag(name = "合同管理", description = "合同登记、修改、逻辑删除、分页查询与审批流程驱动")
@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @Operation(summary = "新增合同登记")
    @SaCheckPermission("contract:create")
    @AuditLog(module = "合同管理", operation = "INSERT", description = "新增合同草稿", bizKey = "#req.contractName")
    @PostMapping
    public Result<IdResp> createContract(@Valid @RequestBody ContractCreateReq req) {
        return Result.ok(contractService.createContract(req));
    }

    @Operation(summary = "修改合同")
    @SaCheckPermission("contract:update")
    @AuditLog(module = "合同管理", operation = "UPDATE", description = "修改合同信息", bizKey = "#id")
    @PutMapping("/{id}")
    public Result<Void> updateContract(
            @Parameter(description = "合同ID") @PathVariable Long id,
            @Valid @RequestBody ContractUpdateReq req) {
        contractService.updateContract(id, req);
        return Result.ok();
    }

    @Operation(summary = "逻辑删除合同")
    @SaCheckPermission("contract:delete")
    @AuditLog(module = "合同管理", operation = "DELETE", description = "逻辑删除合同记录", bizKey = "#id")
    @DeleteMapping("/{id}")
    public Result<Void> deleteContract(@Parameter(description = "合同ID") @PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.ok();
    }

    @Operation(summary = "查询合同详情")
    @SaCheckPermission("contract:query")
    @GetMapping("/{id}")
    public Result<ContractVO> getContractById(@Parameter(description = "合同ID") @PathVariable Long id) {
        return Result.ok(contractService.getContractById(id));
    }

    @Operation(summary = "分页查询合同列表")
    @SaCheckPermission("contract:query")
    @DataScope(scopeType = DataScopeType.DEPARTMENT, tableAlias = "contract")
    @GetMapping("/page")
    public Result<PageResp<ContractVO>> pageContracts(@Valid PageReq req) {
        return Result.ok(contractService.pageContracts(req));
    }

    @Operation(summary = "我的历史合同")
    @SaCheckPermission("contract:query")
    @GetMapping("/my-historical")
    public Result<PageResp<ContractVO>> getMyHistorical(@Valid PageReq req) {
        return Result.ok(contractService.getMyHistoricalContracts(req));
    }

    @Operation(summary = "提报合同审核（启动工作流）")
    @SaCheckPermission("contract:submit")
    @AuditLog(module = "合同管理", operation = "SUBMIT_AUDIT", description = "提报合同审核启动流程", bizKey = "#id")
    @PostMapping("/{id}/submit-audit")
    public Result<String> submitAudit(@Parameter(description = "合同ID") @PathVariable Long id) {
        return Result.ok("流程已成功启动，实例ID: " + contractService.submitAudit(id));
    }

    @Operation(summary = "合同审核通过")
    @SaCheckPermission("contract:audit")
    @AuditLog(module = "合同管理", operation = "APPROVE", description = "财务审核通过合同", bizKey = "#taskId")
    @PostMapping("/audit/{taskId}/approve")
    public Result<Void> approve(
            @Parameter(description = "Flowable任务ID") @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "财务审核通过";
        contractService.approve(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "合同审核驳回（强制≥10字原因）")
    @SaCheckPermission("contract:reject")
    @AuditLog(module = "合同管理", operation = "REJECT", description = "财务驳回合同重改", bizKey = "#taskId")
    @PostMapping("/audit/{taskId}/reject")
    public Result<Void> reject(
            @Parameter(description = "Flowable任务ID") @PathVariable String taskId,
            @Valid @RequestBody ProcessActionReq req) {
        contractService.reject(taskId, req.getReason());
        return Result.ok();
    }
}
