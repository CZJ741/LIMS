package com.lims.common.process.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 待办审批任务数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "待办审批任务数据传输对象")
public class TaskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Flowable任务ID", example = "2501")
    private String taskId;

    @Schema(description = "任务节点名称", example = "财务审核")
    private String taskName;

    @Schema(description = "任务定义Key", example = "task_finance_audit")
    private String taskDefinitionKey;

    @Schema(description = "流程实例ID", example = "2500")
    private String processInstanceId;

    @Schema(description = "流程定义Key", example = "contract-audit")
    private String processDefinitionKey;

    @Schema(description = "流程分类/业务阶段", example = "CONTRACT")
    private String category;

    @Schema(description = "业务唯一单号", example = "HT-2026-001")
    private String businessKey;

    @Schema(description = "认领人", example = "admin")
    private String assignee;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "流程变量集合")
    private Map<String, Object> processVariables;
}
