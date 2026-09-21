package com.lims.entrust.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "委托单展示视图对象")
public class EntrustVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "委托单号", example = "WT-20260921-0001")
    private String entrustCode;

    @Schema(description = "关联合同ID")
    private Long contractId;

    @Schema(description = "关联合同编号")
    private String contractCode;

    @Schema(description = "委托单位")
    private String clientCompany;

    @Schema(description = "联系人")
    private String clientContact;

    @Schema(description = "联系电话")
    private String clientPhone;

    @Schema(description = "委托日期")
    private LocalDate entrustDate;

    @Schema(description = "样品来源")
    private String sampleSource;

    @Schema(description = "样品来源中文")
    private String sampleSourceName;

    @Schema(description = "紧急程度")
    private String urgencyLevel;

    @Schema(description = "委托状态")
    private String status;

    @Schema(description = "状态中文说明")
    private String statusName;

    @Schema(description = "开单专员姓名")
    private String clerkName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
