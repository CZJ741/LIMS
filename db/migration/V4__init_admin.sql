-- =============================================================================
-- LIMS 实验室信息管理系统 - V4__init_admin.sql
-- 数据库版本：MySQL 5.7.44
-- 字符集：utf8mb4 / utf8mb4_general_ci
-- 说明：
-- 包含组织根节点、超级管理员账号、角色权限、菜单目录/页面/按钮权限
-- =============================================================================

SET NAMES utf8mb4;

-- =============================================================================
-- 1. 默认顶级组织架构 (sys_org)
-- =============================================================================
INSERT INTO `sys_org` (`id`, `org_code`, `org_name`, `parent_id`, `ancestors`, `org_type`, `sort_order`, `leader_name`, `leader_phone`, `status`) VALUES
(1, 'ORG_TOP', '国家检验检测认证中心', 0, '0', 'CORP', 1, '管理员', '13800000000', 1),
(2, 'ORG_LAB', '环境检测综合实验室', 1, '0,1', 'LAB', 1, '实验室主任', '13800000001', 1),
(3, 'ORG_QUALITY', '质量管理部', 1, '0,1', 'DEPT', 2, '质量主管', '13800000002', 1),
(4, 'ORG_MARKET', '市场业务部', 1, '0,1', 'DEPT', 3, '销售经理', '13800000003', 1),
(5, 'ORG_FINANCE', '财务结算部', 1, '0,1', 'DEPT', 4, '财务主管', '13800000004', 1);

-- =============================================================================
-- 2. 默认超级管理员账号 (sys_user)
-- 账号：admin / 密码明文：123456
-- 密码哈希值：$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
-- =============================================================================
INSERT INTO `sys_user` (`id`, `org_id`, `username`, `password`, `real_name`, `email`, `phone`, `status`) VALUES
(1, 1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统超级管理员', 'admin@lims.local', '13800000000', 1);

-- =============================================================================
-- 3. 基础角色 (sys_role)
-- =============================================================================
INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `sort_order`, `status`, `remark`) VALUES
(1, 'SUPER_ADMIN', '超级管理员', 1, 1, '系统全局最高管理权限'),
(2, 'ROLE_QUALITY_DIRECTOR', '质量主管角色', 2, 1, '体系审核、流程监督、受控分发'),
(3, 'ROLE_LAB_ANALYST', '检测实验人员角色', 3, 1, '上机分析、结果录入、平行样质控'),
(4, 'ROLE_REPORT_SIGNER', '报告签字人角色', 4, 1, '最终技术审核、授权电子签名签发');

-- =============================================================================
-- 4. 用户-角色关联 (sys_user_role)
-- =============================================================================
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES
(1, 1, 1);

-- =============================================================================
-- 5. 用户-岗位关联 (sys_user_position) - admin 赋予管理层身份
-- =============================================================================
INSERT INTO `sys_user_position` (`id`, `user_id`, `position_id`) VALUES
(1, 1, 1);

-- =============================================================================
-- 6. 默认项目组 (sys_project_group) 与用户项目组关联
-- =============================================================================
INSERT INTO `sys_project_group` (`id`, `group_code`, `group_name`, `leader_id`, `remark`, `status`) VALUES
(1, 'GRP_ENV_WATER', '地表水监测攻坚专班', 1, '重点流域地表水质调查与专项分析', 1);

INSERT INTO `sys_user_project_group` (`id`, `user_id`, `group_id`) VALUES
(1, 1, 1);

-- =============================================================================
-- 7. 菜单与目录 (sys_menu)
-- =============================================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
-- 一级目录：工作台
(1, '工作台', 0, 1, '/home', 'home/index', 0, 'C', 1, 1, 'home:view', 'DataBoard'),

-- 一级目录：业务阶段
(2, '合同管理', 0, 2, '/contract', NULL, 0, 'M', 1, 1, 'contract:menu', 'Document'),
(21, '合同登记', 2, 1, '/contract/list', 'contract/list', 0, 'C', 1, 1, 'contract:list:view', 'DocumentCopy'),
(22, '合同评审', 2, 2, '/contract/audit', 'contract/audit', 0, 'C', 1, 1, 'contract:audit:view', 'Stamp'),

(3, '委托管理', 0, 3, '/entrust', NULL, 0, 'M', 1, 1, 'entrust:menu', 'Tickets'),
(31, '委托受理', 3, 1, '/entrust/list', 'entrust/list', 0, 'C', 1, 1, 'entrust:list:view', 'List'),
(32, '样品台账', 3, 2, '/entrust/sample', 'entrust/sample', 0, 'C', 1, 1, 'entrust:sample:view', 'TakeawayBox'),

