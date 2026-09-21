package com.lims.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求基类
 */
@Data
@Schema(description = "通用分页查询请求参数")
public class PageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码，从1开始", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private int current = 1;

    @Schema(description = "每页显示条数", example = "10")
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 500, message = "每页大小不能大于500")
    private int size = 10;

    @Schema(description = "排序字段名称", example = "create_time")
    private String sortField;

    @Schema(description = "是否升序：true升序，false降序", example = "false")
    private Boolean asc = false;

    @Schema(description = "通用检索关键字（模糊查询）", example = "水质")
    private String keyword;

    @Schema(description = "状态过滤", example = "10")
    private Integer status;

    @Schema(description = "开始时间（ISO8601或标准日期时间串）", example = "2026-09-01T00:00:00")
    private String startTime;

    @Schema(description = "结束时间（ISO8601或标准日期时间串）", example = "2026-09-30T23:59:59")
    private String endTime;

    @Schema(description = "排序字段别名", example = "createTime")
    private String orderBy;

    @Schema(description = "排序方向：asc / desc", example = "desc")
    private String orderDir;
}
