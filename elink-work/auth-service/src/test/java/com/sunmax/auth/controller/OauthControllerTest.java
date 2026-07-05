package com.sunmax.auth.controller;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.auth.service.UserLoginService;
import com.sunmax.common.security.JwtTokenService;
import com.sunmax.common.security.TokenBlacklistService;
import com.sunmax.common.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("OauthController 单元测试")
class OauthControllerTest {

    @Mock
    private UserLoginService userLoginService;

    @Mock
    private RegisteredClientRepository registeredClientRepository;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OauthController oauthController;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        // 默认关闭 JWT，使用 UUID 模式（与生产默认配置一致）
        setJwtEnabled(false);
    }

    /**
     * 通过反射设置 jwtEnabled 私有字段
     */
    private void setJwtEnabled(boolean enabled) {
        try {
            Field field = OauthController.class.getDeclaredField("jwtEnabled");
            field.setAccessible(true);
            field.setBoolean(oauthController, enabled);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("设置 jwtEnabled 失败", e);
        }
    }

    @Test
    @DisplayName("clientId为空时拒绝登录")
    void token_emptyClientId_rejectsLogin() {
        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", null, null, "admin", "password", null, null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("客户端认证失败"));
    }

    @Test
    @DisplayName("clientId未注册时拒绝登录")
    void token_unregisteredClient_rejectsLogin() {
        when(registeredClientRepository.findByClientId("unknown-client")).thenReturn(null);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", "unknown-client", null, "admin", "password", null, null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("客户端认证失败"));
    }

    @Test
    @DisplayName("已注册客户端无密钥时允许登录-用户名密码为空返回参数错误")
    void token_registeredClientNoSecret_emptyCredentials_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", "sunos-client", null, "", "", null, null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("不能为空"));
    }

    @Test
    @DisplayName("不支持的grant_type返回参数错误")
    void token_unsupportedGrantType_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "unsupported_type", "sunos-client", null, "admin", "password", null, null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("不支持的授权类型"));
    }

    @Test
    @DisplayName("客户端密钥校验-密钥不匹配时拒绝")
    void validateClient_secretMismatch_rejects() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);
        when(client.getClientSecret()).thenReturn("$2a$10$encodedSecret");
        when(passwordEncoder.matches("wrong-secret", "$2a$10$encodedSecret")).thenReturn(false);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", "sunos-client", "wrong-secret", "admin", "password", null, null, null, null, null);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("applet登录-code为空返回参数错误")
    void appletLogin_emptyCode_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "applet", "sunos-client", null, null, null, null, "", "appletKey", null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("微信授权码不能为空"));
    }

    @Test
    @DisplayName("登出-有效token时删除Redis缓存")
    void logout_validToken_deletesRedisCache() {
        String token = "test-access-token";
        JSONObject userData = new JSONObject();
        userData.put("id", "user-001");
        userData.put("clientId", "sunos-client");
        when(valueOperations.get("auth:access:" + token)).thenReturn(userData.toJSONString());

        // 新签名：logout(accessToken, authorizationHeader)
        ResponseResult<String> result = oauthController.logout(token, null);

        assertTrue(result.isSuccess());
        verify(stringRedisTemplate).delete("auth:access:" + token);
    }

    @Test
    @DisplayName("登出-空token时直接返回成功")
    void logout_emptyToken_returnsSuccess() {
        // 空 token + 空 Header，应直接返回 paramError（非成功）
        ResponseResult<String> result = oauthController.logout("", null);

        // 改造后空 token 返回 paramError，success=false
        assertFalse(result.isSuccess());
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("登出-支持 Authorization Header 传递 token")
    void logout_authorizationHeader_deletesRedisCache() {
        String token = "header-access-token";
        JSONObject userData = new JSONObject();
        userData.put("id", "user-002");
        userData.put("clientId", "derms-client");
        when(valueOperations.get("auth:access:" + token)).thenReturn(userData.toJSONString());

        ResponseResult<String> result = oauthController.logout(null, "Bearer " + token);

        assertTrue(result.isSuccess());
        verify(stringRedisTemplate).delete("auth:access:" + token);
        verify(stringRedisTemplate).delete("auth:user:token:derms-client:user-002");
    }

    @Test
    @DisplayName("登出-无效 Authorization Header 格式时返回参数错误")
    void logout_invalidAuthorizationHeader_returnsParamError() {
        ResponseResult<String> result = oauthController.logout(null, "Basic abc");

        assertFalse(result.isSuccess());
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("登出-Token解析失败时返回参数错误")
    void logout_invalidTokenData_returnsParamError() {
        String token = "corrupt-token";
        when(valueOperations.get("auth:access:" + token)).thenReturn("not-a-json");

        ResponseResult<String> result = oauthController.logout(token, null);

        assertFalse(result.isSuccess());
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("登出-已失效token幂等返回成功")
    void logout_alreadyInvalidToken_returnsSuccess() {
        String token = "expired-token";
        when(valueOperations.get("auth:access:" + token)).thenReturn(null);

        ResponseResult<String> result = oauthController.logout(token, null);

        assertTrue(result.isSuccess());
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    // ==================== Refresh Token 测试 ====================

    @Test
    @DisplayName("refresh_token-空refreshToken返回参数错误")
    void refresh_emptyRefreshToken_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "sunos-client", null, null, null, "", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("refresh_token"));
    }

    @Test
    @DisplayName("refresh_token-refreshToken不存在返回参数错误")
    void refresh_invalidRefreshToken_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);
        when(valueOperations.get("auth:refresh:invalid-token")).thenReturn(null);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "sunos-client", null, null, null, "invalid-token", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("无效或已过期"));
    }

    @Test
    @DisplayName("refresh_token-clientId不匹配返回参数错误")
    void refresh_clientIdMismatch_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("derms-client")).thenReturn(client);

        JSONObject userData = new JSONObject();
        userData.put("id", "user-001");
        userData.put("userAccount", "cbl");
        userData.put("clientId", "sunos-client"); // 原登录时是 sunos-client
        when(valueOperations.get("auth:refresh:valid-refresh")).thenReturn(userData.toJSONString());

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "derms-client", null, null, null, "valid-refresh", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("clientId"));
        // 不应删除任何 token
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("refresh_token-有效refreshToken返回新令牌并删除旧令牌")
    void refresh_validRefreshToken_returnsNewTokensAndDeletesOld() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        JSONObject userData = new JSONObject();
        userData.put("id", "user-001");
        userData.put("userAccount", "cbl");
        userData.put("clientId", "sunos-client");
        when(valueOperations.get("auth:refresh:valid-refresh")).thenReturn(userData.toJSONString());
        when(valueOperations.get("auth:user:token:sunos-client:user-001")).thenReturn("old-access-token");

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "sunos-client", null, null, null, "valid-refresh", null, null, null, null);

        assertTrue(result.isSuccess());
        JSONObject data = result.getData();
        assertNotNull(data);
        assertNotNull(data.getString("accessToken"));
        assertNotNull(data.getString("refreshToken"));
        assertEquals("sunos-client", data.getString("clientId"));
        assertEquals("user-001", data.getString("id"));

        // 验证删除了旧 refresh_token
        verify(stringRedisTemplate).delete("auth:refresh:valid-refresh");
        // 验证删除了旧 access_token
        verify(stringRedisTemplate).delete("auth:access:old-access-token");
        // 验证写入了新 access_token、refresh_token、用户索引
        verify(valueOperations).set(eq("auth:access:" + data.getString("accessToken")), anyString(), anyLong(), any());
        verify(valueOperations).set(eq("auth:refresh:" + data.getString("refreshToken")), anyString(), anyLong(), any());
        verify(valueOperations).set(eq("auth:user:token:sunos-client:user-001"), eq(data.getString("accessToken")), anyLong(), any());
    }

    @Test
    @DisplayName("refresh_token-二次使用相同refreshToken应失败（一次性使用）")
    void refresh_reuseSameRefreshToken_fails() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        // 第二次调用时，refresh_token 已被删除（返回 null）
        when(valueOperations.get("auth:refresh:used-token")).thenReturn(null);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "sunos-client", null, null, null, "used-token", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("无效或已过期"));
    }

    // ==================== JWT 模式测试（步骤 2.2 新增） ====================

    @Test
    @DisplayName("JWT模式-登录成功后调用 JwtTokenService 签发 JWT")
    void jwtLogin_success_callsJwtTokenService() {
        setJwtEnabled(true);
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        com.sunmax.auth.dto.UserLoginDto user = new com.sunmax.auth.dto.UserLoginDto();
        user.setId("user-001");
        user.setUserAccount("cbl");
        user.setUserRole(0);
        user.setTenantId("tenant-001");
        user.setClientId("sunos-client");
        user.setFullName("测试用户");
        when(userLoginService.loadSysUserByAccountAndPassword("cbl", "pwd", "sunos-client")).thenReturn(user);
        when(jwtTokenService.generateAccessToken("cbl", "user-001", 0, "tenant-001", "sunos-client", "v1"))
                .thenReturn("jwt-access-token-xxx");
        when(jwtTokenService.generateRefreshToken("cbl", "user-001", 0, "tenant-001", "sunos-client"))
                .thenReturn("jwt-refresh-token-yyy");

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", "sunos-client", null, "cbl", "pwd", null, null, null, null, null);

        assertTrue(result.isSuccess());
        JSONObject data = result.getData();
        assertEquals("jwt-access-token-xxx", data.getString("accessToken"));
        assertEquals("jwt-refresh-token-yyy", data.getString("refreshToken"));
        // 验证调用了 JwtTokenService
        verify(jwtTokenService).generateAccessToken("cbl", "user-001", 0, "tenant-001", "sunos-client", "v1");
        verify(jwtTokenService).generateRefreshToken("cbl", "user-001", 0, "tenant-001", "sunos-client");
    }

    @Test
    @DisplayName("JWT模式-登录踢旧token时将旧token加入黑名单")
    void jwtLogin_kickOldToken_addsToBlacklist() {
        setJwtEnabled(true);
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        com.sunmax.auth.dto.UserLoginDto user = new com.sunmax.auth.dto.UserLoginDto();
        user.setId("user-001");
        user.setUserAccount("cbl");
        user.setUserRole(0);
        user.setTenantId("tenant-001");
        user.setClientId("sunos-client");
        user.setFullName("测试用户");
        when(userLoginService.loadSysUserByAccountAndPassword("cbl", "pwd", "sunos-client")).thenReturn(user);
        when(jwtTokenService.generateAccessToken(anyString(), anyString(), any(), anyString(), anyString(), anyString()))
                .thenReturn("new-jwt-access");
        when(jwtTokenService.generateRefreshToken(anyString(), anyString(), any(), anyString(), anyString()))
                .thenReturn("new-jwt-refresh");
        // 模拟存在旧 token
        when(valueOperations.get("auth:user:token:sunos-client:user-001")).thenReturn("old-jwt-access");

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", "sunos-client", null, "cbl", "pwd", null, null, null, null, null);

        assertTrue(result.isSuccess());
        // 验证删除了旧 access_token
        verify(stringRedisTemplate).delete("auth:access:old-jwt-access");
        // 验证将旧 token 加入黑名单
        verify(tokenBlacklistService).blacklistAccessToken(eq("old-jwt-access"), anyLong());
    }

    @Test
    @DisplayName("UUID模式-登录踢旧token时不加入黑名单（jwtEnabled=false）")
    void uuidLogin_kickOldToken_doesNotAddBlacklist() {
        setJwtEnabled(false); // UUID 模式
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        com.sunmax.auth.dto.UserLoginDto user = new com.sunmax.auth.dto.UserLoginDto();
        user.setId("user-001");
        user.setUserAccount("cbl");
        user.setUserRole(0);
        user.setTenantId("tenant-001");
        user.setClientId("sunos-client");
        user.setFullName("测试用户");
        when(userLoginService.loadSysUserByAccountAndPassword("cbl", "pwd", "sunos-client")).thenReturn(user);
        // 模拟存在旧 token
        when(valueOperations.get("auth:user:token:sunos-client:user-001")).thenReturn("old-uuid-token");

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", "sunos-client", null, "cbl", "pwd", null, null, null, null, null);

        assertTrue(result.isSuccess());
        verify(stringRedisTemplate).delete("auth:access:old-uuid-token");
        // UUID 模式下不应调用黑名单
        verify(tokenBlacklistService, never()).blacklistAccessToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("JWT模式-刷新时先验签 JWT，验签失败返回错误")
    void jwtRefresh_invalidJwtSignature_returnsError() throws Exception {
        setJwtEnabled(true);
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);
        when(jwtTokenService.parseAndVerify("invalid-jwt"))
                .thenThrow(new com.nimbusds.jose.JOSEException("验签失败"));

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "sunos-client", null, null, null, "invalid-jwt", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("无效或已过期"));
    }

    @Test
    @DisplayName("JWT模式-刷新时 type!=refresh 返回错误")
    void jwtRefresh_wrongTokenType_returnsError() throws Exception {
        setJwtEnabled(true);
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("type", "access")
                .build();
        when(jwtTokenService.parseAndVerify("jwt-with-access-type")).thenReturn(claims);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "sunos-client", null, null, null, "jwt-with-access-type", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("类型错误"));
    }

    @Test
    @DisplayName("JWT模式-刷新成功后重签新 JWT 并写入 Redis")
    void jwtRefresh_validJwt_returnsNewJwtAndWritesRedis() throws Exception {
        setJwtEnabled(true);
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        // 模拟 JWT 验签通过
        com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("type", JwtTokenService.TOKEN_TYPE_REFRESH)
                .build();
        when(jwtTokenService.parseAndVerify("valid-jwt-refresh")).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(false);

        // 模拟 Redis 返回用户数据
        JSONObject userData = new JSONObject();
        userData.put("id", "user-001");
        userData.put("userAccount", "cbl");
        userData.put("userRole", 0);
        userData.put("tenantId", "tenant-001");
        userData.put("clientId", "sunos-client");
        when(valueOperations.get("auth:refresh:valid-jwt-refresh")).thenReturn(userData.toJSONString());
        when(valueOperations.get("auth:user:token:sunos-client:user-001")).thenReturn("old-access");

        // 模拟新 JWT 生成
        when(jwtTokenService.generateAccessToken("cbl", "user-001", 0, "tenant-001", "sunos-client", "v1"))
                .thenReturn("new-jwt-access");
        when(jwtTokenService.generateRefreshToken("cbl", "user-001", 0, "tenant-001", "sunos-client"))
                .thenReturn("new-jwt-refresh");

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "sunos-client", null, null, null, "valid-jwt-refresh", null, null, null, null);

        assertTrue(result.isSuccess());
        JSONObject data = result.getData();
        assertEquals("new-jwt-access", data.getString("accessToken"));
        assertEquals("new-jwt-refresh", data.getString("refreshToken"));

        // 验证删除了旧 token
        verify(stringRedisTemplate).delete("auth:refresh:valid-jwt-refresh");
        verify(stringRedisTemplate).delete("auth:access:old-access");
        // 验证将旧 token 加入黑名单（JWT 模式专属）
        verify(tokenBlacklistService).blacklistRefreshToken(eq("valid-jwt-refresh"), anyLong());
        verify(tokenBlacklistService).blacklistAccessToken(eq("old-access"), anyLong());
        // 验证写入了新 JWT
        verify(valueOperations).set(eq("auth:access:new-jwt-access"), anyString(), anyLong(), any());
        verify(valueOperations).set(eq("auth:refresh:new-jwt-refresh"), anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("JWT模式-clientId不匹配仍返回错误（与UUID模式一致）")
    void jwtRefresh_clientIdMismatch_returnsError() throws Exception {
        setJwtEnabled(true);
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("derms-client")).thenReturn(client);

        com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("type", JwtTokenService.TOKEN_TYPE_REFRESH)
                .build();
        when(jwtTokenService.parseAndVerify("valid-jwt")).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(false);

        JSONObject userData = new JSONObject();
        userData.put("id", "user-001");
        userData.put("clientId", "sunos-client");
        when(valueOperations.get("auth:refresh:valid-jwt")).thenReturn(userData.toJSONString());

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "refresh_token", "derms-client", null, null, null, "valid-jwt", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("clientId"));
    }
}
