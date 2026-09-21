package com.lims.contract.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.result.Result;
import com.lims.system.audit.AuditLog;
import com.lims.system.datascope.DataScope;
import com.lims.system.datascope.DataScopeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例控制器：演示 @SaCheckPermission 与 @DataScope 注解协同
 */
@Tag(name = "合同业务示范", description = "演示权限校验、数据范围控制与操作审计留痕")
@RestController
@RequestMapping("/api/contract/demo")
public class ContractDemoController {

    @Operation(summary = "审核合同审批")
    @SaCheckPermission("contract:audit")
    @DataScope(scopeType = DataScopeType.DEPARTMENT, tableAlias = "c")
    @AuditLog(module = "合同管理", operation = "AUDIT", description = "审核合同审批记录", bizKey = "#id")
    @PostMapping("/{id}/audit")
    public Result<String> auditContract(
            @PathVariable Long id,
            @RequestParam String auditResult,
            @RequestParam String auditComment) {
        return Result.ok("合同 [ID=" + id + "] 审核完成，结论: " + auditResult);
    }
}
