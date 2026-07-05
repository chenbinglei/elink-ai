# elink-work 登录认证与权限访问控制优化方案

> 版本：v1.8
> 编制日期：2026-06-29
> 适用范围：`elink-work` 后端全部微服务 + `elink-web` 前端三项目（linkos / derms / tycvs）
> 排除范围：R4（双因素/验证码）、R5（密码哈希化存储）、R6（前端传输加密升级）、R7（密钥集中管理）、R20（生产配置加固）
> 本轮约束：暂不考虑灰度环境部署方案，重点确保本地（测试）环境能够正常运行；前端必须通过 5000 端口访问后端；linkos clientId 保持 `sunos-client` 不变；测试账号 `cbl` / `SunmaxCbl`

---

## 零、两套优化方案对比与选择

基于代码审查结果与本地（测试）环境运行要求，制定两套优化方案：

### 方案A：本地测试优先（最小化修复）—— ✅ 推荐执行

**核心思路**：保持现有 UUID token 机制不变，仅修复 P0 安全问题与前端适配问题，确保本地测试环境立即可用。

| 项 | 内容 |
|----|------|
| 令牌体系 | 保持 UUID + Redis（不实施 JWT） |
| Refresh Token | 不实施轮换（保持现状 30 天有效期） |
| 权限校验 | 保持 Feign 远程查询（不实施缓存） |
| configure-service | 修复：启用 UserResourceConfig，删除 SecurityConfig |
| 角色映射 | 修复：RedisTokenAuthenticationFilter 按 userRole 映射真实角色 |
| 前端 Token 传递 | 增加 Authorization Header（保留 data.access_token 兼容） |
| 前端登出 | 新增：调用 /oauth/logout 接口 |
| 前端 401/403 | 新增：响应拦截器处理 HTTP 401/403 |
| clientId | linkos=`sunos-client`(不变)、derms=`derms-client`、tycvs=`tycvs-client` |
| 端口访问 | 所有请求通过 5000 端口（禁用 derms enablePortNum） |
| 风险 | 低：不改变核心令牌体系，改动范围可控 |
| 见效 | 快：修复后立即可本地测试验证 |

### 方案B：全量 JWT 实施（按原文档执行）

**核心思路**：完整实施 JWT 令牌体系、Refresh Token 轮换、权限缓存本地化等全部模块。

| 项 | 内容 |
|----|------|
| 令牌体系 | UUID → JWT（RSA 签名）+ Redis 黑名单 |
| Refresh Token | 实施一次性轮换 |
| 权限校验 | Feign 远程 → Redis 缓存本地化 |
| configure-service | 修复：同方案A |
| 角色映射 | 修复：同方案A |
| 前端 Token 传递 | 改为 Authorization Header（移除 data.access_token） |
| 前端登出 | 新增：同方案A |
| 前端 401/403 | 新增：同方案A |
| 前端自动刷新 | 新增：Refresh Token 自动刷新逻辑 |
| clientId | 同方案A |
| 端口访问 | 同方案A |
| 风险 | 高：涉及令牌体系全面替换，需双轨过渡期，密钥管理复杂 |
| 见效 | 慢：需 2-3 周开发 + 过渡期 |

### 方案对比

| 对比维度 | 方案A（本地测试优先） | 方案B（全量JWT） |
|----------|----------------------|------------------|
| 安全提升 | 中（修复 P0，消除放行/越权） | 高（JWT 验签 + 黑名单 + 轮换） |
| 性能提升 | 无（仍 Feign 查权限） | 高（本地验签 + 缓存） |
| 改动范围 | 小（约 8 个文件） | 大（约 20+ 文件 + 新增类） |
| 本地测试可用性 | ✅ 立即可用 | ❌ 需完整实施后可用 |
| 回滚难度 | 易（Git revert） | 难（涉及数据库 + Redis + 密钥） |
| 过渡期需求 | 无 | 需要 2 周双轨过渡 |
| 后续可扩展 | 可在方案A基础上增量实施JWT | 一步到位 |

### 最终选择：方案A

**选择理由**：
1. 用户明确要求「重点确保本地（测试）环境能够正常运行」，方案A风险最低、见效最快；
2. 方案B的 JWT/缓存/轮换等可在方案A验证通过后增量实施，不必一步到位；
3. 方案A已覆盖全部 P0 安全问题（configure-service 放行、角色固定、clientId 校验等）；
4. 方案B需要 RSA 密钥持久化、双轨过渡期等复杂操作，不利于快速验证。

**后续路径**：方案A验证通过 → 按需增量实施方案B的 JWT 模块（模块A）和权限缓存模块（模块C）。

### 方案执行进度（v1.8 更新）

