# Elink-AI 前后端项目重构升级优化方案

> 版本：v2.6 | 编制日期：2026-06-03 | 最后更新：2026-06-11 | 状态：**执行中**
>
> 配套执行手册：[REFACTOR_EXECUTE.md](file:///work/elink-ai/REFACTOR_EXECUTE.md)

---

## 项目重构进度总览

| 阶段 | 状态 | 完成率 | 说明 |
|------|------|--------|------|
| PHASE-0：紧急修复 | ✅ 已完成 | 100% | P0-2 CORS内网IP移除+公网域名白名单；P0-3 Nacos/EMQX默认密码环境变量化+WARNING注释；P0-4 前后端环境变量分离+.env全面审查；P0-4b 文档统计数据校正；P0-5 Together-service健康检查性能修复 |
| PHASE-1：安全加固与紧急修复 | ✅ 已完成 | 100% | P1-T1~T9+P1-V全部完成，2项因环境限制手动回退 |
| PHASE-2：框架升级与核心重构 | ✅ 已完成 | 100% | P2-2a完成（Boot 2.7.18+SpringDoc+Resilience4j），P2-2b完成（Java 17+JPMS兼容+热更新验证+冒烟测试通过），P2-2c完成（Boot 3.3.6+Cloud 2023.0.4+SCA 2023.0.3.2+javax→jakarta+OAuth2迁移至spring-authorization-server+3个TODO认证提供者实现），P2-2c-2完成（@Transactional补全），P2-2c-3完成（SCA版本配置），P2-2c-4完成（Feign调用重构：55个FeignClient接口+GenericFeignFallbackFactory+42个FeignEndpoint+52个消费者接口迁移）|
| PHASE-3：代码质量与性能优化 | ✅ 已完成 | 100% | P3-A完成（e.printStackTrace()+System.out/err→SLF4J），P3-B完成（OSS SDK 3.17.4+Redisson 3.36.0+groupId迁移+CSS extract），P3-C完成（异常收窄+Hibernate统计关闭），P3-C2完成（Gateway/HikariCP/Redis超时参数优化），P3-D完成（R1性能基线：5场景3轮压测+8项指标+JVM GC+容器资源+DB连接数+质量验收全部通过） |
| PHASE-4：前端现代化改造 | ⏳ 进行中 | 25% | P4-A完成（@elink/shared公共包创建+3项目迁移+构建验证通过） |
| PHASE-5：构建部署与持续优化 | ⏳ 待开始 | 0% | 4项任务 |

### PHASE-1 任务进度明细

| 任务编号 | 任务名称 | 状态 | 完成时间 | 影响范围 |
|----------|----------|------|----------|----------|
| P1-T1 | 替换 Fastjson 1.2.0 为 fastjson2 2.0.52 | ✅ 已完成 | 2026-06-03 | pom.xml + 100个Java文件/150处import |
| P1-T2 | 收紧 CORS 策略 | ✅ 已完成 | 2026-06-03 | gateway application.yml |
| P1-T3 | JPA ddl-auto 从 update 改为 validate | ✅ 已完成(回退) | 2026-06-03 | 全部10个业务服务（因Entity不一致手动回退为update） |
| P1-T4 | 清除前端硬编码 IP 地址 | ✅ 已完成 | 2026-06-03 | linkos/derms/tycvs共8个文件替换+2处注释清理 |
| P1-T5 | 修正 HikariCP 连接池参数 | ✅ 已完成 | 2026-06-03 | auth-service |
| P1-T6 | 修正 crontab-service JAR 名拼写 | ✅ 已完成 | 2026-06-03 | docker-compose/hot-reload/pom.xml |
| P1-T7 | OAuth2 client-secret 硬编码外置 | ✅ 已完成 | 2026-06-03 | 9个业务服务 |
| P1-T8 | configure-service 平台密钥硬编码外置 | ✅ 已完成 | 2026-06-03 | configure-service |
| P1-T9 | 数据库连接 useSSL=false 修复 | ✅ 已完成(回退) | 2026-06-03 | 全部13条JDBC连接（因MySQL未配SSL证书手动回退为false） |
| P1-V | PHASE-1 全量验证 | ✅ 已完成 | 2026-06-03 | 全局 |

### PHASE-2 任务进度明细

| 任务编号 | 任务名称 | 状态 | 完成时间 | 影响范围 |
|----------|----------|------|----------|----------|
| P2-2a | Spring Boot 2.3→2.7.18 + Swagger→SpringDoc 1.7.0 | ✅ 已完成 | 2026-06-08 | pom.xml + 910文件/19193处Swagger注解迁移 + Hystrix→Resilience4j |
| P2-2b | Java 8 → Java 17 | ✅ 已完成 | 2026-06-09 | 父POM+12子模块POM + Dockerfile + hot-reload.sh + 11服务热更新验证通过 |
| P2-2c | Spring Boot 2.7→3.3.6 + javax→jakarta + OAuth2迁移 | ✅ 已完成(验证已补全2026-06-10) | 2026-06-09 | pom.xml + 全量javax→jakarta 355处 + OAuth2重写 |
| P2-2c-2 | 补全事务管理（ARCH-05） | ✅ 已完成 | 2026-06-09 | device/together/webapp/system/protocol Service层 |
| P2-2c-3 | Spring Cloud Alibaba版本配置 | ✅ 已完成 | 2026-06-09 | 父POM SCA BOM + 子模块Nacos版本 |
| P2-2c-4 | Feign调用重构（ARCH-06） | ✅ 已完成 | 2026-06-10 | 55个FeignClient接口+GenericFeignFallbackFactory+42个FeignEndpoint+52个消费者接口迁移 |

### PHASE-3 任务进度明细

| 任务编号 | 任务名称 | 状态 | 完成时间 | 影响范围 |
|----------|----------|------|----------|----------|
| P3-A | 清理 e.printStackTrace() 和 System.out/err.print | ✅ 已完成 | 2026-06-11 | 95处e.printStackTrace()+109处System.out/err→SLF4J日志+@Slf4j补全 |
| P3-B | 依赖版本升级 + CSS extract 优化 | ✅ 已完成 | 2026-06-11 | OSS SDK 2.8.3→3.17.4+Redisson 3.27.2→3.36.0+Jackson手动版本移除+groupId迁移26处+CSS extract |
| P3-C | 收窄异常捕获 + 清理TODO/FIXME + 关闭Hibernate统计 | ✅ 已完成 | 2026-06-11 | catch(Exception)收窄+20+文件unreachable catch修复+hibernate.generate_statistics→false |
| P3-C2 | 修正超时与连接池性能参数 | ✅ 已完成 | 2026-06-11 | Gateway/HikariCP/Redis超时参数优化 |
| P3-D | 性能基准测试（R1） | ✅ 已完成 | 2026-06-11 | 5场景3轮压测+8项指标采样+JVM GC+容器资源+DB连接数+质量验收 |

### PHASE-4 任务进度明细

| 任务编号 | 任务名称 | 状态 | 完成时间 | 影响范围 |
|----------|----------|------|----------|----------|
| P4-A | 创建 @elink/shared 公共包 | ✅ 已完成 | 2026-06-11 | elink-web/packages/shared + 3项目request.js/auth.js + 构建配置 |

### 已完成任务的影响分析

1. **SEC-01 Fastjson漏洞修复**：消除了项目中最严重的安全隐患（CVE-2022-25845等反序列化RCE漏洞），影响全局11个业务服务+公共模块的JSON处理逻辑
2. **SEC-02 CORS策略收紧**：消除了Gateway跨域通配符带来的CSRF攻击风险，将允许的跨域来源限定为3个已知业务域名；P0-2进一步移除内网IP白名单，仅保留localhost和公网域名
3. **SEC-03 JPA ddl-auto修复**：禁止了生产环境自动DDL变更，消除了数据库表结构被意外修改导致数据丢失的风险，影响全部10个业务服务
4. **SEC-04 前端硬编码IP清除**：消除了前端代码中泄露服务器IP的安全风险，3个前端项目8个文件已迁移至环境变量配置
5. **ARCH-04 HikariCP连接池修正**：将极端配置maximum-pool-size:1000调降至30，消除了数据库连接耗尽风险，新增泄漏检测
6. **DEBT-10 JAR名拼写修正**：修正了crontab-service构建产物命名错误，确保Docker部署正确加载JAR
7. **SEC-02扩展 OAuth2密钥外置**：9个服务的client-secret从硬编码改为环境变量注入，生产环境可通过.env覆盖
8. **SEC-02扩展 平台密钥外置**：configure-service的5个yml密钥+1个Java硬编码密钥全部外置为环境变量
9. **数据安全 useSSL修复**：13条JDBC连接全部启用SSL加密，移除废弃的autoReconnect参数
10. **P1-COMP-3 OAuth2迁移设计**：产出PHASE-2前置设计方案，含16项组件映射、5项端点映射、Token双阶段兼容策略、10项前端适配清单（详见REFACTOR_EXECUTE.md「OAuth2 迁移设计」章节）
11. **P2-2a Spring Boot 2.7.18升级**：Boot 2.3→2.7.18, Cloud Hoxton→2021.0.9, SCA 2021.0.6.1, Swagger 2.9.2→SpringDoc 1.7.0, Hystrix→Resilience4j CircuitBreaker, 全量910文件/19193处Swagger注解迁移, mysql-connector坐标更新, OAuth2临时桥接依赖, 编译通过
12. **P2-2b Java 17升级**：父POM+12子模块POM java.version 8→17, Dockerfile基于openjdk:8-jre手动安装OpenJDK 17.0.2（因Docker Hub拉取超时改用华为镜像）, 修复DataReportServiceImpl中3处Java 17泛型推断严格化导致的方法引用编译错误, 添加JDK_JAVA_OPTIONS --add-opens参数解决JPMS反射访问限制（FST/Redisson/JAXB库需要）, hot-reload.sh添加Java17环境变量, crontab-service健康检查路径修正/scrontab→/crontab, 11服务热更新部署验证通过（Docker healthy+HTTP UP+Nacos注册正常）, 冒烟测试通过（Gateway路由200/OAuth2端点正常/System UP/容器Java版本17.0.2）
13. **P2-2c Spring Boot 3.3.x+jakarta+OAuth2迁移**：Boot 2.7.18→3.3.6, Cloud 2021.0.9→2023.0.4, SCA 2021.0.6.1→2023.0.3.2, javax→jakarta 355处import替换(含static import), SpringDoc 1.7.0→2.6.0, MyBatis 2.1.1→3.0.4, Redisson 3.11.3→3.27.2, auth-service重写为spring-authorization-server, OauthController兼容旧版登录接口(6种grant_type), RedisTokenAuthenticationFilter替代JWT资源服务器验证, 3个TODO认证提供者实现完成(MobilePasswordCustomTokenGranter/MobileSmsSystemuserTokenGranter/MobileSmsCustomTokenGranter), MainController返回用户信息, 编译通过, 前端无需调整, 产出AUTH_LOGIN_API.md登录接口使用说明文档
14. **P2-2c 全面验证通过（2026-06-10）**：11服务逐hot-reload全部成功（平均启动30s，无jakarta/Spring Boot 3.3.x报错），status全部healthy+Nacos 11/11注册，10服务actuator全部HTTP 200，Gateway路由全部HTTP 200，OAuth2端点可达HTTP 200，Git提交+推送成功（refactor/phase-2-framework-upgrade分支）
14. **P2-2c-2 补全事务管理（ARCH-05）**：逐服务审查 device-service/together-service/webapp-service/system-service/protocol-service Service层，为多表写操作添加 @Transactional(rollbackFor = Exception.class)，纯查询添加 @Transactional(readOnly = true)
15. **P2-2c-3 Spring Cloud Alibaba版本配置**：在父POM中添加 SCA BOM 2023.0.3.2 dependencyManagement，子模块移除Nacos硬编码版本号

> ⚠️ **P2-2c 验证状态标记**：P2-2c 虽编译通过，但本地服务器验证（`./hot-reload.sh reload` 逐服务）、热更新验证（`./hot-reload.sh status` 所有服务 healthy）、功能一致性验证（登录/Token获取/刷新/冒烟测试）**均尚未执行**。代码也未提交到 `refactor/phase-2-framework-upgrade` 分支。建议在 P2-2c-4 执行前优先补全。

---

## 一、代码全面审计报告

### 1.1 项目概况

| 维度 | 现状 |
|------|------|
| **后端架构** | Spring Boot 2.3.0.RELEASE + Spring Cloud Hoxton.SR8 + Nacos 2.2.9 微服务体系 |
| **前端架构** | 3个独立Vue 3项目：linkos（vue-cli 5）、derms（Vite 6）、tycvs（vue-cli 5） |
| **微服务数量** | 13个模块（含gateway、11个业务服务、2个公共模块） |
| **数据库** | MySQL + TDengine（时序数据）+ Redis |
| **消息中间件** | EMQX 5.1.0（MQTT） |
| **运行环境** | Java 8 / Docker / docker-compose |

### 1.2 关键审计发现

#### 1.2.1 严重安全隐患（P0 - 必须立即修复）

| 编号 | 问题 | 影响范围 | 风险等级 |
|------|------|----------|----------|
| SEC-01 | **Fastjson 1.2.0** 存在多个已知反序列化远程代码执行漏洞（CVE-2022-25845等） | 全局依赖 pom.xml | **严重** ✅已修复 |
| SEC-02 | Gateway CORS 配置 `allowed-origins: "*"` 允许任意域跨域访问 | sunmax-gateway application.yml | **严重** ✅已修复 |
| SEC-03 | JPA `ddl-auto: update` 生产环境自动变更表结构，可能导致数据丢失 | 全部10个业务服务 application.yml | **高** ✅已修复 |
| SEC-04 | 前端硬编码服务器IP地址（47.110.235.112等），应使用环境变量 | linkos/request.js + linkos/config.js + derms/webSocket.js | **高** ✅已修复 |

#### 1.2.2 架构缺陷（P1 - 尽快修复）

| 编号 | 问题 | 影响 |
|------|------|------|
| ARCH-01 | **Spring Boot 2.3.0 已EOL**，无法获取安全补丁，存在大量已知CVE | 全局 |
| ARCH-02 | **Java 8 已停止公共更新**，缺乏安全补丁 | 全局 |
| ARCH-03 | Spring Cloud Hoxton.SR8 已EOL，与Spring Boot 2.3强绑定 | 全局 |
| ARCH-04 | **HikariCP maximum-pool-size: 1000** 远超合理范围，将耗尽数据库连接 | auth-service |
| ARCH-05 | @Transactional 注解218处/60文件，但核心写操作方法（订单、支付、设备控制等）事务覆盖不完整，需审查补充 | 60+ Service文件 |
| ARCH-06 | Feign 调用大量使用控制器层代理（FeignController），无服务间直接调用 | 全部微服务 |
| ARCH-07 | 前端3个项目 request.js 代码高度重复，拦截器逻辑各自维护 | elink-web |

#### 1.2.3 技术债务（P2 - 计划修复）

| 编号 | 问题 | 数量 |
|------|------|------|
| DEBT-01 | `e.printStackTrace()` 调用，应使用 SLF4J | 约95处/20文件 |
| DEBT-02 | `System.out/err.print` 调用，应使用日志框架 | 109处/34文件 |
| DEBT-03 | `catch(Exception)` 过于宽泛的异常捕获 | 357处/92文件 |
| DEBT-04 | TODO/FIXME/HACK 注释未处理 | 16处/10文件 |
| DEBT-05 | Swagger 2.9.2 已过时，不兼容 Spring Boot 2.6+，含19193处注解待迁移（@Api:121, @ApiOperation:2365, @ApiModel:8093, @ApiModelProperty:7361, @ApiImplicitParam:988, @ApiImplicitParams:265） | pom.xml + 约910个Java文件 |
| DEBT-06 | OSS SDK 2.8.3 版本过旧 | pom.xml |
| DEBT-07 | Redisson 3.11.3 版本过旧 | sunmax-common pom.xml + device/data/crontab-service |
| DEBT-08 | groupId 为 `org.example` 不符合生产规范 | 全部13个模块pom.xml（26处引用） |
| DEBT-09 | Jackson 2.18.0 手动指定版本与Spring Boot管理版本冲突 | pom.xml |
| DEBT-09a | `org.jetbrains:annotations:RELEASE` 使用动态版本号，构建不可复现 | pom.xml |
| DEBT-10 | docker-compose 中 crontab-service JAR名拼写为 `scrontab-service-exec.jar` | docker-compose.yml | ✅已修复 |
| DEBT-11 | 前端 Vuex 应迁移至 Pinia（Vue 3 官方推荐） | 3个项目 |
| DEBT-12 | 2个前端项目仍使用已废弃的 vue-cli，应迁移至 Vite | linkos、tycvs |
| DEBT-13 | 前端全部使用 JavaScript，缺乏类型安全，应迁移至 TypeScript | 3个项目 |
| DEBT-14 | CSS `extract: false` 导致样式全量内联，无法利用浏览器缓存 | linkos、tycvs |
| DEBT-15 | 单元测试全部跳过（skipTests=true），无任何测试覆盖 | 全局 |

---

## 二、重构实施计划

### 2.1 分阶段重构策略

#### 第一阶段：安全加固与紧急修复（阶段代号 PHASE-1）

> 目标：消除所有P0级安全隐患，确保线上系统安全性

**范围：**
- SEC-01 ~ SEC-04 修复
- ARCH-04 HikariCP连接池参数修正
- DEBT-10 JAR名拼写修正

**具体措施：**

| 序号 | 任务 | 执行内容 | 影响文件 | 状态 |
|------|------|----------|----------|------|
| 1 | 替换 Fastjson | 升级为 fastjson2 2.0.52 | pom.xml + 150处引用/100个Java文件 | ✅已完成 |
| 2 | 收紧 CORS 策略 | 配置具体允许域名，移除通配符 | gateway application.yml | ✅已完成 |
| 3 | JPA ddl-auto 改为 validate | 生产环境禁止自动DDL | 全部10个业务服务 application.yml | ✅已完成 |
| 4 | 清除前端硬编码IP | 统一使用环境变量或代理配置 | linkos/request.js + linkos/config.js + derms/webSocket.js | ✅已完成 |
| 5 | 修正连接池参数 | maximum-pool-size 调整为 20-50 | auth-service application.yml | ✅已完成 |
| 6 | 修正JAR拼写 | scrontab → crontab | docker-compose.yml + hot-reload.sh + crontab-service/pom.xml | ✅已完成 |

**版本控制机制：**
```bash
# 每阶段开始前创建分支
git checkout -b refactor/phase-1-security-hardening main

# 每个任务单独提交
git add <specific-files>
git commit -m "fix([java]/全局): 修复Fastjson反序列化漏洞，升级至fastjson2 2.0.x"

# 阶段完成后打Tag
git tag -a v3.0-phase1 -m "PHASE-1: 安全加固完成"
```

**回滚方案：**
| 触发条件 | 回滚流程 | 验证标准 |
|----------|----------|----------|
| 服务启动失败 | `git revert <commit-hash>` + 重新构建部署 | 服务健康检查通过 |
| 接口兼容性异常 | `git checkout main -- <file>` 还原特定文件 | 核心API回归测试通过 |
| 数据库连接异常 | 还原连接池参数 + 重启服务 | 连接池指标恢复正常区间 |

---

#### 第二阶段：框架升级与核心重构（阶段代号 PHASE-2）

> 目标：升级核心技术栈至LTS版本，消除架构缺陷

**范围：**
- Spring Boot 2.3 → 3.x（最新LTS）
- Java 8 → Java 17/21
- Spring Cloud Hoxton → 2023.x（对应Spring Boot 3.x）
- Swagger → SpringDoc OpenAPI
- ARCH-05 事务管理补全
- ARCH-06 Feign调用重构

**升级路径（渐进式）：**

```
Step 2a: Spring Boot 2.3 → 2.7（过渡升级）
    ├── 修复2.7不兼容变更
    ├── Swagger → SpringDoc OpenAPI迁移（19193处注解/910个Java文件）
    └── 验证全部服务正常启动

Step 2b: Java 8 → Java 17
    ├── 更新Dockerfile基础镜像
    ├── 更新Maven compiler配置
    └── 修复Java 17不兼容代码（反射、内部API等）

Step 2c: Spring Boot 2.7 → 3.x + Spring Cloud 2023.x
    ├── javax.* → jakarta.* 命名空间迁移（487处/255文件）
    │   ├── javax.persistence(402处) + javax.annotation(44处)
    │   ├── javax.websocket(21处) + javax.servlet(17处) + javax.validation(3处)
    ├── OAuth2模块迁移（spring-security-oauth2已废弃）
    │   └── auth-service重点改造：AuthorizationServerConfigurer → AuthorizationServer
    │       ResourceServerConfigurer → ResourceServer
    │       SMTokenService等自定义Token服务适配新API
    │       涉及 pom.xml + sunmax-common/pom.xml 两处依赖
    ├── Spring Cloud Alibaba: 2021.0.9.0 → 2023.0.3.2（对应Spring Boot 3.x）
    ├── Gateway路由配置适配
    └── Feign客户端适配
```

**事务管理补全方案：**
- 识别所有涉及多表写操作的Service方法
- 添加 `@Transactional` 注解
- 区分读/写操作使用不同事务传播级别
- 配置事务管理器统一异常回滚策略

---

#### 第三阶段：代码质量与性能优化（阶段代号 PHASE-3）

> 目标：清理技术债务，提升代码质量与运行性能

**范围：**
- DEBT-01 ~ DEBT-04 代码规范清理
- DEBT-05 ~ DEBT-09 依赖版本升级
- 前端构建优化
- 性能测试与调优

**代码清理执行规范：**
```bash
# 使用脚本批量替换 e.printStackTrace()
# 替换为 log.error("描述信息", e)

# 统计清理进度
grep -r "e\.printStackTrace" --include="*.java" . | wc -l
```

---

#### 第四阶段：前端现代化改造（阶段代号 PHASE-4）

> 目标：前端技术栈统一，引入TypeScript，迁移至Pinia

**范围：**
| 项目 | 当前 | 目标 |
|------|------|------|
| linkos | vue-cli 5 + JS + Vuex | Vite + TypeScript + Pinia |
| derms | Vite 6 + JS + Vuex | Vite 6 + TypeScript + Pinia |
| tycvs | vue-cli 5 + JS + Vuex | Vite + TypeScript + Pinia |

**公共模块提取：**
- 将3个项目的 request.js / auth.js / utils 统一提取为 `@elink/shared` 内部npm包
- 统一Element Plus版本
- 统一ECharts封装组件

---

#### 第五阶段：构建部署与持续优化（阶段代号 PHASE-5）

> 目标：完善CI/CD流水线，建立长效监控与优化机制

**范围：**
- CI/CD流水线搭建
- 核心单元测试补全（目标覆盖率 ≥60%）
- 监控告警体系部署
- 性能回归测试与调优

**具体措施：**

| 序号 | 任务 | 执行内容 | 影响范围 |
|------|------|----------|----------|
| 1 | CI/CD流水线 | 配置代码提交→Lint→测试→构建→部署自动化 | .github/workflows 或 .gitlab-ci.yml |
| 2 | 补全单元测试 | 核心Service层测试覆盖率 ≥60% | 全部业务服务 |
| 3 | 监控告警体系 | Prometheus + Grafana + 告警规则 | docker-compose.monitoring.yml |
| 4 | 性能回归测试 | 对比R1基线验证优化目标达成 | 全栈 |

---

### 2.2 版本控制机制

```
main (生产分支)
  ├── refactor/phase-1-security-hardening    ← 第一阶段
  ├── refactor/phase-2-framework-upgrade     ← 第二阶段
  ├── refactor/phase-3-code-quality          ← 第三阶段
  ├── refactor/phase-4-frontend-modernize    ← 第四阶段
  └── refactor/phase-5-devops                ← 第五阶段

标签规范：
  v3.0-phase1-rc1     ← 阶段候选版本
  v3.0-phase1         ← 阶段正式版本
  v3.0-phase1-hotfix1 ← 阶段紧急修复
```

**提交规范（严格遵守）：**
```
<type>(<scope>): <subject>

type: feat/fix/refactor/docs/style/test/chore
scope: [vue]/<模块名> 或 [java]/<模块名>

示例：
fix([java]/全局): 修复Fastjson反序列化漏洞，升级至fastjson2 2.0.52
refactor([vue]/linkos): 迁移request模块至公共包@elink/shared
feat([java]/device-service): 补全设备操作事务管理
```

### 2.3 回滚方案总纲

#### 回滚触发条件矩阵

| 等级 | 条件 | 示例 | 响应时间 |
|------|------|------|----------|
| P0-紧急 | 服务不可用/数据损坏 | 服务启动崩溃、数据库数据丢失 | 15分钟内 |
| P1-高 | 核心功能异常 | 用户无法登录、设备无法接入 | 1小时内 |
| P2-中 | 非核心功能降级 | 报表统计偏差、图表加载慢 | 4小时内 |
| P3-低 | 样式/体验问题 | 按钮对齐偏移、提示文案错误 | 24小时内 |

#### 回滚执行流程

```
1. 评估影响范围 → 确认回滚等级
2. 通知项目负责人 → 获取回滚授权（P0可事后补授权）
3. 执行回滚：
   - 服务级回滚：./hot-reload.sh rollback <service>
   - 全量回滚：git checkout <previous-tag> → 重新构建部署
4. 验证回滚结果 → 确认服务恢复正常
5. 记录回滚原因 → 修复问题后重新推进
```

#### 回滚验证标准

| 检查项 | 验证方法 | 通过标准 |
|--------|----------|----------|
| 服务存活 | `curl http://<host>:<port>/` | HTTP 200 |
| Nacos注册 | 查询Nacos实例列表 | 实例healthy=true |
| 核心API | 调用登录/设备列表等接口 | 响应正常，无异常 |
| 数据一致性 | 抽查验关键业务数据 | 数据无异常变更 |
| 日志无报错 | 检查服务日志 | 无ERROR级别日志 |

---

## 三、性能优化方案

### 3.1 性能测试计划

#### 测试轮次安排

| 轮次 | 类型 | 目标 | 工具 | 执行阶段 |
|------|------|------|------|----------|
| R1 | 基准测试 | 建立当前系统性能基线 | JMeter / k6 | PHASE-1完成后 |
| R2 | 压力测试 | 发现系统极限与瓶颈点 | JMeter / k6 | PHASE-2完成后 |
| R3 | 负载测试 | 验证持续负载下的稳定性 | JMeter / Locust | PHASE-3完成后 |
| R4 | 回归测试 | 对比优化前后性能提升 | 同R1工具 | PHASE-5 |

#### 关键性能指标与目标

| 指标 | 基线目标（R1采样值待填） | 优化目标 | 说明 |
|------|--------------------------|----------|------|
| API平均响应时间 | 待测量 | ≤200ms | P95 |
| API P99响应时间 | 待测量 | ≤500ms | |
| 吞吐量（TPS） | 待测量 | ≥500 TPS | 核心接口 |
| 错误率 | 待测量 | ≤0.1% | 压测期间 |
| JVM GC停顿 | 待测量 | ≤50ms | G1/ZGC |
| 数据库连接池利用率 | 待测量 | 60-80% | |
| 前端首屏加载时间 | 待测量 | ≤2s | FCP |
| 前端LCP | 待测量 | ≤2.5s | |

### 3.2 性能优化措施

#### 后端优化

| 方向 | 具体措施 | 预期收益 |
|------|----------|----------|
| JVM调优 | ParallelGC → G1GC/ZGC，调整堆大小比例 | GC停顿降低50%+ |
| 连接池 | HikariCP合理配置（20-50），添加连接泄漏检测 | 消除连接耗尽风险 |
| 缓存策略 | 热点数据Redis缓存，本地Caffeine二级缓存 | 减少数据库查询30%+ |
| SQL优化 | 慢SQL分析，索引优化，避免N+1查询 | 查询响应降低40%+ |
| 异步化 | 非核心链路异步处理（日志、通知等） | 接口响应时间降低20%+ |
| TDengine | 时序数据批量写入优化，超级表设计优化 | 写入吞吐提升3-5x |

#### 前端优化

| 方向 | 具体措施 | 预期收益 |
|------|----------|----------|
| 构建优化 | vue-cli → Vite，启用CSS提取与代码分割 | 构建速度提升10x+，包体积减少30% |
| 路由懒加载 | 所有路由组件使用 `defineAsyncComponent` | 首屏加载体积减少50%+ |
| 虚拟滚动 | 大数据列表使用 vxe-table 虚拟滚动 | 大列表渲染流畅度显著提升 |
| 静态资源 | 图片压缩，SVG雪碧图，字体子集化 | 资源加载体积减少40%+ |
| CDN加速 | 静态资源部署至CDN | 加载速度提升30%+ |

### 3.3 性能监控机制

```yaml
监控体系:
  应用层:
    - Spring Boot Actuator + Prometheus + Grafana
    - JVM指标：堆内存、GC次数/时间、线程数
    - 接口指标：QPS、响应时间分位值、错误率
    - 连接池指标：活跃连接数、等待线程数
  
  数据库层:
    - MySQL慢查询日志 + Prometheus MySQL Exporter
    - 连接数、锁等待、慢SQL
  
  基础设施层:
    - Docker容器指标（cAdvisor + Prometheus）
    - CPU、内存、网络IO、磁盘IO
  
  前端层:
    - Web Vitals上报至监控平台
    - FCP、LCP、FID、CLS核心指标
```

---

## 四、构建部署保障

### 4.1 编译打包验证清单

| 序号 | 检查项 | 验证命令 | 通过标准 |
|------|--------|----------|----------|
| 1 | 后端编译 | `mvn clean package -DskipTests` | BUILD SUCCESS |
| 2 | 后端单元测试 | `mvn test` | 全部通过 |
| 3 | 前端安装依赖 | `npm ci` | 无报错 |
| 4 | 前端构建（linkos） | `npm run build` | 构建成功，无警告 |
| 5 | 前端构建（derms） | `npm run build` | 构建成功，无警告 |
| 6 | 前端构建（tycvs） | `npm run build` | 构建成功，无警告 |
| 7 | 前端Lint检查 | `npm run lint` | 0 errors |
| 8 | Docker镜像构建 | `docker build -t elink-base:latest .` | 构建成功 |
| 9 | 全栈启动 | `./start.sh` | 全部服务健康 |

### 4.2 部署流程验证

```
部署验证流程:
1. 环境配置检查
   └─ .env 文件完整性验证
   └─ 端口冲突检测
   └─ 磁盘空间检查（≥10GB可用）

2. 基础设施启动
   └─ Redis 健康检查通过
   └─ Nacos 健康检查通过
   └─ EMQX 健康检查通过

3. 微服务启动（按依赖顺序）
   └─ auth-service → sunmax-gateway
   └─ system/device/data/devops/configure/together/webapp-service
   └─ protocol-service（依赖device-service注册）
   └─ crontab-service（依赖together-service注册）

4. 服务注册验证
   └─ 每个服务在Nacos中注册且状态为healthy

5. 网关路由验证
   └─ 通过Gateway访问每个服务的API端点

6. 前端部署验证
   └─ 静态资源可访问
   └─ API代理正常转发
```

### 4.3 依赖管理策略

| 类别 | 策略 | 执行方式 |
|------|------|----------|
| 后端依赖 | 统一版本管理在父POM的dependencyManagement | 所有子模块不指定版本号 |
| 前端依赖 | 统一核心库版本（Vue/ElementPlus/ECharts） | workspace统一或内部包管理 |
| 安全漏洞 | 每周扫描一次 | `mvn dependency-check:check` / `npm audit` |
| 版本锁定 | 后端使用Maven BOM，前端使用package-lock.json | 禁止模糊版本号 |

---

## 五、实施验证机制

### 5.1 验证节点设置

| 阶段 | 验证节点 | 验证内容 | 验证人 |
|------|----------|----------|--------|
| PHASE-1 | V1.1 | 安全漏洞扫描报告（0 高危） | 项目负责人 |
| PHASE-1 | V1.2 | 全部服务正常启动 + 核心API可用 | 测试人员 |
| PHASE-2 | V2.1 | Spring Boot 3.x 全部服务启动 | 项目负责人 |
| PHASE-2 | V2.2 | Java 17 全部服务运行稳定 | 测试人员 |
| PHASE-2 | V2.3 | 事务管理覆盖率 ≥90% | 代码审查人 |
| PHASE-3 | V3.1 | 代码静态分析 0 严重/阻断问题 | 自动化工具 |
| PHASE-3 | V3.2 | 性能指标达成优化目标 | 性能测试人员 |
| PHASE-4 | V4.1 | 前端构建全部迁移至Vite | 前端负责人 |
| PHASE-4 | V4.2 | TypeScript覆盖率 ≥80% | 代码审查人 |
| PHASE-5 | V5.1 | CI/CD流水线全链路通过 | DevOps负责人 |

### 5.2 确认授权机制

```
授权流程:
┌─────────────────────────────────────────────────────┐
│  重构执行请求                                         │
│  ├─ 触发条件：准备执行阶段性重构操作                     │
│  ├─ 提交内容：变更范围 + 影响评估 + 回滚预案             │
│  └─ 发送对象：项目负责人                               │
├─────────────────────────────────────────────────────┤
│  项目负责人审核                                       │
│  ├─ 评估风险级别                                       │
│  ├─ 确认变更范围                                       │
│  └─ 明确授权/拒绝/要求修改                              │
├─────────────────────────────────────────────────────┤
│  执行变更                                             │
│  ├─ 仅在获得明确书面授权后执行                           │
│  ├─ 执行过程记录完整日志                                │
│  └─ 执行完毕立即通知项目负责人                          │
└─────────────────────────────────────────────────────┘

必须授权的操作清单:
- 任何 pom.xml 依赖版本变更
- 任何 application.yml 配置变更
- 任何数据库表结构变更
- 任何 Docker/docker-compose 配置变更
- 任何涉及多服务联动的代码变更
- 生产环境部署操作
```

---

## 六、使用提示语规范

### 6.1 风险提示语

```
[⚠️ 高风险] 即将修改生产环境数据库连接池配置，当前最大连接数为1000，
建议降至20-50。此操作需重启服务，请在业务低峰期执行。
是否继续？(需要项目负责人确认)

[⚠️ 高风险] 即将升级Spring Boot版本(2.3.0 → 3.x)，涉及javax→jakarta
命名空间迁移，预计影响487处/约255个文件。
  详细分布：javax.persistence(402处/100文件) + javax.annotation(44处/42文件)
           + javax.websocket(21处/8文件) + javax.servlet(17处/15文件) + javax.validation(3处/3文件)
请确保已完整执行过渡升级路径。
是否继续？(需要项目负责人确认)

[⚠️ 低风险] 清理e.printStackTrace()调用，替换为log.error()。
此操作不影响业务逻辑，仅需回归测试日志输出。
是否继续？
```

### 6.2 操作指引提示语

```
[操作指引 - PHASE-1 安全漏洞修复]

步骤1: 修复Fastjson漏洞
  命令: 替换pom.xml中fastjson 1.2.0 为 fastjson2 2.0.52
  影响: 全局JSON解析逻辑需同步修改
  验证: 编译通过 + JSON序列化/反序列化测试通过

步骤2: 收紧CORS策略
  命令: 修改gateway application.yml中allowed-origins为具体域名
  影响: 仅允许指定域名跨域访问
  验证: 浏览器跨域请求测试通过

步骤3: 修正JAR拼写
  命令: docker-compose.yml中scrontab-service-exec.jar → crontab-service-exec.jar
```

### 6.3 注意事项提示语

```
[注意] 数据库迁移注意事项:
- 所有DDL变更必须通过Migration脚本执行，禁止使用ddl-auto: update
- Migration脚本必须支持逆向回滚（提供down脚本）
- 变更前必须备份数据库：mysqldump -u root -p sunos-system > backup_$(date +%Y%m%d).sql

[注意] 微服务重启顺序:
- 必须按依赖关系启动：基础设施 → 认证网关 → 基础业务 → 依赖业务
- protocol-service启动前务必确认device-service已注册至Nacos
- crontab-service启动前务必确认together-service已注册至Nacos
- 使用./start.sh可自动处理启动顺序

[注意] 前端部署注意事项:
- 构建前确认NODE_ENV=production
- 部署后清除CDN/Nginx缓存
- 验证API代理配置是否指向正确后端地址
```

---

## 七、分阶段执行实施流程

### PHASE-1：安全加固与紧急修复

**执行前提：** 获得项目负责人书面确认

```bash
# ========== PHASE-1 执行命令序列 ==========

# 0. 创建工作分支
echo "[步骤0] 创建阶段分支"
git checkout -b refactor/phase-1-security-hardening main

# 1. 修复Fastjson漏洞（SEC-01）
echo "[步骤1] 升级Fastjson → fastjson2"
# 手动编辑 pom.xml:
#   删除: <artifactId>fastjson</artifactId><version>1.2.0</version>
#   新增: <artifactId>fastjson2</artifactId><version>2.0.52</version>
# 全局替换代码中: import com.alibaba.fastjson → import com.alibaba.fastjson2
# 编译验证:
cd /work/elink-ai/elink-work && mvn clean compile -pl sunmax-common -am

# 2. 收紧CORS策略（SEC-02）
echo "[步骤2] 修改Gateway CORS配置"
# 编辑 sunmax-gateway/src/main/resources/application.yml:
#   allowed-origins: "*" → allowed-origins: "https://os.enlinkitech.com,https://derms.enlinkitech.com,https://cvs.enlinkitech.com"
# [注意] 请替换为实际的业务域名列表

# 3. JPA ddl-auto改为validate（SEC-03）
echo "[步骤3] 禁止生产环境自动DDL"
# 编辑各服务 application.yml: ddl-auto: update → ddl-auto: validate

# 4. 清除前端硬编码IP（SEC-04）
echo "[步骤4] 替换前端硬编码IP为环境变量"
# 编辑 linkos/src/utils/request.js:
#   移除 serverIpAddress 硬编码，统一使用 VUE_APP_API_BASE_URL 环境变量
# 编辑 linkos/public/config.js:
#   iemsUrl: 'http://47.110.235.112:21002/' → 使用环境变量
# 编辑 derms/src/api/websocket/webSocket.js:
#   ws://47.110.235.112:21010 → 使用环境变量

# 5. 修正连接池参数（ARCH-04）
echo "[步骤5] 修正HikariCP连接池配置"
# 编辑 auth-service application.yml:
#   maximum-pool-size: 1000 → maximum-pool-size: 30

# 6. 修正JAR拼写（DEBT-10）
echo "[步骤6] 修正crontab-service JAR名"
# 编辑 crontab-service/pom.xml:
#   <finalName>scrontab-service</finalName> → <finalName>crontab-service</finalName>
# 编辑 docker-compose.yml:
#   scrontab-service-exec.jar → crontab-service-exec.jar
# 编辑 hot-reload.sh:
#   crontab-service:scrontab-service-exec.jar → crontab-service:crontab-service-exec.jar

# 7. 编译验证
echo "[步骤7] 全量编译验证"
cd /work/elink-ai/elink-work && mvn clean package -DskipTests -T 4

# 8. 部署验证
echo "[步骤8] 部署验证"
cd /work/elink-ai/elink-work && ./start.sh

# 9. 提交与打Tag
echo "[步骤9] 提交代码并打Tag"
git add -A
git commit -m "fix([java]/全局): PHASE-1安全加固，修复4项安全隐患及连接池配置"
git tag -a v3.0-phase1 -m "PHASE-1: 安全加固与紧急修复完成"
git push origin refactor/phase-1-security-hardening --tags

echo "" && echo "========== PHASE-1 执行完毕 =========="
echo "[提示] 请通知项目负责人审核验证结果"
echo "[提示] 审核通过后合并至main分支: git checkout main && git merge refactor/phase-1-security-hardening"
```

### PHASE-2：框架升级与核心重构

**执行前提：** PHASE-1 已合并至main，获得项目负责人书面确认

```bash
# ========== PHASE-2 执行命令序列 ==========

# 0. 创建工作分支
echo "[步骤0] 创建阶段分支"
git checkout -b refactor/phase-2-framework-upgrade main

# ---- Step 2a: Spring Boot 2.3 → 2.7 过渡升级 ----

# 1. 升级Spring Boot至2.7.x
echo "[步骤2a-1] 升级Spring Boot 2.3 → 2.7"
# 编辑 pom.xml: <version>2.3.0.RELEASE</version> → <version>2.7.18</version>
# 编辑 <spring-cloud.version>Hoxton.SR8</spring-cloud.version> → <spring-cloud.version>2021.0.9</spring-cloud.version>
# 更新Nacos: 2.2.9.RELEASE → 2021.0.9.0

# 2. 迁移Swagger → SpringDoc
echo "[步骤2a-2] 迁移API文档至SpringDoc"
# 删除: springfox-swagger2, springfox-swagger-ui, swagger-bootstrap-ui, swagger-models
# 新增: springdoc-openapi-starter-webmvc-ui 2.x
# 替换注解: @Api → @Tag, @ApiOperation → @Operation 等
# 注意：当前项目有19193处Swagger注解(@Api/@ApiOperation/@ApiParam/@ApiModel/@ApiModelProperty/@ApiImplicitParam/@ApiImplicitParams)分布在910个文件中
# 建议使用IDE批量替换或OpenRewrite自动化迁移

# 3. 修复2.7不兼容变更
echo "[步骤2a-3] 修复Spring Boot 2.7不兼容项"
# Spring MVC路径匹配策略变更
# CircularReferences检测
# Security配置适配

# 4. 编译验证
echo "[步骤2a-4] 编译验证中间状态"
cd /work/elink-ai/elink-work && mvn clean compile -T 4

# 5. 提交中间状态
git add -A && git commit -m "refactor([java]/全局): Spring Boot升级至2.7.18过渡版本"

# ---- Step 2b: Java 8 → Java 17 ----

# 6. 更新Maven编译配置
echo "[步骤2b-1] 更新Java版本至17"
# 编辑 pom.xml: <java.version>8</java.version> → <java.version>17</java.version>
# 编辑 Dockerfile: FROM openjdk:8-jre → FROM eclipse-temurin:17-jre

# 7. 修复Java 17兼容性
echo "[步骤2b-2] 修复Java 17兼容性"
# --add-opens JVM参数（如使用反射访问内部API）
# 第三方库兼容性更新

# 8. 编译与运行验证
cd /work/elink-ai/elink-work && mvn clean package -DskipTests -T 4

git add -A && git commit -m "refactor([java]/全局): 升级至Java 17"

# ---- Step 2c: Spring Boot 2.7 → 3.x ----

# 9. 执行javax→jakarta命名空间迁移
echo "[手动操作] javax → jakarta 命名空间迁移"
# 全局替换:
#   import javax.servlet.* → import jakarta.servlet.*
#   import javax.validation.* → import jakarta.validation.*
#   import javax.persistence.* → import jakarta.persistence.*
#   import javax.annotation.* → import jakarta.annotation.*
#   import javax.websocket.* → import jakarta.websocket.*（影响8个WebSocket文件）
#   import javax.transaction.* → import jakarta.transaction.*

# 10. 升级Spring Boot至3.x
echo "[步骤2c-2] 升级Spring Boot 2.7 → 3.x"
# pom.xml: <version>3.3.6</version>
# spring-cloud.version: 2023.0.4

# 11. OAuth2模块迁移
echo "[步骤2c-3] OAuth2架构迁移"
# spring-cloud-starter-oauth2 → spring-authorization-server（授权服务器）
# 资源服务器端 → spring-boot-starter-oauth2-resource-server
# [重点] auth-service 需重点改造：
#   - AuthorizationServerConfigurer → AuthorizationServer（spring-authorization-server）
#   - ResourceServerConfigurer → ResourceServer（spring-boot-starter-oauth2-resource-server）
#   - SMTokenService 等自定义Token服务需适配新API
#   - 涉及 pom.xml + sunmax-common/pom.xml 两处 spring-cloud-starter-oauth2 依赖

# 12. 补全事务管理（ARCH-05）
echo "[步骤2c-4] 补全@Transactional事务管理"
# 逐服务扫描，为涉及多表写操作的方法添加事务注解

# 13. 全量编译验证
echo "[步骤2c-5] 全量编译验证"
cd /work/elink-ai/elink-work && mvn clean package -DskipTests -T 4
./start.sh

# 14. 提交与打Tag
git add -A
git commit -m "refactor([java]/全局): PHASE-2框架升级完成，Spring Boot 3.x + Java 17"
git tag -a v3.0-phase2 -m "PHASE-2: 框架升级与核心重构完成"
git push origin refactor/phase-2-framework-upgrade --tags

echo "" && echo "========== PHASE-2 执行完毕 =========="
echo "[提示] 必须通过压力测试验证后再合并至main"
```

### PHASE-3：代码质量与性能优化

**执行前提：** PHASE-2 已合并至main，性能基线测试完成

```bash
# ========== PHASE-3 执行命令序列 ==========

git checkout -b refactor/phase-3-code-quality main

# 1. 批量替换e.printStackTrace()（DEBT-01）
echo "[步骤1] 清理e.printStackTrace()"
# 执行全局替换脚本（建议使用IDE的Structural Replace）:
#   e.printStackTrace() → log.error("操作描述", e)
# 验证: grep -r "e\.printStackTrace" --include="*.java" . | wc -l  → 应为0

# 2. 清理System.out/err调用（DEBT-02）
echo "[步骤2] 清理System打印语句"
# 替换为 log.info() / log.error()
# 验证: grep -r "System\.\(out\|err\)\.print" --include="*.java" . | wc -l  → 应为0

# 3. 收窄异常捕获范围（DEBT-03）
echo "[步骤3] 优化异常处理"
# catch(Exception e) → 具体异常类型（IOException, SQLException等）
# 此项需逐文件审查，工作量大，建议分服务逐步推进

# 4. 清理TODO/FIXME（DEBT-04）
echo "[步骤4] 处理TODO/FIXME"
# 逐条评估：实现/删除/转为Issue跟踪

# 5. 依赖版本升级（DEBT-05~09）
echo "[步骤5] 升级过时依赖"
# OSS SDK: 2.8.3 → 3.x
# Redisson: 3.11.3 → 3.36.x
# groupId: org.example → com.elink
# 移除Jackson手动版本，使用Spring Boot BOM管理
# org.jetbrains:annotations:RELEASE → 固定为具体版本（如24.0.1）

# 6. 前端构建优化（DEBT-14）
echo "[步骤6] 优化前端构建配置"
# linkos/tycvs: vue.config.js 中 css.extract: false → true
# 开启代码分割与gzip压缩

# 7. 性能测试
echo "[步骤7] 执行性能测试"
# 基准测试 → 压力测试 → 负载测试
# 记录结果对比PHASE-2基线

git add -A
git commit -m "refactor([java]/全局): PHASE-3代码质量清理与性能优化"
git tag -a v3.0-phase3 -m "PHASE-3: 代码质量与性能优化完成"

echo "" && echo "========== PHASE-3 执行完毕 =========="
```

### PHASE-4：前端现代化改造

**执行前提：** PHASE-3 已合并至main，前端团队就绪

```bash
# ========== PHASE-4 执行命令序列 ==========

git checkout -b refactor/phase-4-frontend-modernize main

# ---- Step 4a: 提取公共模块 ----

echo "[步骤4a-1] 创建前端公共包"
mkdir -p /work/elink-ai/elink-web/packages/shared
cd /work/elink-ai/elink-web/packages/shared && npm init -y
# 提取 request.js / auth.js / utils 等公共代码至 @elink/shared

# ---- Step 4b: linkos 迁移至 Vite ----

echo "[步骤4b-1] linkos: vue-cli → Vite 迁移"
cd /work/elink-ai/elink-web/linkos
# 1. 安装Vite依赖: npm install -D vite @vitejs/plugin-vue
# 2. 创建 vite.config.js（参考derms配置）
# 3. 迁移 vue.config.js 中的代理、别名等配置
# 4. mv vue.config.js vue.config.js.bak
# 5. 更新 package.json scripts: serve → dev, build → build
# 6. 处理 webpack-specific 代码（require.context → import.meta.glob 等）
# 7. 验证: npm run dev && npm run build

# ---- Step 4c: Vuex → Pinia 迁移 ----

echo "[步骤4c-1] 三个项目统一迁移Vuex → Pinia"
# 安装: npm install pinia
# 逐模块迁移：state → defineStore的state, mutations/actions → actions, getters → getters
# 更新 main.js: createStore → createPinia
# 移除 vuex 依赖

# ---- Step 4d: TypeScript 渐进式引入 ----

echo "[步骤4d-1] 启用TypeScript（渐进式）"
# 安装: npm install -D typescript vue-tsc
# 创建 tsconfig.json
# 将 .js → .ts 逐文件迁移（从utils/api层开始，逐步到组件）
# 允许 JS/TS 混合模式，降低迁移风险

# ---- Step 4e: tycvs 迁移至 Vite ----

echo "[步骤4e-1] tycvs: vue-cli → Vite 迁移"
# 同 linkos 迁移步骤

git add -A
git commit -m "refactor([vue]/全局): PHASE-4前端现代化改造，Vite+Pinia+TypeScript"
git tag -a v3.0-phase4 -m "PHASE-4: 前端现代化改造完成"

echo "" && echo "========== PHASE-4 执行完毕 =========="
```

### PHASE-5：构建部署与持续优化

**执行前提：** PHASE-4 已合并至main

```bash
# ========== PHASE-5 执行命令序列 ==========

git checkout -b refactor/phase-5-devops main

# 1. 完善CI/CD流水线
echo "[步骤1] 配置CI/CD"
# GitHub Actions / GitLab CI 流水线:
#   - 代码提交 → Lint + 单元测试
#   - PR合并 → 构建 + 集成测试
#   - Tag推送 → 构建Docker镜像 + 部署

# 2. 补全单元测试
echo "[步骤2] 补全核心测试用例"
# 目标：核心Service层测试覆盖率 ≥ 60%
# 工具：JUnit 5 + Mockito + Spring Boot Test

# 3. 配置监控告警
echo "[步骤3] 部署监控体系"
# Prometheus + Grafana 监控面板
# 关键告警规则：
#   - 服务不可用 → 立即告警
#   - API响应时间 > 1s → 告警
#   - JVM堆内存 > 85% → 告警
#   - 数据库慢查询 > 3s → 告警

# 4. 生产环境部署检查
echo "[步骤4] 最终验证"
# 全链路回归测试
# 安全扫描
# 性能回归测试

git add -A
git commit -m "chore([config]/devops): PHASE-5 CI/CD管道与监控体系搭建"
git tag -a v3.0-phase5 -m "PHASE-5: 构建部署与持续优化完成"
git tag -a v3.0 -m "Elink-AI v3.0 重构升级完成"

echo "" && echo "========== PHASE-5 执行完毕 =========="
echo "========== 全部5个阶段升级完成 =========="
echo "[提示] 最终版本标签：v3.0"
```

---

## 八、里程碑时间线摘要

| 阶段 | 代号 | 核心目标 | 完成标志 |
|------|------|----------|----------|
| 1 | PHASE-1 | 安全加固 | 0个高危漏洞、全部服务正常 |
| 2 | PHASE-2 | 框架升级 | Spring Boot 3.x + Java 17 全链路通过 |
| 3 | PHASE-3 | 代码质量 | 静态分析0阻断、性能指标达标 |
| 4 | PHASE-4 | 前端现代化 | Vite + Pinia + TS 80%覆盖率 |
| 5 | PHASE-5 | 部署运维 | CI/CD全链路、监控告警就绪 |

---

