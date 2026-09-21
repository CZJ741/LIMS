package com.lims.finance.service;

import com.lims.common.enums.SettlementStatus;

import java.util.Map;

/**
 * 阶段六：财务结算业务状态机驱动服务
 */
public interface SettlementService {

    /**
     * 基于报告生成结算单并启动流程
     */
    String create(String settlementNo, Map<String, Object> variables);

    /**
     * 财务确认核销到账
     */
    void confirm(String taskId, String comment);

    /**
     * 财务驳回结算单
     */
    void reject(String taskId, String reason);

    /**
     * 回退
     */
    void rollback(String taskId, String targetActivityId, String reason);

    SettlementStatus getStatus(String settlementNo);
}
