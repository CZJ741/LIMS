# LIMS 数据库架构与运维设计说明书

## 1. 数据库表清单（共 43 张）

| 模块 | 表名 | 中文说明 | 核心特征/约束 |
| :--- | :--- | :--- | :--- |
| **lims-system** | `sys_org` | 组织机构表 | 树形结构、`org_code` 唯一键 |
| | `sys_user` | 系统用户表 | `username` 唯一键、BCrypt 哈希 |
| | `sys_position` | 职位身份表 | 预置 19 类专业职位、`pos_code` 唯一键 |
| | `sys_role` | 系统角色表 | `role_code` 唯一键 |
| | `sys_user_role` | 用户-角色关联表 | 联合唯一键 `(user_id, role_id)` |
| | `sys_user_position`| 用户-岗位关联表 | 联合唯一键 `(user_id, position_id)` |
| | `sys_project_group`| 项目组/班组表 | `group_code` 唯一键、组长外键 |
| | `sys_user_project_group`| 用户-项目组关联表 | 联合唯一键 `(user_id, group_id)` |
| | `sys_menu` | 菜单权限表 | 树形菜单、路由/组件定义 |
| | `sys_button` | 按钮权限表 | 联合唯一键 `(menu_id, btn_code)` |
| | `sys_dict_category`| 数据字典分类表 | `category_code` 唯一键 |
| | `sys_dict_item` | 数据字典明细表 | 联合唯一键 `(category_id, item_value)` |
| | `sys_audit_log` | 系统审计日志表 | **按月 RANGE 分区**、联合主键 `(id, create_time)` |
| **lims-contract** | `contract` | 合同主表 | `contract_code` 唯一键、销售人员外键 |
| | `contract_monitoring_scheme` | 监测方案表 | `scheme_code` 唯一键、关联合同外键 |
| | `contract_audit_record` | 合同审核记录表 | 记录节点、审核结论与审核人外键 |
| | `contract_detection_package`| 检测套餐包表 | `package_code` 唯一键、单价与项目集 |
| **lims-entrust** | `entrust_order` | 委托单主表 | `entrust_code` 唯一键、关联合同/接样员外键 |
| | `entrust_item` | 委托单明细表 | 检测项目、分析标准、判定标准外键 |
| | `entrust_order_flow` | 订单流转记录表 | 全生命周期状态变更轨迹留痕 |
| **lims-sampling** | `sampling_task` | 采样任务主表 | `task_code` 唯一键、组长/委托单外键 |
| | `sampling_task_item`| 采样点位明细表 | 采样点位、指定采样员外键 |
| | `sampling_record` | 采样记录表 | 现场温湿度/气压/风速及原始记录 |
| | `sample` | 样品台账表 | `sample_code` 唯一键、存样位置与留样期 |
| | `sample_transport`| 样品运输记录表 | 冷链温控、押运人与接样人外键 |
| **lims-detection**| `detection_task` | 检测任务主表 | `task_code` 唯一键、主任派工外键 |
| | `detection_task_item`| 检测任务明细表 | 样品、分析员、标准方法外键 |
| | `experiment_type` | 实验类型定义表 | `type_code` 唯一键 |
| | `standard_curve` | 标准曲线表 | `curve_code` 唯一键、回归方程与相关系数 |
| | `detection_recheck_record`| 复审记录表 | 复审人、质控核查意见与结论 |
| | `detection_result` | 检测结果明细表 | **按年 RANGE 分区**、平行样与修约值 |
| **lims-report** | `report_template` | 报告模板表 | `template_code` 唯一键、富文本/HTML |
| | `report_assign` | 报告三级分配表 | 编制人、审核人、签字人外键 |
| | `report_audit_record`| 报告审核记录表 | 多级审核意见与批注留痕 |
| | `report` | 报告主表 | **按年 RANGE 分区**、联合主键/唯一键 |
| **lims-finance** | `finance_contract_audit`| 合同财务审核表 | 财务核定金额、税率、付款条款 |
| | `finance_report_audit` | 报告财务审核表 | 费用结清校验、出具报告放行控制 |
| | `settlement` | 财务结算单表 | `settlement_code` 唯一键、付款流水 |
| **lims-device** | `instrument` | 仪器设备台账表 | `device_code` 唯一键、检定校准周期 |
| | `instrument_connection`| 仪器连接配置表 | `instrument_id` 唯一、串口/网络通信配置 |
| | `instrument_interface_log`| 接口诊断日志表 | 直连报文帧抓取与诊断记录 |
| **lims-file** | `file_upload` | 文件上传记录表 | `file_code` 唯一键、MinIO 对象键 |
| | `file_archive` | 文件归档管理表 | `archive_code` 唯一键、法定保存期 |

