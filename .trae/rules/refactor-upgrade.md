---
alwaysApply: true
scene: refactor_upgrade
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

# Elink-AI 重构升级执行规则

当执行项目重构、框架升级、依赖迁移操作时，必须严格遵循以下规则。

## 1. 【升级路径（不可跳过）】

```
当前状态: Spring Boot 2.3.0 + Java 8
    ↓ PHASE-1: 安全加固 + 低风险修复
过渡状态: Spring Boot 2.3.0 + Java 8（安全基线提升）
    ↓ PHASE-2 Step 2a: 2.3.0 → 2.7.x
过渡状态: Spring Boot 2.7.x + Java 8
    ↓ PHASE-2 Step 2b: Java 8 → Java 17
过渡状态: Spring Boot 2.7.x + Java 17
    ↓ PHASE-2 Step 2c: 2.7.x → 3.x
目标状态: Spring Boot 3.3.x + Java 17 + Spring Cloud 2023.x
```

**禁止直接从 2.3.0 跳到 3.x**，必须经过 2.7.x 过渡。

## 2. 【版本锁定矩阵】

| 组件 | 当前版本 | 目标版本 | 不可跳至 |
|------|---------|---------|---------|
| Java | 8 | 17 | 21（需等3.x稳定） |
| Spring Boot | 2.3.0 | 3.3.x | - |
| Spring Cloud | Hoxton.SR1 | 2023.0.x | - |
| Spring Cloud Alibaba | - | 2023.0.3.2 | - |
| Fastjson | 1.2.0 | fastjson2 2.0.52 | - |
| Swagger | 2.9.2 | springdoc-openapi 2.2.0 | 3.x |
| Redisson | 3.11.3 | 3.36.0 | - |
| OSS SDK | 2.8.3 | 3.17.4 | - |
| Nacos Client | - | 对应SCA 2023.0.3.2 | - |

## 3. 【命名空间迁移清单】

javax → jakarta 迁移（487处/255文件），必须全部替换：

| 原命名空间 | 目标命名空间 | 影响范围 |
|-----------|-------------|---------|
| javax.persistence.* | jakarta.persistence.* | 402处/100文件 |
| javax.annotation.* | jakarta.annotation.* | 44处/42文件 |
| javax.websocket.* | jakarta.websocket.* | 21处/8文件 |
| javax.servlet.* | jakarta.servlet.* | 17处/15文件 |
| javax.validation.* | jakarta.validation.* | 3处/3文件 |
| javax.transaction.* | jakarta.transaction.* | 0处 |

**推荐工具**：OpenRewrite 自动迁移
```bash
mvn org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.activeRecipes=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_3
```

## 4. 【Swagger 注解迁移映射】

19193处注解分布在910个文件中，需替换：

| Swagger 2 | SpringDoc OpenAPI | 影响数量 |
|-----------|-------------------|---------|
| @Api | @Tag | 121处 |
| @ApiOperation | @Operation | 2365处 |
| @ApiParam | @Parameter | 0处 |
| @ApiModel | @Schema | 8093处 |
| @ApiModelProperty | @Schema | 7361处 |
| @ApiImplicitParam | @Parameter | 988处 |
| @ApiImplicitParams | @Parameter | 265处 |

## 5. 【OAuth2 迁移要点】

spring-cloud-starter-oauth2 已废弃，需迁移至：
- 授权服务器：spring-authorization-server
- 资源服务器：spring-boot-starter-oauth2-resource-server

重点改造类（auth-service）：
- AuthorizationServerConfigurer → AuthorizationServer
- ResourceServerConfigurer → ResourceServer
- SMTokenService（实现4个接口，需逐个适配）

涉及文件：
- elink-work/pom.xml
- sunmax-common/pom.xml
- auth-service 全部安全配置

## 6. 【前端迁移清单】

### 构建工具迁移
| 项目 | 当前 | 目标 |
|------|------|------|
| linkos | Vue CLI 5 (webpack) | Vite 6 |
| tycvs | Vue CLI 5 (webpack) | Vite 6 |
| derms | Vite 6 | 已完成 |

### 状态管理迁移
| 项目 | 当前 | 目标 |
|------|------|------|
| linkos | Vuex 4 | Pinia |
| tycvs | Vuex 4 | Pinia |
| derms | Vuex 4 | Pinia |

### 硬编码IP清除
| 文件 | 硬编码内容 | 修复方式 |
|------|-----------|---------|
| linkos/src/utils/request.js:13 | 47.110.235.112:21010 | 使用环境变量 |
| linkos/src/api/websocket/webSocket.js:4 | ws://192.168.2.158:5000 | 使用环境变量 |
| derms/src/api/websocket/webSocket.js:12 | ws://47.110.235.112:21010 | 使用环境变量 |
| tycvs/src/utils/requestPath.js:5 | 192.168.2.158:5000 | 使用环境变量 |
| linkos/vue.config.js:47 | 192.168.2.158:5000 | 使用环境变量 |
| tycvs/vue.config.js:56 | 192.168.2.158:5000 | 使用环境变量 |

## 7. 【编译验证检查点】

每个 PHASE 完成后必须执行：
```bash
cd /work/elink-ai/elink-work
mvn clean compile -DskipTests
```

每个 Step 完成后必须执行：
```bash
mvn clean package -pl <service> -am -DskipTests
```

## 8. 【回滚触发条件】

| 条件 | 级别 | 操作 |
|------|------|------|
| 编译失败 | P1 | 回退代码变更，恢复上一版本 |
| 单元测试失败率 >5% | P1 | 分析失败原因，决定回退或修复 |
| 服务启动失败 | P0 | 自动回滚JAR到上一版本 |
| Nacos注册超时(180s) | P0 | 自动回滚 |
| 内存增长 >100MiB | P2 | 记录告警，继续观察 |
| 接口响应时间增长 >30% | P2 | 记录告警，评估回滚 |