| 阶段 | 状态 | 说明 |
|------|------|------|
| 方案A 阶段一（紧急安全修复） | ✅ 已完成 | configure-service 修复、角色贯通、clientId 强制校验、登出身份校验、异常状态码标准化（v1.3-v1.6） |
| 方案A 阶段五（前端基础适配） | ✅ 已完成 | Token Header、401/403 处理、登出调用、derms clientId 修正（v1.3-v1.4） |
| 方案A 步骤 5.3（Refresh Token 自动刷新） | ✅ 已完成 | 后端 refresh_token 接口 + 前端 tokenRefresh.ts 处理器（v1.5） |
| Refresh Token 端到端验证 | ✅ 已完成 | 8 项 Redis 直查验证 + 三平台 Vite Proxy 链路（v1.7） |
| 选项3 修复 tokenRefresh 重发请求 | ✅ 已完成 | serviceGetter 参数 + serviceRef 绑定 + FormData 幂等性（v1.8） |
| 选项2 端到端全链路验证 | ✅ 已完成 | 12 项验证全通过（v1.8） |
| 选项1 浏览器实测全场景验证 | ✅ 已完成 | 3 场景全通过：完整链路 9 步 + 并发排队 + 刷新失败（v1.9） |
| WebSocket 鉴权 | ❌ 不执行 | 第三方平台在用，本轮不考虑（用户明确要求） |
| 方案B 阶段二 步骤 2.1（新增 JwtTokenService） | ✅ 已完成 | JWT 签发/验签/过期检查 + RSA 密钥对持久化 + 10 个单元测试全通过（v2.0） |
| 方案B 阶段二 步骤 2.2（OauthController 集成 JWT） | ✅ 已完成 | jwt.enabled 开关 + buildTokenResponse 签发 JWT + handleRefreshToken 验签重签 + 5 个 JWT 用例（共22个全通过）（v2.1） |
| 方案B 阶段二 步骤 2.3（RedisTokenAuthenticationFilter 支持 JWT） | ✅ 已完成 | 双轨过渡：JWT 验签优先，失败回退 UUID 查 Redis + isJwtFormat 识别 + tryJwtAuthentication/tryRedisAuthentication 抽取 + 10 个 JWT 用例（共21个全通过）（v2.2） |
| 方案B 阶段二 步骤 2.4（JWT 黑名单机制） | ✅ 已完成 | TokenBlacklistService（access/refresh 双黑名单 + TTL 自动清理）+ OauthController 登出/刷新/踢旧 token 集成 + 11 个用例全通过 + 9 项端到端验证全通过（v2.2） |
| 方案B 阶段三（多服务接入 JWT 验签） | ✅ 已完成 | 9 个业务服务共享 RSA 密钥 + docker-compose keys 只读挂载 + 9 项端到端验证全通过 + UUID 模式回归通过 + 10 个服务双模式健康（v2.3） |
| 方案B 阶段四 步骤 4.1（Nacos 动态配置切换） | ⏭️ 跳过 | auth-service 无 Nacos config 配置 + actuator 未暴露 refresh + 改造较大非必需，环境变量+重启方式已满足（v2.4） |
| 方案B 阶段四 步骤 4.3（JWT 安全加固） | ✅ 已完成 | 算法白名单(RS256) + issuer/audience 校验 + clock skew 60s 容忍度 + RSA 密钥权限审计(600/644) + 7 个新单元测试(共17个) + 49 个全量回归 + 6 项端到端验证全通过（v2.4） |
| 方案B 阶段四 步骤 4.4（监控与告警 + 性能对比测试） | ✅ 已完成 | Micrometer 计数器（jwt.generate/jwt.verify/jwt.blacklist.add/jwt.blacklist.check）+ Prometheus 4 条告警规则 + actuator/prometheus 端点放行 + JWT vs UUID 三场景性能对比测试报告（JWT 登录快 14.3%/访问快 13.6%/刷新慢 16.9%但<20ms）（v2.5） |
| 其它任务（refresh token 清理改造） | ✅ 已完成 | OauthController 三方法改造：①buildTokenResponse 重新登录删除旧 refresh_token + 索引更新；②handleRefreshToken 索引轮换更新；③logout 删除 refresh_token + 索引清理 + JWT 模式黑名单。修复 JWT jti 唯一性严重 bug（同秒生成相同 token）（v2.5） |
| 方案B 阶段四收尾 | ✅ 已完成 | JWT_ENABLED=false 切回 UUID 模式（生产默认）+ 灰度切换能力保留（JWT_ENABLED=true + 重启 25s）+ UUID 模式登录验证通过 + 三平台业务流程正常（v2.5） |
| 方案B 阶段五（前端 JWT 适配验证） | ✅ 已完成 | 前端 HTTP 侧已通过 @elink/shared/http 全面适配：Cookie 存储 + Authorization Header + 401/403 处理 + Refresh Token 自动刷新+并发排队。JWT 模式网关 5000 端口端到端验证全通过（v2.5） |
| 最终测试与文档更新 | ✅ 已完成 | 49 单元测试全通过 + 22 JWT e2e 全通过 + 18 UUID e2e（4 预期 FAIL）+ 三平台 Vite Proxy 链路验证。新增 PERFORMANCE_TEST_REPORT.md v1.0；更新 EXECUTE.md v2.5、PLAN.md v1.8（v2.5） |

**当前状态**：方案B 阶段四步骤 4.4（监控与告警 + 性能对比测试）、阶段四收尾、阶段五前端适配验证、其它任务（refresh token 清理改造）、最终测试与文档更新已全部完成。JWT 模式已具备完整闭环能力（签发→验签→黑名单→监控→告警→性能对比），生产环境默认 UUID 模式（JWT_ENABLED=false），灰度切换能力保留。

**下一步可选方向**：
1. 长期运行 JWT 模式稳定性观察：在测试环境持续运行 JWT 模式（JWT_ENABLED=true），观察监控指标（jwt.verify/jwt.blacklist）的长期趋势，验证 RSA 密钥持久化、黑名单 TTL 清理、内存占用等长期稳定性。
2. 生产灰度切换：选择非核心服务（如 devops-service 或 webapp-service）先切换 JWT 模式，观察 1-2 周后再扩展到其他服务。
3. 阶段三权限缓存本地化（方案B 模块C）：实施 canAccess 本地读取 + 权限变更失效机制，进一步降低 Feign 远程查询开销。
4. 其他独立任务：如 WebSocket 鉴权闭环（当前已标记不执行，可按需开启）、密码哈希化存储（R5）、前端传输加密升级（R6）等。

---

## 一、需求分析

### 1.1 业务背景

`elink-work` 现有登录认证为「伪 OAuth2」实现：

- `OauthController.postAccessToken` 自行接收参数、自行校验、自行生成 UUID token 存入 Redis；
- `AuthorizationServerConfigurer` 中配置的 Spring Authorization Server 标准端点 `/oauth2/token` 未被业务使用；
- 权限校验依赖每次请求远程 Feign 调用 `auth-service` 拉取 URL 列表；
- `configure-service` 安全配置完全 `permitAll()`，认证机制失效；
- `RedisTokenAuthenticationFilter` 对所有登录用户固定授予 `ROLE_USER`，未区分真实角色。

### 1.2 核心需求

**【强制需求】clientId 平台标识全链路贯通**

前端在调用接口时必须传递自定义 `clientId` 参数，用于区分不同平台（linkos / derms / tycvs / applet 等），以获取对应平台的登录菜单与接口权限。要求：

1. 登录请求携带 `clientId`，服务端基于 `clientId` 查询 `b_product` 产品模块，进而过滤出该平台下的菜单权限（`menuList`）与接口权限（URL 列表）；
2. `clientId` 必须写入令牌载体（JWT claim / Redis 缓存），后续每次请求无需前端重复传递；
3. 权限校验阶段从令牌中取出 `clientId`，查询对应平台的接口权限进行匹配；
4. 同一用户在不同平台（不同 `clientId`）登录互不影响，可同时持有各自有效令牌；
5. 同一用户在同一平台重复登录时，按 `clientId + userId` 维度踢出旧令牌。

**【其他需求】**

- 令牌体系升级为 JWT 自包含令牌，支持本地验签；
- Refresh Token 轮换，旧 token 失效；
- 权限校验本地化，消除每次请求的远程调用；
- 权限模型增加 HTTP Method 维度；
- `/feign/**` 内部接口加固；
- `configure-service` 安全配置修复；
- 公共安全配置抽取；
- 异常处理与响应状态码标准化；
- 审计日志补全。

**【前端适配需求】**

后端令牌体系与鉴权方式变更后，前端三个项目（linkos / derms / tycvs）需同步调整：

