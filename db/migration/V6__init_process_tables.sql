-- ====================================================================
-- LIMS 流程关联与驳回轨迹数据库表 (MySQL 5.7+ / 8.0+)
-- ====================================================================

-- 1. 业务与流程实例关联表 (多对多：一个业务主单号可串联多个阶段流程实例)
CREATE TABLE IF NOT EXISTS `process_business_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_key` VARCHAR(64) NOT NULL COMMENT '业务编号 (合同编号/委托单号/采样批次号/检测任务号/报告号/结算单号)',
    `stage` VARCHAR(32) NOT NULL COMMENT '业务阶段编码 (CONTRACT, ENTRUST, SAMPLING, DETECTION, REPORT, SETTLEMENT)',
    `process_key` VARCHAR(64) NOT NULL COMMENT '流程定义Key (如 contract-audit, sampling-process)',
    `process_instance_id` VARCHAR(64) NOT NULL COMMENT 'Flowable流程实例ID (proc_inst_id_)',
    `status` VARCHAR(32) NOT NULL DEFAULT 'RUNNING' COMMENT '流程状态 (RUNNING运行中, COMPLETED已完成, TERMINATED已终止)',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '启动人账号/ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_pbr_biz_key` (`business_key`),
    INDEX `idx_pbr_proc_inst_id` (`process_instance_id`),
    INDEX `idx_pbr_stage` (`stage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='业务与流程实例关联表';

-- 2. 流程驳回/回退审计记录表
CREATE TABLE IF NOT EXISTS `process_reject_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_key` VARCHAR(64) NOT NULL COMMENT '业务唯一编号',
    `process_key` VARCHAR(64) NOT NULL COMMENT '流程定义Key',
    `process_instance_id` VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    `current_task_id` VARCHAR(64) NOT NULL COMMENT '被驳回/执行回退的任务ID',
    `current_task_name` VARCHAR(128) DEFAULT NULL COMMENT '当前任务节点名称',
    `target_activity_id` VARCHAR(64) NOT NULL COMMENT '目标节点ActivityId',
    `target_activity_name` VARCHAR(128) DEFAULT NULL COMMENT '目标节点名称',
    `reject_type` VARCHAR(32) NOT NULL DEFAULT 'REJECT' COMMENT '流转类型: REJECT(驳回上一节点), ROLLBACK(回退任意节点)',
    `reason` TEXT NOT NULL COMMENT '驳回/回退详细原因 (必填，>=10字)',
    `rejected_by` VARCHAR(64) NOT NULL COMMENT '操作人ID/账号',
    `rejected_by_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人姓名',
    `rejected_by_position` VARCHAR(64) DEFAULT NULL COMMENT '操作人职位编码',
    `operator_ip` VARCHAR(64) DEFAULT NULL COMMENT '操作终端IP',
    `previous_status` VARCHAR(32) DEFAULT NULL COMMENT '变更前状态',
    `current_status` VARCHAR(32) DEFAULT NULL COMMENT '变更后状态',
    `rejected_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '驳回时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_prr_biz_key` (`business_key`),
    INDEX `idx_prr_proc_inst` (`process_instance_id`),
    INDEX `idx_prr_rejected_at` (`rejected_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='流程驳回与回退轨迹审计表';
