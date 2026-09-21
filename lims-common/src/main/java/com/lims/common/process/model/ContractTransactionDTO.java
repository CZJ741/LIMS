package com.lims.common.process.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 合同维度全生命周期事务聚合视图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "跨阶段合同事务聚合模型")
public class ContractTransactionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "合同唯一编号", example = "HT-2026-001")
    private String contractNo;

    @Schema(description = "当前所处最高业务阶段", example = "SAMPLING")
    private String currentStage;

    @Schema(description = "合同整体综合状态", example = "进行中")
    private String overallStatus;

    @Schema(description = "当前阶段待办任务列表")
    private List<TaskDTO> pendingTasks;

    @Schema(description = "已启动/已完成的流程阶段列表")
    private List<StageSummary> stageSummaries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单阶段概要")
    public static class StageSummary implements Serializable {
        private static final long serialVersionUID = 1L;

        private String stage;
        private String stageName;
        private String processInstanceId;
        private String status;
    }
}
