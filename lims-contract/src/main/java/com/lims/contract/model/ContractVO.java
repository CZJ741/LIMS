package com.lims.contract.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "合同详情/列表视图对象")
public class ContractVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID", example = "101")
    private Long id;

    @Schema(description = "合同编号", example = "HT-20260921-0001")
    private String contractCode;

    @Schema(description = "合同名称", example = "2026年XX经开区地表水常规监测合同")
    private String contractName;

    @Schema(description = "委托方客户公司", example = "江苏XX环保科技发展有限公司")
    private String clientCompany;

    @Schema(description = "客户联系人", example = "李经理")
    private String clientContact;

    @Schema(description = "客户联系电话", example = "13800000000")
    private String clientPhone;

    @Schema(description = "合同总金额", example = "120000.00")
    private BigDecimal totalAmount;

    @Schema(description = "生效开始日期")
    private LocalDate startDate;

    @Schema(description = "截止日期")
    private LocalDate endDate;

    @Schema(description = "业务状态码", example = "DRAFT")
    private String status;

    @Schema(description = "业务状态中文名称", example = "待审核")
    private String statusName;

    @Schema(description = "签订日期")
    private LocalDate signDate;

    @Schema(description = "合同附件ID")
    private Long attachmentId;

    @Schema(description = "销售人员ID")
    private Long salesUserId;

    @Schema(description = "销售人员姓名", example = "张业务")
    private String salesUserName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
