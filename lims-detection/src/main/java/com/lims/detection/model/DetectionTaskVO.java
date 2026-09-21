package com.lims.detection.model;

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
@Schema(description = "检测任务展示对象")
public class DetectionTaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "检测任务编号", example = "JC-20260921-0001")
    private String taskCode;

    @Schema(description = "委托单ID")
    private Long entrustId;

    @Schema(description = "主任/组长姓名")
    private String labHeadName;

    @Schema(description = "派发时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime assignTime;

    @Schema(description = "要求完成截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态中文说明")
    private String statusName;
}
