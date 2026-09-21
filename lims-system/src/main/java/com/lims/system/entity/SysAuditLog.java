package com.lims.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审计日志实体
 */
@Data
@TableName("sys_audit_log")
public class SysAuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("user_id")
    private Long userId;

    @TableField("username")
    private String username;

    @TableField("real_name")
    private String realName;

    @TableField("module_name")
    private String moduleName;

    @TableField("operation_type")
    private String operationType;

    @TableField("method_name")
    private String methodName;

    @TableField("request_uri")
    private String requestUri;

    @TableField("request_ip")
    private String requestIp;

    @TableField("param_before")
    private String paramBefore;

    @TableField("param_after")
    private String paramAfter;

    @TableField("status")
    private Integer status;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("execution_time")
    private Long executionTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("create_by")
    private Long createBy;

    @TableField("update_by")
    private Long updateBy;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
