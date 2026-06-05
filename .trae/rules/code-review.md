---
alwaysApply: true
scene: code_review
version: "1.1.0"
lastUpdated: "2026-06-03"
changelog:
  - version: "1.0.0"
    date: "2026-06-03"
    changes: "初始版本"
  - version: "1.1.0"
    date: "2026-06-03"
    changes: "集成至智能体系统，添加场景元数据和迭代追踪"
---

# Elink-AI 项目代码审查规则

当执行代码审查、安全扫描或质量检查任务时，必须严格遵循以下规则。

## 1. 【审查范围与优先级】

### P0 - 安全漏洞（必须立即修复）
- 硬编码IP地址/密钥/凭证（47.110.235.112、192.168.2.158、1.95.55.247）
- CORS allowed-origins 配置为 "*"
- OAuth2 client-secret 硬编码（sunos-client）
- configure-service 中平台密钥硬编码（MACQKWDXI、JbhI7olOTAKs2ZNU等）
- Fastjson 1.2.0 反序列化漏洞
- .env 文件泄露风险

### P1 - 性能瓶颈（必须修复）
- auth-service HikariCP maximum-pool-size: 1000
- Gateway connect-timeout: 600000ms（10分钟）
- Hystrix timeout.enabled: false（全局禁用超时）
- Redis timeout: 60s（过长）
- JPA ddl-auto: update（生产环境危险）

### P2 - 架构缺陷（计划修复）
- javax.* 命名空间需迁移至 jakarta.*（317处/120文件）
- Swagger 2 注解需迁移至 SpringDoc（1335处/100文件）
- groupId: org.example 不符合生产规范
- Jackson 2.18.0 手动版本与 Spring Boot BOM 冲突
- org.jetbrains:annotations:RELEASE 动态版本号

## 2. 【审查检查清单】

### 后端审查
- [ ] pom.xml 依赖版本是否与 REFACTOR_PLAN.md 目标版本一致
- [ ] application.yml 中是否仍有硬编码IP或密钥
- [ ] JPA ddl-auto 是否为 update（应为 validate）
- [ ] HikariCP 连接池大小是否合理（建议10-50）
- [ ] Redis/Lettuce 连接池参数是否合理
- [ ] OAuth2 配置是否使用环境变量
- [ ] 异常捕获是否过于宽泛（catch(Exception)）
- [ ] Swagger 注解是否已迁移至 SpringDoc
- [ ] javax.* import 是否已迁移至 jakarta.*
- [ ] Fastjson import 是否已替换

### 前端审查
- [ ] 是否存在硬编码IP地址
- [ ] Vuex 是否已迁移至 Pinia
- [ ] Vue CLI 项目是否已迁移至 Vite
- [ ] 生产构建是否关闭 sourceMap
- [ ] 依赖版本是否存在已知漏洞
- [ ] WebSocket 连接是否使用环境变量
- [ ] axios 请求拦截器是否正确处理 token 刷新

### Docker 审查
- [ ] 容器是否设置内存/CPU限制（当前缺失）
- [ ] JAR 文件名是否正确（crontab-service 拼写）
- [ ] 健康检查配置是否合理
- [ ] 网络隔离是否合理
- [ ] 日志驱动是否配置
- [ ] 安全相关端口是否暴露

## 3. 【审查输出格式】

```
### 审查结果：[服务名/模块名]
| 级别 | 编号 | 问题描述 | 文件位置 | 修复建议 |
|------|------|----------|----------|----------|
| P0   | SEC-01 | ... | file:line | ... |
```

## 4. 【服务清单与端口映射】

| 服务 | 端口 | Nacos名 | 上下文路径 |
|------|------|---------|-----------|
| auth-service | 60001 | sauth-service | /sauth |
| sunmax-gateway | 5000 | sunmax-gateway | / |
| system-service | 60002 | system-service | /system |
| device-service | 60003 | device-service | /device |
| data-service | 60004 | sunos-data-service | /data |
| protocol-service | 60005 | sunos-protocol-service | /protocol |
| crontab-service | 60006 | scrontab-service | /scrontab |
| devops-service | 60007 | devops-service | /devops |
| configure-service | 60008 | configure-service | /configure |
| together-service | 60009 | together-service | /together |
| webapp-service | 60010 | swebapp-service | /swebapp |

## 5. 【数据库清单】

| 数据库 | 使用服务 |
|--------|---------|
| sunos-system | auth-service, system-service |
| sunos-operate | protocol-service, together-service, devops-service, webapp-service |
| sunos-data | data-service |
| sunos-model | device-service |
| sunos-access | device-service, crontab-service |
| sunos-log | system-service, together-service |
| sunos-configure | configure-service |
| sunosdbnode | crontab-service(TDengine) |
| sunosdata | data-service(TDengine) |
