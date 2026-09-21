package com.lims.report.service.impl;

import com.lims.common.enums.ReportStatus;
import com.lims.common.process.service.ProcessService;
import com.lims.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ProcessService processService;
    private static final String PROCESS_KEY = "report-process";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String initReport(String reportNo, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("reportStatus", ReportStatus.PENDING_INIT.getCode());
        log.info("发起报告流程, reportNo={}", reportNo);
        return processService.startProcess(PROCESS_KEY, reportNo, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void selectTemplate(String taskId, Long templateId) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("templateId", templateId);
        vars.put("reportStatus", ReportStatus.PENDING_EDIT.getCode());
        processService.completeTask(taskId, vars);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(String taskId, String writerId) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("writerId", writerId);
        vars.put("reportStatus", ReportStatus.EDITING.getCode());
        processService.completeTask(taskId, vars);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(String taskId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("reportStatus", ReportStatus.PENDING_AUDIT.getCode());
        processService.completeTask(taskId, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(String taskId, String comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("techAuditPass", true);
        vars.put("comment", comment);
        vars.put("reportStatus", ReportStatus.PENDING_FINANCE.getCode());
        processService.completeTask(taskId, vars);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void financeAudit(String taskId, String comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("financeAuditPass", true);
        vars.put("comment", comment);
        vars.put("reportStatus", ReportStatus.PUBLISHED.getCode());
        processService.completeTask(taskId, vars);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(String taskId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("reportStatus", ReportStatus.PUBLISHED.getCode());
        processService.completeTask(taskId, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String taskId, String reason) {
        processService.rejectToPrevious(taskId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(String taskId, String targetActivityId, String reason) {
        processService.rollbackTo(taskId, targetActivityId, reason);
    }

    @Override
    public ReportStatus getStatus(String reportNo) {
        return ReportStatus.PENDING_INIT;
    }
}