1. Token 传递方式从 `config.data.access_token` 改为 `Authorization: Bearer <token>` Header；
2. axios 响应拦截器需处理 HTTP 401/403 状态码（而非仅检查业务码 9999）；
3. 新增 Refresh Token 自动刷新逻辑（token 过期时静默刷新，避免用户频繁重新登录）；
4. 新增登出时调用后端 `/oauth/logout` 接口（当前前端无登出接口调用）；
5. **linkos 项目 `clientId` 保持 `sunos-client` 不变**（已在 `oauth2_registered_client` 表注册，且 `b_product` 表有对应产品模块数据）；
6. 登录响应新增字段（`tokenType`、`expiresIn`、`clientId`）向后兼容，前端可选使用；
7. **本地（测试）环境优先**：本轮优化暂不考虑灰度环境部署方案，重点确保本地（测试）环境能够正常运行；
8. **统一端口访问**：前端应用必须通过 5000 端口（sunmax-gateway）访问后端服务，所有测试用例均需通过该端口执行；
9. **测试账号**：统一使用账号 `cbl` 和密码 `SunmaxCbl` 进行登录验证。

### 1.3 现状问题摘要

| 现状问题 | 影响 | 对应方案模块 |
|----------|------|--------------|
| UUID token 无法本地验签、日志泄露即被盗 | 安全风险 | 模块A |
| Refresh Token 30天可重复使用 | 安全风险 | 模块A |
| `RedisTokenAuthenticationFilter` 固定 `ROLE_USER` | 角色越权 | 模块B |
| 每次请求 Feign 查权限 | 性能瓶颈 | 模块C |
| 权限仅校验 URI，不校验 Method | 越权风险 | 模块D |
| `/feign/**` 全放行 | 横向越权 | 模块E |
| `configure-service` 完全放行 | 认证失效 | 模块F |
| 10 服务 `UserResourceConfig` 重复 | 维护成本 | 模块G |
| `MobilePasswordSystemUserTokenGranter` 等废弃 | 维护成本 | 模块H |
| 未认证返回 HTTP 200 | 监控盲区 | 模块I |
| 安全事件无审计 | 合规风险 | 模块J |
| 前端 Token 注入到 `config.data.access_token` | 不符合规范、JWT 过长导致 GET URL 超限 | 模块K |
| 前端无 Refresh Token 逻辑 | 用户频繁重新登录 | 模块K |
| 前端无登出接口调用 | 服务端 token 不失效 | 模块K |
| 前端仅检查业务码 9999，不处理 HTTP 401/403 | 监控盲区、不符合 REST 规范 | 模块K |
| linkos `clientId` 为 `sunos-client` | 已注册，保持不变 | 不需修改 |

### 1.4 优化目标

1. **安全**：消除 P0 级认证越权与配置放行问题；
2. **性能**：权限校验从「每请求远程调用」降为「本地缓存命中」；
3. **可维护**：公共配置下沉到 `sunmax-common`，10 服务配置收敛至 ≤20 行；
4. **兼容**：前端调用方式与响应结构保持不变，平滑迁移；
5. **可扩展**：支持新增平台 `clientId` 时零代码改动。

---

## 二、总体技术方案

### 2.1 架构演进思路

```
现状：  前端 → OauthController(自定义) → UUID Token → Redis → Feign查权限(每请求)
          ↓
目标：  前端 → OauthController(适配层) → JWT(RSA签名) → 本地验签 + Redis权限缓存
                                         ↓
                                    Refresh Token 轮换
```

**关键决策：**

| 决策项 | 方案 | 理由 |
|--------|------|------|
| 令牌载体 | JWT（RSA 签名）+ Redis 黑名单 | 自包含验签，减少 Redis 依赖；黑名单支持主动吊销 |
| 是否废弃 `/oauth/token` | 保留作为适配层 | 前端兼容，内部委托 Spring Authorization Server 签发 |
| 权限缓存 | Redis（登录时写入，TTL 与 token 一致） | 权限变更可主动失效；避免 DB 压力 |
| 角色传递 | JWT claim `roles` + `clientId` | 过滤器本地构建 `GrantedAuthority` |
| 权限模型 | URL + Method 二维校验 | 防止 GET 接口被 POST 越权调用 |

### 2.2 clientId 全链路设计

```
┌─────────┐   1.登录携带clientId    ┌──────────────┐
│  前端   │ ──────────────────────→ │ OauthController│
└─────────┘                         └──────┬───────┘
     ↑                                     │ 2.查b_product(clientId)
     │                                     ↓
     │                              ┌──────────────┐
     │                              │ UserLoginService│
     │                              └──────┬───────┘
     │                                     │ 3.按平台过滤权限
     │                                     ↓
     │  6.响应含menuList(平台菜单)   ┌──────────────┐
     └─────────────────────────────│ buildTokenResp │
                                    └──────┬───────┘
                                           │ 4.JWT claim: clientId, roles, perms版本号
                                           ↓
                                    ┌──────────────┐
                                    │   Redis缓存   │ ← auth:perms:{userId}:{clientId}
                                    └──────┬───────┘
                                           │ 5.权限URL+Method列表
┌─────────┐   7.请求带JWT              ┌──────────────┐
│  前端   │ ─────────────────────────→│RedisTokenFilter│
└─────────┘                           └──────┬───────┘
                                            │ 8.本地验签+查Redis权限缓存
                                            ↓
                                     ┌──────────────┐
                                     │UserResourceConfig│
                                     └──────────────┘
```

**关键约束：**

- `clientId` 在 JWT claim 中存储，过滤器解析后写入 `Authentication.details`；
- 权限缓存 key：`auth:perms:{userId}:{clientId}`，TTL 与 access token 一致（3天）；
- 权限变更时通过 `auth:perms:invalidate:{userId}` 发布失效信号；
- 踢出旧 token 维度：`auth:user:token:{clientId}:{userId}`（保持现状逻辑）。

---

## 三、技术实现方案（分模块）

### 模块A：JWT 令牌体系 + Refresh Token 轮换

**目标：** 替换 UUID token 为 JWT，实现本地验签；Refresh Token 一次性使用。

**实现要点：**

1. **复用现有 RSA JWK 配置**
   - `AuthorizationServerConfigurer.jwkSource()` 已生成 RSA 密钥对，直接复用；
   - 新增 `JwtTokenService`（位于 `sunmax-common`），封装签发与验签逻辑。

2. **Access Token 结构（JWT）**

   ```json
   {
     "sub": "userAccount",
     "id": "userId",
     "userRole": 0,
     "tenantId": "xxx",
     "clientId": "sunos-client",
     "roles": ["ROLE_PLATFORM_ADMIN"],
     "permsVer": "20260626001",
     "iat": 1730000000,
     "exp": 1730259200
   }
   ```

3. **Refresh Token 轮换**
   - Refresh Token 同样为 JWT，但 `exp` 为 30 天，claim `type=refresh`；
   - 每次刷新：
     - 校验旧 refresh token 有效性 + 是否在黑名单；
     - 签发新 access + 新 refresh；
     - 旧 refresh token 加入 Redis 黑名单（`auth:refresh:blacklist:{oldToken}`，TTL = 剩余有效期）；
   - 新增 `/oauth/refresh_token` 端点（或复用 `grant_type=refresh_token`）。

