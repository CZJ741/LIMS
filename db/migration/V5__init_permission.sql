-- =============================================================================
-- LIMS 实验室信息管理系统 - V5__init_permission.sql
-- 数据库版本：MySQL 5.7.44
-- 说明：全量功能菜单树（含任务/合同/委托/采样/样品/检测/报告/业务基础/文件/客商/分包/系统）
-- 按钮权限点及业务字典初始化
-- =============================================================================

SET NAMES utf8mb4;

-- 1. 清理原有旧菜单数据与角色菜单绑定
DELETE FROM `sys_role_menu`;
DELETE FROM `sys_button`;
DELETE FROM `sys_menu`;

-- 2. 插入全量系统功能菜单树
-- 根层级 (parent_id = 0)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(100, '任务列表', 0, 1, '/task', NULL, 0, 'M', 1, 1, 'task:menu', 'Checked'),
(200, '合同/委托协议管理', 0, 2, '/contract', NULL, 0, 'M', 1, 1, 'contract:menu', 'Document'),
(300, '委托单管理', 0, 3, '/entrust', NULL, 0, 'M', 1, 1, 'entrust:menu', 'Tickets'),
(400, '采样管理', 0, 4, '/sampling', NULL, 0, 'M', 1, 1, 'sampling:menu', 'LocationInformation'),
(500, '样品流转', 0, 5, '/sample', NULL, 0, 'M', 1, 1, 'sample:menu', 'Box'),
(600, '检测管理', 0, 6, '/detection', NULL, 0, 'M', 1, 1, 'detection:menu', 'Aim'),
(700, '报告管理', 0, 7, '/report', NULL, 0, 'M', 1, 1, 'report:menu', 'Files'),
(800, '业务基础管理', 0, 8, '/base', NULL, 0, 'M', 1, 1, 'base:menu', 'DataAnalysis'),
(900, '文件管理', 0, 9, '/file', NULL, 0, 'M', 1, 1, 'file:menu', 'Folder'),
(1000, '客商管理', 0, 10, '/merchant', NULL, 0, 'M', 1, 1, 'merchant:menu', 'OfficeBuilding'),
(1100, '分包管理', 0, 11, '/subcontract', NULL, 0, 'M', 1, 1, 'subcontract:menu', 'Share'),
(1200, '系统管理', 0, 12, '/system', NULL, 0, 'M', 1, 1, 'system:menu', 'Setting');

-- 任务列表二级菜单 (100)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(101, '我的申请', 100, 1, '/task/my-apply', 'task/MyApply', 0, 'C', 1, 1, 'task:myApply:view', 'User'),
(102, '我的审批任务', 100, 2, '/task/my-audit', 'task/MyAudit', 0, 'C', 1, 1, 'task:myAudit:view', 'Stamp'),
(103, '我的综合事务查询', 100, 3, '/task/query', 'task/ComprehensiveQuery', 0, 'C', 1, 1, 'task:query:view', 'Search');

-- 合同/委托协议管理二级菜单 (200)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(201, '合同登记', 200, 1, '/contract/create', 'contract/ContractCreate', 0, 'C', 1, 1, 'contract:create:view', 'EditPen'),
(202, '委托协议登记', 200, 2, '/contract/protocol-create', 'contract/ProtocolCreate', 0, 'C', 1, 1, 'contract:protocol:view', 'DocumentAdd'),
(203, '我的历史合同/委托协议', 200, 3, '/contract/my-history', 'contract/MyContractHistory', 0, 'C', 1, 1, 'contract:history:view', 'Clock'),
(204, '合同/委托协议书汇总', 200, 4, '/contract/summary', 'contract/ContractSummary', 0, 'C', 1, 1, 'contract:summary:view', 'Collection'),
(205, '合同/委托协议审批', 200, 5, '/contract/audit', 'contract/ContractAudit', 0, 'C', 1, 1, 'contract:audit:view', 'Stamp'),
(206, '检测包维护', 200, 6, '/contract/package', 'contract/PackageMaintenance', 0, 'C', 1, 1, 'contract:package:view', 'Goods');

