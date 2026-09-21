package com.lims.entrust.entity;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 委托单实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("entrust_order")
public class EntrustOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("entrust_code")
    private String entrustCode;

    @TableField("contract_id")
    private Long contractId;

    @TableField("client_company")
    private String clientCompany;

    @TableField("client_contact")
    private String clientContact;

    @TableField("client_phone")
    private String clientPhone;

    @TableField("entrust_date")
    private LocalDate entrustDate;

    @TableField("sample_source")
    private String sampleSource;

    @TableField("urgency_level")
    private String urgencyLevel;

    @TableField("status")
    private String status;

    @TableField("clerk_id")
    private Long clerkId;

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
