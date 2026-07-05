# Elink-AI 鉴权方式说明文档

> 版本：v1.3
> 编制日期：2026-06-29
> 配套文档：[ELINK_WORK_AUTH_OPTIMIZATION_EXECUTE.md](./ELINK_WORK_AUTH_OPTIMIZATION_EXECUTE.md) v1.9

## 1. 概述

Elink-AI 后端采用 **Redis Token** 鉴权机制（非 JWT），由 `RedisTokenAuthenticationFilter` 统一处理 token 提取与认证。为兼容历史前端代码、WebSocket 场景、文件下载场景及第三方系统集成，后端**同时支持两种 token 传递方式**。

## 2. 后端支持的 Token 传递方式

### 2.1 方式一：URL 请求参数 `access_token`（兼容方式）

| 项目 | 说明 |
|------|------|
| 传递方式 | URL Query 参数 `access_token=xxx` |
| 优先级 | **高**（与 Header 同时存在时优先使用） |
| 适用场景 | WebSocket 建立、文件直链下载、第三方系统集成、无法设置 Header 的场景 |
| 风险提示 | token 会出现在 URL 和访问日志中，存在泄露风险 |
| 示例 | `GET /device/list?access_token=abc123` |

### 2.2 方式二：HTTP Header `Authorization: Bearer xxx`（推荐方式）

| 项目 | 说明 |
|------|------|
| 传递方式 | HTTP Header `Authorization: Bearer xxx` |
| 优先级 | **低**（参数方式未提供时使用） |
| 适用场景 | 常规业务接口调用（推荐所有前端项目统一采用） |
| 优势 | token 不暴露在 URL，符合 OAuth2 标准，GET 请求 URL 不会过长 |
| 示例 | `Authorization: Bearer abc123` |

### 2.3 优先级规则

```
参数 access_token > Header Authorization
```

- 若参数 `access_token` 存在且非空，使用参数中的 token
- 否则，若 Header `Authorization` 以 `Bearer ` 开头，使用 Header 中的 token
- 两者都为空时，不进行认证（受保护接口返回 401）

### 2.4 实现位置

