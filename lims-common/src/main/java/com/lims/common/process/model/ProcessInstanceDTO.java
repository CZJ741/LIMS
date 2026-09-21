package com.lims.common.process.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程发起记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "我的发起流程实例响应模型")
public class ProcessInstanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "流程实例ID", example = "2500")
    private String processInstanceId;

    @Schema(description = "业务唯一单号", example = "HT-2026-001")
    private String businessKey;

    @Schema(description = "业务阶段", example = "CONTRACT")
    private String stage;

    @Schema(description = "流程定义Key", example = "contract-audit")
    private String processDefinitionKey;

    @Schema(description = "流程定义名称", example = "阶段一：合同登记与审核流程")
    private String processDefinitionName;

    @Schema(description = "发起人账号", example = "admin")
    private String startUserId;

    @Schema(description = "启动时间")
    private Date startTime;

    @Schema(description = "结束时间")
    private Date endTime;

    @Schema(description = "流程是否已结束", example = "false")
    private Boolean ended;
}