-- 委托单管理二级菜单 (300)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(301, '无报价任务登记', 300, 1, '/entrust/no-quote', 'entrust/NoQuoteTask', 0, 'C', 1, 1, 'entrust:noQuote:view', 'Document'),
(302, '有报价任务登记', 300, 2, '/entrust/quote', 'entrust/QuoteTask', 0, 'C', 1, 1, 'entrust:quote:view', 'Money'),
(303, '委托下单', 300, 3, '/entrust/order', 'entrust/EntrustOrder', 0, 'C', 1, 1, 'entrust:order:view', 'Plus'),
(304, '我的委托单信息', 300, 4, '/entrust/my-entrust', 'entrust/MyEntrust', 0, 'C', 1, 1, 'entrust:myEntrust:view', 'UserFilled'),
(305, '委托单汇总', 300, 5, '/entrust/summary', 'entrust/EntrustSummary', 0, 'C', 1, 1, 'entrust:summary:view', 'List');

-- 采样管理二级菜单 (400)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(401, '采样前准备', 400, 1, '/sampling/prepare', 'sampling/SamplingPrepare', 0, 'C', 1, 1, 'sampling:prepare:view', 'Suitcase'),
(402, '我的采样', 400, 2, '/sampling/my-sampling', 'sampling/MySampling', 0, 'C', 1, 1, 'sampling:mySampling:view', 'Van'),
(403, '采样任务汇总', 400, 3, '/sampling/summary', 'sampling/SamplingSummary', 0, 'C', 1, 1, 'sampling:summary:view', 'DataAnalysis');

-- 样品流转二级菜单 (500)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(501, '样品运输', 500, 1, '/sample/transport', 'sample/SampleTransport', 0, 'C', 1, 1, 'sample:transport:view', 'Van'),
(502, '样品接收', 500, 2, '/sample/receive', 'sample/SampleReceive', 0, 'C', 1, 1, 'sample:receive:view', 'CircleCheck'),
(503, '样品领用', 500, 3, '/sample/borrow', 'sample/SampleBorrow', 0, 'C', 1, 1, 'sample:borrow:view', 'Upload'),
(504, '样品退库', 500, 4, '/sample/return', 'sample/SampleReturn', 0, 'C', 1, 1, 'sample:return:view', 'Download'),
(505, '留样处置', 500, 5, '/sample/dispose', 'sample/SampleDispose', 0, 'C', 1, 1, 'sample:dispose:view', 'Delete'),
(506, '样品受理', 500, 6, '/sample/acceptance', 'sample/SampleAcceptance', 0, 'C', 1, 1, 'sample:acceptance:view', 'Finished');

-- 检测管理二级菜单 (600)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(601, '检测任务待认领', 600, 1, '/detection/pending-claim', 'detection/PendingClaim', 0, 'C', 1, 1, 'detection:claim:view', 'Bell'),
(602, '检测任务分发', 600, 2, '/detection/dispatch', 'detection/TaskDispatch', 0, 'C', 1, 1, 'detection:dispatch:view', 'Share'),
(603, '样品制备及前处理', 600, 3, '/detection/pretreatment', 'detection/Pretreatment', 0, 'C', 1, 1, 'detection:pretreat:view', 'Filter'),
(604, '检测任务汇总', 600, 4, '/detection/summary', 'detection/DetectionSummary', 0, 'C', 1, 1, 'detection:summary:view', 'List'),
(605, '我的检测任务', 600, 5, '/detection/my-task', 'detection/MyDetectionTask', 0, 'C', 1, 1, 'detection:myTask:view', 'User'),
(606, '我的实验任务', 600, 6, '/detection/my-experiment', 'detection/MyExperimentTask', 0, 'C', 1, 1, 'detection:myExp:view', 'Experiment'),
(607, '实验数据复审', 600, 7, '/detection/recheck', 'detection/DataRecheck', 0, 'C', 1, 1, 'detection:recheck:view', 'View'),
(608, '标曲维护', 600, 8, '/detection/curve', 'detection/CurveMaintenance', 0, 'C', 1, 1, 'detection:curve:view', 'TrendCharts'),
(609, '实验类型维护', 600, 9, '/detection/exp-type', 'detection/ExperimentType', 0, 'C', 1, 1, 'detection:expType:view', 'SetUp'),
(610, '已删除检测任务', 600, 10, '/detection/deleted-tasks', 'detection/DeletedTasks', 0, 'C', 1, 1, 'detection:deleted:view', 'Delete');

