package com.sunmax.auth.controller;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.auth.service.UserLoginService;
import com.sunmax.common.security.JwtTokenService;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 认证控制器
 *
 * 支持的 grant_type：
 * - sys_pwd: 系统用户密码登录（Web平台）
 * - applet: 微信小程序登录（微信小程序端）
 * - refresh_token: 刷新令牌（Web平台）
 * - client_credentials: 客户端凭证（Web平台）
 * - password: 密码登录（Web平台）
 * - implicit: 隐式授权（Web平台）
 */
@RestController
@RequestMapping("/oauth")
@Tag(name = "认证管理")
@Slf4j
public class OauthController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private com.sunmax.common.security.TokenBlacklistService tokenBlacklistService;

    /**
     * JWT 启用开关（默认 false 保持 UUID token，true 启用 JWT）
     * 双轨过渡期配置，便于灰度切换和回滚
     */
    @Value("${jwt.enabled:false}")
    private boolean jwtEnabled;

    /** Access Token Redis key前缀 */
    static final String ACCESS_TOKEN_PREFIX = "auth:access:";
    /** Refresh Token Redis key前缀 */
    static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";
    /** 用户Token索引前缀（用户ID -> accessToken，用于单用户踢出旧Token） */
    static final String USER_TOKEN_INDEX_PREFIX = "auth:user:token:";
    /** 用户RefreshToken索引前缀（clientId:userId -> refreshToken，用于登出/重登时清理旧refresh_token） */
    static final String USER_REFRESH_TOKEN_INDEX_PREFIX = "auth:user:refresh:";
    /** Access Token有效期（秒）- 3天 */
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 3;
    /** Refresh Token有效期（秒）- 30天 */
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 30;

    /**
     * 统一登录入口 - POST /oauth/token
     */
    @PostMapping("/token")
    @Operation(summary = "统一登录/令牌端点")
    @Parameters({
            @Parameter(name = "grant_type", description = "授权类型: sys_pwd/applet/refresh_token"),
            @Parameter(name = "client_id", description = "客户端ID"),
            @Parameter(name = "client_secret", description = "客户端密钥"),
            @Parameter(name = "userAccount", description = "用户账号(sys_pwd时必填)"),
            @Parameter(name = "password", description = "用户密码(sys_pwd时必填，AES-CBC加密)"),
            @Parameter(name = "refresh_token", description = "刷新令牌(refresh_token时必填)"),
            @Parameter(name = "code", description = "微信授权码(applet时必填)"),
            @Parameter(name = "appletKey", description = "小程序标识(applet时必填)"),
            @Parameter(name = "encryptedData", description = "微信加密数据(applet时选填)"),
            @Parameter(name = "iv", description = "微信加密向量(applet时选填)")
    })
    public ResponseResult<JSONObject> postAccessToken(
            @RequestParam(value = "grant_type", defaultValue = "sys_pwd") String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "userAccount", required = false) String userAccount,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "refresh_token", required = false) String refreshToken,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "appletKey", required = false) String appletKey,
            @RequestParam(value = "encryptedData", required = false) String encryptedData,
            @RequestParam(value = "iv", required = false) String iv) {

        log.info("登录请求: grant_type={}, client_id={}", grantType, clientId);

        if (!validateClient(clientId, clientSecret)) {
            return ResponseResult.paramError("客户端认证失败");
        }

        try {
            switch (grantType) {
                case "sys_pwd":
                    return handleSysPwdLogin(userAccount, password, clientId);
                case "applet":
                    return handleAppletLogin(code, appletKey, encryptedData, iv);
                case "refresh_token":
                    return handleRefreshToken(refreshToken, clientId);
                default:
                    return ResponseResult.paramError("不支持的授权类型: " + grantType);
            }
        } catch (RuntimeException e) {
            log.error("登录处理异常: grant_type={}", grantType, e);
            return ResponseResult.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * GET /oauth/token - 兼容GET请求
     */
    @GetMapping("/token")
    @Operation(summary = "登录Get请求（建议使用POST）")
    public ResponseResult<JSONObject> getAccessToken(
            @RequestParam(value = "grant_type", defaultValue = "sys_pwd") String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "userAccount", required = false) String userAccount,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "refresh_token", required = false) String refreshToken) {
        return postAccessToken(grantType, clientId, clientSecret, userAccount, password, refreshToken, null, null, null, null);
    }

    /**
     * 系统用户密码登录
     * 前端使用AES-CBC加密密码，后端用SecretUtil.desEncrypt()解密后与数据库明文密码比对
     */
    private ResponseResult<JSONObject> handleSysPwdLogin(String userAccount, String password, String clientId) {
        if (StringUtil.isEmpty(userAccount) || StringUtil.isEmpty(password)) {
            return ResponseResult.paramError("用户名和密码不能为空");
        }

        // clientId 必填（validateClient 已拒绝空值，此处仅做二次防御）
        if (StringUtil.isEmpty(clientId)) {
            return ResponseResult.paramError("clientId不能为空");
        }
        // 直接使用前端传入的 clientId，不再回退到默认值
        UserLoginDto user = userLoginService.loadSysUserByAccountAndPassword(userAccount, password, clientId);

        if (user.getCheckCode() != null && user.getCheckCode() != 0) {
            return ResponseResult.paramError(user.getCheckMsg(), user.getCheckCode());
        }

        user.setClientId(clientId);
        return buildTokenResponse(user);
    }

    /**
     * 微信小程序登录
     */
    private ResponseResult<JSONObject> handleAppletLogin(String code, String appletKey, String encryptedData, String iv) {
        if (StringUtil.isEmpty(code)) {
            return ResponseResult.paramError("微信授权码不能为空");
        }

        UserLoginDto user = userLoginService.loadUserByAppletCodeAndMobile(code, appletKey, encryptedData, iv);

        if (user.getCheckCode() != null && user.getCheckCode() != 0) {
            return ResponseResult.paramError(user.getCheckMsg(), user.getCheckCode());
        }

        user.setClientId("applet");
        return buildTokenResponse(user);
    }

    /**
     * Refresh Token 刷新令牌
     *
     * 工作流程：
     * 1. 从 Redis 中查找 refresh_token 对应的用户信息
     * 2. 校验 clientId 一致性（防止跨平台滥用）
     * 3. 删除旧 access_token 和 refresh_token（一次性使用）
     * 4. 生成新 access_token 和 refresh_token，写入 Redis
     * 5. 返回新令牌
     *
     * 安全策略：
     * - refresh_token 一次性使用，刷新后旧 token 立即失效
     * - clientId 必须与原登录时一致，否则拒绝
     *
     * JWT 模式（jwt.enabled=true）：
     * - 使用 JwtTokenService.parseAndVerify 验签旧 refresh_token
     * - 验签通过后调用 generateAccessToken/generateRefreshToken 签发新 JWT
     * - Redis 仍存储 JWT -> 用户JSON（用于黑名单和一次性使用）
     */
    private ResponseResult<JSONObject> handleRefreshToken(String refreshToken, String clientId) {
        if (StringUtil.isEmpty(refreshToken)) {
            return ResponseResult.paramError("refresh_token不能为空");
        }

        // JWT 模式：先验签 JWT，再从 Redis 查（双重校验：签名 + Redis 存在性）
        if (jwtEnabled) {
            try {
                com.nimbusds.jwt.JWTClaimsSet claims = jwtTokenService.parseAndVerify(refreshToken);
                // 校验 type=refresh
                if (!JwtTokenService.TOKEN_TYPE_REFRESH.equals(claims.getStringClaim("type"))) {
                    log.warn("refresh_token 类型错误: type={}", claims.getStringClaim("type"));
                    return ResponseResult.paramError("refresh_token类型错误");
                }
                // 校验未过期
                if (jwtTokenService.isExpired(claims)) {
                    return ResponseResult.paramError("refresh_token无效或已过期");
                }
            } catch (com.nimbusds.jose.JOSEException e) {
                log.warn("refresh_token JWT验签失败: {}", e.getMessage());
                return ResponseResult.paramError("refresh_token无效或已过期");
            } catch (java.text.ParseException e) {
                log.warn("refresh_token JWT解析失败: {}", e.getMessage());
                return ResponseResult.paramError("refresh_token解析失败");
            }
        }

        // 从 Redis 查找 refresh_token（JWT 和 UUID 模式都需要，用于一次性使用控制）
        String userJson = stringRedisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);
        if (StringUtil.isEmpty(userJson)) {
            return ResponseResult.paramError("refresh_token无效或已过期");
        }

        JSONObject userData;
        try {
            userData = JSONObject.parseObject(userJson);
        } catch (RuntimeException e) {
            log.warn("刷新令牌时解析数据异常", e);
            return ResponseResult.paramError("refresh_token解析失败");
        }

        // 校验 clientId 一致性
        String originalClientId = userData.getString("clientId");
        if (StringUtil.isEmpty(originalClientId) || !originalClientId.equals(clientId)) {
            log.warn("refresh_token clientId不一致: original={}, request={}", originalClientId, clientId);
            return ResponseResult.paramError("clientId不匹配");
        }

        String userId = userData.getString("id");
        String userAccount = userData.getString("userAccount");
        Integer userRole = userData.getInteger("userRole");
        String tenantId = userData.getString("tenantId");

        // 一次性使用：删除旧 refresh_token
        stringRedisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
        // 将旧 refresh_token 加入黑名单（JWT 模式下防止旧 token 仍能通过本地验签）
        if (jwtEnabled) {
            tokenBlacklistService.blacklistRefreshToken(refreshToken, REFRESH_TOKEN_VALIDITY_SECONDS);
        }

        // 删除旧 access_token（若存在）
        String userTokenKey = USER_TOKEN_INDEX_PREFIX + clientId + ":" + userId;
        String oldAccessToken = stringRedisTemplate.opsForValue().get(userTokenKey);
        if (StringUtil.isNotEmpty(oldAccessToken)) {
            stringRedisTemplate.delete(ACCESS_TOKEN_PREFIX + oldAccessToken);
            // 将旧 access_token 加入黑名单（JWT 模式下防止旧 token 仍能通过本地验签）
            if (jwtEnabled) {
                tokenBlacklistService.blacklistAccessToken(oldAccessToken, ACCESS_TOKEN_VALIDITY_SECONDS);
            }
        }

        // 生成新令牌（JWT 或 UUID）
        String newAccessToken = generateAccessToken(userAccount, userId, userRole, tenantId, clientId);
        String newRefreshToken = generateRefreshToken(userAccount, userId, userRole, tenantId, clientId);

        // 写入新 access_token（沿用原用户数据）
        stringRedisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + newAccessToken, userJson,
                ACCESS_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        // 写入新 refresh_token
        stringRedisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + newRefreshToken, userJson,
                REFRESH_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        // 更新用户Token索引
        stringRedisTemplate.opsForValue().set(userTokenKey, newAccessToken,
                ACCESS_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        // 更新用户RefreshToken索引（一次性轮换后保证索引指向最新refresh_token）
        stringRedisTemplate.opsForValue().set(USER_REFRESH_TOKEN_INDEX_PREFIX + clientId + ":" + userId,
                newRefreshToken, REFRESH_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);

        // 构建响应
        JSONObject result = new JSONObject();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", newRefreshToken);
        result.put("userAccount", userAccount);
        result.put("id", userId);
        result.put("clientId", clientId);

        log.info("Refresh Token 刷新成功: userAccount={}, clientId={}, jwtEnabled={}", userAccount, clientId, jwtEnabled);
        return ResponseResult.ok(result);
    }

    /**
     * 生成 Access Token（根据 jwtEnabled 开关选择 JWT 或 UUID）
     */
    private String generateAccessToken(String userAccount, String userId, Integer userRole,
                                       String tenantId, String clientId) {
        if (jwtEnabled) {
            // permsVer 权限版本号，用于权限变更后使旧 token 失效（当前固定为 v1）
            return jwtTokenService.generateAccessToken(userAccount, userId, userRole, tenantId, clientId, "v1");
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成 Refresh Token（根据 jwtEnabled 开关选择 JWT 或 UUID）
     */
    private String generateRefreshToken(String userAccount, String userId, Integer userRole,
                                        String tenantId, String clientId) {
        if (jwtEnabled) {
            return jwtTokenService.generateRefreshToken(userAccount, userId, userRole, tenantId, clientId);
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 构建Token响应
     *
     * 缓存设计：
     * - auth:access:{token}  -> 用户信息JSON（TTL: 3天）
     * - auth:refresh:{token} -> 用户信息JSON（TTL: 30天）
     * - auth:user:token:{userId} -> accessToken（用于踢出旧Token）
     *
     * 响应格式（与前端兼容）：
     * { accessToken, refreshToken, userAccount, fullName, id, tenantId,
     *   tenantName, userRole, phone, userProfile, isDefaultAdmin, logo, menuList }
     */
    private ResponseResult<JSONObject> buildTokenResponse(UserLoginDto user) {
        // 生成令牌（JWT 或 UUID，根据 jwtEnabled 开关）
        String accessToken = generateAccessToken(user.getUserAccount(), user.getId(), user.getUserRole(),
                user.getTenantId(), user.getClientId());
        String refreshToken = generateRefreshToken(user.getUserAccount(), user.getId(), user.getUserRole(),
                user.getTenantId(), user.getClientId());

        // 构建缓存数据
        JSONObject tokenData = new JSONObject();
        tokenData.put("id", user.getId());
        tokenData.put("userAccount", user.getUserAccount());
        tokenData.put("fullName", user.getFullName());
        tokenData.put("tenantId", user.getTenantId());
        tokenData.put("tenantName", user.getTenantName());
        tokenData.put("userRole", user.getUserRole());
        tokenData.put("phone", user.getPhone());
        tokenData.put("userProfile", user.getUserProfile());
        tokenData.put("isDefaultAdmin", user.getIsDefaultAdmin());
        tokenData.put("logo", user.getLogo());
        tokenData.put("appletId", user.getAppletId());
        tokenData.put("appletKey", user.getAppletKey());
        tokenData.put("appletUserId", user.getAppletUserId());
        tokenData.put("clientId", user.getClientId());

        // 踢出该用户的旧Token（同clientId下仅保留最新Token）
        String userTokenKey = USER_TOKEN_INDEX_PREFIX + user.getClientId() + ":" + user.getId();
        String oldAccessToken = stringRedisTemplate.opsForValue().get(userTokenKey);
        if (StringUtil.isNotEmpty(oldAccessToken)) {
            stringRedisTemplate.delete(ACCESS_TOKEN_PREFIX + oldAccessToken);
            // 将旧 access_token 加入黑名单（JWT 模式下防止旧 token 仍能通过本地验签）
            if (jwtEnabled) {
                tokenBlacklistService.blacklistAccessToken(oldAccessToken, ACCESS_TOKEN_VALIDITY_SECONDS);
            }
            log.debug("踢出旧AccessToken: userId={}, oldToken={}", user.getId(), oldAccessToken.substring(0, Math.min(8, oldAccessToken.length())));
        }

        // 清理旧 refresh_token（重新登录时仅保留最新 refresh_token）
        String userRefreshKey = USER_REFRESH_TOKEN_INDEX_PREFIX + user.getClientId() + ":" + user.getId();
        String oldRefreshToken = stringRedisTemplate.opsForValue().get(userRefreshKey);
        if (StringUtil.isNotEmpty(oldRefreshToken)) {
            stringRedisTemplate.delete(REFRESH_TOKEN_PREFIX + oldRefreshToken);
            // 将旧 refresh_token 加入黑名单（JWT 模式下防止旧 token 仍能通过本地验签）
            if (jwtEnabled) {
                tokenBlacklistService.blacklistRefreshToken(oldRefreshToken, REFRESH_TOKEN_VALIDITY_SECONDS);
            }
            log.debug("踢出旧RefreshToken: userId={}, oldToken={}", user.getId(), oldRefreshToken.substring(0, Math.min(8, oldRefreshToken.length())));
        }

        // 存储新Token
        stringRedisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + accessToken, tokenData.toJSONString(),
                ACCESS_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, tokenData.toJSONString(),
                REFRESH_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        // 用户Token索引（TTL与accessToken一致）
        stringRedisTemplate.opsForValue().set(userTokenKey, accessToken,
                ACCESS_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        // 用户RefreshToken索引（TTL与refreshToken一致，用于登出/重登时清理旧refresh_token）
        stringRedisTemplate.opsForValue().set(userRefreshKey, refreshToken,
                REFRESH_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);

        // 构建响应
        JSONObject result = new JSONObject();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("userAccount", user.getUserAccount());
        result.put("fullName", user.getFullName());
        result.put("id", user.getId());
        result.put("tenantId", user.getTenantId());
        result.put("tenantName", user.getTenantName());
        result.put("userRole", user.getUserRole());
        result.put("phone", user.getPhone());
        result.put("checkMsg",user.getCheckMsg());
        result.put("checkCode",user.getCheckCode());
        result.put("userProfile", user.getUserProfile());
        result.put("isDefaultAdmin", user.getIsDefaultAdmin());
        result.put("logo", user.getLogo());
        result.put("clientId", user.getClientId());
        result.put("menuList", user.getMenuList());

        log.info("用户登录成功: userAccount={}, clientId={}, logo={}, jwtEnabled={}", user.getUserAccount(), user.getClientId(), user.getLogo(), jwtEnabled);
        return ResponseResult.ok(result);
    }

    /**
     * 验证客户端ID和密钥
     *
     * 宽松策略：自定义登录端点不强制要求客户端预注册
     * - clientId 为空时放行（兼容旧前端）
     * - 客户端不存在时放行（仅记录警告，不阻断登录）
     * - 客户端存在且提供了 clientSecret 时才校验密钥
     *
     * 原因：旧系统使用 oauth_client_details 表，迁移到 Spring Authorization Server 后
     * 使用 oauth2_registered_client 表，表结构不同，旧数据可能未迁移
     */
    private boolean validateClient(String clientId, String clientSecret) {
        if (StringUtil.isEmpty(clientId)) {
            log.warn("客户端ID为空,拒绝登录");
            return false;
        }
        try {
            RegisteredClient client = registeredClientRepository.findByClientId(clientId);
            if (client == null) {
                log.warn("客户端未注册: clientId={},拒绝登录", clientId);
                return false;
            }
            if (StringUtil.isNotEmpty(clientSecret)) {
                return passwordEncoder.matches(clientSecret, client.getClientSecret());
            }
            return true;
        } catch (RuntimeException e) {
            log.warn("客户端验证异常: clientId={},拒绝登录: {}", clientId, e.getMessage());
            return false;
        }
    }

    /**
     * 登出 - 删除Redis中的token及用户索引
     *
     * 支持两种方式传递待登出的 access_token：
     * 1. 请求参数 access_token（向后兼容）
     * 2. Header Authorization: Bearer xxx（推荐）
     *
     * 身份校验：当前认证用户的 userId 必须与待登出 token 中的 userId 一致，
     * 避免 A 用户登出 B 用户的 token。
     */
    @PostMapping("/logout")
    @Operation(summary = "登出")
    public ResponseResult<String> logout(
            @RequestParam(value = "access_token", required = false) String accessToken,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        // 优先从 Header 获取 token
        String targetToken = accessToken;
        if (StringUtil.isEmpty(targetToken) && StringUtil.isNotEmpty(authorizationHeader)
                && authorizationHeader.startsWith("Bearer ")) {
            targetToken = authorizationHeader.substring(7);
        }

        if (StringUtil.isEmpty(targetToken)) {
            return ResponseResult.paramError("access_token不能为空");
        }

        // 获取待登出 token 对应的用户数据
        String userJson = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN_PREFIX + targetToken);
        if (StringUtil.isEmpty(userJson)) {
            // token 已失效，幂等返回成功
            return ResponseResult.ok("登出成功");
        }

        String tokenUserId;
        String tokenClientId;
        try {
            JSONObject userData = JSONObject.parseObject(userJson);
            tokenUserId = userData.getString("id");
            tokenClientId = userData.getString("clientId");
        } catch (RuntimeException e) {
            log.warn("登出时解析Token数据异常", e);
            return ResponseResult.paramError("Token解析失败");
        }

        // 身份校验：当前认证用户必须与 token 中的 userId 一致
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            String currentUserId = null;
            if (details instanceof JSONObject) {
                currentUserId = ((JSONObject) details).getString("id");
            }
            // 若当前有认证用户且 userId 不一致，则拒绝
            if (StringUtil.isNotEmpty(currentUserId) && !currentUserId.equals(tokenUserId)) {
                log.warn("登出身份校验失败: currentUser={}, tokenUser={}", currentUserId, tokenUserId);
                return ResponseResult.<String>error("无权登出他人Token",
                        com.sunmax.common.util.ResponseResult.CodeStatus.ACCESS_FAIL, null);
            }
        }

        // 清除用户Token索引
        if (StringUtil.isNotEmpty(tokenUserId) && StringUtil.isNotEmpty(tokenClientId)) {
            stringRedisTemplate.delete(USER_TOKEN_INDEX_PREFIX + tokenClientId + ":" + tokenUserId);
        }
        // 清除 access_token
        stringRedisTemplate.delete(ACCESS_TOKEN_PREFIX + targetToken);
        // 将 access_token 加入黑名单（JWT 模式下防止旧 token 仍能通过本地验签）
        if (jwtEnabled) {
            tokenBlacklistService.blacklistAccessToken(targetToken, ACCESS_TOKEN_VALIDITY_SECONDS);
        }

        // 清除用户的 refresh_token 及其索引（登出时立即失效，防止 refresh_token 在登出后仍可换取新令牌）
        if (StringUtil.isNotEmpty(tokenUserId) && StringUtil.isNotEmpty(tokenClientId)) {
            String userRefreshKey = USER_REFRESH_TOKEN_INDEX_PREFIX + tokenClientId + ":" + tokenUserId;
            String oldRefreshToken = stringRedisTemplate.opsForValue().get(userRefreshKey);
            if (StringUtil.isNotEmpty(oldRefreshToken)) {
                stringRedisTemplate.delete(REFRESH_TOKEN_PREFIX + oldRefreshToken);
                // 将 refresh_token 加入黑名单（JWT 模式下防止旧 token 仍能通过本地验签）
                if (jwtEnabled) {
                    tokenBlacklistService.blacklistRefreshToken(oldRefreshToken, REFRESH_TOKEN_VALIDITY_SECONDS);
                }
                log.debug("登出清理RefreshToken: userId={}, oldToken={}", tokenUserId, oldRefreshToken.substring(0, Math.min(8, oldRefreshToken.length())));
            }
            // 清除用户RefreshToken索引
            stringRedisTemplate.delete(userRefreshKey);
        }
        log.info("用户登出: userId={}, clientId={}, token={}..., jwtEnabled={}",
                tokenUserId, tokenClientId, targetToken.substring(0, Math.min(8, targetToken.length())), jwtEnabled);
        return ResponseResult.ok("登出成功");
    }

    /**
     * 校验Token有效性
     */
    @GetMapping("/check_token")
    @Operation(summary = "校验Token有效性")
    public ResponseResult<JSONObject> checkToken(@RequestParam(value = "access_token") String accessToken) {
        String userJson = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN_PREFIX + accessToken);
        if (StringUtil.isEmpty(userJson)) {
            return ResponseResult.paramError("Token无效或已过期");
        }
        try {
            JSONObject userData = JSONObject.parseObject(userJson);
            return ResponseResult.ok(userData);
        } catch (RuntimeException e) {
            return ResponseResult.paramError("Token解析失败");
        }
    }
}