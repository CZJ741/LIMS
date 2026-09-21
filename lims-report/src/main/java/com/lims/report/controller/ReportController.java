package com.lims.report.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lims.common.model.IdResp;
import com.lims.common.process.model.ProcessActionReq;
import com.lims.common.result.Result;
import com.lims.report.service.ReportService;
import com.lims.report.service.TemplateEngineService;
import com.lims.system.audit.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 检测报告管理与模板引擎发布控制器
 */
@Tag(name = "报告管理", description = "报告发起、模板渲染、HTML转PDF、三级签发、防伪发布与批量ZIP导出")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final TemplateEngineService templateEngineService;

    @Operation(summary = "发起报告编制流程")
    @SaCheckPermission("report:initiate")
    @AuditLog(module = "报告管理", operation = "INITIATE", description = "发起报告编制流程", bizKey = "#reportNo")
    @PostMapping("/initiate")
    public Result<String> initiateReport(@RequestParam String reportNo) {
        String procInstId = reportService.initReport(reportNo, null);
        return Result.ok("报告编制流程已成功发起，实例ID: " + procInstId);
    }

    @Operation(summary = "选择报告模板")
    @SaCheckPermission("report:edit")
    @AuditLog(module = "报告管理", operation = "SELECT_TEMPLATE", description = "选择报告模板", bizKey = "#taskId")
    @PostMapping("/select-template")
    public Result<Void> selectTemplate(@RequestParam String taskId, @RequestParam Long templateId) {
        reportService.selectTemplate(taskId, templateId);
        return Result.ok();
    }

    @Operation(summary = "生成报告（PDF转换与渲染）")
    @SaCheckPermission("report:generate")
    @AuditLog(module = "报告管理", operation = "GENERATE", description = "生成合规检测报告", bizKey = "#reportNo")
    @PostMapping("/generate")
    public Result<IdResp> generateReport(@RequestParam String reportNo) {
        // 模拟提取检测项目数据并利用 FreeMarker 模板渲染
        Map<String, Object> data = new HashMap<>();
        data.put("reportCode", reportNo);
        data.put("contractCode", "HT-20260921-0001");
        data.put("clientCompany", "环境监测委托机构");

        String demoTemplate = "<html><head><style>body{font-family: sans-serif;}</style></head><body>"
                + "<h1>检测报告 [${reportCode}]</h1>"
                + "<p>委托单位: ${clientCompany}</p>"
                + "<p>关联合同: ${contractCode}</p>"
                + "<hr/><p>依据 GB/T 27025-2019 标准检测结论: 合格。</p>"
                + "</body></html>";

        byte[] pdfBytes = templateEngineService.generateReportPdf(demoTemplate, data);
        return Result.ok(IdResp.of(1001L, "PDF生成成功，大小: " + pdfBytes.length + " 字节"));
    }

    @Operation(summary = "在线预览报告（HTML）")
    @SaCheckPermission("report:query")
    @GetMapping("/{id}/preview")
    public Result<String> previewReport(@PathVariable Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("reportCode", "BG-20260921-" + id);
        data.put("clientCompany", "江苏环保监测中心");
        String html = templateEngineService.renderHtml("<h2>LIMS检测报告在线预览 [${reportCode}]</h2><p>委托方：${clientCompany}</p>", data);
        return Result.ok(html);
    }

    @Operation(summary = "分配报告编制员")
    @SaCheckPermission("report:assign")
    @AuditLog(module = "报告管理", operation = "ASSIGN", description = "指派报告编制员", bizKey = "#taskId")
    @PostMapping("/{id}/assign")
    public Result<Void> assignWriter(
            @PathVariable Long id,
            @RequestParam String taskId,
            @RequestParam String writerId) {
        reportService.assign(taskId, writerId);
        return Result.ok();
    }

    @Operation(summary = "报告技术审核通过")
    @SaCheckPermission("report:audit")
    @AuditLog(module = "报告管理", operation = "AUDIT", description = "报告技术审核通过", bizKey = "#taskId")
    @PostMapping("/audit/{taskId}/approve")
    public Result<Void> auditApprove(
            @PathVariable String taskId,
            @RequestBody(required = false) ProcessActionReq req) {
        String comment = req != null ? req.getReason() : "技术审核合格";
        reportService.audit(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "报告财务审核通过")
    @SaCheckPermission("report:audit")
    @AuditLog(module = "报告管理", operation = "FINANCE_AUDIT", description = "报告财务签审通过", bizKey = "#taskId")
    @PostMapping("/finance-audit")
    public Result<Void> financeAudit(
            @RequestParam String taskId,
            @RequestParam(required = false, defaultValue = "账款核对无误准予出具") String comment) {
        reportService.financeAudit(taskId, comment);
        return Result.ok();
    }

    @Operation(summary = "正式发布与发放（电子签章与归档）")
    @SaCheckPermission("report:publish")
    @AuditLog(module = "报告管理", operation = "PUBLISH", description = "正式发布报告并加盖印章", bizKey = "#taskId")
    @PostMapping("/{id}/publish")
    public Result<Void> publishReport(@PathVariable Long id, @RequestParam String taskId) {
        reportService.publish(taskId, null);
        return Result.ok();
    }

    @Operation(summary = "下载报告 PDF")
    @SaCheckPermission("report:query")
    @GetMapping("/{id}/download")
    public void downloadReport(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("reportCode", "BG-20260921-000" + id);
        data.put("clientCompany", "国家地表水环境重点实验室");
        byte[] pdf = templateEngineService.generateReportPdf("<html><body><h1>正式检测报告 [${reportCode}]</h1><p>${clientCompany}</p></body></html>", data);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + id + ".pdf");
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    @Operation(summary = "批量导出报告（ZIP压缩包）")
    @SaCheckPermission("report:export")
    @AuditLog(module = "报告管理", operation = "EXPORT", description = "批量ZIP打包导出报告")
    @PostMapping("/batch-export")
    public void batchExport(@RequestBody List<Long> reportIds, HttpServletResponse response) throws IOException {
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reports-bundle.zip");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (Long id : reportIds) {
                Map<String, Object> data = Collections.singletonMap("reportCode", "BG-20260921-" + id);
                byte[] pdf = templateEngineService.generateReportPdf("<html><body><h1>批量检测报告: ${reportCode}</h1></body></html>", data);
                ZipEntry entry = new ZipEntry("report_" + id + ".pdf");
                zos.putNextEntry(entry);
                zos.write(pdf);
                zos.closeEntry();
            }
            zos.finish();
        }
    }
}
