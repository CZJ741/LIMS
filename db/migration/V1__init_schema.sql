-- =============================================================================
-- LIMS 实验室信息管理系统 - V1__init_schema.sql
-- 数据库版本：MySQL 5.7.44
-- 字符集：utf8mb4 / utf8mb4_general_ci
-- 说明：基础表结构定义（不包含待分区表的单独分区脚本，分区表在 V2 中统一执行）
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================================
-- 模块一：lims-system 系统管理模块
-- =============================================================================

-- 1. 组织架构表
CREATE TABLE IF NOT EXISTS `sys_org` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `org_code` VARCHAR(64) NOT NULL COMMENT '组织编码',
  `org_name` VARCHAR(128) NOT NULL COMMENT '组织名称',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父组织ID(顶级为0)',
  `ancestors` VARCHAR(512) NOT NULL DEFAULT '' COMMENT '祖级列表(逗号分隔)',
  `org_type` VARCHAR(32) NOT NULL DEFAULT 'DEPT' COMMENT '组织类型(CORP=机构/DEPT=部门/LAB=检测室)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `leader_name` VARCHAR(64) NULL DEFAULT NULL COMMENT '负责人姓名',
  `leader_phone` VARCHAR(32) NULL DEFAULT NULL COMMENT '负责人电话',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=启用)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_org_code` (`org_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组织机构表';

-- 2. 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `org_id` BIGINT NOT NULL COMMENT '所属组织ID',
  `username` VARCHAR(64) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(128) NOT NULL COMMENT '密码哈希值(BCrypt)',
  `real_name` VARCHAR(64) NOT NULL COMMENT '真实姓名',
  `email` VARCHAR(128) NULL DEFAULT NULL COMMENT '电子邮箱',
  `phone` VARCHAR(32) NULL DEFAULT NULL COMMENT '手机号码',
  `avatar` VARCHAR(256) NULL DEFAULT NULL COMMENT '用户头像地址',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '帐号状态(0=停用 1=正常)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_user_org` FOREIGN KEY (`org_id`) REFERENCES `sys_org` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户表';