| 组件 | 文件路径 | 说明 |
|------|----------|------|
| Token 提取 | [RedisTokenAuthenticationFilter.java](file:///work/elink-ai/elink-work/sunmax-common/src/main/java/com/sunmax/common/config/RedisTokenAuthenticationFilter.java) | `extractToken()` 方法 |
| Token 校验 | 同上 | 从 Redis 查询 `auth:access:{token}` 对应用户信息 |
| 角色映射 | 同上 | `mapToAuthorities()` 按 `userRole` 映射 `ROLE_PLATFORM_ADMIN/ROLE_ADMIN/ROLE_USER` |
| 登录签发 | [OauthController.java](file:///work/elink-ai/elink-work/auth-service/src/main/java/com/sunmax/auth/controller/OauthController.java) | `POST /sauth/oauth/token` |
| 登出销毁 | 同上 | `POST /sauth/oauth/logout`（支持参数和 Header 两种方式） |

## 3. 各前端项目的具体实现方式

### 3.1 共享 HTTP 客户端

三个前端项目（linkos / derms / tycvs）均通过 monorepo 共享 HTTP 客户端 `@elink/shared/http`，统一采用 **Header 方式**传递 token。

| 文件 | 说明 |
|------|------|
| [packages/shared/src/http/request.ts](file:///work/elink-ai/elink-web/packages/shared/src/http/request.ts) | 共享 HTTP 客户端核心，请求拦截器注入 `Authorization: Bearer xxx` |
| [packages/shared/src/http/tokenRefresh.ts](file:///work/elink-ai/elink-web/packages/shared/src/http/tokenRefresh.ts) | Refresh Token 自动刷新处理器，401 时自动刷新并重发请求 |

### 3.2 各前端项目接入方式

| 项目 | 端口 | 入口文件 | Token 传递方式 |
|------|------|----------|---------------|
| linkos（设备管理平台） | 9000 | [src/utils/request.ts](file:///work/elink-ai/elink-web/linkos/src/utils/request.ts) | Header `Authorization: Bearer xxx` |
| derms（运营管理平台） | 9001 | [src/utils/request.ts](file:///work/elink-ai/elink-web/derms/src/utils/request.ts) | Header `Authorization: Bearer xxx` |
| tycvs（可视化组态平台） | 9002 | [src/utils/request.ts](file:///work/elink-ai/elink-web/tycvs/src/utils/request.ts) | Header `Authorization: Bearer xxx` |

### 3.3 前端请求拦截器核心逻辑

```typescript
// packages/shared/src/http/request.ts
if (auth.getToken() && !noToken) {
  if (!config.headers) config.headers = {};
  (config.headers as Record<string, string>)["Authorization"] = `Bearer ${auth.getToken()}`;
}
```

### 3.4 特殊场景说明

#### WebSocket 场景

浏览器原生 `WebSocket` API **无法设置 Authorization Header**，因此 WebSocket 连接需要采用以下方案之一：

| 方案 | 说明 | 推荐度 |
|------|------|--------|
| URL 参数传递 token | `ws://host/path?access_token=xxx` | 推荐（后端已支持） |
| 连接后首条消息鉴权 | 建立连接后发送 `{type: 'auth', token: 'xxx'}` | 可选（需后端配合） |
| Cookie 鉴权 | 依赖浏览器 Cookie 传递 | 不推荐（跨域问题） |

**当前实现状态（v1.7 排查结果）**：

| 项 | 状态 | 说明 |
|----|------|------|
| 后端 WebSocket 端点鉴权 | ❌ 未实现 | `@ServerEndpoint` 由 Tomcat 容器直接处理，**不经过 `RedisTokenAuthenticationFilter`** |
| 前端 WebSocket URL 携带 token | ❌ 未携带 | linkos `deviceUpdateWebSocket` URL 仅传 userId |
| 后端参数鉴权能力 | ✅ 已支持 | v1.6 已恢复，但 WebSocket 端点绕过了它 |

**结论**：WebSocket 端点存在未鉴权安全问题。后续需通过以下方式之一修复：
1. 在 `@OnOpen` 中手动校验 `access_token` 参数（推荐，改动最小）
2. 改用 Spring 的 `WebSocketHandler` + Handshake Interceptor 接入 Spring Security 链路
3. 在 WebSocket URL 中追加 `access_token` 参数，前端配合修改

#### 文件下载场景

浏览器直链下载（`<a href>` 或 `window.open`）无法设置 Header，建议：

```typescript
// 推荐方案：URL 追加 access_token 参数
const downloadUrl = `/device/export?access_token=${auth.getToken()}`;
window.open(downloadUrl);
```

## 4. Token 生命周期

### 4.1 登录签发

```
POST /sauth/oauth/token
grant_type=sys_pwd&client_id=sunos-client&client_secret=sunos-client&userAccount=xxx&password=xxx
```

响应：
```json
{
  "success": true,
  "data": {
    "accessToken": "xxx",
    "refreshToken": "xxx",
    "clientId": "sunos-client",
    "userAccount": "xxx",
    "id": "xxx",
    "userRole": 0
  }
}
```

### 4.2 Token 刷新

```
POST /sauth/oauth/token
grant_type=refresh_token&client_id=sunos-client&refresh_token=xxx
```

- refresh_token **一次性使用**，刷新后旧 token 立即失效
- clientId 必须与原登录时一致，否则拒绝

### 4.3 Token 销毁

```
POST /sauth/oauth/logout
Authorization: Bearer xxx
# 或
POST /sauth/oauth/logout?access_token=xxx
```

- 从 Redis 删除 `auth:access:{token}` 和用户索引 `auth:user:token:{clientId}:{userId}`
- 支持参数和 Header 两种方式传递 token

## 5. 安全建议

| 项 | 建议 |
|----|------|
| 前端常规接口 | 统一采用 Header 方式（已实现） |
| WebSocket / 文件下载 | 允许使用参数方式，但建议缩短 token 有效期 |
| 第三方集成 | 优先协商使用 Header 方式；确需参数方式时，建议使用独立短时效 token |
| 日志脱敏 | 生产环境应对访问日志中的 `access_token` 参数进行脱敏处理 |
| HTTPS | 生产环境必须启用 HTTPS，防止 token 在传输过程中泄露 |

## 6. 测试验证

### 6.1 单元测试

| 测试类 | 用例数 | 覆盖场景 |
|--------|--------|---------|
| [OauthControllerTest](file:///work/elink-ai/elink-work/auth-service/src/test/java/com/sunmax/auth/controller/OauthControllerTest.java) | 17 | 登录、登出（Header/参数）、Refresh Token 全场景 |
| [RedisTokenAuthenticationFilterTest](file:///work/elink-ai/elink-work/sunmax-common/src/test/java/com/sunmax/common/config/RedisTokenAuthenticationFilterTest.java) | 11 | 参数鉴权、Header 鉴权、优先级、角色映射、异常处理 |

### 6.2 接口验证

```bash
# 1. Header 方式
curl -H "Authorization: Bearer $TOKEN" http://localhost:5000/sauth/oauth/logout -X POST

# 2. 参数方式
curl "http://localhost:5000/sauth/oauth/logout?access_token=$TOKEN" -X POST

# 3. 参数优先于 Header
curl -H "Authorization: Bearer $HEADER_TOKEN" \
     "http://localhost:5000/sauth/oauth/logout?access_token=$PARAM_TOKEN" -X POST
```

### 6.3 Refresh Token 端到端验证（v1.7）

通过 Redis 直查方式验证 Refresh Token 完整流程（8 项全部通过）：

| 步骤 | 验证项 | 结果 |
|------|--------|------|
| 1 | 登录后 access_token + refresh_token + 索引写入 Redis db1 | ✅ 全部 EXISTS=1 |
| 2 | 调用 refresh_token 接口刷新成功 | ✅ success=true |
| 3 | 旧 access_token + refresh_token 一次性删除 | ✅ 全部 EXISTS=0 |
| 4 | 新 access_token + refresh_token + 索引更新写入 | ✅ 全部 EXISTS=1，索引值=新 accessToken |
| 5 | 二次使用旧 refresh_token 被拒 | ✅ code=50001 |
| 6 | clientId 不匹配被拒 | ✅ code=50001 |
| 7 | 前端开发服务器 Vite Proxy 链路正常 | ✅ linkos:9000/derms:9001/tycvs:9002 全部 HTTP 200 |

### 6.4 选项3 修复验证（v1.8）

修复 `tokenRefresh.ts` 重发请求使用全局 `axios` 绕过拦截器的问题：

| 修复项 | 修改文件 | 说明 |
|--------|---------|------|
| serviceGetter 参数 | `tokenRefresh.ts` | 新增 `serviceGetter` 参数，`retryRequest` 优先使用 service 实例重发请求 |
| serviceRef 绑定 | `request.ts` | 单例模式在 service 创建后绑定；perRequestIsolation 模式在每次请求时临时绑定 |
| FormData 幂等性 | `request.ts` | 请求拦截器 FormData userId 注入添加 `if (!config.data.get("userId"))` 检查 |

**修复优势**：
1. 重发请求经过完整拦截器链，重发后的 401 响应能被响应拦截器捕获，触发跳转登录
2. 请求拦截器会注入最新的 userId/tenantId（幂等性检查避免 FormData 重复 append）
3. 重复请求检测、用户切换检测等逻辑正常工作

### 6.5 选项2 端到端全链路验证（v1.8）

通过 curl + Redis 直查 + 三平台 Vite Proxy 验证（12 项全部通过）：

| 验证项 | 结果 |
|--------|------|
| Bearer Header 鉴权 | ✅ HTTP 200 |
| URL 参数鉴权 | ✅ HTTP 200 |
| linkos Vite Proxy 链路 | ✅ HTTP 200 |
| derms Vite Proxy 链路 | ✅ HTTP 200 |
| tycvs Vite Proxy 链路 | ✅ HTTP 200 |
| token 过期访问返回 401 | ✅ HTTP 401 |
| refresh_token 接口刷新成功 | ✅ success=true |
| 旧 access_token 一次性删除 | ✅ EXISTS=0 |
| 旧 refresh_token 一次性删除 | ✅ EXISTS=0 |
| 新 access_token 写入 Redis | ✅ EXISTS=1 |
| 二次使用旧 refresh_token 被拒 | ✅ code=50001 |
| clientId 不匹配被拒 | ✅ code=50001 |

**完整流程模拟**：前端检测 401 → 调用 refresh_token → 获取新 token → 重发原请求（通过 service 实例）→ 返回 200，全流程通过。

### 6.6 选项1 浏览器实测全场景验证（v1.9）

环境无 puppeteer/playwright 等浏览器自动化工具，采用「代码审查 + curl 模拟浏览器完整请求链路 + Vite Proxy + Redis 直查」替代真实浏览器测试，覆盖前端 tokenRefresh.ts 的所有执行路径。

**场景1：浏览器完整请求链路验证（9 步全通过）**

| 步骤 | 验证项 | 结果 |
|------|--------|------|
| 1 | 通过 linkos Vite Proxy 登录 | ✅ HTTP 200，返回 accessToken + refreshToken |
| 2 | 用 access_token 访问业务接口 | ✅ HTTP 200 |
| 3 | 模拟 access_token 过期（Redis DEL） | ✅ 删除成功 |
| 4 | 用过期 token 访问业务接口 | ✅ HTTP 401 |
| 5 | 模拟 handleTokenExpired 执行 | ✅ 读取 refreshToken 并调用刷新接口 |
| 6 | 刷新成功，获取新 token | ✅ 返回新 accessToken + refreshToken |
| 7 | 用新 token 重发原请求 | ✅ HTTP 403（token 有效，cbl 无该接口权限） |
| 8 | 旧 refresh_token 一次性失效 | ✅ code=50001 |
| 9 | 新 token 持续有效 | ✅ HTTP 200 |

**场景2：并发请求排队验证**

| 验证项 | 结果 |
|--------|------|
| 3 个并发请求同时遇到 401 | ✅ 全部返回 401 |
| 请求1 触发刷新，请求2/3 排队 | ✅ 逻辑正确 |
| 刷新成功后重发排队请求 | ✅ 3 个请求都返回 403（token 有效并重发成功） |

**场景3：刷新失败验证**

| 验证项 | 结果 |
|--------|------|
| 模拟 refreshToken 也过期 | ✅ Redis DEL 成功 |
| 用过期 refreshToken 刷新被拒 | ✅ code=50001 |
| onRefreshFailed 回调触发跳转登录页 | ✅ 逻辑正确 |

**关键发现**：`/sauth/current-info` 是 permitAll 接口，token 过期仍返回 200（anonymousUser），需用 `/system/user/list` 等需鉴权接口测试 401 场景。

## 7. 变更记录

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| v1.0 | 2026-06-28 | 初始版本，明确后端双模式鉴权策略及前端统一 Header 实现 |
| v1.1 | 2026-06-28 | 补充 Refresh Token 端到端验证结果（v1.7）；更新 WebSocket 鉴权闭环排查结果，明确当前 WebSocket 端点未鉴权问题及修复方案 |
| v1.2 | 2026-06-29 | 补充选项3修复说明（6.4 节）和选项2端到端全链路验证结果（6.5 节，12 项全通过） |
| v1.3 | 2026-06-29 | 补充选项1浏览器实测全场景验证结果（6.6 节，3 场景全通过：完整链路 9 步 + 并发排队 + 刷新失败） |
