package com.lims.detection.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "创建/分发检测任务请求体")
public class DetectionTaskCreateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "委托单ID", example = "101")
    @NotNull(message = "委托单ID不能为空")
    private Long entrustId;

    @Schema(description = "要求完成截止时间", example = "2026-09-25T18:00:00")
    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;
}