-- 报告管理二级菜单 (700)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(701, '报告编制', 700, 1, '/report/compile', 'report/ReportCompile', 0, 'C', 1, 1, 'report:compile:view', 'Edit'),
(702, '报告审核', 700, 2, '/report/audit', 'report/ReportAudit', 0, 'C', 1, 1, 'report:audit:view', 'Finished'),
(703, '报告发放', 700, 3, '/report/release', 'report/ReportRelease', 0, 'C', 1, 1, 'report:release:view', 'Promotion');

-- 业务基础管理二级菜单 (800)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(801, '检测类别', 800, 1, '/base/detection-category', 'base/DetectionCategory', 0, 'C', 1, 1, 'base:category:view', 'Menu'),
(802, '检测项目', 800, 2, '/base/detection-item', 'base/DetectionItem', 0, 'C', 1, 1, 'base:item:view', 'CollectionTag'),
(803, '检测标准', 800, 3, '/base/detection-standard', 'base/DetectionStandard', 0, 'C', 1, 1, 'base:standard:view', 'Reading'),
(804, '判定标准', 800, 4, '/base/judgment-standard', 'base/JudgmentStandard', 0, 'C', 1, 1, 'base:judgment:view', 'ScaleToOriginal'),
(805, '采样规则', 800, 5, '/base/sampling-rule', 'base/SamplingRule', 0, 'C', 1, 1, 'base:samplingRule:view', 'Guide'),
(806, '修约规则', 800, 6, '/base/rounding-rule', 'base/RoundingRule', 0, 'C', 1, 1, 'base:rounding:view', 'Operation'),
(807, '仪器管理', 800, 7, '/base/instrument', 'base/InstrumentManagement', 0, 'C', 1, 1, 'base:instrument:view', 'Cpu'),
(808, '点位管理', 800, 8, '/base/point', 'base/PointManagement', 0, 'C', 1, 1, 'base:point:view', 'Location'),
(809, '编号规则配置', 800, 9, '/base/code-rule', 'base/CodeRuleConfig', 0, 'C', 1, 1, 'base:codeRule:view', 'Key'),
(810, '原始记录单模版管理', 800, 10, '/base/raw-record-template', 'base/RawRecordTemplate', 0, 'C', 1, 1, 'base:rawTpl:view', 'Memo'),
(811, '原始记录单操作历史', 800, 11, '/base/raw-record-history', 'base/RawRecordHistory', 0, 'C', 1, 1, 'base:rawHistory:view', 'History'),
(812, '报告模板管理', 800, 12, '/base/report-template', 'base/ReportTemplate', 0, 'C', 1, 1, 'base:reportTpl:view', 'DocumentCopy'),
(813, '数学编辑器', 800, 13, '/base/math-editor', 'base/MathEditor', 0, 'C', 1, 1, 'base:mathEditor:view', 'EditPen'),
(814, '车辆管理', 800, 14, '/base/vehicle', 'base/VehicleManagement', 0, 'C', 1, 1, 'base:vehicle:view', 'Van');

-- 文件管理二级菜单 (900)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(901, '文件上传', 900, 1, '/file/upload', 'file/FileUpload', 0, 'C', 1, 1, 'file:upload:view', 'Upload'),
(902, '文件归档', 900, 2, '/file/archive', 'file/FileArchive', 0, 'C', 1, 1, 'file:archive:view', 'FolderChecked');

-- 客商管理二级菜单 (1000)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(1001, '客户基本信息', 1000, 1, '/merchant/customer', 'merchant/CustomerList', 0, 'C', 1, 1, 'merchant:customer:view', 'Avatar'),
(1002, '供应商信息', 1000, 2, '/merchant/supplier', 'merchant/SupplierList', 0, 'C', 1, 1, 'merchant:supplier:view', 'Goods');

-- 分包管理二级菜单 (1100)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(1101, '分包协议', 1100, 1, '/subcontract/agreement', 'subcontract/AgreementList', 0, 'C', 1, 1, 'subcontract:agreement:view', 'Tickets'),
(1102, '分包评审', 1100, 2, '/subcontract/review', 'subcontract/ReviewList', 0, 'C', 1, 1, 'subcontract:review:view', 'Reading'),
(1103, '分包商管理', 1100, 3, '/subcontract/vendor', 'subcontract/VendorList', 0, 'C', 1, 1, 'subcontract:vendor:view', 'OfficeBuilding');

