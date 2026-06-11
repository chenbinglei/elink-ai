# Auth-Service 登录接口使用说明

> 版本：v1.0 | 更新日期：2026-06-09
>
> 本文档用于说明 auth-service 重构后的登录认证接口，供前端开发人员评估是否需要调整前端代码。

---

## 一、接口概览

| 项目 | 说明 |
|------|------|
| 服务名称 | auth-service |
| 服务端口 | 60001 |
| 上下文路径 | /sauth |
| 接口地址 | `POST /sauth/oauth/token` |
| 认证方式 | Redis Token（UUID格式，非JWT） |

**核心变更说明**：auth-service 已从旧版 Spring Security OAuth2 迁移至 Spring Authorization Server，但登录接口的 URL、请求参数和响应格式**完全保持不变**，前端无需修改。

---

## 二、认证流程

```
┌──────────┐     1. POST /sauth/oauth/token      ┌──────────────┐
│   前端    │ ──────────────────────────────────▶  │  auth-service │
│ (浏览器)  │                                      │  (OauthController)│
└──────────┘                                      └──────┬───────┘
     ▲                                                   │
     │                                                   ▼
     │                                           3. 返回Token+用户信息
     │                                                   │
     │                                          ┌────────┴────────┐
     │                                          │  Redis存储Token  │
     │                                          │  key: oauth:token:{accessToken}  │
     │                                          │  val: 用户信息JSON │
     │                                          │  TTL: 3天         │
     │                                          └─────────────────┘
     │                                                   │
     │              2. 验证用户凭证                        │
     │          (密码/验证码/微信code等)                    │
     │                                                   │
     ▼                                                   ▼
┌──────────┐                                    ┌──────────────┐
│  其他服务  │  4. 请求携带access_token           │  Redis验证    │
│ (system/  │ ──────────────────────────────▶   │  Token有效性  │
│  device等)│                                    │  (RedisToken  │
└──────────┘                                    │  AuthFilter)  │
                                                └──────────────┘
```

### 认证步骤说明

1. **前端发起登录请求**：POST `/sauth/oauth/token`，携带 grant_type 和对应参数
2. **auth-service 验证用户凭证**：根据 grant_type 调用不同的验证逻辑
3. **生成 Token 并存入 Redis**：
   - access_token：有效期 3 天
   - refresh_token：有效期 30 天
4. **返回 Token 和用户信息**：响应格式与旧版完全一致
5. **后续请求认证**：前端在请求参数中携带 `access_token`，各微服务通过 `RedisTokenAuthenticationFilter` 从 Redis 验证 Token 有效性

---

## 三、登录接口详情

### 3.1 统一登录端点

```
POST /sauth/oauth/token
Content-Type: application/x-www-form-urlencoded
```

### 3.2 通用请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| grant_type | String | 否 | 授权类型，默认 `sys_pwd` |
| client_id | String | 否 | 客户端ID，默认 `sunos-client` |
| client_secret | String | 否 | 客户端密钥 |

### 3.3 各授权类型参数

#### (1) 系统用户密码登录 (grant_type=sys_pwd)

**适用场景**：Web平台登录（linkos、derms、tycvs）

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| grant_type | String | 是 | 固定值 `sys_pwd` |
| client_id | String | 否 | 客户端ID |
| client_secret | String | 否 | 客户端密钥 |
| userAccount | String | 是 | 用户账号 |
| password | String | 是 | 用户密码（DES加密后） |

**请求示例**：
```http
POST /sauth/oauth/token
Content-Type: application/x-www-form-urlencoded

grant_type=sys_pwd&client_id=sunos-client&client_secret=sunos-client&userAccount=admin&password=DES加密后的密码
```

**前端调用示例（当前代码）**：
```javascript
login({
    grant_type: "sys_pwd",
    client_id: "sunos-client",
    client_secret: "sunos-client",
    userAccount: that.loginForm.username,
    password: Crypto.CBC_encrypt(that.loginForm.password)
})
```

#### (2) 微信小程序登录 (grant_type=applet)

**适用场景**：微信小程序端登录

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| grant_type | String | 是 | 固定值 `applet` |
| code | String | 是 | 微信授权码 |
| appletKey | String | 是 | 小程序标识 |
| encryptedData | String | 否 | 微信加密数据 |
| iv | String | 否 | 微信加密向量 |

#### (3) 刷新令牌 (grant_type=refresh_token)

**适用场景**：Token过期后刷新

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| grant_type | String | 是 | 固定值 `refresh_token` |
| refresh_token | String | 是 | 刷新令牌 |
| client_id | String | 否 | 客户端ID |

#### (4) 短信验证码登录 (grant_type=sms)

**适用场景**：手机号+验证码登录

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| grant_type | String | 是 | 固定值 `sms` |
| userAccount | String | 是 | 手机号 |
| code | String | 是 | 短信验证码 |
| client_id | String | 否 | 客户端ID |

#### (5) 自定义密码登录 (grant_type=password)

