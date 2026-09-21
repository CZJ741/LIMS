package com.lims.common.process.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 全链路业务追溯时间线模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "全流程留痕追溯响应模型")
public class TraceTimelineDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "业务主键号", example = "HT-2026-001")
    private String businessKey;

    @Schema(description = "全流程阶段轨迹列表")
    private List<StageTimeline> stages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单阶段时间线轨迹")
    public static class StageTimeline implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "阶段代码", example = "CONTRACT")
        private String stage;

        @Schema(description = "流程定义Key", example = "contract-audit")
        private String processKey;

        @Schema(description = "流程实例ID", example = "2500")
        private String processInstanceId;

        @Schema(description = "状态", example = "RUNNING / COMPLETED")
        private String status;

        @Schema(description = "该阶段节点流水")
        private List<NodeAuditLog> nodes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "节点审批与驳回流水记录")
    public static class NodeAuditLog implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "任务或活动名称", example = "财务审核")
        private String nodeName;

        @Schema(description = "操作人账号", example = "admin")
        private String operator;

        @Schema(description = "操作人姓名", example = "张财务")
        private String operatorName;

        @Schema(description = "操作职位", example = "FINANCE_SPECIALIST")
        private String operatorPosition;

        @Schema(description = "操作类型", example = "COMPLETE / REJECT / ROLLBACK")
        private String actionType;

        @Schema(description = "审批意见或驳回原因")
        private String commentOrReason;

        @Schema(description = "操作时间")
        private Date operateTime;

        @Schema(description = "操作终端IP")
        private String operatorIp;
    }
}
