package com.lims.contract.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "修改合同请求参数")
public class ContractUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "合同名称")
    private String contractName;

    @Schema(description = "委托方公司")
    private String clientCompany;

    @Schema(description = "联系人")
    private String clientContact;

    @Schema(description = "联系电话")
    private String clientPhone;

    @Schema(description = "合同金额")
    @DecimalMin(value = "0.01", message = "合同金额必须大于0")
    private BigDecimal totalAmount;

    @Schema(description = "生效开始日期")
    private LocalDate startDate;

    @Schema(description = "截止日期")
    private LocalDate endDate;

    @Schema(description = "附件ID")
    private Long attachmentId;

    @Schema(description = "备注")
    private String remark;
}
