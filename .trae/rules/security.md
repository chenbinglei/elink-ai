---
alwaysApply: true
scene: security
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

# Elink-AI 安全规范规则

当执行任何代码修改、配置变更、部署操作时，必须遵循以下安全规范。

## 1. 【凭证管理】

### 禁止硬编码
以下内容禁止出现在源代码中，必须使用环境变量：
- IP地址（公网/内网）
- 数据库密码
- Redis密码
- OAuth2 client-secret
- API密钥（阿里云OSS/SMS/平台对接密钥）
- 微信支付密钥
- 邮箱授权码
- MQTT用户名/密码

### 环境变量管理
- 生产凭证存储在 `.env` 文件中（已加入 .gitignore）
- `.env.example` 仅包含占位符，不包含真实值
- Docker Compose 通过 `env_file` 注入环境变量

### 当前硬编码清单（必须修复）

| 位置 | 内容 | 优先级 |
|------|------|--------|
| linkos/src/utils/request.js:13 | 47.110.235.112:21010 | P0 |
| derms/src/api/websocket/webSocket.js:12 | ws://47.110.235.112:21010 | P0 |
| tycvs/src/utils/requestPath.js:5 | 192.168.2.158:5000 | P0 |
| linkos/src/api/websocket/webSocket.js:4 | ws://192.168.2.158:5000 | P0 |
| configure-service/application.yml:104 | http://1.95.55.247/... | P0 |
| configure-service/application.yml:98-101 | 平台密钥(MACQKWDXI等) | P0 |
| 各服务 application.yml | OAuth2 client-secret: sunos-client | P1 |

## 2. 【网络安全】

### CORS策略
- 生产环境禁止 `allowed-origins: "*"`
- 必须配置具体允许的域名列表
- 当前违规位置：sunmax-gateway/application.yml

### 端口暴露
- 仅暴露必要端口（网关5000、各服务业务端口）
- Redis 6379 不应暴露到公网
- EMQX管理端口 18083/28083 不应暴露到公网
- Nacos 8848 不应暴露到公网

### 数据库连接
- 禁止 useSSL=false（生产环境必须启用SSL）
- 禁止 autoReconnect=true（HikariCP已处理连接管理）

## 3. 【依赖安全】

### 已知漏洞依赖
| 依赖 | 当前版本 | 风险 | 修复方案 |
|------|---------|------|---------|
| fastjson | 1.2.0 | 反序列化RCE | 升级至fastjson2 2.0.52 |
| springfox-swagger2 | 2.9.2 | SSRF/XSS | 迁移至springdoc-openapi |
| org.jetbrains:annotations | RELEASE | 供应链风险 | 固定为24.0.1 |

## 4. 【数据安全】

### JPA ddl-auto
- 生产环境必须设为 `validate`
- 当前所有10个业务服务均为 `update`（高危）
- 数据库变更必须通过 Flyway/Liquibase 迁移脚本管理

### 日志安全
- 禁止在日志中输出敏感信息（密码、token、密钥）
- JPA show-sql 当前为 false（正确）
- hibernate.generate_statistics 部分服务为 true（生产应关闭）

## 5. 【容器安全】

### Docker安全基线
- 使用非root用户运行应用（当前为root）
- 只读挂载JAR目录（当前已配置 :ro）
- 设置内存/CPU限制（当前缺失）
- 禁用特权模式
- 配置日志驱动和日志轮转