4. **黑名单机制**
   - 登出时：access token 加入 `auth:access:blacklist:{token}`，TTL = 剩余有效期；
   - 过滤器验签后检查黑名单。

**涉及文件：**

- 新增：`sunmax-common/.../security/JwtTokenService.java`
- 新增：`sunmax-common/.../security/TokenBlacklistService.java`
- 修改：`auth-service/.../controller/OauthController.java` `buildTokenResponse`、新增 `refreshToken` 方法
- 修改：`sunmax-common/.../config/RedisTokenAuthenticationFilter.java` 改为 JWT 验签

### 模块B：clientId 平台标识与角色贯通

**目标：** JWT 携带真实角色，过滤器构建正确 `GrantedAuthority`。

**实现要点：**

1. **角色映射规则**

   | userRole | 角色 | 权限范围 |
   |----------|------|----------|
   | 0 | `ROLE_PLATFORM_ADMIN` | 全平台 |
   | 1 | `ROLE_ADMIN` | 租户授权 |
   | 2 | `ROLE_USER` | 用户组授权 |

2. **`OauthController.buildTokenResponse` 改造**
   - 签发 JWT 时写入 `roles` claim（根据 `userRole` 映射）；
   - `clientId` claim 必填，缺失则拒绝登录。

3. **`RedisTokenAuthenticationFilter` 改造**
   - JWT 验签后，从 `roles` claim 构建 `List<SimpleGrantedAuthority>`；
   - 不再固定 `ROLE_USER`；
   - `Authentication.details` 仍保留完整用户 JSON（兼容 `MainController` / `UserResourceConfig.getClientId`）。

4. **clientId 强制校验**
   - `validateClient` 已实现：clientId 为空拒绝、未注册拒绝；
   - 补充：`handleSysPwdLogin` 中 `effectiveClientId` 不再回退到默认 `sunos-client`，必须使用前端传入的 clientId。

**涉及文件：**

- 修改：`auth-service/.../controller/OauthController.java`
- 修改：`sunmax-common/.../config/RedisTokenAuthenticationFilter.java`

### 模块C：权限校验本地化

**目标：** 消除每次请求的 Feign 远程调用，权限数据缓存到 Redis。

**实现要点：**

1. **登录时写入权限缓存**
   - `buildTokenResponse` 中，将 `menuList` 中 `type=2`（控件/接口）的权限 URL+Method 列表写入 Redis：
     - Key：`auth:perms:{userId}:{clientId}`
     - Value：JSON `[{"url":"/xxx","method":"GET"}, ...]`
     - TTL：与 access token 一致（3天）

2. **过滤器本地读取**
   - `UserResourceConfig.canAccess` 改为从 Redis 读取权限列表，本地匹配；
   - Feign 调用仅作为缓存未命中时的回源（fallback）。

3. **权限版本号机制**
   - JWT claim `permsVer` 标识权限版本；
   - Redis 维护 `auth:perms:ver:{userId}` 当前版本号；
   - 权限变更（管理员调整角色/用户组）时，版本号 +1，缓存失效；
   - 过滤器校验时比对版本号，不一致则强制刷新缓存。

**涉及文件：**

- 新增：`sunmax-common/.../security/PermissionCacheService.java`
- 修改：`auth-service/.../controller/OauthController.java`
- 修改：10 个服务的 `UserResourceConfig.canAccess`

### 模块D：权限模型增强（HTTP Method + 数据权限）

**目标：** URL + Method 二维校验；引入数据权限 claim。

**实现要点：**

1. **数据库表扩展**
   - `b_permission` 表新增 `http_method` 字段（varchar(10)），存储 `GET/POST/PUT/DELETE/*`；
   - 存量数据默认 `*`（兼容）；
   - 提供 DDL 迁移脚本。

2. **权限匹配规则**

   ```java
   boolean match = permission.getUrl().equals(requestURI)
                && (permission.getMethod().equals("*")
                    || permission.getMethod().equalsIgnoreCase(requestMethod));
   ```

3. **数据权限 claim**
   - JWT 携带 `tenantId`、`organId`；
   - 业务层通过 AOP 注解 `@DataScope` 做数据范围过滤（本阶段预留接口，不强制全量落地）。

**涉及文件：**

- 修改：`auth-service/.../entity/PermissionEntity.java`
- 修改：`UserResourceConfig.canAccess` 匹配逻辑
- 新增：DDL 迁移脚本 `auth-service/src/main/resources/db/migration/V2026062601__add_permission_http_method.sql`

### 模块E：Feign 接口加固

**目标：** `/feign/**` 不再无条件放行，增加服务间认证。

**实现要点：**

1. **网关层剥离**
   - `sunmax-gateway` 路由配置中不暴露 `/feign/**` 路径，外部请求无法直达。

2. **服务间认证（service account JWT）**
   - 每个服务配置一个内部 `clientId`（如 `internal-system-service`）；
   - Feign 拦截器自动在请求头添加 `Authorization: Bearer <internal-jwt>`；
   - 各服务 `UserResourceConfig` 对 `/feign/**` 校验内部 JWT，拒绝外部请求。

3. **兼容方案**
   - 过渡期：`/feign/**` 增加 IP 白名单（仅允许容器网段）；
   - 长期：mTLS 或 service account。

**涉及文件：**

- 新增：`sunmax-common/.../feign/InternalAuthFeignInterceptor.java`
- 修改：各服务 `UserResourceConfig` 中 `/feign/**` 规则
- 修改：`sunmax-gateway` 路由配置

### 模块F：configure-service 安全修复

**目标：** 修复 `SecurityConfig` 完全放行问题。

**实现要点：**

1. **删除现有 `SecurityConfig`**
   - `configure-service/.../config/SecurityConfig.java` 中 `anyRequest().permitAll()` 使认证失效。

2. **接入统一安全配置**
   - 新建 `configure-service` 的 `UserResourceConfig`，继承模块G的 `AbstractUserResourceConfig`；
   - 接入 `RedisTokenAuthenticationFilter`；
   - 接入 `canAccess` 权限校验。

3. **平台密钥保留现状**
   - 按 R7 排除，密钥外置不在本次范围；
   - 但 `SecurityConfig` 放行问题必须修复（属 SEC-01，非密钥管理）。

**涉及文件：**

- 删除：`configure-service/.../config/SecurityConfig.java`
- 新增：`configure-service/.../config/UserResourceConfig.java`

### 模块G：公共安全配置抽取

**目标：** 10 服务 `UserResourceConfig` 收敛到公共基类。

**实现要点：**

1. **新增 `AbstractUserResourceConfig`**
   - 位于 `sunmax-common/.../config/AbstractUserResourceConfig.java`；
   - 提供 `SecurityFilterChain` 模板方法；
   - 子类只需实现 `configurePermitPaths()`（特殊放行路径）和 `configureAdditionalRules()`（扩展规则）。

