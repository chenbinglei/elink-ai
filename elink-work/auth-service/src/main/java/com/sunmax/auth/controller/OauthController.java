package com.sunmax.auth.controller;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.auth.service.UserLoginService;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /** Access Token Redis key前缀 */
    static final String ACCESS_TOKEN_PREFIX = "auth:access:";
    /** Refresh Token Redis key前缀 */
    static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";
    /** 用户Token索引前缀（用户ID -> accessToken，用于单用户踢出旧Token） */
    static final String USER_TOKEN_INDEX_PREFIX = "auth:user:token:";
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
            @Parameter(name = "grant_type", description = "授权类型: sys_pwd/applet"),
            @Parameter(name = "client_id", description = "客户端ID"),
            @Parameter(name = "client_secret", description = "客户端密钥"),
            @Parameter(name = "userAccount", description = "用户账号(sys_pwd时必填)"),
            @Parameter(name = "password", description = "用户密码(sys_pwd时必填，AES-CBC加密)"),
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
                default:
                    return ResponseResult.paramError("不支持的授权类型: " + grantType);
            }
        } catch (Exception e) {
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
            @RequestParam(value = "password", required = false) String password) {
        return postAccessToken(grantType, clientId, clientSecret, userAccount, password, null, null, null, null);
    }

    /**
     * 系统用户密码登录
     * 前端使用AES-CBC加密密码，后端用SecretUtil.desEncrypt()解密后与数据库明文密码比对
     */
    private ResponseResult<JSONObject> handleSysPwdLogin(String userAccount, String password, String clientId) {
        if (StringUtil.isEmpty(userAccount) || StringUtil.isEmpty(password)) {
            return ResponseResult.paramError("用户名和密码不能为空");
        }

        String effectiveClientId = StringUtil.isNotEmpty(clientId) ? clientId : "sunos-client";
        UserLoginDto user = userLoginService.loadSysUserByAccountAndPassword(userAccount, password, effectiveClientId);

        if (user.getCheckCode() != null && user.getCheckCode() != 0) {
            return ResponseResult.paramError(user.getCheckMsg(), user.getCheckCode());
        }

        user.setClientId(effectiveClientId);
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
        String accessToken = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = UUID.randomUUID().toString().replace("-", "");

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
            log.debug("踢出旧Token: userId={}, oldToken={}", user.getId(), oldAccessToken.substring(0, 8));
        }

        // 存储新Token
        stringRedisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + accessToken, tokenData.toJSONString(),
                ACCESS_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, tokenData.toJSONString(),
                REFRESH_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        // 用户Token索引（TTL与accessToken一致）
        stringRedisTemplate.opsForValue().set(userTokenKey, accessToken,
                ACCESS_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);

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
        result.put("menuList", user.getMenuList());

        log.info("用户登录成功: userAccount={}, clientId={}, logo={}", user.getUserAccount(), user.getClientId(), user.getLogo());
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
        } catch (Exception e) {
            log.warn("客户端验证异常: clientId={},拒绝登录: {}", clientId, e.getMessage());
            return false;
        }
    }

    /**
     * 登出 - 删除Redis中的token及用户索引
     */
    @PostMapping("/logout")
    @Operation(summary = "登出")
    public ResponseResult<String> logout(@RequestParam(value = "access_token", required = false) String accessToken) {
        if (StringUtil.isNotEmpty(accessToken)) {
            String userJson = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN_PREFIX + accessToken);
            if (StringUtil.isNotEmpty(userJson)) {
                try {
                    JSONObject userData = JSONObject.parseObject(userJson);
                    String userId = userData.getString("id");
                    String clientId = userData.getString("clientId");
                    // 清除用户Token索引
                    if (StringUtil.isNotEmpty(userId) && StringUtil.isNotEmpty(clientId)) {
                        stringRedisTemplate.delete(USER_TOKEN_INDEX_PREFIX + clientId + ":" + userId);
                    }
                } catch (Exception e) {
                    log.warn("登出时解析Token数据异常", e);
                }
            }
            stringRedisTemplate.delete(ACCESS_TOKEN_PREFIX + accessToken);
            log.info("用户登出: token={}...", accessToken.substring(0, Math.min(8, accessToken.length())));
        }
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
        } catch (Exception e) {
            return ResponseResult.paramError("Token解析失败");
        }
    }
}