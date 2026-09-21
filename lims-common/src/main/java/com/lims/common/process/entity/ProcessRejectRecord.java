package com.lims.common.process.entity;

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
import java.time.LocalDateTime;

/**
 * 流程驳回与回退轨迹审计表实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("process_reject_record")
public class ProcessRejectRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("business_key")
    private String businessKey;

    @TableField("process_key")
    private String processKey;

    @TableField("process_instance_id")
    private String processInstanceId;

    @TableField("current_task_id")
    private String currentTaskId;

    @TableField("current_task_name")
    private String currentTaskName;

    @TableField("target_activity_id")
    private String targetActivityId;

    @TableField("target_activity_name")
    private String targetActivityName;

    @TableField("reject_type")
    private String rejectType; // REJECT / ROLLBACK

    @TableField("reason")
    private String reason;

    @TableField("rejected_by")
    private String rejectedBy;

    @TableField("rejected_by_name")
    private String rejectedByName;

    @TableField("rejected_by_position")
    private String rejectedByPosition;

    @TableField("operator_ip")
    private String operatorIp;

    @TableField("previous_status")
    private String previousStatus;

    @TableField("current_status")
    private String currentStatus;

    @TableField("rejected_at")
    private LocalDateTime rejectedAt;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
