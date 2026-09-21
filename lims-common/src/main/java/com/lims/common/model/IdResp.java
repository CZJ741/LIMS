package com.lims.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 主键统一响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用主键响应对象")
public class IdResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID", example = "10001")
    private Long id;

    @Schema(description = "业务单号或字符串编码", example = "HT-20260920-0001")
    private String code;

    public static IdResp of(Long id) {
        return new IdResp(id, String.valueOf(id));
    }

    public static IdResp of(Long id, String code) {
        return new IdResp(id, code);
    }
}