2. **子类示例（system-service）**

   ```java
   @Configuration
   public class UserResourceConfig extends AbstractUserResourceConfig {
       @Override
       protected String[] configurePermitPaths() {
           return new String[]{"/doc.html", "/swagger-ui/**", "/v3/api-docs/**", "/feign/**", "/actuator/health"};
       }
   }
   ```

3. **迁移路径**
   - 逐服务替换，每次替换后验证。

**涉及文件：**

- 新增：`sunmax-common/.../config/AbstractUserResourceConfig.java`
- 修改：10 个服务的 `UserResourceConfig` / `UserResourceConfiguration`

### 模块H：废弃代码清理

**目标：** 清理未使用的 `AuthenticationProvider` 实现与注释代码。

**实现要点：**

1. **确认废弃**
   - `MobilePasswordSystemUserTokenGranter`、`MobileAppletCustomTokenGranter` 未被 `AuthorizationServerConfigurer` 注册；
   - `OauthController` 完全不走 Spring Security 认证流程。

2. **处理方式**
   - 若模块A落地（JWT 令牌）：将 `MobilePasswordSystemUserTokenGranter` 接入标准 `AuthenticationProvider`，作为 `sys_pwd` grant type 的实现；
   - 否则：删除两个类，清理 `AuthorizationServerConfigurer` 中相关注释。

**涉及文件：**

- 评估后删除或重构：`auth-service/.../granter/MobilePasswordSystemUserTokenGranter.java`、`MobileAppletCustomTokenGranter.java`

### 模块I：异常处理与响应标准化

**目标：** 细化异常；未认证返回 401，无权限返回 403。

**实现要点：**

1. **`SMAuthenticationEntryPoint` 改造**
   - `sunmax-common/.../config/SMAuthenticationEntryPoint.java` 返回 HTTP 401；
   - Body 保留业务码 `9999` 供前端兼容。

2. **`SMAccessDeniedHandler` 改造**
   - 返回 HTTP 403。

3. **`OauthController` 异常细化**
   - `OauthController.java` `catch (RuntimeException)` 改为：
     - `BadCredentialsException` → 401 业务码 5002
     - `ClientAuthenticationException` → 401 业务码 4001
     - 其他 → 500 业务码 5000

4. **`/oauth/logout` 身份校验**
   - `OauthController.java` `/oauth/logout` 要求当前认证用户与 token 中 userId 一致。

**涉及文件：**

- 修改：`SMAuthenticationEntryPoint`、`SMAccessDeniedHandler`、`OauthController`

### 模块J：审计日志

**目标：** 登录/登出/权限变更/敏感操作全记录。

**实现要点：**

1. **审计事件类型**
   - `LOGIN_SUCCESS`、`LOGIN_FAIL`、`LOGOUT`、`TOKEN_REFRESH`、`PERMISSION_CHANGE`

2. **日志结构**

   ```json
   {"eventType":"LOGIN_SUCCESS","userId":"xxx","userAccount":"xxx",
    "clientId":"sunos-client","ip":"1.2.3.4","userAgent":"xxx","timestamp":"..."}
   ```

3. **实现方式**
   - `OauthController` 中埋点；
   - 统一输出到日志文件 `auth-audit.log`，由日志平台采集。

**涉及文件：**

- 新增：`sunmax-common/.../audit/AuditLogger.java`
- 修改：`OauthController`

### 模块K：前端登录接口适配

**目标：** 前端三个项目（linkos / derms / tycvs）适配后端令牌体系与鉴权方式变更。

**现状分析（基于代码审查）：**

| 项目 | 登录接口 | clientId | Token 传递方式 | Refresh | Logout | 401/403 处理 |
|------|----------|----------|----------------|---------|--------|--------------|
| linkos | `/sauth/oauth/token` | 硬编码 `sunos-client` | `config.data.access_token` | 无 | 无 | 仅检查业务码 9999 |
| derms | `/sauth/oauth/token` | props 传入（`iems-client` 等） | `config.data.access_token` | 无 | 无 | 仅检查业务码 9999 |
| tycvs | `/sauth/oauth/token` | 硬编码 `tycvs-client` | `config.data.access_token` | 无 | 无 | 仅检查业务码 9999 |

**关键代码位置：**

- 公共 HTTP 客户端：`elink-web/packages/shared/src/http/request.ts`（Token 注入逻辑在 L130-L160）
- 公共 Auth 管理器：`elink-web/packages/shared/src/auth/index.ts`（Cookie 存储）
- linkos 登录页：`elink-web/linkos/src/views/login/components/AccountPasswordLogin.vue`（L94-L102 调用登录）
- derms 登录页：`elink-web/derms/src/views/login/_components/loginForm.vue`（L140-L150 调用登录）
- tycvs 登录页：`elink-web/tycvs/src/views/login/components/AccountPasswordLogin.vue`（L93-L101 调用登录）
- 登录 API：各项目 `src/api/login/login.ts`

**实现要点：**

1. **Token 传递方式改造**

   修改 `elink-web/packages/shared/src/http/request.ts` 请求拦截器：
   - 移除将 `access_token` 注入 `config.data` 的逻辑（L136、L147、L157）
   - 改为设置 `config.headers.Authorization = 'Bearer ' + auth.getToken()`
   - 保留 `userId`、`tenantId` 注入 `config.data` 的逻辑（业务需要）
   - 过渡期：同时支持 Header 与参数两种方式，后端逐步移除参数方式

2. **HTTP 401/403 状态码处理**

   修改 `elink-web/packages/shared/src/http/request.ts` 响应拦截器：
   - 在 `error` 拦截分支增加 HTTP 状态码判断：
     - `401`：Token 无效/过期，触发 Refresh Token 或跳转登录
     - `403`：权限不足，提示"无操作权限"
   - 保留现有业务码 9999 处理（过渡期兼容）

3. **Refresh Token 自动刷新**

   新增 `elink-web/packages/shared/src/http/tokenRefresh.ts`：
   - 监听 401 响应，检查是否有 `refreshToken`（存于 localStorage `USER_INFO`）
   - 调用 `POST /sauth/oauth/token`（`grant_type=refresh_token`）获取新 token
   - 刷新成功：重发原请求
   - 刷新失败：清除 token，跳转登录页
   - 并发请求队列：刷新期间其他 401 请求排队等待，刷新成功后统一重发

4. **登出接口调用**

   新增 `elink-web/packages/shared/src/api/auth.ts`（或各项目 `src/api/login/login.ts`）：
   ```typescript
   export function logout() {
     return request({
       url: '/sauth/oauth/logout',
       method: 'post',
       noLoginRequired: true  // 登出接口本身需要 token，但不再注入 userId 等
     })
   }
   ```
   - 各项目在用户主动登出（菜单按钮）或检测到 token 失效时调用
   - 登出后清除 Cookie token、localStorage `USER_INFO`、`AUTH_ROUTER`

