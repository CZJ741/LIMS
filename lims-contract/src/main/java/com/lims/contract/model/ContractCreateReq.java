package com.lims.contract.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "创建合同请求参数")
public class ContractCreateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "合同名称", example = "2026年XX经开区地表水常规监测合同")
    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    @Schema(description = "委托方客户公司名称", example = "江苏XX环保科技发展有限公司")
    @NotBlank(message = "委托方公司名称不能为空")
    private String clientCompany;

    @Schema(description = "客户联系人", example = "李经理")
    @NotBlank(message = "客户联系人不能为空")
    private String clientContact;

    @Schema(description = "联系电话", example = "13800000000")
    @NotBlank(message = "联系电话不能为空")
    private String clientPhone;

    @Schema(description = "合同总金额", example = "120000.00")
    @NotNull(message = "合同金额不能为空")
    @DecimalMin(value = "0.01", message = "合同金额必须大于0")
    private BigDecimal totalAmount;

    @Schema(description = "生效开始日期", example = "2026-09-01")
    @NotNull(message = "生效日期不能为空")
    private LocalDate startDate;

    @Schema(description = "截止日期", example = "2027-08-31")
    @NotNull(message = "截止日期不能为空")
    private LocalDate endDate;

    @Schema(description = "合同附件文件ID", example = "1001")
    private Long attachmentId;

    @Schema(description = "备注条款")
    private String remark;
}
