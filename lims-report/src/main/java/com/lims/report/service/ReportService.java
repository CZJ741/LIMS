package com.lims.report.service;

import com.lims.common.enums.ReportStatus;

import java.util.Map;

/**
 * 阶段五：报告编制与审核业务状态机驱动服务
 */
public interface ReportService {

    /**
     * 发起报告编制流程
     */
    String initReport(String reportNo, Map<String, Object> variables);

    /**
     * 选择模板生成
     */
    void selectTemplate(String taskId, Long templateId);

    /**
     * 分配报告编制员
     */
    void assign(String taskId, String writerId);

    /**
     * 编制员提交审核
     */
    void edit(String taskId, Map<String, Object> variables);

    /**
     * 技术审核员审核通过
     */
    void audit(String taskId, String comment);

    /**
     * 财务审核通过
     */
    void financeAudit(String taskId, String comment);

    /**
     * 正式盖章发布报告
     */
    void publish(String taskId, Map<String, Object> variables);

    /**
     * 审核驳回
     */
    void reject(String taskId, String reason);

    /**
     * 回退
     */
    void rollback(String taskId, String targetActivityId, String reason);

    ReportStatus getStatus(String reportNo);
}