5. **linkos clientId 保持不变**

   `elink-web/linkos/src/views/login/components/AccountPasswordLogin.vue` 中 `clientId` 保持 `sunos-client`：
   - `client_id: "sunos-client"`（不变）
   - `client_secret: "sunos-client"`（不变）
   - `sunos-client` 已在 `oauth2_registered_client` 表注册，`b_product` 表有对应产品模块数据

6. **derms clientId 统一管理**

   derms 现已通过 props 传入 `clientId`，保持不变。补充：
   - 将 `clientId` 存入 localStorage `USER_INFO`，供请求拦截器使用（后端从 JWT 读取，前端可选）
   - 不同 `clientId` 对应不同登录入口：`derms-client`（运营管理平台）、`microgrid-client`（单站点登录）
   - **注意**：`externalJump.vue` 中使用的 `iems-client` 未在 `oauth2_registered_client` 表注册，需修正为 `derms-client`

7. **登录响应字段适配**

   登录成功后，将 `refreshToken` 存入 localStorage `USER_INFO`：
   ```javascript
   // 修改前
   setToken(userInfo.accessToken)
   localStorage.setItem("USER_INFO", JSON.stringify(userInfo))

   // 修改后
   setToken(userInfo.accessToken)
   // refreshToken 已包含在 userInfo 中，无需额外处理
   localStorage.setItem("USER_INFO", JSON.stringify(userInfo))
   ```

8. **Token 过期时间感知（可选）**

   前端可读取 `expiresIn` 字段，在 token 即将过期前主动刷新（如过期前 5 分钟）。

**涉及文件：**

- 修改：`elink-web/packages/shared/src/http/request.ts`（Token 传递、401/403 处理）
- 新增：`elink-web/packages/shared/src/http/tokenRefresh.ts`（Refresh Token 逻辑）
- 新增：`elink-web/packages/shared/src/api/auth.ts`（登出 API）
- 修改：`elink-web/linkos/src/views/login/components/AccountPasswordLogin.vue`（clientId 修正）
- 修改：`elink-web/derms/src/views/login/_components/loginForm.vue`（refreshToken 存储）
- 修改：`elink-web/tycvs/src/views/login/components/AccountPasswordLogin.vue`（refreshToken 存储）
- 修改：各项目 `src/api/login/login.ts`（新增 logout、refreshToken API）

---

## 四、接口设计

### 4.1 登录接口（保持兼容）

```
POST /sauth/oauth/token
Content-Type: application/x-www-form-urlencoded

grant_type=sys_pwd&client_id=sunos-client&client_secret=xxx&userAccount=cbl&password=<AES加密>
```

**响应（保持现有结构，新增字段向后兼容）：**

```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "accessToken": "<JWT>",
    "refreshToken": "<JWT>",
    "tokenType": "Bearer",
    "expiresIn": 259200,
    "userAccount": "cbl",
    "fullName": "管理员",
    "id": "xxx",
    "tenantId": "xxx",
    "tenantName": "xxx",
    "userRole": 0,
    "isDefaultAdmin": 1,
    "clientId": "sunos-client",
    "menuList": [...]
  }
}
```

**前端适配要点：**

- `client_id` 取值与前端项目对应：linkos=`sunos-client`、derms=动态传入（如 `iems-client`/`microgrid-client`）、tycvs=`tycvs-client`；
- `accessToken` / `refreshToken` 由 UUID 改为 JWT（前端无感知，但 `refreshToken` 必须存入 localStorage `USER_INFO` 供刷新使用）；
- 新增 `tokenType`、`expiresIn`（前端可选用）；
- 新增 `clientId` 回显（前端可校验）；
- 登录成功后前端需保存 `refreshToken`，否则无法自动刷新。

### 4.2 刷新令牌接口

```
POST /sauth/oauth/token
grant_type=refresh_token&refresh_token=<旧refreshToken>&client_id=sunos-client
```

**响应：** 同登录，返回新的 access + refresh token。

**前端适配要点：**

- 前端响应拦截器收到 HTTP 401 时调用此接口；
- 刷新成功后，更新 Cookie 中的 `accessToken` 与 localStorage `USER_INFO` 中的 `refreshToken`；
- 刷新期间并发的 401 请求需排队等待，刷新成功后统一重发（避免并发刷新）；
- 刷新接口本身失败（HTTP 401）时，直接跳转登录页，不再尝试刷新。

### 4.3 登出接口（增加身份校验）

```
POST /sauth/oauth/logout
Authorization: Bearer <accessToken>
```

**响应：**

```json
{"code": 0, "msg": "登出成功"}
```

**前端适配要点：**

- 前端调用登出时必须通过 `Authorization` Header 传递 token（不再使用 `access_token` 参数）；
- 登出成功后前端需清除：Cookie token、localStorage `USER_INFO`、localStorage `AUTH_ROUTER`；
- 调用登出接口后才执行 `location.reload()`，确保服务端 token 已失效；
- 当前前端三个项目均未调用登出接口（仅清除本地存储），需新增调用。

### 4.4 权限查询接口（Feign，内部）

```
POST /sauth/feign/permission/findPermissionByUserAccount
userAccount=cbl&clientId=sunos-client
```

**响应新增 `method` 字段：**

```json
{
  "code": 0,
  "data": [
    {"id": "xxx", "url": "/system/user/list", "method": "GET", "type": 2, ...}
  ]
}
```

### 4.5 Token 校验接口（保持兼容）

```
GET /sauth/oauth/check_token?access_token=<JWT>
```

**响应：** 返回 JWT 解析后的 claim 集合。

---

## 五、数据流程

### 5.1 登录流程

```
1. 前端 → POST /oauth/token (grant_type=sys_pwd, clientId, userAccount, password[AES])
2. OauthController.validateClient(clientId, clientSecret)
   ├─ clientId 为空 → 拒绝
   ├─ RegisteredClient 未找到 → 拒绝
   └─ clientSecret 非空 → BCrypt 校验
3. UserLoginServiceImpl.loadSysUserByAccountAndPassword
   ├─ 查 b_user by userAccount
   ├─ SecretUtil.desEncrypt(password) → 明文密码
   ├─ equals 比对（注：R5排除，暂不哈希化）
   ├─ 账号到期校验
   ├─ 查 b_product by clientId → 产品模块
   ├─ 按 userRole 查权限：
   │   ├─ 0 平台管理员：b_permission by moduleId
   │   ├─ 1 管理员：b_tenant_apply_empower + b_permission
   │   └─ 2 普通用户：b_group_apply_empower + b_permission
   └─ 过滤 type != 4，构建 menuList
4. OauthController.buildTokenResponse
   ├─ 根据 userRole 映射 roles
   ├─ 签发 JWT access token (3天, claim: sub/id/userRole/clientId/roles/permsVer)
   ├─ 签发 JWT refresh token (30天, type=refresh)
   ├─ 踢出旧 token: auth:user:token:{clientId}:{userId}
   ├─ 写权限缓存: auth:perms:{userId}:{clientId} → [url+method列表]
   ├─ 写 token 索引: auth:user:token:{clientId}:{userId} → accessToken
   └─ 审计日志: LOGIN_SUCCESS
5. 响应前端（结构兼容）
```