-- 3. 职位表（19类预置身份）
CREATE TABLE IF NOT EXISTS `sys_position` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pos_code` VARCHAR(64) NOT NULL COMMENT '职位编码',
  `pos_name` VARCHAR(128) NOT NULL COMMENT '职位名称',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=启用)',
  `remark` VARCHAR(256) NULL DEFAULT NULL COMMENT '备注说明',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pos_code` (`pos_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='职位身份表';

-- 4. 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色权限字符',
  `role_name` VARCHAR(128) NOT NULL COMMENT '角色名称',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '角色状态(0=停用 1=正常)',
  `remark` VARCHAR(256) NULL DEFAULT NULL COMMENT '备注说明',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- 5. 用户-角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-角色关联表';

-- 6. 用户-岗位关联表
CREATE TABLE IF NOT EXISTS `sys_user_position` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `position_id` BIGINT NOT NULL COMMENT '职位ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_pos` (`user_id`, `position_id`),
  KEY `idx_position_id` (`position_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_up_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_up_position` FOREIGN KEY (`position_id`) REFERENCES `sys_position` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-岗位关联表';

-- 7. 项目组表（班组/工作组）
CREATE TABLE IF NOT EXISTS `sys_project_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_code` VARCHAR(64) NOT NULL COMMENT '项目组编码',
  `group_name` VARCHAR(128) NOT NULL COMMENT '项目组名称',
  `leader_id` BIGINT NOT NULL COMMENT '组长用户ID',
  `remark` VARCHAR(256) NULL DEFAULT NULL COMMENT '项目组说明',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=启用)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_code` (`group_code`),
  KEY `idx_leader_id` (`leader_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_group_leader` FOREIGN KEY (`leader_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目组信息表';

-- 8. 用户-项目组关联表
CREATE TABLE IF NOT EXISTS `sys_user_project_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `group_id` BIGINT NOT NULL COMMENT '项目组ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_group` (`user_id`, `group_id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_upg_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_upg_group` FOREIGN KEY (`group_id`) REFERENCES `sys_project_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-项目组关联表';

-- 9. 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `menu_name` VARCHAR(64) NOT NULL COMMENT '菜单名称',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID(顶级为0)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `route_path` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '路由地址',
  `component_path` VARCHAR(256) NULL DEFAULT NULL COMMENT '组件路径',
  `is_frame` TINYINT NOT NULL DEFAULT 0 COMMENT '是否外链(0=否 1=是)',
  `menu_type` VARCHAR(16) NOT NULL DEFAULT 'C' COMMENT '类型(M=目录 C=菜单 F=按钮)',
  `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '显示状态(0=隐藏 1=显示)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '菜单状态(0=停用 1=正常)',
  `perms` VARCHAR(128) NULL DEFAULT NULL COMMENT '权限标识',
  `icon` VARCHAR(128) NULL DEFAULT NULL COMMENT '菜单图标',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';

-- 10. 按钮权限表
CREATE TABLE IF NOT EXISTS `sys_button` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `menu_id` BIGINT NOT NULL COMMENT '所属菜单ID',
  `btn_code` VARCHAR(64) NOT NULL COMMENT '按钮编码',
  `btn_name` VARCHAR(64) NOT NULL COMMENT '按钮名称',
  `perm_tag` VARCHAR(128) NOT NULL COMMENT '权限鉴权标识(如: contract:btn:audit)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=启用)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_btn_code` (`menu_id`, `btn_code`),
  KEY `idx_menu_id` (`menu_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_btn_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='按钮权限表';

-- 11. 字典分类表
CREATE TABLE IF NOT EXISTS `sys_dict_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_code` VARCHAR(64) NOT NULL COMMENT '分类编码',
  `category_name` VARCHAR(128) NOT NULL COMMENT '分类名称',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=正常)',
  `remark` VARCHAR(256) NULL DEFAULT NULL COMMENT '备注说明',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据字典分类表';

-- 12. 字典项表
CREATE TABLE IF NOT EXISTS `sys_dict_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `item_label` VARCHAR(128) NOT NULL COMMENT '字典标签(展示文本)',
  `item_value` VARCHAR(128) NOT NULL COMMENT '字典键值',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=正常)',
  `remark` VARCHAR(256) NULL DEFAULT NULL COMMENT '备注说明',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cat_item_val` (`category_id`, `item_value`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_dict_item_category` FOREIGN KEY (`category_id`) REFERENCES `sys_dict_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据字典明细项表';


-- =============================================================================
-- 模块二：lims-contract 合同管理模块
-- =============================================================================

-- 13. 合同主表
CREATE TABLE IF NOT EXISTS `contract` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contract_code` VARCHAR(64) NOT NULL COMMENT '合同编号(HT-YYYYMMDD-XXXX)',
  `contract_name` VARCHAR(256) NOT NULL COMMENT '合同名称',
  `client_company` VARCHAR(256) NOT NULL COMMENT '委托方客户名称',
  `client_contact` VARCHAR(64) NOT NULL COMMENT '客户联系人',
  `client_phone` VARCHAR(32) NOT NULL COMMENT '客户联系电话',
  `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '合同总金额(元)',
  `start_date` DATE NOT NULL COMMENT '合同生效日期',
  `end_date` DATE NOT NULL COMMENT '合同截止日期',
  `sales_user_id` BIGINT NOT NULL COMMENT '销售人员用户ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '合同状态(DRAFT=草稿/PENDING=待审核/APPROVED=已生效/REJECTED=已驳回/COMPLETED=已结项)',
  `sign_date` DATE NULL DEFAULT NULL COMMENT '签订日期',
  `attachment_id` BIGINT NULL DEFAULT NULL COMMENT '合同附件文件ID',
  `remark` TEXT NULL COMMENT '合同备注条款',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_code` (`contract_code`),
  KEY `idx_sales_user_id` (`sales_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_client_company` (`client_company`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_cnt_sales_user` FOREIGN KEY (`sales_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检测合同主表';

-- 14. 监测方案表
CREATE TABLE IF NOT EXISTS `contract_monitoring_scheme` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contract_id` BIGINT NOT NULL COMMENT '所属合同ID',
  `scheme_code` VARCHAR(64) NOT NULL COMMENT '监测方案编号',
  `scheme_name` VARCHAR(256) NOT NULL COMMENT '监测方案名称',
  `target_domain` VARCHAR(64) NOT NULL COMMENT '监测领域(水质/环境空气/土壤等)',
  `frequency` VARCHAR(64) NOT NULL COMMENT '监测频次要求',
  `point_count` INT NOT NULL DEFAULT 1 COMMENT '点位数量',
  `detail_json` TEXT NULL COMMENT '方案参数配置JSON',
  `status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '方案状态(DRAFT/CONFIRMED)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scheme_code` (`scheme_code`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_cms_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同监测方案表';

-- 15. 合同审核记录表
CREATE TABLE IF NOT EXISTS `contract_audit_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contract_id` BIGINT NOT NULL COMMENT '合同ID',
  `audit_node` VARCHAR(64) NOT NULL COMMENT '审核节点(MARKET/QUALITY/TECH/FINANCE)',
  `auditor_id` BIGINT NOT NULL COMMENT '审核人用户ID',
  `audit_result` VARCHAR(32) NOT NULL COMMENT '审核结论(PASSED=通过/REJECTED=驳回)',
  `audit_comment` VARCHAR(512) NOT NULL DEFAULT '' COMMENT '审核意见',
  `audit_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_auditor_id` (`auditor_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_audit_time` (`audit_time`),
  CONSTRAINT `fk_car_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_car_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同评审审核记录表';

-- 16. 检测包定义表
CREATE TABLE IF NOT EXISTS `contract_detection_package` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `package_code` VARCHAR(64) NOT NULL COMMENT '检测套餐包编码',
  `package_name` VARCHAR(128) NOT NULL COMMENT '检测套餐包名称',
  `category_id` BIGINT NOT NULL COMMENT '所属检测类别字典项ID',
  `standard_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '标准单价(元)',
  `item_codes` VARCHAR(1024) NOT NULL COMMENT '包含的检测项目编码列表(逗号分隔)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=启用)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_package_code` (`package_code`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_cdp_category` FOREIGN KEY (`category_id`) REFERENCES `sys_dict_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检测项目套餐包表';


-- =============================================================================
-- 模块三：lims-entrust 委托管理模块
-- =============================================================================

-- 17. 委托单主表
CREATE TABLE IF NOT EXISTS `entrust_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entrust_code` VARCHAR(64) NOT NULL COMMENT '委托单编号(WT-YYYYMMDD-XXXX)',
  `contract_id` BIGINT NULL DEFAULT NULL COMMENT '关联关联合同ID(无合同委托可空)',
  `client_company` VARCHAR(256) NOT NULL COMMENT '委托单位',
  `client_contact` VARCHAR(64) NOT NULL COMMENT '委托人姓名',
  `client_phone` VARCHAR(32) NOT NULL COMMENT '委托人联系电话',
  `entrust_date` DATE NOT NULL COMMENT '委托日期',
  `sample_source` VARCHAR(32) NOT NULL DEFAULT 'DELIVERY' COMMENT '样品来源(DELIVERY=送样/SAMPLING=现场采样)',
  `urgency_level` VARCHAR(16) NOT NULL DEFAULT 'NORMAL' COMMENT '紧急程度(NORMAL=普通/URGENT=加急/EXTRA_URGENT=特急)',
  `status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '委托状态(DRAFT=草稿/RECEIVED=已接样/IN_PROGRESS=流转中/COMPLETED=已完成/CANCELLED=已撤单)',
  `clerk_id` BIGINT NOT NULL COMMENT '接样专员用户ID',
  `remark` VARCHAR(512) NULL DEFAULT NULL COMMENT '委托要求备注',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_entrust_code` (`entrust_code`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_clerk_id` (`clerk_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_eo_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_eo_clerk` FOREIGN KEY (`clerk_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检测委托单主表';

-- 18. 委托单明细表
CREATE TABLE IF NOT EXISTS `entrust_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entrust_id` BIGINT NOT NULL COMMENT '委托单ID',
  `sample_type` VARCHAR(64) NOT NULL COMMENT '样品类型(地下水/工业废水/废气等)',
  `detection_item_id` BIGINT NOT NULL COMMENT '检测项目字典ID',
  `standard_id` BIGINT NOT NULL COMMENT '检测方法标准字典ID',
  `judgment_standard_id` BIGINT NOT NULL COMMENT '评价判定标准字典ID',
  `unit_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '检测单价(元)',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `total_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总价(元)',
  `special_requirements` VARCHAR(256) NULL DEFAULT NULL COMMENT '特殊检测要求',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_item_id` (`detection_item_id`),
  KEY `idx_standard_id` (`standard_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_ei_entrust` FOREIGN KEY (`entrust_id`) REFERENCES `entrust_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ei_item` FOREIGN KEY (`detection_item_id`) REFERENCES `sys_dict_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ei_std` FOREIGN KEY (`standard_id`) REFERENCES `sys_dict_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ei_jud_std` FOREIGN KEY (`judgment_standard_id`) REFERENCES `sys_dict_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='委托单检测项目明细表';

-- 19. 订单流转记录表
CREATE TABLE IF NOT EXISTS `entrust_order_flow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entrust_id` BIGINT NOT NULL COMMENT '委托单ID',
  `stage_node` VARCHAR(32) NOT NULL COMMENT '流转阶段(ENTRUST/SAMPLING/DETECTION/REPORT/FINANCE)',
  `from_status` VARCHAR(32) NOT NULL COMMENT '变动前状态',
  `to_status` VARCHAR(32) NOT NULL COMMENT '变动后状态',
  `operator_id` BIGINT NOT NULL COMMENT '操作人用户ID',
  `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `log_comment` VARCHAR(512) NULL DEFAULT NULL COMMENT '流转操作说明',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_operation_time` (`operation_time`),
  CONSTRAINT `fk_eof_entrust` FOREIGN KEY (`entrust_id`) REFERENCES `entrust_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_eof_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='委托订单生命周期流转记录表';


-- =============================================================================
-- 模块四：lims-sampling 采样管理模块
-- =============================================================================

-- 20. 采样任务表
CREATE TABLE IF NOT EXISTS `sampling_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_code` VARCHAR(64) NOT NULL COMMENT '采样任务编号(CY-YYYYMMDD-XXXX)',
  `entrust_id` BIGINT NOT NULL COMMENT '委托单ID',
  `leader_id` BIGINT NOT NULL COMMENT '采样组长用户ID',
  `sampling_site` VARCHAR(256) NOT NULL COMMENT '采样地点/详细工程现场',
  `plan_start_time` DATETIME NOT NULL COMMENT '计划开始时间',
  `plan_end_time` DATETIME NOT NULL COMMENT '计划完成时间',
  `actual_start_time` DATETIME NULL DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` DATETIME NULL DEFAULT NULL COMMENT '实际完成时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING=待派发/IN_PROGRESS=采样中/REVIEWING=待审核/COMPLETED=已归档)',
  `remark` VARCHAR(512) NULL DEFAULT NULL COMMENT '任务备注要求',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sampling_task_code` (`task_code`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_leader_id` (`leader_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_st_entrust` FOREIGN KEY (`entrust_id`) REFERENCES `entrust_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_st_leader` FOREIGN KEY (`leader_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='现场采样任务主表';

-- 21. 采样任务明细表
CREATE TABLE IF NOT EXISTS `sampling_task_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sampling_task_id` BIGINT NOT NULL COMMENT '采样任务ID',
  `point_code` VARCHAR(64) NOT NULL COMMENT '点位编号(如: 1#排气筒/总排口)',
  `point_name` VARCHAR(128) NOT NULL COMMENT '点位名称',
  `detection_item_id` BIGINT NOT NULL COMMENT '检测项目字典ID',
  `sampler_id` BIGINT NOT NULL COMMENT '指定采样人员ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '明细状态(PENDING/FINISHED)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sampling_task_id` (`sampling_task_id`),
  KEY `idx_sampler_id` (`sampler_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_sti_task` FOREIGN KEY (`sampling_task_id`) REFERENCES `sampling_task` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sti_sampler` FOREIGN KEY (`sampler_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='采样任务点位明细表';

-- 22. 采样记录表
CREATE TABLE IF NOT EXISTS `sampling_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sampling_task_id` BIGINT NOT NULL COMMENT '采样任务ID',
  `point_code` VARCHAR(64) NOT NULL COMMENT '点位编号',
  `weather` VARCHAR(64) NOT NULL COMMENT '天气状况(晴/多云/雨等)',
  `temperature` DECIMAL(4,1) NOT NULL COMMENT '现场环境温度(℃)',
  `humidity` DECIMAL(4,1) NOT NULL COMMENT '相对湿度(%)',
  `air_pressure` DECIMAL(6,2) NULL DEFAULT NULL COMMENT '现场大气压(kPa)',
  `wind_speed` DECIMAL(4,1) NULL DEFAULT NULL COMMENT '风速(m/s)',
  `sample_count` INT NOT NULL DEFAULT 1 COMMENT '采集样品数量',
  `equipment_ids` VARCHAR(256) NULL DEFAULT NULL COMMENT '采样使用仪器ID集合',
  `record_user_id` BIGINT NOT NULL COMMENT '记录人员ID',
  `record_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '现场记录时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sampling_task_id` (`sampling_task_id`),
  KEY `idx_record_user_id` (`record_user_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_record_time` (`record_time`),
  CONSTRAINT `fk_sr_task` FOREIGN KEY (`sampling_task_id`) REFERENCES `sampling_task` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sr_user` FOREIGN KEY (`record_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='现场采样原始记录表';

-- 23. 样品台账表
CREATE TABLE IF NOT EXISTS `sample` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sample_code` VARCHAR(64) NOT NULL COMMENT '样品唯一编号(YP-YYYYMMDD-XXXX)',
  `entrust_id` BIGINT NOT NULL COMMENT '委托单ID',
  `sampling_task_id` BIGINT NULL DEFAULT NULL COMMENT '关联采样任务ID(送样可为空)',
  `sample_name` VARCHAR(128) NOT NULL COMMENT '样品名称',
  `sample_type` VARCHAR(64) NOT NULL COMMENT '样品类型',
  `sample_status` VARCHAR(32) NOT NULL DEFAULT 'PREPARED' COMMENT '样品状态(PREPARED=已制备/STORED=在库/TESTING=检测中/RETAINED=留样/DISPOSED=已处置)',
  `storage_condition` VARCHAR(64) NOT NULL DEFAULT '常温' COMMENT '保存条件(常温/冷藏4℃/避光等)',
  `storage_location` VARCHAR(128) NULL DEFAULT NULL COMMENT '存样位置/冰箱货架号',
  `sample_quantity` VARCHAR(32) NOT NULL DEFAULT '1' COMMENT '样品规格与数量(如: 500mL*2)',
  `expire_date` DATE NULL DEFAULT NULL COMMENT '留样有效期至',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sample_code` (`sample_code`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_sampling_task_id` (`sampling_task_id`),
  KEY `idx_sample_status` (`sample_status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_smp_entrust` FOREIGN KEY (`entrust_id`) REFERENCES `entrust_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_smp_task` FOREIGN KEY (`sampling_task_id`) REFERENCES `sampling_task` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='样品基本信息台账表';

-- 24. 样品运输记录表
CREATE TABLE IF NOT EXISTS `sample_transport` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sampling_task_id` BIGINT NOT NULL COMMENT '采样任务ID',
  `carrier_id` BIGINT NOT NULL COMMENT '押运人员用户ID',
  `transport_tool` VARCHAR(64) NOT NULL COMMENT '运输方式/车辆车牌号',
  `incubator_code` VARCHAR(64) NOT NULL COMMENT '冷藏保温箱编号',
  `start_temp` DECIMAL(4,1) NOT NULL COMMENT '起运箱内温度(℃)',
  `arrival_temp` DECIMAL(4,1) NOT NULL COMMENT '送达箱内温度(℃)',
  `start_time` DATETIME NOT NULL COMMENT '出发时间',
  `arrival_time` DATETIME NOT NULL COMMENT '送达时间',
  `receiver_id` BIGINT NOT NULL COMMENT '实验室接样人ID',
  `is_intact` TINYINT NOT NULL DEFAULT 1 COMMENT '样品包装与封条是否完好(1=是 0=否)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sampling_task_id` (`sampling_task_id`),
  KEY `idx_carrier_id` (`carrier_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_str_task` FOREIGN KEY (`sampling_task_id`) REFERENCES `sampling_task` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_str_carrier` FOREIGN KEY (`carrier_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_str_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='样品流转与现场冷链运输记录表';


-- =============================================================================
-- 模块五：lims-detection 检测管理模块
-- =============================================================================

-- 25. 检测任务主表
CREATE TABLE IF NOT EXISTS `detection_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_code` VARCHAR(64) NOT NULL COMMENT '检测任务编号(JC-YYYYMMDD-XXXX)',
  `entrust_id` BIGINT NOT NULL COMMENT '委托单ID',
  `lab_head_id` BIGINT NOT NULL COMMENT '下发主任/组长用户ID',
  `assign_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '派发时间',
  `deadline` DATETIME NOT NULL COMMENT '要求完成截止时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态(PENDING=待实验/TESTING=检测中/RECHECK=待复审/COMPLETED=已完成)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_det_task_code` (`task_code`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_lab_head_id` (`lab_head_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_dt_entrust` FOREIGN KEY (`entrust_id`) REFERENCES `entrust_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_dt_lab_head` FOREIGN KEY (`lab_head_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='实验室检测任务分配主表';

-- 26. 检测任务明细表
CREATE TABLE IF NOT EXISTS `detection_task_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `detection_task_id` BIGINT NOT NULL COMMENT '检测任务ID',
  `sample_id` BIGINT NOT NULL COMMENT '样品台账ID',
  `item_id` BIGINT NOT NULL COMMENT '检测项目字典ID',
  `standard_id` BIGINT NOT NULL COMMENT '检测标准方法字典ID',
  `analyst_id` BIGINT NOT NULL COMMENT '主检实验人员用户ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ASSIGNED' COMMENT '明细状态(ASSIGNED=已派发/ANALYZING=分析中/SUBMITTED=已提交)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_detection_task_id` (`detection_task_id`),
  KEY `idx_sample_id` (`sample_id`),
  KEY `idx_analyst_id` (`analyst_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_dti_task` FOREIGN KEY (`detection_task_id`) REFERENCES `detection_task` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_dti_sample` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_dti_analyst` FOREIGN KEY (`analyst_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检测任务明细派工表';

-- 27. 实验类型定义表
CREATE TABLE IF NOT EXISTS `experiment_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_code` VARCHAR(64) NOT NULL COMMENT '实验类型编码',
  `type_name` VARCHAR(128) NOT NULL COMMENT '实验类型名称(常规分析/色谱分析/光谱分析/现场快检)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=启用)',
  `remark` VARCHAR(256) NULL DEFAULT NULL COMMENT '备注说明',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='实验方法类别表';

-- 28. 标准曲线表
CREATE TABLE IF NOT EXISTS `standard_curve` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `curve_code` VARCHAR(64) NOT NULL COMMENT '标曲唯一编号',
  `curve_name` VARCHAR(128) NOT NULL COMMENT '标准曲线名称',
  `item_id` BIGINT NOT NULL COMMENT '对应检测项目ID',
  `equation` VARCHAR(128) NOT NULL COMMENT '拟合回归方程(如: Y=0.0452X+0.0012)',
  `r_squared` DECIMAL(6,5) NOT NULL COMMENT '相关系数R平方值(如: 0.99992)',
  `analyst_id` BIGINT NOT NULL COMMENT '校准绘制人员ID',
  `make_date` DATE NOT NULL COMMENT '绘制日期',
  `expire_date` DATE NOT NULL COMMENT '曲线有效截止期',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1=有效 0=已作废)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_curve_code` (`curve_code`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_analyst_id` (`analyst_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_sc_item` FOREIGN KEY (`item_id`) REFERENCES `sys_dict_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sc_analyst` FOREIGN KEY (`analyst_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检测仪器标准曲线记录表';

-- 29. 复审记录表
CREATE TABLE IF NOT EXISTS `detection_recheck_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_item_id` BIGINT NOT NULL COMMENT '检测明细任务ID',
  `reviewer_id` BIGINT NOT NULL COMMENT '数据复审员用户ID',
  `review_result` VARCHAR(32) NOT NULL COMMENT '复审结论(PASSED=通过/REJECTED=重做)',
  `review_comment` VARCHAR(512) NOT NULL DEFAULT '' COMMENT '复审质控核查意见',
  `review_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '复审时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_item_id` (`task_item_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_review_time` (`review_time`),
  CONSTRAINT `fk_drr_item` FOREIGN KEY (`task_item_id`) REFERENCES `detection_task_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_drr_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='实验检测数据复审记录表';


-- =============================================================================
-- 模块六：lims-report 报告管理模块
-- =============================================================================

-- 30. 报告模板表
CREATE TABLE IF NOT EXISTS `report_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_code` VARCHAR(64) NOT NULL COMMENT '模板编码',
  `template_name` VARCHAR(128) NOT NULL COMMENT '模板名称',
  `domain_type` VARCHAR(64) NOT NULL COMMENT '适用领域(水质/环境空气/土壤等)',
  `template_content` MEDIUMTEXT NOT NULL COMMENT '模板HTML/富文本设计内容',
  `version` INT NOT NULL DEFAULT 1 COMMENT '版本号',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0=停用 1=启用)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检验检测报告模板表';

-- 31. 报告编制分配表
CREATE TABLE IF NOT EXISTS `report_assign` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entrust_id` BIGINT NOT NULL COMMENT '委托单ID',
  `writer_id` BIGINT NOT NULL COMMENT '报告编制员用户ID',
  `auditor_id` BIGINT NOT NULL COMMENT '报告审核员用户ID',
  `signer_id` BIGINT NOT NULL COMMENT '授权签字人用户ID',
  `assign_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配派发时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_writer_id` (`writer_id`),
  KEY `idx_auditor_id` (`auditor_id`),
  KEY `idx_signer_id` (`signer_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_ra_entrust` FOREIGN KEY (`entrust_id`) REFERENCES `entrust_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ra_writer` FOREIGN KEY (`writer_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ra_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ra_signer` FOREIGN KEY (`signer_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报告编制三级责任人分配表';

-- 32. 报告审核记录表
CREATE TABLE IF NOT EXISTS `report_audit_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_id` BIGINT NOT NULL COMMENT '关联报告ID',
  `audit_stage` VARCHAR(32) NOT NULL COMMENT '审核阶段(AUDIT=报告初审/SIGN=授权签字签发)',
  `auditor_id` BIGINT NOT NULL COMMENT '审核人用户ID',
  `audit_result` VARCHAR(32) NOT NULL COMMENT '审核结论(PASSED=通过/REJECTED=驳回修改)',
  `audit_opinion` VARCHAR(512) NOT NULL DEFAULT '' COMMENT '审核意见与签批批注',
  `audit_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_report_id` (`report_id`),
  KEY `idx_auditor_id` (`auditor_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_audit_time` (`audit_time`),
  CONSTRAINT `fk_rar_auditor` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检验检测报告多级审核签发记录表';


-- =============================================================================
-- 模块七：lims-finance 财务管理模块
-- =============================================================================

-- 33. 合同财务审核表
CREATE TABLE IF NOT EXISTS `finance_contract_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contract_id` BIGINT NOT NULL COMMENT '合同ID',
  `finance_user_id` BIGINT NOT NULL COMMENT '财务审核人用户ID',
  `audit_amount` DECIMAL(12,2) NOT NULL COMMENT '财务核定总金额(元)',
  `tax_rate` DECIMAL(5,2) NOT NULL DEFAULT 6.00 COMMENT '开票税率(%)',
  `payment_clause` VARCHAR(256) NOT NULL COMMENT '付款条款与账期约定',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PASSED' COMMENT '审核状态(PASSED/REJECTED)',
  `audit_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_finance_user_id` (`finance_user_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_audit_time` (`audit_time`),
  CONSTRAINT `fk_fca_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fca_user` FOREIGN KEY (`finance_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同财务收支审核表';

-- 34. 报告财务审核表（签发/出具前财务拦截核算）
CREATE TABLE IF NOT EXISTS `finance_report_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_id` BIGINT NOT NULL COMMENT '报告ID',
  `entrust_id` BIGINT NOT NULL COMMENT '对应委托单ID',
  `finance_user_id` BIGINT NOT NULL COMMENT '财务核算人用户ID',
  `is_paid` TINYINT NOT NULL DEFAULT 0 COMMENT '检测费用是否已结清(1=是 0=否/允许挂账)',
  `release_allowed` TINYINT NOT NULL DEFAULT 1 COMMENT '是否准许发放/签发报告(1=允许 0=拦截阻断)',
  `audit_comment` VARCHAR(256) NULL DEFAULT NULL COMMENT '财务放行意见',
  `audit_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核放行时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_report_id` (`report_id`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_finance_user_id` (`finance_user_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_fra_entrust` FOREIGN KEY (`entrust_id`) REFERENCES `entrust_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fra_user` FOREIGN KEY (`finance_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报告出具财务核算放行表';

-- 35. 财务结算单表
CREATE TABLE IF NOT EXISTS `settlement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `settlement_code` VARCHAR(64) NOT NULL COMMENT '结算单编号(JS-YYYYMMDD-XXXX)',
  `contract_id` BIGINT NULL DEFAULT NULL COMMENT '关联合同ID',
  `entrust_id` BIGINT NOT NULL COMMENT '关联委托单ID',
  `payer_name` VARCHAR(256) NOT NULL COMMENT '付款单位全称',
  `settle_amount` DECIMAL(12,2) NOT NULL COMMENT '实际结算金额(元)',
  `pay_method` VARCHAR(32) NOT NULL DEFAULT 'BANK_TRANSFER' COMMENT '支付方式(BANK_TRANSFER=对公转账/CASH=现金/CHECK=支票/ONLINE=在线支付)',
  `invoice_number` VARCHAR(64) NULL DEFAULT NULL COMMENT '开具发票号码',
  `settle_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结算到账时间',
  `settler_id` BIGINT NOT NULL COMMENT '经办财务人员用户ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'COMPLETED' COMMENT '状态(PENDING=待结清/COMPLETED=已结清)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_settlement_code` (`settlement_code`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_entrust_id` (`entrust_id`),
  KEY `idx_settler_id` (`settler_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_settle_time` (`settle_time`),
  CONSTRAINT `fk_set_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_set_entrust` FOREIGN KEY (`entrust_id`) REFERENCES `entrust_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_set_settler` FOREIGN KEY (`settler_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='财务结算对账单表';


-- =============================================================================
-- 模块八：lims-device 设备管理模块
-- =============================================================================

-- 36. 仪器设备台账表
CREATE TABLE IF NOT EXISTS `instrument` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `device_code` VARCHAR(64) NOT NULL COMMENT '仪器唯一设备资产编号',
  `device_name` VARCHAR(128) NOT NULL COMMENT '设备名称(如: 气相色谱质谱联用仪)',
  `model` VARCHAR(128) NOT NULL COMMENT '规格型号',
  `manufacturer` VARCHAR(128) NULL DEFAULT NULL COMMENT '生产厂商',
  `access_type` VARCHAR(32) NOT NULL DEFAULT 'API_WORKSTATION' COMMENT '直连采集协议(API_WORKSTATION/CSV/PDF/TXT/SERIAL_PORT)',
  `status` VARCHAR(32) NOT NULL DEFAULT 'NORMAL' COMMENT '设备状态(NORMAL=正常/IN_USE=使用中/MAINTENANCE=维护中/SCRAPPED=报废)',
  `manager_id` BIGINT NOT NULL COMMENT '设备管理员用户ID',
  `last_calibration_date` DATE NULL DEFAULT NULL COMMENT '上次计量检定/校准日期',
  `next_calibration_date` DATE NULL DEFAULT NULL COMMENT '下次计划校准日期',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_code` (`device_code`),
  KEY `idx_access_type` (`access_type`),
  KEY `idx_manager_id` (`manager_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_inst_manager` FOREIGN KEY (`manager_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='检测仪器设备台账表';

-- 37. 仪器连接配置表
CREATE TABLE IF NOT EXISTS `instrument_connection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `instrument_id` BIGINT NOT NULL COMMENT '仪器设备ID',
  `ip_address` VARCHAR(64) NULL DEFAULT NULL COMMENT '工作站IP/网关地址',
  `port` INT NULL DEFAULT NULL COMMENT '端口号',
  `serial_port` VARCHAR(32) NULL DEFAULT NULL COMMENT '串口号(COM1/COM2/ttyUSB0)',
  `baud_rate` INT NULL DEFAULT 9600 COMMENT '波特率(9600/115200等)',
  `data_bits` INT NULL DEFAULT 8 COMMENT '数据位(8/7)',
  `stop_bits` INT NULL DEFAULT 1 COMMENT '停止位(1/2)',
  `parity` VARCHAR(16) NULL DEFAULT 'NONE' COMMENT '奇偶校验(NONE/ODD/EVEN)',
  `watch_folder` VARCHAR(256) NULL DEFAULT NULL COMMENT '文件监测目录(适用于CSV/PDF/TXT)',
  `is_connected` TINYINT NOT NULL DEFAULT 0 COMMENT '当前通信状态(0=离线 1=正常在线)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_instrument_id` (`instrument_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_ic_instrument` FOREIGN KEY (`instrument_id`) REFERENCES `instrument` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪器直连物理通信配置表';

-- 38. 接口诊断日志表
CREATE TABLE IF NOT EXISTS `instrument_interface_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `instrument_id` BIGINT NOT NULL COMMENT '仪器设备ID',
  `log_type` VARCHAR(32) NOT NULL COMMENT '日志类型(CONNECT=连接/RECEIVE=数据接收/PARSE=数据解析/HEARTBEAT=心跳)',
  `request_payload` TEXT NULL COMMENT '下发或接收的原始报文帧',
  `parse_result` TEXT NULL COMMENT '解析生成的JSON结构',
  `is_success` TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功(1=成功 0=失败)',
  `error_message` VARCHAR(512) NULL DEFAULT NULL COMMENT '异常错误诊断信息',
  `log_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志记录时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_instrument_id` (`instrument_id`),
  KEY `idx_log_type` (`log_type`),
  KEY `idx_is_success` (`is_success`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_log_time` (`log_time`),
  CONSTRAINT `fk_iil_instrument` FOREIGN KEY (`instrument_id`) REFERENCES `instrument` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪器数据采集接口诊断日志表';


-- =============================================================================
-- 模块九：lims-file 文件存储模块
-- =============================================================================

-- 39. 文件上传记录表
CREATE TABLE IF NOT EXISTS `file_upload` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_code` VARCHAR(64) NOT NULL COMMENT '文件唯一业务识别码',
  `original_name` VARCHAR(256) NOT NULL COMMENT '原始文件名',
  `file_suffix` VARCHAR(32) NOT NULL COMMENT '文件扩展名(如: pdf, docx, csv)',
  `file_size` BIGINT NOT NULL COMMENT '文件大小(字节Bytes)',
  `content_type` VARCHAR(128) NOT NULL COMMENT '文件MIME类型',
  `bucket_name` VARCHAR(64) NOT NULL COMMENT 'MinIO存储桶名',
  `object_name` VARCHAR(512) NOT NULL COMMENT 'MinIO对象键路径',
  `storage_type` VARCHAR(32) NOT NULL DEFAULT 'MINIO' COMMENT '存储驱动类型',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_code` (`file_code`),
  KEY `idx_bucket_object` (`bucket_name`, `object_name`(128)),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件上传元数据记录表';

-- 40. 文件归档管理表
CREATE TABLE IF NOT EXISTS `file_archive` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `archive_code` VARCHAR(64) NOT NULL COMMENT '档案编号(DA-YYYYMMDD-XXXX)',
  `archive_title` VARCHAR(256) NOT NULL COMMENT '档案卷宗标题',
  `business_type` VARCHAR(64) NOT NULL COMMENT '关联业务(CONTRACT/REPORT/INSTRUMENT)',
  `business_id` BIGINT NOT NULL COMMENT '关联主业务单据ID',
  `file_id` BIGINT NOT NULL COMMENT '对应文件上传ID',
  `retention_period` INT NOT NULL DEFAULT 6 COMMENT '法定归档保存期(年)',
  `archive_date` DATE NOT NULL COMMENT '归档日期',
  `archivist_id` BIGINT NOT NULL COMMENT '归档人员用户ID',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未删 1=已删',
  `create_by` BIGINT NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_archive_code` (`archive_code`),
  KEY `idx_business` (`business_type`, `business_id`),
  KEY `idx_file_id` (`file_id`),
  KEY `idx_archivist_id` (`archivist_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_fa_file` FOREIGN KEY (`file_id`) REFERENCES `file_upload` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_fa_archivist` FOREIGN KEY (`archivist_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电子档案全生命周期归档表';

SET FOREIGN_KEY_CHECKS = 1;
