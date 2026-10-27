# HANDOFF.md - LIMS 环境检测实验室信息管理系统上下文交接

## 1. 架构总览与合规基线
- **后端架构**：Django 5.x + DRF + django-fsm + django-simple-history + SimpleJWT。
  - 核心基类：`common.models.BaseModel`（UUID主键、逻辑删除、创建/修改时间留痕）。
  - 合规审计留痕：`apps.workflow.models.FlowActionLog` 统一记录审批流转、驳回原因与操作人快照。
  - 全局异常拦截：`common.exceptions.custom_exception_handler`，递归解析 DRF ValidationError 并提取清晰具体的中文报错（避免笼统掩盖为网络错误）。
  - 领域划分：`authentication`, `workflow`, `contract`, `entrust`, `sampling`, `laboratory`, `report`。
- **前端架构**：Vite 5 + Vue 3 + TypeScript + Element Plus + Pinia。
  - 通用流转组件：`@/components/RejectModal.vue`（通用驳回弹窗），`@/components/WorkflowTimeline.vue`（通用审批流时间轴）。
  - 单文件规范：遵循单 `.vue` 文件紧凑设计，表单子组件独立抽离至 `components/` 目录。
  - 错误与表单规范：表单配置 `:show-message="false"` 与 `scroll-to-error`，由顶部全局弹窗汇总未填字段并自动聚焦定位；Axios 拦截器深度递归提取后端具体业务错误。

---

## 2. 标准化 Prompts 资产 (位于 /prompts/ 及项目根目录)
- `CLAUDE.md`: 项目特有指令，包含 CMA/CNAS 合规原则、UI 规范、前后端异常透传规范、密集表单校验体验规范。
- `SYSTEM_DESIGN_RULES.md`: 顶层架构规范与文件行数约束（杜绝巨石文件），固化表单与报错直观透传准则。
- `COMPLIANCE_AND_AUDIT.md`: CMA/CNAS 合规留痕与状态机设计标准。
- `MODULE_IMPLEMENTATION_PROMPT.md`: 业务 App 级实施标准 Prompt 模板。
- `备忘.txt`: 人员与角色物理删除机制的合规风险提示及后续上线前彻底废除硬删除的改造待办说明。

---

## 3. 当前已完成工作（最新更新：2026-10-27）

### 3.1 组织管理与权限认证模块 (Authentication)
1. **角色管理 (`Role`)**：
   - 模型支持 `is_active`（启用/停用）与 `menu_permissions`（JSON 格式菜单权限键列表），已执行并应用数据库迁移 `0004_role_is_active` 与 `0005_role_menu_permissions`；
   - 自动编码支持：未填角色编码时自动从纯数字段（10001 起）安全自增生成；
   - 权限抽屉（`RolePermissionDrawer.vue`）：权限树与系统当前真实侧边栏 1:1 严格对齐（移除了未上线的虚构业务流节点），支持勾选后通过 `PATCH` 局部更新真实持久化至数据库；
   - 超级管理员核心角色 `ADMIN` 受系统底层安全防护，严禁删除、停用，且在前端操作列中隐藏禁用类操作按钮。
2. **人员管理 (`User` / `user-org.vue`)**：
   - 界面布局对标 UI 规范基准（浅灰检索面板、青绿色工具栏、徽章式微型行内操作按钮、单行绝对不折行）；
   - 表格列展示优化：新增“登录账号”（`username` 徽章）、“所任职务”（`roles_info` 标签组）、“最近登录”（替换原邮件列，显示格式化时间或“从未登录”），邮件列移至详情弹窗内；
   - 查看与编辑闭环：支持列表点击【查看】打开详情弹窗，并在弹窗右下角点击【编辑信息】平滑解锁切换为编辑态并提交保存（编码锁定只读防篡改）；
   - 停用/启用功能：根据 `is_active` 状态动态渲染切换按钮，点击具备确认提示框；
   - 重置密码功能：操作栏配备【重置密码】微型按钮，系统全量默认密码统一设定为 `123456`，重置后给出明确弹窗提示；
   - 人员定职功能：新增 `UserRoleAssignDialog.vue` 子组件，支持为人员从已启用的角色列表中多选授予岗位职务。
3. **多执业身份切换与侧边栏权限动态联动**：
   - 针对 CMA/CNAS / ISO 17025 职责分离与多职务兼任场景，实现了【方案 A：全局工作身份切换】；
   - 页面顶部右侧展示当前执业身份徽章下拉框（如 `当前执业身份: 现场采样组长 ▾`）；
   - 兼任多职务的人员可随时一键切换当前身份，侧边栏菜单与可访问功能即刻根据新角色的 `menu_permissions` 动态增减过滤，并具备无权路由自动重定向保护。

### 3.2 基础服务与业务骨架
- 后端基础工程骨架、通用公共模块（BaseModel、响应封装、分页、全局异常处理器）、工作流引擎（FlowActionLog, WorkflowDispatcher）。
- 示范性业务模型：`apps.entrust.models.EntrustTask`（继承 BaseModel，挂载 django-fsm 状态机与 django-simple-history）。
- 前端通用 RejectModal、WorkflowTimeline 组件及 Entrust 委托单受理示范页面。

---

## 4. 下一步待办规划 (Next Steps)
1. **业务流模块侧边栏正式上线与串联**：
   - 合同评审 (`apps/contract`)、业务委托受理 (`apps/entrust`)、现场采样与交接 (`apps/sampling`)、实验室分析检测 (`apps/laboratory`)、检测报告签发 (`apps/report`) 的完整路由与侧边栏挂载；
   - 将业务大类菜单同步纳入角色菜单权限配置树中。
2. **流转动作中的职务身份绑定**：
   - 在关键工作流节点（采样人签字、分析人出具原始记录、复核人审核、授权签字人批准）流转时，自动固化绑定操作人当前的激活职务快照至 `FlowActionLog`，并增加职责互斥防范（如同一检测人禁止复核自己出具的数据）。
3. **废除物理删除（上线前硬性合规要求）**：
   - 参照 `备忘.txt`，将人员和角色的“删除”操作从界面彻底移除，转为受控的停用或业务安全归档流程。
