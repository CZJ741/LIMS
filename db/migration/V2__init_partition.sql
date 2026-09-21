-- =============================================================================
-- LIMS 实验室信息管理系统 - V2__init_partition.sql
-- 数据库版本：MySQL 5.7.44
-- 字符集：utf8mb4 / utf8mb4_general_ci
-- 说明：
-- 包含 sys_audit_log（按月分区）、detection_result（按年分区）、report（按年分区）
-- 特殊设计：
-- 1. MySQL 5.7 规定：分区键（create_time）必须包含在主键及所有唯一键中。
-- 2. MySQL 5.7 规定：分区表不支持对外键（FOREIGN KEY）的引用与被引用约束，
--    因此分区表在物理层面不建立 FOREIGN KEY，业务级关联关系通过索引与应用层保证。
-- =============================================================================

SET NAMES utf8mb4;

-- =============================================================================
-- 1. sys_audit_log（系统审计日志表，按月分区）
-- 初始创建 12 个月分区 (2024-01 至 2024-12) + pmax 分区
-- 分区表达式：PARTITION BY RANGE (TO_DAYS(create_time))
-- =============================================================================
CREATE TABLE IF NOT EXISTS `sys_audit_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间(分区键)',
  `user_id` BIGINT NULL DEFAULT NULL COMMENT '操作人用户ID',
  `username` VARCHAR(64) NULL DEFAULT NULL COMMENT '操作人账号',
  `real_name` VARCHAR(64) NULL DEFAULT NULL COMMENT '操作人姓名',
  `module_name` VARCHAR(64) NOT NULL COMMENT '业务模块',
  `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型(INSERT/UPDATE/DELETE/AUDIT/EXPORT/SIGN)',
  `method_name` VARCHAR(256) NOT NULL COMMENT 'Java方法全限定名',
  `request_uri` VARCHAR(256) NOT NULL COMMENT '请求URL路径',
  `request_ip` VARCHAR(64) NOT NULL COMMENT '客户端IP地址',
  `param_before` MEDIUMTEXT NULL COMMENT '变更前数据JSON',
  `param_after` MEDIUMTEXT NULL COMMENT '变更后数据JSON',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '操作状态(1=成功 0=失败)',
  `error_msg` TEXT NULL COMMENT '失败异常堆栈信息',
  `execution_time` BIGINT NOT NULL DEFAULT 0 COMMENT '方法执行耗时(毫秒)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `create_time`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module_op` (`module_name`, `operation_type`),
  KEY `idx_request_ip` (`request_ip`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统关键操作留痕审计日志表(按月分区)'
PARTITION BY RANGE (TO_DAYS(create_time)) (
  PARTITION p202401 VALUES LESS THAN (TO_DAYS('2024-02-01')),
  PARTITION p202402 VALUES LESS THAN (TO_DAYS('2024-03-01')),
  PARTITION p202403 VALUES LESS THAN (TO_DAYS('2024-04-01')),
  PARTITION p202404 VALUES LESS THAN (TO_DAYS('2024-05-01')),
  PARTITION p202405 VALUES LESS THAN (TO_DAYS('2024-06-01')),
  PARTITION p202406 VALUES LESS THAN (TO_DAYS('2024-07-01')),
  PARTITION p202407 VALUES LESS THAN (TO_DAYS('2024-08-01')),
  PARTITION p202408 VALUES LESS THAN (TO_DAYS('2024-09-01')),
  PARTITION p202409 VALUES LESS THAN (TO_DAYS('2024-10-01')),
  PARTITION p202410 VALUES LESS THAN (TO_DAYS('2024-11-01')),
  PARTITION p202411 VALUES LESS THAN (TO_DAYS('2024-12-01')),
  PARTITION p202412 VALUES LESS THAN (TO_DAYS('2025-01-01')),
  PARTITION pmax    VALUES LESS THAN MAXVALUE
);

-- =============================================================================
-- 2. detection_result（检测结果表，按年分区）
-- 初始创建：当年(2024) + 未来5年(2025, 2026, 2027, 2028, 2029) + pmax
-- 分区表达式：PARTITION BY RANGE (YEAR(create_time))
-- =============================================================================
CREATE TABLE IF NOT EXISTS `detection_result` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(分区键)',
  `task_item_id` BIGINT NOT NULL COMMENT '检测任务明细ID',
  `sample_id` BIGINT NOT NULL COMMENT '样品台账ID',
  `item_id` BIGINT NOT NULL COMMENT '检测项目字典ID',
  `instrument_id` BIGINT NULL DEFAULT NULL COMMENT '所用检测仪器ID',
  `curve_id` BIGINT NULL DEFAULT NULL COMMENT '使用标准曲线ID',
  `raw_value` VARCHAR(128) NOT NULL COMMENT '仪器直连或录入原始读数',
  `replicate_1` VARCHAR(64) NULL DEFAULT NULL COMMENT '平行样1测定值',
  `replicate_2` VARCHAR(64) NULL DEFAULT NULL COMMENT '平行样2测定值',
  `relative_error` DECIMAL(5,2) NULL DEFAULT NULL COMMENT '平行双样相对偏差(%)',
  `result_value` VARCHAR(128) NOT NULL COMMENT '最终计算检测结果',
  `rounded_value` VARCHAR(128) NOT NULL COMMENT '按规则修约后报出值',
  `unit` VARCHAR(32) NOT NULL COMMENT '计量单位(mg/L, mg/kg等)',
  `sub_limit` VARCHAR(32) NULL DEFAULT NULL COMMENT '检出限(低于报L/ND)',
  `judgment_result` VARCHAR(32) NOT NULL DEFAULT 'QUALIFIED' COMMENT '合格判定(QUALIFIED=合格/UNQUALIFIED=超标/NO_EVAL=不评价)',
  `analyst_id` BIGINT NOT NULL COMMENT '实验检测人员ID',
  `analysis_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '实验完成时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态(SUBMITTED=已提交/REVIEWED=已复审/REJECTED=已驳回)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `create_time`),
  KEY `idx_task_item_id` (`task_item_id`),
  KEY `idx_sample_id` (`sample_id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_instrument_id` (`instrument_id`),
  KEY `idx_analyst_id` (`analyst_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_analysis_time` (`analysis_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='实验检测结果明细表(按年分区)'
PARTITION BY RANGE (YEAR(create_time)) (
  PARTITION p2024 VALUES LESS THAN (2025),
  PARTITION p2025 VALUES LESS THAN (2026),
  PARTITION p2026 VALUES LESS THAN (2027),
  PARTITION p2027 VALUES LESS THAN (2028),
  PARTITION p2028 VALUES LESS THAN (2029),
  PARTITION p2029 VALUES LESS THAN (2030),
  PARTITION pmax  VALUES LESS THAN MAXVALUE
);

-- =============================================================================
-- 3. report（检测报告主表，按年分区）
-- 初始创建：当年(2024) + 未来5年(2025, 2026, 2027, 2028, 2029) + pmax
-- 分区表达式：PARTITION BY RANGE (YEAR(create_time))
-- 注意：业务编号 report_code 增加唯一键时必须联合分区键 create_time
-- =============================================================================
CREATE TABLE IF NOT EXISTS `report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编制创建时间(分区键)',
  `report_code` VARCHAR(64) NOT NULL COMMENT '报告唯一编号(BG-YYYYMMDD-XXXX)',
  `entrust_id` BIGINT NOT NULL COMMENT '对应委托单ID',
  `contract_id` BIGINT NULL DEFAULT NULL COMMENT '关联合同ID',
  `template_id` BIGINT NOT NULL COMMENT '所用模板ID',
  `report_title` VARCHAR(256) NOT NULL COMMENT '检测报告完整标题',
  `client_company` VARCHAR(256) NOT NULL COMMENT '受检/委托单位名称',
  `writer_id` BIGINT NOT NULL COMMENT '报告编制人ID',
  `auditor_id` BIGINT NULL DEFAULT NULL COMMENT '审核人ID',
  `signer_id` BIGINT NULL DEFAULT NULL COMMENT '授权签字人ID',
  `sign_date` DATE NULL DEFAULT NULL COMMENT '签发批准日期',
  `status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '报告状态(DRAFT=草稿/AUDITING=审核中/SIGNING=待签发/FINANCE_HOLD=财务待核/RELEASED=已签发归档/REJECTED=已驳回)',
  `pdf_file_id` BIGINT NULL DEFAULT NULL COMMENT '生成的PDF报告文件ID',
  `qr_code_path` VARCHAR(256) NULL DEFAULT NULL COMMENT '防伪验真二维码图片路径',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`, `create_time`),
  UNIQUE KEY `uk_report_code_time` (`report_code`, `create_time`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_writer_id` (`writer_id`),
  KEY `idx_auditor_id` (`auditor_id`),
  KEY `idx_signer_id` (`signer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检验检测报告主表(按年分区)'
PARTITION BY RANGE (YEAR(create_time)) (
  PARTITION p2024 VALUES LESS THAN (2025),
  PARTITION p2025 VALUES LESS THAN (2026),
  PARTITION p2026 VALUES LESS THAN (2027),
  PARTITION p2027 VALUES LESS THAN (2028),
  PARTITION p2028 VALUES LESS THAN (2029),
  PARTITION p2029 VALUES LESS THAN (2030),
  PARTITION pmax  VALUES LESS THAN MAXVALUE
);