**适用场景**：移动端密码登录

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| grant_type | String | 是 | 固定值 `password` |
| userAccount | String | 是 | 用户账号 |
| password | String | 是 | 用户密码 |
| client_id | String | 否 | 客户端ID |

#### (6) 支付宝小程序登录 (grant_type=alipay)

**适用场景**：支付宝小程序端登录

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| grant_type | String | 是 | 固定值 `alipay` |
| code | String | 是 | 支付宝授权码 |
| appletKey | String | 是 | 小程序标识 |

---

## 四、响应数据格式

### 4.1 成功响应

**HTTP状态码**：200

```json
{
    "code": 20000,
    "success": true,
    "message": "操作成功",
    "data": {
        "accessToken": "a1b2c3d4e5f6...",
        "refreshToken": "f6e5d4c3b2a1...",
        "userAccount": "admin",
        "fullName": "管理员",
        "id": "1",
        "tenantId": "1001",
        "tenantName": "默认租户",
        "userRole": 0,
        "phone": "加密手机号",
        "userProfile": "头像URL",
        "isDefaultAdmin": 1,
        "logo": 1,
        "menuList": [
            {
                "id": "菜单ID",
                "name": "菜单名称",
                "url": "/dashboard",
                "iconPath": "icon-name",
                "isLayout": 1,
                "isHidden": 0,
                "directoryDesc": "排序号",
                "parentId": "父级ID",
                "type": 1
            }
        ]
    }
}
```

### 4.2 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| accessToken | String | 访问令牌，用于后续API请求认证 |
| refreshToken | String | 刷新令牌，用于Token过期后获取新Token |
| userAccount | String | 用户账号 |
| fullName | String | 用户姓名 |
| id | String | 用户唯一ID |
| tenantId | String | 所属租户ID |
| tenantName | String | 所属租户名称 |
| userRole | Integer | 用户角色：0-平台管理员 1-管理员 2-普通用户 |
| phone | String | 手机号（加密存储） |
| userProfile | String | 用户头像URL |
| isDefaultAdmin | Integer | 是否默认管理员：1-是 |
| logo | Integer | 登录标识：1-账号密码 2-微信小程序 3-支付宝小程序 |
| menuList | Array | 菜单权限列表 |

### 4.3 失败响应

```json
{
    "code": 5001,
    "success": false,
    "message": "用户账号不存在,请核对",
    "data": null
}
```

**常见错误码**：

| 错误码 | 说明 |
|--------|------|
| 5001 | 账号相关错误（账号为空、账号不存在、账号已到期） |
| 5002 | 密码相关错误（密码为空、密码错误） |
| 9999 | 未登录或登录已失效（Token无效/过期） |

---

## 五、Token使用方式

### 5.1 请求携带Token

前端在后续API请求中通过以下方式携带Token（与旧版完全一致，无需修改）：

**方式1：请求参数（当前前端使用的方式）**
```javascript
// 在请求体中添加 access_token
config.data.access_token = getToken();
```

**方式2：请求Header**
```http
Authorization: Bearer {accessToken}
```

**方式3：URL查询参数**
```
/api/resource?access_token={accessToken}
```

### 5.2 Token有效期

| Token类型 | 有效期 | 说明 |
|-----------|--------|------|
| access_token | 3天 | 访问令牌，用于API请求认证 |
| refresh_token | 30天 | 刷新令牌，用于获取新的access_token |

### 5.3 Token存储

Token信息存储在Redis中：
- access_token：`oauth:token:{accessToken}` → 用户信息JSON
- refresh_token：`oauth:refresh:{refreshToken}` → 用户信息JSON

---

## 六、辅助接口

### 6.1 登出

```
POST /sauth/oauth/logout
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| access_token | String | 是 | 访问令牌 |

**响应**：
```json
{
    "code": 20000,
    "success": true,
    "message": "操作成功",
    "data": "登出成功"
}
```

### 6.2 校验Token有效性

```
GET /sauth/oauth/check_token
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| access_token | String | 是 | 访问令牌 |

**成功响应**：
```json
{
    "code": 20000,
    "success": true,
    "message": "操作成功",
    "data": {
        "id": "1",
        "userAccount": "admin",
        "fullName": "管理员",
        "tenantId": "1001",
        "tenantName": "默认租户",
        "userRole": 0,
        "phone": "加密手机号",
        "userProfile": "头像URL",
        "isDefaultAdmin": 1,
        "logo": 1
    }
}
```

**Token无效响应**：
```json
{
    "code": 5001,
    "success": false,
    "message": "Token无效或已过期",
    "data": null
}
```

### 6.3 获取当前登录用户信息

```
GET /sauth/current-info?access_token={accessToken}
```

**响应**：返回当前Token对应的用户信息（与check_token类似，但通过SecurityContext获取）。

### 6.4 修改用户密码

```
POST /sauth/oauth/user/updateUserPassword
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userAccount | String | 是 | 用户账号 |
| phone | String | 是 | 用户手机号 |
| password | String | 是 | 新密码 |

### 6.5 查询用户手机号是否存在

```
POST /sauth/oauth/user/findUserIsExitByPhone
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userAccount | String | 是 | 用户账号 |
| phone | String | 是 | 手机号 |