### 5.2 请求鉴权流程

```
1. 前端 → 任意业务接口 (Authorization: Bearer <JWT>)
2. RedisTokenAuthenticationFilter
   ├─ 提取 JWT
   ├─ RSA 公钥验签（本地，不查 Redis）
   ├─ 检查黑名单: auth:access:blacklist:{token}
   ├─ 解析 claim: userAccount/userRole/clientId/roles/permsVer
   ├─ 构建 Authentication:
   │   ├─ principal = userAccount
   │   ├─ authorities = [ROLE_PLATFORM_ADMIN / ROLE_ADMIN / ROLE_USER]
   │   └─ details = 完整用户 JSON
   └─ 写入 SecurityContext
3. UserResourceConfig.canAccess
   ├─ 从 details 取 clientId
   ├─ 读 Redis: auth:perms:{userId}:{clientId}
   │   ├─ 命中 → 本地匹配 URL + Method
   │   └─ 未命中 → Feign 回源 findPermissionByUserAccount，写缓存
   ├─ 比对 permsVer（JWT claim vs Redis 当前版本）
   │   └─ 不一致 → 强制刷新缓存
   └─ 返回 AuthorizationDecision
```

### 5.3 权限变更生效流程

```
管理员调整用户角色/用户组
  ├─ auth-service 更新 DB
  ├─ auth:perms:ver:{userId} ← 版本号 +1
  ├─ 删除 auth:perms:{userId}:{clientId}（所有平台）
  └─ 用户下次请求时，过滤器发现 permsVer 不一致 → 重新拉取权限 → 写缓存
```

---

## 六、异常处理

### 6.1 异常分类与处理策略

| 异常类型 | HTTP 状态码 | 业务码 | 处理 | 前端表现 |
|----------|-------------|--------|------|----------|
| clientId 为空 | 400 | 4001 | 拒绝登录 | 提示"客户端标识缺失" |
| 客户端未注册 | 401 | 4002 | 拒绝登录 | 提示"客户端未授权" |
| clientSecret 错误 | 401 | 4003 | 拒绝登录 | 提示"客户端认证失败" |
| 用户名/密码错误 | 401 | 5002 | 拒绝登录 | 提示"用户名或密码错误" |
| 账号已过期 | 401 | 5003 | 拒绝登录 | 提示"账号已过期" |
| 账号已冻结 | 401 | 5004 | 拒绝登录 | 提示"账号已冻结" |
| Token 无效/过期 | 401 | 9999 | 跳转登录 | 跳转登录页 |
| Token 验签失败 | 401 | 9998 | 跳转登录 | 跳转登录页 |
| 权限不足 | 403 | 9997 | 提示无权限 | 提示"无操作权限" |
| Refresh Token 已用 | 401 | 5005 | 跳转登录 | 重新登录 |
| 系统异常 | 500 | 5000 | 记录日志 | 提示"系统繁忙" |

### 6.2 异常处理实现

```java
// OauthController 异常处理（示意，非最终代码）
try {
    switch (grantType) {
        case "sys_pwd": return handleSysPwdLogin(...);
        case "applet": return handleAppletLogin(...);
        default: return ResponseResult.paramError("不支持的授权类型: " + grantType);
    }
} catch (BadCredentialsException e) {
    auditLogger.log("LOGIN_FAIL", ...);
    return ResponseResult.error(5002, "用户名或密码错误");
} catch (ClientAuthenticationException e) {
    auditLogger.log("LOGIN_FAIL", ...);
    return ResponseResult.error(4002, "客户端认证失败");
} catch (Exception e) {
    log.error("登录处理异常", e);
    return ResponseResult.error(5000, "系统繁忙");
}
```

### 6.3 Feign 降级

- `findPermissionByUserAccount` Feign 失败时：
  - 缓存命中 → 继续使用缓存（即使版本号不一致也容忍）；
  - 缓存未命中 → 拒绝访问（fail-close）；
  - 记录告警日志。

---

## 七、兼容性考虑

### 7.1 前端兼容

| 变更项 | 兼容策略 | 影响前端 |
|--------|----------|----------|
| UUID → JWT | 前端无需改动，token 仍为字符串 | 无 |
| 登录响应结构 | 新增字段，不删除现有字段 | 无 |
| Token 传递方式 | 过渡期后端同时支持 Header 与参数；1个月后仅支持 Header | 需前端适配（提供过渡期） |
| 登出参数 | 从 `access_token` 参数改为 Header | 需前端适配（提供过渡期） |
| HTTP 401/403 | 前端 axios 拦截器需处理 401 触发刷新/跳转登录 | 需前端适配 |
| Refresh Token | 新增前端刷新逻辑，避免用户频繁重登 | 需前端适配 |
| 登出接口调用 | 前端需在登出时调用后端接口 | 需前端适配 |
| 权限 Method 维度 | 前端无感知 | 无 |

### 7.2 过渡期策略

1. **令牌双轨（2周）**
   - 同时支持 UUID token（旧）与 JWT（新）；
   - 过滤器优先尝试 JWT 验签，失败则回退 UUID 查 Redis；
   - 2周后全部用户 token 自然过期，移除 UUID 支持。

2. **Token 传递方式双轨（1个月）**
   - 后端同时支持 `Authorization: Bearer` Header 与 `config.data.access_token` 参数；
   - 前端可分批迁移到 Header 方式；
   - 1个月后仅支持 Header。

3. **登出参数兼容（1个月）**
   - 同时支持 `access_token` 参数与 `Authorization` Header；
   - 1个月后仅支持 Header。

4. **前端 Refresh Token 灰度（2周）**
   - 先在 derms 单项目试点，验证通过后推广至 linkos、tycvs；
   - 灰度期间若刷新失败，回退到原"跳转登录"逻辑。

5. **权限缓存回源（持续）**
   - Redis 缓存未命中时 Feign 回源，保证不因缓存问题阻断请求。

### 7.3 数据库兼容

- `b_permission` 新增 `http_method` 字段，默认 `*`，存量数据无影响；
- 不修改现有表结构，仅新增字段。

### 7.4 客户端兼容

- 现有 `oauth2_registered_client` 表中已注册客户端保持不变；
- 新增平台时，只需在表中插入新 `clientId` 记录，无需代码改动。

---

## 八、测试策略

### 8.1 单元测试

| 测试项 | 测试点 | 预期 |
|--------|--------|------|
| `JwtTokenService` | 签发/验签/过期 | 正确签发、正确验签、过期拒绝 |
| `TokenBlacklistService` | 加入黑名单/查询 | 黑名单命中、TTL 正确 |
| `PermissionCacheService` | 写入/读取/失效 | 缓存命中、版本号失效 |
| `validateClient` | 空 clientId/未注册/密钥错误 | 分别返回 4001/4002/4003 |
| `canAccess` | URL+Method 匹配 | GET 接口 POST 调用被拒 |