(4, '采样管理', 0, 4, '/sampling', NULL, 0, 'M', 1, 1, 'sampling:menu', 'LocationInformation'),
(41, '采样排程', 4, 1, '/sampling/task', 'sampling/task', 0, 'C', 1, 1, 'sampling:task:view', 'Calendar'),
(42, '现场记录', 4, 2, '/sampling/record', 'sampling/record', 0, 'C', 1, 1, 'sampling:record:view', 'EditPen'),

(5, '检测管理', 0, 5, '/detection', NULL, 0, 'M', 1, 1, 'detection:menu', 'Aim'),
(51, '实验派工', 5, 1, '/detection/task', 'detection/task', 0, 'C', 1, 1, 'detection:task:view', 'UserFilled'),
(52, '数据录入', 5, 2, '/detection/result', 'detection/result', 0, 'C', 1, 1, 'detection:result:view', 'Edit'),
(53, '数据复审', 5, 3, '/detection/recheck', 'detection/recheck', 0, 'C', 1, 1, 'detection:recheck:view', 'Checked'),

(6, '报告管理', 0, 6, '/report', NULL, 0, 'M', 1, 1, 'report:menu', 'Files'),
(61, '报告编制', 6, 1, '/report/list', 'report/list', 0, 'C', 1, 1, 'report:list:view', 'DocumentAdd'),
(62, '报告审核', 6, 2, '/report/audit', 'report/audit', 0, 'C', 1, 1, 'report:audit:view', 'Finished'),
(63, '授权签发', 6, 3, '/report/sign', 'report/sign', 0, 'C', 1, 1, 'report:sign:view', 'GoldMedal'),

(7, '财务管理', 0, 7, '/finance', NULL, 0, 'M', 1, 1, 'finance:menu', 'Money'),
(71, '结算对账', 7, 1, '/finance/settlement', 'finance/settlement', 0, 'C', 1, 1, 'finance:settle:view', 'Wallet'),

(8, '设备直连', 0, 8, '/device', NULL, 0, 'M', 1, 1, 'device:menu', 'Cpu'),
(81, '仪器台账', 8, 1, '/device/list', 'device/list', 0, 'C', 1, 1, 'device:list:view', 'Monitor'),
(82, '接口日志', 8, 2, '/device/log', 'device/log', 0, 'C', 1, 1, 'device:log:view', 'Operation'),

(9, '系统设置', 0, 9, '/system', NULL, 0, 'M', 1, 1, 'system:menu', 'Setting'),
(91, '组织用户', 9, 1, '/system/user', 'system/user/index', 0, 'C', 1, 1, 'system:user:view', 'User'),
(92, '角色权限', 9, 2, '/system/role', 'system/role/index', 0, 'C', 1, 1, 'system:role:view', 'Key'),
(93, '数据字典', 9, 3, '/system/dict', 'system/dict/index', 0, 'C', 1, 1, 'system:dict:view', 'Collection'),
(94, '审计日志', 9, 4, '/system/audit', 'system/audit/index', 0, 'C', 1, 1, 'system:audit:view', 'Timer');

-- =============================================================================
-- 8. 关键按钮权限 (sys_button)
-- =============================================================================
INSERT INTO `sys_button` (`id`, `menu_id`, `btn_code`, `btn_name`, `perm_tag`, `status`) VALUES
-- 合同按钮权限
(1001, 21, 'btn_contract_add', '新增合同', 'contract:btn:add', 1),
(1002, 21, 'btn_contract_edit', '编辑合同', 'contract:btn:edit', 1),
(1003, 22, 'btn_contract_audit', '合同审核审批', 'contract:btn:audit', 1),

-- 委托按钮权限
(1004, 31, 'btn_entrust_add', '新增委托受理', 'entrust:btn:add', 1),
(1005, 31, 'btn_entrust_flow', '查看流转轨迹', 'entrust:btn:flow', 1),

-- 采样按钮权限
(1006, 41, 'btn_sampling_dispatch', '派发现场任务', 'sampling:btn:dispatch', 1),
(1007, 42, 'btn_sampling_record_save', '提交采样记录', 'sampling:btn:record:save', 1),

-- 检测按钮权限
(1008, 51, 'btn_detection_assign', '任务分派指派', 'detection:btn:assign', 1),
(1009, 52, 'btn_detection_result_submit', '录入结果提交', 'detection:btn:result:submit', 1),
(1010, 53, 'btn_detection_recheck_pass', '复审通过/驳回', 'detection:btn:recheck:audit', 1),

-- 报告按钮权限
(1011, 61, 'btn_report_generate', '自动生成报告', 'report:btn:generate', 1),
(1012, 62, 'btn_report_audit_pass', '审核报告批注', 'report:btn:audit:pass', 1),
(1013, 63, 'btn_report_sign_pass', '授权签字盖章', 'report:btn:sign:pass', 1),

-- 财务与系统按钮
(1014, 71, 'btn_finance_settle', '确认财务收款', 'finance:btn:settle', 1),
(1015, 91, 'btn_user_reset_pwd', '重置用户密码', 'system:user:resetPwd', 1);