---

## 七、错误处理机制

### 7.1 认证失败处理

当Token无效或过期时，所有微服务统一返回：

```json
{
    "code": 9999,
    "success": false,
    "message": "未登录或登陆失效",
    "data": null
}
```

前端应在响应拦截器中检测 `code === 9999`，执行清除Token并跳转登录页的逻辑。

### 7.2 权限不足处理

当用户无权访问某资源时，返回：

```json
{
    "code": 50003,
    "success": false,
    "message": "无权访问,请联系管理员开通权限",
    "data": null
}
```

### 7.3 常见问题排查

| 问题 | 可能原因 | 排查方法 |
|------|----------|----------|
| 登录返回"客户端认证失败" | client_id/client_secret错误 | 检查请求参数中的client_id |
| 登录返回"用户名或密码错误" | 密码DES加密方式不匹配 | 确认前端使用Crypto.CBC_encrypt加密 |
| 请求返回9999 | Token已过期或无效 | 检查Redis中Token是否存在，或使用check_token接口验证 |
| 刷新Token失败 | refresh_token已过期（30天） | 需要重新登录 |
| 短信验证码登录失败 | 验证码过期或错误 | 验证码存储在Redis `sms:code:{phone}`，有效期由发送时设定 |
| 微信小程序登录失败 | code过期或appletKey不匹配 | 检查微信小程序配置和code有效性 |

---

## 八、前端兼容性评估

### 8.1 三个前端项目兼容性分析

| 前端项目 | 登录接口 | Token传递方式 | 响应字段使用 | 是否需要调整 |
|----------|----------|--------------|-------------|-------------|
| **linkos** (设备管理平台) | `/sauth/oauth/token` | 请求参数 `access_token` | `accessToken`, `menuList`, `id` 等 | **无需调整** |
| **derms** (运营管理平台) | `/sauth/oauth/token` | 请求参数 `access_token` | `accessToken`, `menuList`, `id` 等 | **无需调整** |
| **tycvs** (可视化组态平台) | `/sauth/oauth/token` | 请求参数 `access_token` | `accessToken`, `menuList`, `id` 等 | **无需调整** |

### 8.2 兼容性详细说明

1. **接口URL不变**：三个前端项目均调用 `/sauth/oauth/token`，路径未变
2. **请求参数不变**：`grant_type=sys_pwd`、`client_id=sunos-client`、`userAccount`、`password` 等参数完全兼容
3. **响应格式不变**：返回的 `accessToken`、`refreshToken`、`menuList` 等字段名称和结构完全一致
4. **Token传递方式不变**：前端通过请求参数 `access_token` 传递，后端 `RedisTokenAuthenticationFilter` 同时支持参数和Header两种方式
5. **错误码不变**：`9999`（未登录）、`5001`（账号错误）、`5002`（密码错误）等错误码保持一致
6. **密码加密方式不变**：前端仍使用 `Crypto.CBC_encrypt()` 进行DES加密

### 8.3 后续优化建议（非必须）

以下为可选优化项，不影响当前功能正常运行：

1. **Token刷新机制**：当前前端未使用 refresh_token，建议在Token即将过期时自动刷新，避免用户频繁重新登录
2. **Bearer Token方式**：当前前端通过请求参数传递Token，后续可考虑迁移至 `Authorization: Bearer` Header方式，更符合RESTful规范
3. **登出接口调用**：建议在用户主动登出时调用 `/sauth/oauth/logout` 接口，清除服务端Token

---

## 九、与旧版差异对照

| 对比项 | 旧版 (Spring Security OAuth2) | 新版 (Spring Authorization Server) |
|--------|------|------|
| 框架 | spring-security-oauth2 (已废弃) | spring-authorization-server 1.3.x |
| Token格式 | UUID (RedisTokenStore) | UUID (Redis存储，格式不变) |
| Token验证 | JWT资源服务器验证 | Redis Token验证 (RedisTokenAuthenticationFilter) |
| 登录端点 | /oauth/token (框架内置) | /oauth/token (自定义Controller) |
| 客户端认证 | ClientDetailsService | RegisteredClientRepository (JDBC) |
| Token增强 | TokenEnhancer (CustomAdditionalInformation) | OAuth2TokenCustomizer |
| 密码加密 | BCryptPasswordEncoder | BCryptPasswordEncoder (不变) |
| 前端兼容性 | - | 完全兼容，无需修改 |

---

## 十、接口安全说明

1. **HTTPS**：生产环境必须使用HTTPS
2. **CORS**：已配置业务域名白名单，不再使用通配符 `*`
3. **CSRF**：已禁用（无状态Token认证，不需要CSRF防护）
4. **密码传输**：前端DES加密后传输，后端解密验证
5. **Token存储**：Redis存储，设置合理过期时间
6. **客户端认证**：支持client_id/client_secret验证，兼容无客户端模式