### 8.2 集成测试

| 测试场景 | 步骤 | 预期 |
|----------|------|------|
| linkos 平台登录 | clientId=sunos-client | 返回 linkos 菜单 + JWT |
| derms 平台登录 | clientId=derms-client | 返回 derms 菜单 + JWT |
| 同用户跨平台 | linkos + derms 分别登录 | 两 token 均有效 |
| 同用户同平台重复登录 | linkos 登录两次 | 旧 token 失效 |
| 权限变更生效 | 管理员调整角色 → 用户请求 | permsVer 不一致 → 刷新缓存 → 新权限生效 |
| Refresh Token 轮换 | 刷新一次 | 旧 refresh 失效，新 token 有效 |
| configure-service 鉴权 | 无 token 请求 | 返回 401 |
| /feign/** 外部访问 | 网关外请求 /feign/** | 404 或 401 |
| 登出身份校验 | A 用户登出 B 用户 token | 403 |

### 8.3 性能测试

| 指标 | 现状 | 目标 |
|------|------|------|
| 权限校验 RT | ~30-50ms（Feign） | <5ms（本地缓存） |
| 登录 RT | ~200ms | <200ms（增加缓存写入，但消除首次请求 Feign） |
| 并发 1000 QPS 鉴权 | Feign 雪崩风险 | 本地验签，无外部依赖 |

### 8.4 安全测试

- JWT 伪造测试（篡改 payload → 验签失败）；
- JWT `alg=none` 攻击测试；
- 越权测试（普通用户访问管理员接口）；
- 横向越权测试（A 租户访问 B 租户数据）；
- Token 重放测试（登出后旧 token 不可用）。

---

## 九、关键文件清单

| 类别 | 文件 | 操作 |
|------|------|------|
| 新增 | `sunmax-common/.../security/JwtTokenService.java` | JWT 签发验签 |
| 新增 | `sunmax-common/.../security/TokenBlacklistService.java` | 黑名单管理 |
| 新增 | `sunmax-common/.../security/PermissionCacheService.java` | 权限缓存 |
| 新增 | `sunmax-common/.../config/AbstractUserResourceConfig.java` | 公共安全配置 |
| 新增 | `sunmax-common/.../feign/InternalAuthFeignInterceptor.java` | Feign 内部认证 |
| 新增 | `sunmax-common/.../audit/AuditLogger.java` | 审计日志 |
| 新增 | `configure-service/.../config/UserResourceConfig.java` | 配置服务安全 |
| 新增 | `auth-service/.../db/migration/V2026062601__add_permission_http_method.sql` | DDL 迁移 |
| 修改 | `auth-service/.../controller/OauthController.java` | JWT 签发、refresh 轮换、登出校验 |
| 修改 | `sunmax-common/.../config/RedisTokenAuthenticationFilter.java` | JWT 验签、角色构建 |
| 修改 | `auth-service/.../service/impl/UserLoginServiceImpl.java` | 权限数据含 Method |
| 修改 | `auth-service/.../entity/PermissionEntity.java` | 新增 httpMethod 字段 |
| 修改 | 10 个服务 `UserResourceConfig` | 继承公共基类、本地权限校验 |
| 修改 | `SMAuthenticationEntryPoint` / `SMAccessDeniedHandler` | HTTP 401/403 |
| 删除 | `configure-service/.../config/SecurityConfig.java` | 被 `UserResourceConfig` 替代 |
| 评估 | `MobilePasswordSystemUserTokenGranter` / `MobileAppletCustomTokenGranter` | 接入或删除 |
| 修改 | `elink-web/packages/shared/src/http/request.ts` | Token 传递改为 Header、401/403 处理 |
| 新增 | `elink-web/packages/shared/src/http/tokenRefresh.ts` | Refresh Token 自动刷新 |
| 新增 | `elink-web/packages/shared/src/api/auth.ts` | 登出 API |
| 修改 | `elink-web/linkos/src/views/login/components/AccountPasswordLogin.vue` | clientId 修正 |
| 修改 | `elink-web/derms/src/views/login/_components/loginForm.vue` | refreshToken 存储 |
| 修改 | `elink-web/tycvs/src/views/login/components/AccountPasswordLogin.vue` | refreshToken 存储 |
| 修改 | 各项目 `src/api/login/login.ts` | 新增 logout、refreshToken API |

---

## 十、预期效果

### 10.1 安全效果

| 指标 | 现状 | 预期 |
|------|------|------|
| `configure-service` 认证 | 完全放行 | 强制鉴权 |
| 令牌安全性 | UUID 日志泄露即被盗 | JWT 验签，黑名单可吊销 |
| Refresh Token | 30天可重复使用 | 一次性，轮换失效 |
| 角色权限 | 固定 `ROLE_USER` | 真实角色映射 |
| `/feign/**` | 全放行 | 内部认证 + 网关剥离 |
| 登出越权 | 可登出他人 | 身份校验 |

### 10.2 性能效果

| 指标 | 现状 | 预期 | 提升 |
|------|------|------|------|
| 权限校验 RT | 30-50ms | <5ms | ~90% |
| Feign 调用次数/请求 | 1 次 | 0 次（缓存命中） | 100% |
| 并发鉴权能力 | 受 Feign 限制 | 本地验签 | 显著提升 |

### 10.3 可维护性效果

| 指标 | 现状 | 预期 |
|------|------|------|
| 安全配置代码行数 | 10 服务 × ~100 行 | 10 服务 × ~20 行 + 公共 1 份 |
| 废弃代码 | 2 个未使用类 | 清理 |
| 异常处理 | `catch (RuntimeException)` | 分类处理 |
| 审计日志 | 无 | 全量记录 |

### 10.4 可扩展性效果

| 场景 | 现状 | 预期 |
|------|------|------|
| 新增平台 clientId | 需评估代码改动 | 仅插入数据库记录 |
| 新增角色 | 修改过滤器硬编码 | 配置化映射 |
| 权限变更生效 | 重新登录 | 实时生效（版本号机制） |

### 10.5 前端体验效果

| 指标 | 现状 | 预期 |
|------|------|------|
| Token 传递 | `config.data.access_token`（GET URL 超限风险） | `Authorization` Header（规范、无长度限制） |
| Token 过期处理 | 直接跳转登录页 | 静默刷新，用户无感知 |
| 登出 | 仅清除本地存储 | 调用后端接口，服务端 token 失效 |
| HTTP 错误处理 | 仅检查业务码 9999 | 区分 401/403，错误信息更明确 |
| 长会话体验 | 30 天内必须重登 | Refresh Token 持续刷新，长期在线 |
| linkos 平台标识 | 保持 `sunos-client` 不变 | 已注册，无需修改 |
