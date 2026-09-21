package com.lims.sampling.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "创建采样任务请求体")
public class SamplingTaskCreateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联委托单ID", example = "101")
    @NotNull(message = "委托单ID不能为空")
    private Long entrustId;

    @Schema(description = "采样现场详细地址", example = "江苏省南京市江北新区1号排污口")
    @NotBlank(message = "采样地址不能为空")
    private String samplingSite;

    @Schema(description = "计划开始时间", example = "2026-09-22T09:00:00")
    @NotNull(message = "计划开始时间不能为空")
    private LocalDateTime planStartTime;

    @Schema(description = "计划完成时间", example = "2026-09-22T17:00:00")
    @NotNull(message = "计划完成时间不能为空")
    private LocalDateTime planEndTime;

    @Schema(description = "任务备注")
    private String remark;
}