---

## 2. 索引策略与 MySQL 5.7 规范说明

1. **主键与外键策略**：
   - 统一采用 `BIGINT AUTO_INCREMENT` 作为主键（不采用无序 UUID，减少 B+Tree 页分裂）。
   - 关系表严格定义 `FOREIGN KEY ... ON DELETE RESTRICT ON UPDATE RESTRICT`，严禁使用级联删除（CASCADE），删除由业务逻辑层基于 `is_deleted` 软删除字段统一调度。
2. **唯一性索引（UK）**：
   - 所有业务编号字段（如 `contract_code`、`entrust_code`、`task_code`、`report_code` 等）强制声明 `UNIQUE KEY`。
3. **检索高频索引（KEY）**：
   - 所有表的 `is_deleted`、`create_time`、`status` 均建立普通索引，匹配 MyBatis-Plus 全局逻辑删除与倒序分页查询需求。
4. **分区表约束与设计**：
   - 依照 MySQL 5.7 约束：**分区表的每个唯一键（包括主键）必须包含分区表达式所引用的列**。
   - 分区表联合主键采用 `(id, create_time)`。
   - 分区表内部不建立物理外键约束（MySQL 5.7 语法限制），通过索引与应用层保证关联完整性。

---

## 3. 分区自动化运维：自动扩展下月分区存储过程与事件调度器

为防止 `sys_audit_log` 月度分区用尽触发 `pmax` 分区导致性能下降，设计如下生产级存储过程与定时事件：

```sql
DELIMITER $$

-- 1. 自动新增下月分区存储过程
CREATE PROCEDURE `sp_auto_create_audit_partition`()
BEGIN
    DECLARE v_next_month_str VARCHAR(10);
    DECLARE v_next_month_end VARCHAR(10);
    DECLARE v_part_name VARCHAR(16);
    DECLARE v_sql VARCHAR(1024);

    -- 计算下下个月的第一天（例如当前是 2024-05，则创建 p202406，其边界为 LESS THAN 2024-07-01）
    SET v_next_month_str = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 MONTH), '%Y%m');
    SET v_next_month_end = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-01');
    SET v_part_name = CONCAT('p', v_next_month_str);

    -- 检查该分区是否已存在
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.partitions
        WHERE table_schema = DATABASE()
          AND table_name = 'sys_audit_log'
          AND partition_name = v_part_name
    ) THEN
        -- 使用 REORGANIZE PARTITION 将 pmax 拆分出新的月度分区
        SET @v_sql = CONCAT(
            'ALTER TABLE `sys_audit_log` REORGANIZE PARTITION pmax INTO (',
            'PARTITION ', v_part_name, ' VALUES LESS THAN (TO_DAYS(''', v_next_month_end, ''')),',
            'PARTITION pmax VALUES LESS THAN MAXVALUE);'
        );
        PREPARE stmt FROM @v_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

-- 2. 每月 25 日凌晨 02:00 自动执行创建
CREATE EVENT IF NOT EXISTS `evt_auto_create_audit_partition`
ON SCHEDULE EVERY 1 MONTH
STARTS DATE_ADD(DATE_ADD(DATE_SUB(CURDATE(), INTERVAL DAY(CURDATE())-1 DAY), INTERVAL 1 MONTH), INTERVAL 2 HOUR)
ON COMPLETION PRESERVE ENABLE
COMMENT '每月自动预建下月审计日志分区'
DO
BEGIN
    CALL sp_auto_create_audit_partition();
END$$

DELIMITER ;
```
