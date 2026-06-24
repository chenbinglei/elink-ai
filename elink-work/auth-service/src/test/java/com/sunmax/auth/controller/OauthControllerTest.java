package com.sunmax.auth.controller;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.auth.service.UserLoginService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OauthController oauthController;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("clientId为空时拒绝登录")
    void token_emptyClientId_rejectsLogin() {
        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", null, null, "admin", "password", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("客户端认证失败"));
    }

    @Test
    @DisplayName("clientId未注册时拒绝登录")
    void token_unregisteredClient_rejectsLogin() {
        when(registeredClientRepository.findByClientId("unknown-client")).thenReturn(null);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", "unknown-client", null, "admin", "password", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("客户端认证失败"));
    }

    @Test
    @DisplayName("已注册客户端无密钥时允许登录-用户名密码为空返回参数错误")
    void token_registeredClientNoSecret_emptyCredentials_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "sys_pwd", "sunos-client", null, "", "", null, null, null, null);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("不能为空"));
    }

    @Test
    @DisplayName("不支持的grant_type返回参数错误")
    void token_unsupportedGrantType_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "unsupported_type", "sunos-client", null, "admin", "password", null, null, null, null);

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
                "sys_pwd", "sunos-client", "wrong-secret", "admin", "password", null, null, null, null);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("applet登录-code为空返回参数错误")
    void appletLogin_emptyCode_returnsParamError() {
        RegisteredClient client = mock(RegisteredClient.class);
        when(registeredClientRepository.findByClientId("sunos-client")).thenReturn(client);

        ResponseResult<JSONObject> result = oauthController.postAccessToken(
                "applet", "sunos-client", null, null, null, "", "appletKey", null, null);

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

        ResponseResult<String> result = oauthController.logout(token);

        assertTrue(result.isSuccess());
        verify(stringRedisTemplate).delete("auth:access:" + token);
    }

    @Test
    @DisplayName("登出-空token时直接返回成功")
    void logout_emptyToken_returnsSuccess() {
        ResponseResult<String> result = oauthController.logout("");

        assertTrue(result.isSuccess());
        verify(stringRedisTemplate, never()).delete(anyString());
    }
}
