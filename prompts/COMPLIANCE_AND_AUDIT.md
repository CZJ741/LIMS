# AI 标准化系统提示词：CMA/CNAS 合规留痕与状态机设计标准

## 核心背景与准则
根据 CMA（检验检测机构资质认定）与 CNAS（中国合格评定国家认可委员会）准则要求，环境检测数据与流转流程具有法定效力，必须满足“全程留痕、防篡改、可溯源、责任到人”的最高审计标准。

---

## 一、 防篡改留痕规范 (Audit Trail)

1. **底层变更追踪**：
   - 凡涉及检测业务、样品信息、原始记录、判定结果的关键业务 Model（如 `EntrustTask`, `Sample`, `LabResult`, `Report`），必须引入 `django-simple-history`：
     ```python
     from simple_history.models import HistoricalRecords

     class EntrustTask(BaseModel):
         ...
         history = HistoricalRecords()
     ```
   - 记录谁在何年何月何日进行了新增、修改、逻辑删除，自动保留变更前后的字段快照。

2. **操作留痕与签字绑定**：
   - 用户进行提交、审核、批准、修改等关键操作时，必须绑定当前操作人及职位，严禁使用系统无名账户执行合规操作。

---

## 二、 严密的状态机流转规范 (FSM)

1. **杜绝直接赋值 status**：
   - 禁止在业务代码中直接执行 `instance.status = "APPROVED"` 或 `instance.update(status=...)`。
   - 所有业务状态流转必须使用 `django-fsm` 的 `@transition` 装饰器进行显式定义。

2. **状态流转定义标准**：
   ```python
   from django_fsm import FSMField, transition

   class EntrustTask(BaseModel):
       status = FSMField(default='DRAFT', choices=STATUS_CHOICES, protected=True)

       @transition(field=status, source='SUBMITTED', target='AUDITING')
       def start_audit(self, operator):
           pass

       @transition(field=status, source=['SUBMITTED', 'AUDITING'], target='REJECTED')
       def reject(self, operator, reason):
           # 强制触发流转流水记录
           pass
   ```

---

## 三、 通用流转日志记录 (FlowActionLog)

1. **统一审批日志模型**：
   - 每次状态转换（尤其是通过、驳回、回退、加签、终止），必须在 `apps.workflow.models.FlowActionLog` 中写入一条完整的流水日志。
   - 日志包含：
     - `content_type` & `object_id`：支持通用外键关联任意业务实体
     - `action_type`：动作类型（提交、通过、驳回、退回上一级、终止等）
     - `operator` & `operator_role`：操作人员及当时的职位/角色
     - `source_status` & `target_status`：流转前置与后置状态
     - `comment`：审核意见/驳回原因（驳回操作时强制校验 comment 非空）
     - `created_at`：不可篡改的操作时间戳

2. **调度服务统一拦截**：
   - 所有审批动作由 `apps.workflow.services.WorkflowDispatcher` 统一调度，自动完成：
     1. 权限与角色判定
     2. FSM 状态转换校验与执行
     3. 事务内插入 `FlowActionLog`
     4. 触发下游通知/协同任务
