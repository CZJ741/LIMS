# AI 标准化实施 Prompt：业务 App 领域模块开发模板

当你需要为该系统增加或开发一个新的业务 App（如 contract, entrust, sampling, laboratory, report）时，请使用以下标准提示词模板指引 AI：

---

```markdown
你现在需要为环境检测 LIMS 系统开发领域模块：【{模块名称，如：entrust/委托单受理}】。
请严格遵循 SYSTEM_DESIGN_RULES.md 与 COMPLIANCE_AND_AUDIT.md 规范：

1. 模块结构：
   - models.py：所有业务模型继承 common.models.BaseModel。
   - 状态机：引入 django-fsm，定义状态机字段 status = FSMField(protected=True)，通过 @transition 定义流转动作。
   - CMA/CNAS 合规：添加 history = HistoricalRecords() 进行底层变更留痕。
   - services.py：下沉所有业务流转、审核逻辑，统一接入 workflow.services.WorkflowDispatcher。
   - serializers.py：区分列表、详情、表单提交序列化器，字段验证清晰。
   - views.py：遵循单文件 <= 250 行原则，使用 DRF ViewSet / APIView，继承通用权限基类。
   - admin.py：配置 Django Admin 注册，字段列表、筛选器和搜索配置齐全。

2. 状态机规范与驳回逻辑：
   - 必须实现 reject() 状态机方法，接收 operator 与 reason 参数。
   - 驳回理由强制非空。
   - 触发写入 apps.workflow.models.FlowActionLog。

3. 前端配套（lims_web）：
   - api/{module_name}.ts：导出该模块标准的 CRUD 及流转（submit, audit, reject）API。
   - views/{module_name}/index.vue：列表页严格控制在 250 行以内，包含查询表单、表格、分页。
   - views/{module_name}/components/FormDialog.vue：表单录入弹窗。
   - 流转接入：引入 @/components/RejectModal.vue 处理驳回，引入 @/components/WorkflowTimeline.vue 渲染审批历史。
```
