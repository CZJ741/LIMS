# HANDOFF.md - LIMS 环境检测实验室信息管理系统上下文交接

## 1. 架构总览与合规基线
- **后端架构**：Django 5.x + DRF + django-fsm + django-simple-history + SimpleJWT。
  - 核心基类：`common.models.BaseModel`（UUID主键、逻辑删除、创建/修改时间）。
  - 合规审计留痕：`apps.workflow.models.FlowActionLog` 统一记录审批流转、驳回原因与操作人快照。
  - 领域划分：`authentication`, `workflow`, `contract`, `entrust`, `sampling`, `laboratory`, `report`。
- **前端架构**：Vite 5 + Vue 3 + TypeScript + Element Plus + Pinia。
  - 通用流转组件：`@/components/RejectModal.vue`（通用驳回弹窗），`@/components/WorkflowTimeline.vue`（通用审批流时间轴）。
  - 单文件规范：单 `.vue` 文件严控在 100~250 行。

## 2. 标准化 Prompts 资产 (位于 /prompts/)
- `SYSTEM_DESIGN_RULES.md`: 顶层架构规范与文件行数约束（杜绝巨石文件）。
- `COMPLIANCE_AND_AUDIT.md`: CMA/CNAS 合规留痕与状态机设计标准。
- `MODULE_IMPLEMENTATION_PROMPT.md`: 业务 App 级实施标准 Prompt 模板。

## 3. 当前已完成工作
- 后端基础工程骨架、通用公共模块（BaseModel、响应封装、分页）、认证授权（User/Role/Department）、工作流引擎（FlowActionLog, WorkflowDispatcher, FlowActionLogListView）。
- 示范性业务模型：`apps.entrust.models.EntrustTask`（继承 BaseModel，挂载 django-fsm 状态机与 django-simple-history）。
- 前端工程骨架、Layout 布局、Axios 拦截器、通用 RejectModal 与 WorkflowTimeline 组件、Entrust 委托单受理示范页面。
