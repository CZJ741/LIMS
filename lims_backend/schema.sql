-- LIMS 实验室信息管理系统 MySQL 初始化脚本
CREATE DATABASE IF NOT EXISTS `lims_demo` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `lims_demo`;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `real_name` VARCHAR(50) DEFAULT NULL,
  `dept_id` INT DEFAULT NULL,
  `dept_name` VARCHAR(100) DEFAULT NULL,
  `status` INT DEFAULT 1,
  `mobile` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 委托检测单表
CREATE TABLE IF NOT EXISTS `lims_entrust_task` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `task_code` VARCHAR(64) NOT NULL UNIQUE,
  `client_name` VARCHAR(150) NOT NULL,
  `contact_person` VARCHAR(50) DEFAULT NULL,
  `contact_phone` VARCHAR(50) DEFAULT NULL,
  `project_name` VARCHAR(150) NOT NULL,
  `status` VARCHAR(30) DEFAULT '待采样',
  `amount` DOUBLE DEFAULT 0.0,
  `remark` TEXT DEFAULT NULL,
  `creator` VARCHAR(50) DEFAULT 'admin',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 样品流转台账表
CREATE TABLE IF NOT EXISTS `lims_sample` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `sample_code` VARCHAR(64) NOT NULL UNIQUE,
  `sample_name` VARCHAR(100) NOT NULL,
  `entrust_id` INT DEFAULT NULL,
  `entrust_code` VARCHAR(64) DEFAULT NULL,
  `sample_type` VARCHAR(50) DEFAULT NULL,
  `storage_condition` VARCHAR(100) DEFAULT '常温密闭',
  `status` VARCHAR(30) DEFAULT '已登记',
  `sampling_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 实验室检测分析任务表
CREATE TABLE IF NOT EXISTS `lims_detection_task` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `detection_code` VARCHAR(64) NOT NULL UNIQUE,
  `sample_id` INT DEFAULT NULL,
  `sample_code` VARCHAR(64) DEFAULT NULL,
  `sample_name` VARCHAR(100) DEFAULT NULL,
  `item_name` VARCHAR(100) NOT NULL,
  `method_name` VARCHAR(150) DEFAULT NULL,
  `result_val` VARCHAR(50) DEFAULT NULL,
  `unit` VARCHAR(30) DEFAULT 'mg/L',
  `inspector` VARCHAR(50) DEFAULT 'admin',
  `reviewer` VARCHAR(50) DEFAULT NULL,
  `status` VARCHAR(30) DEFAULT '待检测',
  `complete_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 检测报告表
CREATE TABLE IF NOT EXISTS `lims_report` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `report_code` VARCHAR(64) NOT NULL UNIQUE,
  `entrust_code` VARCHAR(64) DEFAULT NULL,
  `report_title` VARCHAR(200) NOT NULL,
  `client_name` VARCHAR(150) NOT NULL,
  `issuer` VARCHAR(50) DEFAULT 'admin',
  `status` VARCHAR(30) DEFAULT '编制中',
  `issue_date` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 合同/委托协议表
CREATE TABLE IF NOT EXISTS `lims_contract` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `client_name` VARCHAR(150) NOT NULL,
  `contract_name` VARCHAR(200) NOT NULL,
  `quotation_code` VARCHAR(64) DEFAULT NULL,
  `sign_date` VARCHAR(50) DEFAULT NULL,
  `service_type` VARCHAR(50) DEFAULT '委托检测',
  `detection_type` VARCHAR(50) DEFAULT '环境检测',
  `salesman` VARCHAR(50) DEFAULT '业务经理-张伟',
  `total_amount_excl_tax` FLOAT DEFAULT 0.0,
  `preset_approver` VARCHAR(50) DEFAULT '技术主管-李工',
  `start_date` VARCHAR(50) DEFAULT NULL,
  `end_date` VARCHAR(50) DEFAULT NULL,
  `tested_company` VARCHAR(150) DEFAULT NULL,
  `order_cs` VARCHAR(50) DEFAULT '客服-王敏',
  `status` VARCHAR(30) DEFAULT '已生效',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化管理员
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `dept_name`, `status`)
VALUES ('admin', '123456', '超级管理员', '国家级环境检测中心', 1)
ON DUPLICATE KEY UPDATE `username`=`username`;
