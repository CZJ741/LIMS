package com.lims.common.process.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史流程活动审计节点模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "历史流程活动审计节点")
public class HistoricActivityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "活动ID", example = "task_finance_audit")
    private String activityId;

    @Schema(description = "活动名称", example = "财务审核")
    private String activityName;

    @Schema(description = "活动类型", example = "userTask")
    private String activityType;

    @Schema(description = "处理人", example = "admin")
    private String assignee;

    @Schema(description = "候选组/职位", example = "FINANCE_SPECIALIST")
    private String candidateGroup;

    @Schema(description = "开始时间")
    private Date startTime;

    @Schema(description = "结束时间")
    private Date endTime;

    @Schema(description = "耗时(毫秒)")
    private Long durationInMillis;

    @Schema(description = "审批或驳回意见")
    private String comment;
}
