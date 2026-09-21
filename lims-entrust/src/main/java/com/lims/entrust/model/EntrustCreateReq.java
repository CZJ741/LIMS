package com.lims.entrust.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "创建委托单请求体")
public class EntrustCreateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联合同ID(可选)")
    private Long contractId;

    @Schema(description = "委托客户单位", example = "江苏省某某水务集团")
    @NotBlank(message = "委托单位不能为空")
    private String clientCompany;

    @Schema(description = "联系人姓名", example = "王工")
    @NotBlank(message = "联系人不能为空")
    private String clientContact;

    @Schema(description = "联系电话", example = "13912345678")
    @NotBlank(message = "联系电话不能为空")
    private String clientPhone;

    @Schema(description = "委托日期", example = "2026-09-21")
    private LocalDate entrustDate;

    @Schema(description = "样品来源：DELIVERY(送样) / SAMPLING(现场采样)", example = "SAMPLING")
    @NotBlank(message = "样品来源不能为空")
    private String sampleSource;

    @Schema(description = "紧急程度：NORMAL(普通) / URGENT(加急) / EXTRA_URGENT(特急)", example = "NORMAL")
    private String urgencyLevel;

    @Schema(description = "特殊技术要求/备注")
    private String remark;
}
