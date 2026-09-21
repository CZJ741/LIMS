package com.lims.contract.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("contract")
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("contract_code")
    private String contractCode;

    @TableField("contract_name")
    private String contractName;

    @TableField("client_company")
    private String clientCompany;

    @TableField("client_contact")
    private String clientContact;

    @TableField("client_phone")
    private String clientPhone;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("sales_user_id")
    private Long salesUserId;

    @TableField("status")
    private String status;

    @TableField("sign_date")
    private LocalDate signDate;

    @TableField("attachment_id")
    private Long attachmentId;

    @TableField("remark")
    private String remark;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("create_by")
    private Long createBy;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_by")
    private Long updateBy;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