-- 系统管理二级菜单 (1200)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(1201, '菜单管理', 1200, 1, '/system/menu', 'system/MenuManagement', 0, 'C', 1, 1, 'system:menu:view', 'Menu'),
(1202, '流程业务配置', 1200, 2, '/system/flow-config', 'system/FlowConfig', 0, 'C', 1, 1, 'system:flow:view', 'Help'),
(1203, '权限管理', 1200, 3, '/system/permission', NULL, 0, 'M', 1, 1, 'system:perm:menu', 'Lock'),
(1204, '用户管理', 1200, 4, '/system/user', 'system/UserManagement', 0, 'C', 1, 1, 'system:user:view', 'User'),
(1205, '系统设置', 1200, 5, '/system/setting', 'system/SystemSetting', 0, 'C', 1, 1, 'system:setting:view', 'Tools'),
(1206, '个人设置', 1200, 6, '/system/profile', 'system/PersonalProfile', 0, 'C', 1, 1, 'system:profile:view', 'Avatar');

-- 权限管理三级子菜单 (1203)
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `route_path`, `component_path`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`) VALUES
(12031, '组织管理', 1203, 1, '/system/permission/org', 'system/OrgManagement', 0, 'C', 1, 1, 'system:org:view', 'OfficeBuilding'),
(12032, '人员维护', 1203, 2, '/system/permission/staff', 'system/StaffMaintenance', 0, 'C', 1, 1, 'system:staff:view', 'UserFilled'),
(12033, '角色管理', 1203, 3, '/system/permission/role', 'system/RoleManagement', 0, 'C', 1, 1, 'system:role:view', 'Key'),
(12034, '职位维护', 1203, 4, '/system/permission/position', 'system/PositionManagement', 0, 'C', 1, 1, 'system:position:view', 'Medal'),
(12035, '人员查询', 1203, 5, '/system/permission/query', 'system/StaffQuery', 0, 'C', 1, 1, 'system:staffQuery:view', 'Search');

-- 3. 按钮权限插入
INSERT INTO `sys_button` (`id`, `menu_id`, `btn_code`, `btn_name`, `perm_tag`, `status`) VALUES
-- 合同按钮
(2001, 201, 'contract_create_btn', '新建合同', 'contract:create:btn', 1),
(2002, 205, 'contract_audit_btn', '审核通过/驳回', 'contract:audit:btn', 1),
-- 委托按钮
(2003, 303, 'entrust_order_btn', '提交委托', 'entrust:order:btn', 1),
-- 采样按钮
(2004, 401, 'sampling_prepare_btn', '生成准备清单', 'sampling:prepare:btn', 1),
(2005, 402, 'sampling_submit_btn', '现场提交采样数据', 'sampling:submit:btn', 1),
-- 样品流转按钮
(2006, 502, 'sample_receive_btn', '确认接样入库', 'sample:receive:btn', 1),
-- 检测管理按钮
(2007, 601, 'detection_claim_btn', '认领检测任务', 'detection:claim:btn', 1),
(2008, 602, 'detection_dispatch_btn', '任务指派分发', 'detection:dispatch:btn', 1),
(2009, 605, 'detection_result_btn', '录入结果报出', 'detection:result:btn', 1),
(2010, 607, 'detection_recheck_btn', '复核确认/打回', 'detection:recheck:btn', 1),
-- 报告管理按钮
(2011, 701, 'report_compile_btn', '一键生成报告', 'report:compile:btn', 1),
(2012, 702, 'report_audit_btn', '审核签署', 'report:audit:btn', 1),
(2013, 703, 'report_release_btn', '加盖电子签章发放', 'report:release:btn', 1),
-- 用户与权限管理按钮
(2014, 1204, 'user_add_btn', '创建用户', 'system:user:add', 1),
(2015, 1204, 'user_edit_btn', '编辑用户', 'system:user:edit', 1),
(2016, 1204, 'user_delete_btn', '删除用户', 'system:user:delete', 1),
(2017, 12033, 'role_assign_btn', '分配菜单权限矩阵', 'system:role:assignPerm', 1);

-- 4. 超级管理员赋予所有菜单权限 (SUPER_ADMIN: role_id = 1)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `data_scope`)
SELECT 1, id, 'GLOBAL' FROM `sys_menu`;
