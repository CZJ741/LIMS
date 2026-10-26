# LIMS - 环境检测全生命周期实验室信息管理系统

基于 Django 5.x + DRF + django-fsm + django-simple-history 与 Vite + Vue 3 + TypeScript + Element Plus 构建，符合 CMA/CNAS 资质认定与评审准则要求。

---

## 项目结构 (Project Structure)

```
D:\czj_project\LIMS\
├── prompts/                         # AI 辅助研发标准化系统提示词与工程规范
│   ├── SYSTEM_DESIGN_RULES.md       # 顶层架构与代码组织规范 (防巨石文件)
│   ├── COMPLIANCE_AND_AUDIT.md      # CMA/CNAS 合规留痕与状态机设计标准
│   └── MODULE_IMPLEMENTATION_PROMPT.md # 业务 App 级标准化实施 Prompt 模板
├── lims_server/                     # 后端工程 (Django 5.x + DRF)
│   ├── config/                      # 全局工程配置与路由聚合
│   ├── common/                      # 基础模型、通用权限、统一分页与响应
│   └── apps/
│       ├── authentication/          # 组织架构、扩展用户、职位与 RBAC 权限
│       ├── workflow/                # 统一工作流流转、驳回与全生命周期审计留痕
│       ├── contract/                # 报价与合同评审
│       ├── entrust/                 # 委托单受理与任务下达
│       ├── sampling/                # 现场采样、点位管理与交接
│       ├── laboratory/              # 实验室检测、原始数据录入与公式计算
│       └── report/                  # 报告编制、三级审核与签发归档
└── lims_web/                        # 前端工程 (Vite + Vue 3 + TypeScript + Element Plus)
    ├── src/
    │   ├── api/                     # 统一 Axios 封装与模块化接口
    │   ├── router/                  # 动态路由与权限拦截守卫
    │   ├── stores/                  # Pinia 状态管理 (用户、权限)
    │   ├── layout/                  # 主布局外壳 (Sidebar, Header, Breadcrumb)
    │   ├── components/              # 跨业务通用组件 (RejectModal, WorkflowTimeline)
    │   └── views/                   # 业务页面 (按领域切分，表单独立拆分)
```
