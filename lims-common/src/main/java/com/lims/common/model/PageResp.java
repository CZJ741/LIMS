package com.lims.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用分页响应体
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用分页响应数据")
public class PageResp<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码", example = "1")
    private long current;

    @Schema(description = "每页条数", example = "10")
    private long size;

    @Schema(description = "总记录数", example = "100")
    private long total;

    @Schema(description = "总页数", example = "10")
    private long pages;

    @Schema(description = "数据记录列表")
    private List<T> records;

    public static <T> PageResp<T> empty(long current, long size) {
        return new PageResp<>(current, size, 0L, 0L, Collections.emptyList());
    }

    public static <T> PageResp<T> of(long current, long size, long total, List<T> records) {
        long pages = (total + size - 1) / size;
        return new PageResp<>(current, size, total, pages, records);
    }
}
