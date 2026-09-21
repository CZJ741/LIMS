# LIMS 实验室信息管理系统

## 1. 项目简介
本项目是一套对齐 **GB/T 27025-2019** 与 **ISO/IEC 17025** 国际标准的数字化实验室全流程管理系统，涵盖检测实验室、环境监测机构、第三方检测公司等专业场景。

### 核心特性
- **六大业务阶段全闭环**：合同登记与审核、委托下单、采样管理、检测分析与复审、报告编制与签发、财务结算。
- **19 种专业职位体系**：从管理层、质量负责人、采样/检测/报告专员到设备耗材管理员全角色内置。
- **三维度 RBAC 授权**：按岗位（Position）、人员（User）、项目组（Group）进行细粒度权限控制。
- **设备数据接入层**：支持 API/工作站软件直连、CSV、PDF 图谱、TXT、串口 (RS232/RS485) 5 种直连采集协议（适配器 + 工厂模式）。
- **技术栈架构**：Spring Boot 3.2.x + JDK 17 + Flowable 7 + Sa-Token + MyBatis-Plus + Vue 3.5 + Element Plus 2.8。

---

## 2. 模块结构

```text
lims-parent
├── lims-common       （公共工具、常量、异常处理、Result/Page模型）
├── lims-framework    （Web、MyBatis-Plus、Redis/Redisson、Sa-Token 自动装配）
├── lims-system       （系统管理：组织/用户/角色/权限/字典/日志）
├── lims-contract     （合同管理）
├── lims-entrust      （委托管理）
├── lims-sampling     （采样管理）
├── lims-detection    （检测管理）
├── lims-report       （报告管理）
├── lims-finance      （财务管理）
├── lims-device       （设备管理 + 5 种直连采集适配层）
├── lims-file         （MinIO 文件存储管理）
├── lims-app          （主启动模块，聚合全部业务模块）
└── lims-web          （Vue 3 + Vite + TypeScript 前端工程）
```

---

## 3. 本地启动顺序

### 第一步：启动基础中间件（Docker Compose）
在项目根目录下执行以下命令，一键启动 MySQL 5.7、Redis 7、MinIO 以及 Nginx：
```bash
docker-compose up -d lims-mysql lims-redis lims-minio
```
- **MySQL 5.7**：端口 3306，字符集 `utf8mb4 / utf8mb4_general_ci`，用户名 `root`，密码 `rootpassword`（或业务用户 `lims_user / lims_staging_pwd`）
- **Redis 7**：端口 6379，无密码（本地 dev 环境直连）
- **MinIO**：API 端口 9000，控制台端口 9001，账号密码 `minioadmin / minioadmin`

### 第二步：编译并启动后端服务（Spring Boot）
在项目根目录下编译安装，并运行主入口：
```bash
# 1. 编译并安装到本地 Maven 仓库
mvn clean install -DskipTests

# 2. 启动后端 Spring Boot 应用
mvn --projects lims-app spring-boot:run
```
- 后端服务端口：`8080`
- Swagger / OpenAPI 文档地址：`http://localhost:8080/swagger-ui.html`

### 第三步：启动前端项目（Vue 3 Web）
进入前端工程目录安装依赖并启动：
```bash
cd lims-web
npm install
npm run dev
```
- 前端开发服务器地址：`http://localhost:5173`
- 浏览器打开后自动进入系统登录页

---

## 4. 默认账号与权限
- **超级管理员账号**：`admin`
- **默认密码**：`123456`
- **权限范围**：拥有超级管理员角色 `SUPER_ADMIN`，通配权限 `*:*:*`

---

## 5. 常见问题排查 (FAQ)

1. **MySQL 编码或排序规则错误**：
   - 确保 MySQL 配置文件显式声明了：
     ```ini
     character-set-server = utf8mb4
     collation-server = utf8mb4_general_ci
     ```
   - 确认连接串中包含 `useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai`。

2. **Flowable 表未初始化报错**：
   - 生产环境中 `flowable.database-schema-update` 设为 `false`。
   - 在首次启动前需导入 Flowable 7.0 官方对应 MySQL 的初始化 DDL 脚本。

3. **前端反向代理 502 / 无法连接后端**：
   - 请检查 `lims-web/vite.config.ts` 中的 `/api` 代理目标是否指向正在运行的后端（默认 `http://localhost:8080`）。
   - 检查跨域拦截与 OPTIONS 预检请求是否已被 `lims-framework` 的 `WebAutoConfiguration` 放行。
