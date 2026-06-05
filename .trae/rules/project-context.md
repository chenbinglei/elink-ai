---
alwaysApply: true
scene: project_context
version: "1.2.0"
lastUpdated: "2026-06-03"
changelog:
  - version: "1.0.0"
    date: "2026-06-03"
    changes: "初始版本"
  - version: "1.1.0"
    date: "2026-06-03"
    changes: "集成至智能体系统，添加场景元数据和迭代追踪"
  - version: "1.2.0"
    date: "2026-06-03"
    changes: "更新P1-T1/P1-T2完成状态，新增文档更新机制引用"
---

# Elink-AI 项目上下文记忆规则

本文件作为智能体的长期记忆，记录项目架构、技术栈、关键决策和当前状态。

## 1. 【项目概况】

- **项目名称**：Elink-AI（物联网设备管理平台）
- **技术栈**：Spring Boot 2.3.0 + Vue 3 + Docker
- **代码仓库**：/work/elink-ai
- **后端目录**：/work/elink-ai/elink-work（Maven多模块）
- **前端目录**：/work/elink-ai/elink-web（3个独立Vue项目）
- **重构方案**：REFACTOR_PLAN.md v1.2 + REFACTOR_EXECUTE.md v1.3
- **进度报告**：PROGRESS_REPORT.md v1.0
- **文档规范**：DOC_UPDATE_GUIDELINES.md v1.0

## 2. 【后端架构】

### 模块结构
```
elink-work/
├── pom.xml                    # 父POM，管理全局依赖
├── sunmax-common/             # 公共模块（Redisson、OAuth2、工具类）
├── auth-service/              # 认证服务 (60001, /sauth)
├── sunmax-gateway/            # API网关 (5000)
├── system-service/            # 系统管理 (60002, /system)
├── device-service/            # 设备管理 (60003, /device)
├── data-service/              # 数据服务 (60004, /data) + TDengine
├── protocol-service/          # 协议服务 (60005, /protocol) + MQTT
├── crontab-service/           # 定时任务 (60006, /scrontab) + TDengine
├── devops-service/            # 运维服务 (60007, /devops)
├── configure-service/         # 配置服务 (60008, /configure) + MQTT
├── together-service/          # 业务聚合 (60009, /together)
└── webapp-service/            # Web应用 (60010, /swebapp)
```

### 关键依赖
- Spring Boot 2.3.0.RELEASE
- Spring Cloud Hoxton.SR1
- ~~Fastjson 1.2.0（全局150处引用）~~ ✅ 已替换为 fastjson2 2.0.52
- Swagger 2.9.2（1335处注解）
- Redisson 3.11.3
- OSS SDK 2.8.3
- spring-cloud-starter-oauth2（已废弃）
- Jackson 2.18.0（手动版本）
- org.jetbrains:annotations:RELEASE（动态版本）

### 数据库
- MySQL：8个业务数据库
- TDengine：2个时序数据库（data-service、crontab-service）
- Redis：全部11个服务共用，database=1

### 消息中间件
- EMQX 5.1.0：MQTT Broker（双实例，未集群化）
- protocol-service：web(1883) + inter(2883)
- configure-service：web(1883)

## 3. 【前端架构】

### 项目结构
```
elink-web/
├── linkos/    # 设备管理平台 (Vue CLI 5 + webpack, port:9000)
├── derms/     # 运营管理平台 (Vite 6, port:9001)
└── tycvs/     # 可视化组态平台 (Vue CLI 5 + webpack, port:9002)
```

### 前端共同问题
- 全部使用 Vuex 4（需迁移至 Pinia）
- linkos/tycvs 使用 Vue CLI（需迁移至 Vite）
- 多处硬编码IP地址
- 生产构建关闭了 sourceMap（正确）

## 4. 【Docker部署】

### 基础设施
- Redis 7-alpine（容器内）
- Nacos v2.1.0 standalone（容器内，无持久化）
- EMQX 5.1.0 双实例（容器内，未集群化）

### 应用容器
- 基础镜像：openjdk:8-jre + curl + libjemalloc2
- 统一入口：docker-entrypoint.sh（支持Nacos服务等待）
- 热更新：hot-reload.sh v2.0（958行，支持自动回滚）

## 5. 【重构进度追踪】

### 已完成
- [x] 项目全面审计（安全、架构、技术债务）
- [x] 重构方案文档 v1.2
- [x] 执行手册 v1.3
- [x] 智能体规则体系建立
- [x] P1-T1：Fastjson 1.2.0 → fastjson2 2.0.52（2026-06-03完成）
- [x] P1-T2：CORS策略收紧，通配符→3个业务域名白名单（2026-06-03完成）

### 当前执行
- PHASE-1 安全加固与紧急修复（执行中，完成率33%）

### 待执行（按优先级）
1. P1-T3~T9：PHASE-1剩余安全修复任务
2. PHASE-2：框架升级（2.3→2.7→Java17→3.x）
3. PHASE-3：代码质量优化
4. PHASE-4：前端现代化
5. PHASE-5：构建部署与持续优化

## 6. 【文档更新机制】

> 详细规范参见 [DOC_UPDATE_GUIDELINES.md](file:///work/elink-ai/DOC_UPDATE_GUIDELINES.md)

### 核心规则

1. **任务完成后必须更新以下4份文档**：
   - `REFACTOR_TASKS.md`：标记状态+完成时间+更新进度总览
   - `REFACTOR_EXECUTE.md`：新增执行记录（过程/问题/方案/验证）
   - `PROGRESS_REPORT.md`：更新进度+已完成详情+修改记录
   - `REFACTOR_PLAN.md`：更新进度总览+任务状态+审计标记

2. **任务失败/回滚时必须记录**：
   - 失败原因和错误信息
   - 回滚操作详情
   - 后续修复计划

3. **版本号递增规则**：
   - 任务完成 → 次版本+1（如 v1.2 → v1.3）
   - 阶段变更 → 主版本+1（如 v1.x → v2.0）

4. **数据一致性**：
   - 进度报告完成率 = 任务清单已完成数/总数
   - 审计标记 = 实际已完成的SEC编号
   - 执行记录数 = 已完成任务数

5. **禁止行为**：
   - 未验证通过不得标记为已完成
   - 不得回退已完成任务状态

## 7. 【关键决策记录】

| 日期 | 决策 | 原因 |
|------|------|------|
| 2026-06-03 | Fastjson迁移至fastjson2而非Jackson | 兼容性更好，API改动最小 |
| 2026-06-03 | 渐进式升级而非一步到位 | 降低风险，每步可验证可回滚 |
| 2026-06-03 | OAuth2迁移至spring-authorization-server | Spring官方推荐方案 |
| 2026-06-03 | 前端Vue CLI迁移至Vite | 构建速度提升10x+ |
| 2026-06-03 | 使用OpenRewrite自动迁移javax→jakarta | 317处手动替换风险高 |
| 2026-06-03 | 建立文档更新机制规范 | 确保重构过程可追溯、文档状态准确 |
