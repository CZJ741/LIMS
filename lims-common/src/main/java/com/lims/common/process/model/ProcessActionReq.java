package com.lims.common.process.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 流程操作基础入参请求
 */
@Data
@Schema(description = "流程驳回/回退操作通用请求体")
public class ProcessActionReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "驳回或回退原因(必填，长度不少于10个字)", example = "经复核，合同第3条付款条款存在歧义，请市场员重新核实修订。")
    @NotBlank(message = "原因不能为空")
    @Size(min = 10, message = "驳回或回退原因不得少于10个字")
    private String reason;

    @Schema(description = "回退目标节点ActivityId (仅回退操作需提供)", example = "task_finance_audit")
    private String targetActivityId;

    @Schema(description = "流程附加变量")
    private Map<String, Object> variables;
}
