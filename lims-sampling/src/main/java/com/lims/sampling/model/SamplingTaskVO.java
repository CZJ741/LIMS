package com.lims.sampling.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "采样任务视图对象")
public class SamplingTaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "采样任务编号", example = "CY-20260921-0001")
    private String taskCode;

    @Schema(description = "委托单ID")
    private Long entrustId;

    @Schema(description = "采样组长姓名")
    private String leaderName;

    @Schema(description = "采样地址")
    private String samplingSite;

    @Schema(description = "计划开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime planStartTime;

    @Schema(description = "计划完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime planEndTime;

    @Schema(description = "实际开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime actualStartTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态中文说明")
    private String statusName;

    @Schema(description = "备注")
    private String remark;
}
