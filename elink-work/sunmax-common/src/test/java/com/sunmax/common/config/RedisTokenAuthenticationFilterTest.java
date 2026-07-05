package com.sunmax.common.config;

import com.alibaba.fastjson2.JSONObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.sunmax.common.security.JwtTokenService;
import com.sunmax.common.security.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RedisTokenAuthenticationFilter 单元测试
 *
 * 覆盖场景：
 * 1. SecurityContext 已存在认证信息 → 跳过
 * 2. 请求无 token → 跳过
 * 3. 参数 access_token → 认证成功
 * 4. Header Authorization: Bearer xxx → 认证成功
 * 5. Redis 中无 token → 跳过
 * 6. userRole 映射：0/1/2/null
 * 7. Token 异常 → 跳过且不抛出
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RedisTokenAuthenticationFilter 单元测试")
class RedisTokenAuthenticationFilterTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RedisTokenAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityContext已存在认证信息时跳过token验证")
    void doFilter_existingAuthentication_skipsTokenValidation() throws Exception {
        // 预设已有认证信息
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("existingUser", null, java.util.Collections.emptyList()));
        SecurityContextHolder.setContext(context);

        filter.doFilterInternal(request, response, filterChain);

        // 不应查询Redis
        verify(stringRedisTemplate, never()).opsForValue();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("请求无token时跳过认证")
    void doFilter_noToken_skipsAuthentication() throws Exception {
        when(request.getParameter("access_token")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Header Authorization Bearer认证成功-平台管理员角色")
    void doFilter_bearerHeader_platformAdminRole() throws Exception {
        String token = "platform-admin-token";
        when(request.getParameter("access_token")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        JSONObject userData = new JSONObject();
        userData.put("userAccount", "admin");
        userData.put("userRole", 0); // 平台管理员
        when(valueOperations.get("auth:access:" + token)).thenReturn(userData.toJSONString());

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("admin", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PLATFORM_ADMIN")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("参数access_token认证成功-普通用户角色（兼容旧前端/第三方调用）")
    void doFilter_paramAccessToken_userRole() throws Exception {
        String token = "param-user-token";
        when(request.getParameter("access_token")).thenReturn(token);
        when(request.getHeader("Authorization")).thenReturn(null);

        JSONObject userData = new JSONObject();
        userData.put("userAccount", "thirdparty");
        userData.put("userRole", 2); // 普通用户
        when(valueOperations.get("auth:access:" + token)).thenReturn(userData.toJSONString());

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("thirdparty", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("参数access_token优先于Header（参数优先策略）")
    void doFilter_paramTokenTakesPrecedenceOverHeader() throws Exception {
        String paramToken = "param-token";
        String headerToken = "header-token";
        when(request.getParameter("access_token")).thenReturn(paramToken);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + headerToken);

        JSONObject userData = new JSONObject();
        userData.put("userAccount", "test");
        userData.put("userRole", 2);
        when(valueOperations.get("auth:access:" + paramToken)).thenReturn(userData.toJSONString());

        filter.doFilterInternal(request, response, filterChain);

        // 应查询 param-token，而非 header-token
        verify(valueOperations).get("auth:access:" + paramToken);
        verify(valueOperations, never()).get("auth:access:" + headerToken);
    }

    @Test
    @DisplayName("Header Authorization Bearer认证成功-管理员角色")
    void doFilter_bearerHeader_adminRole() throws Exception {
        String token = "admin-token";
        when(request.getParameter("access_token")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        JSONObject userData = new JSONObject();
        userData.put("userAccount", "manager");
        userData.put("userRole", 1); // 管理员
        when(valueOperations.get("auth:access:" + token)).thenReturn(userData.toJSONString());

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("manager", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        // details 应携带 userData
        Object details = auth.getDetails();
        assertTrue(details instanceof JSONObject);
        assertEquals("manager", ((JSONObject) details).getString("userAccount"));
    }

    @Test
    @DisplayName("普通用户角色映射正确")
    void doFilter_normalUser_userRole() throws Exception {
        String token = "user-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        JSONObject userData = new JSONObject();
        userData.put("userAccount", "cbl");
        userData.put("userRole", 2); // 普通用户
        when(valueOperations.get("auth:access:" + token)).thenReturn(userData.toJSONString());

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("userRole为null时兜底为ROLE_USER")
    void doFilter_nullUserRole_defaultsToUserRole() throws Exception {
        String token = "null-role-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        JSONObject userData = new JSONObject();
        userData.put("userAccount", "test");
        userData.put("userRole", null);
        when(valueOperations.get("auth:access:" + token)).thenReturn(userData.toJSONString());

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Redis中无token时跳过认证")
    void doFilter_tokenNotInRedis_skipsAuthentication() throws Exception {
        String token = "missing-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(valueOperations.get("auth:access:" + token)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization Header非Bearer格式时不提取token")
    void doFilter_nonBearerHeader_skipsAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(stringRedisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Redis解析异常时跳过认证且不抛出")
    void doFilter_redisParseException_skipsAndNoException() throws Exception {
        String token = "corrupt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        // 模拟解析异常：返回非JSON字符串
        when(valueOperations.get("auth:access:" + token)).thenReturn("not-a-json");

        // 不应抛出异常
        assertDoesNotThrow(() -> filter.doFilterInternal(request, response, filterChain));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    // ==================== JWT 模式测试（步骤 2.3 新增） ====================

    /**
     * 构造一个合法的 JWT 格式字符串（三段式）
     */
    private static final String JWT_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJjYmwiLCJ0eXBlIjoiYWNjZXNzIn0.signature";

    @Test
    @DisplayName("JWT模式-JWT格式token+验签成功+非黑名单 → 从claim构建Authentication")
    void doFilter_jwtToken_validAndNotBlacklisted_buildsAuthFromClaims() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT_TOKEN);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("id", "user-001")
                .claim("userRole", 0)
                .claim("tenantId", "tenant-001")
                .claim("clientId", "sunos-client")
                .claim("type", JwtTokenService.TOKEN_TYPE_ACCESS)
                .build();
        when(jwtTokenService.parseAndVerify(JWT_TOKEN)).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(false);
        when(tokenBlacklistService.isAccessTokenBlacklisted(JWT_TOKEN)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("cbl", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PLATFORM_ADMIN")));
        // details 应包含 claim 信息
        Object details = auth.getDetails();
        assertTrue(details instanceof JSONObject);
        JSONObject json = (JSONObject) details;
        assertEquals("user-001", json.getString("id"));
        assertEquals("sunos-client", json.getString("clientId"));
        assertEquals("tenant-001", json.getString("tenantId"));
        // JWT 认证成功后不应查 Redis
        verify(stringRedisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("JWT模式-验签失败 → 回退 UUID Redis 查询")
    void doFilter_jwtToken_verifyFails_fallsBackToRedis() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT_TOKEN);
        when(jwtTokenService.parseAndVerify(JWT_TOKEN))
                .thenThrow(new RuntimeException("验签失败"));
        // 回退到 UUID 模式，Redis 也没找到
        when(valueOperations.get("auth:access:" + JWT_TOKEN)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        // 应尝试 Redis 查询（回退）
        verify(valueOperations).get("auth:access:" + JWT_TOKEN);
        verify(tokenBlacklistService, never()).isAccessTokenBlacklisted(anyString());
    }

    @Test
    @DisplayName("JWT模式-验签失败 → 回退 UUID 且 Redis 命中 → 认证成功")
    void doFilter_jwtToken_verifyFails_redisHit_fallsBackToRedisAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT_TOKEN);
        when(jwtTokenService.parseAndVerify(JWT_TOKEN))
                .thenThrow(new RuntimeException("验签失败"));

        JSONObject userData = new JSONObject();
        userData.put("userAccount", "fallback-user");
        userData.put("userRole", 2);
        when(valueOperations.get("auth:access:" + JWT_TOKEN)).thenReturn(userData.toJSONString());

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("fallback-user", auth.getPrincipal());
        // 是 Redis 模式构建的（非 JWT）
        verify(tokenBlacklistService, never()).isAccessTokenBlacklisted(anyString());
    }

    @Test
    @DisplayName("JWT模式-JWT已过期 → 回退 UUID 模式")
    void doFilter_jwtToken_expired_fallsBackToRedis() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT_TOKEN);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("type", JwtTokenService.TOKEN_TYPE_ACCESS)
                .build();
        when(jwtTokenService.parseAndVerify(JWT_TOKEN)).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(true);
        when(valueOperations.get("auth:access:" + JWT_TOKEN)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(valueOperations).get("auth:access:" + JWT_TOKEN);
    }

    @Test
    @DisplayName("JWT模式-type!=access → 拒绝（不回退，但调用方应处理）")
    void doFilter_jwtToken_wrongType_fallsBackToRedis() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT_TOKEN);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("type", "refresh")
                .build();
        when(jwtTokenService.parseAndVerify(JWT_TOKEN)).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(false);
        when(valueOperations.get("auth:access:" + JWT_TOKEN)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenBlacklistService, never()).isAccessTokenBlacklisted(anyString());
    }

    @Test
    @DisplayName("JWT模式-JWT在黑名单中（已登出/已刷新） → 回退 UUID 模式")
    void doFilter_jwtToken_blacklisted_fallsBackToRedis() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT_TOKEN);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("type", JwtTokenService.TOKEN_TYPE_ACCESS)
                .build();
        when(jwtTokenService.parseAndVerify(JWT_TOKEN)).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(false);
        when(tokenBlacklistService.isAccessTokenBlacklisted(JWT_TOKEN)).thenReturn(true);
        when(valueOperations.get("auth:access:" + JWT_TOKEN)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        // 黑名单命中，回退到 Redis（Redis 也没找到）
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(valueOperations).get("auth:access:" + JWT_TOKEN);
    }

    @Test
    @DisplayName("JWT模式-非JWT格式token（UUID） → 直接走 UUID Redis 模式")
    void doFilter_uuidToken_skipsJwtPath() throws Exception {
        String uuidToken = "abc123def456"; // 无点号，非 JWT 格式
        when(request.getHeader("Authorization")).thenReturn("Bearer " + uuidToken);

        JSONObject userData = new JSONObject();
        userData.put("userAccount", "uuid-user");
        userData.put("userRole", 1);
        when(valueOperations.get("auth:access:" + uuidToken)).thenReturn(userData.toJSONString());

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("uuid-user", auth.getPrincipal());
        // 不应调用 JWT 服务
        verify(jwtTokenService, never()).parseAndVerify(anyString());
        verify(tokenBlacklistService, never()).isAccessTokenBlacklisted(anyString());
    }

    @Test
    @DisplayName("JWT模式-JWT认证成功后不查Redis（短路）")
    void doFilter_jwtAuthSuccess_doesNotQueryRedis() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT_TOKEN);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("userRole", 2)
                .claim("type", JwtTokenService.TOKEN_TYPE_ACCESS)
                .build();
        when(jwtTokenService.parseAndVerify(JWT_TOKEN)).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(false);
        when(tokenBlacklistService.isAccessTokenBlacklisted(JWT_TOKEN)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(stringRedisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("JWT模式-userRole=1 映射为 ROLE_ADMIN")
    void doFilter_jwtToken_adminRole_mapping() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT_TOKEN);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("admin-user")
                .claim("userRole", 1)
                .claim("type", JwtTokenService.TOKEN_TYPE_ACCESS)
                .build();
        when(jwtTokenService.parseAndVerify(JWT_TOKEN)).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(false);
        when(tokenBlacklistService.isAccessTokenBlacklisted(JWT_TOKEN)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("JWT模式-参数 access_token 携带 JWT → 也能识别为 JWT 模式")
    void doFilter_jwtTokenInQueryParam_recognizedAsJwt() throws Exception {
        when(request.getParameter("access_token")).thenReturn(JWT_TOKEN);
        when(request.getHeader("Authorization")).thenReturn(null);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("cbl")
                .claim("userRole", 2)
                .claim("type", JwtTokenService.TOKEN_TYPE_ACCESS)
                .build();
        when(jwtTokenService.parseAndVerify(JWT_TOKEN)).thenReturn(claims);
        when(jwtTokenService.isExpired(claims)).thenReturn(false);
        when(tokenBlacklistService.isAccessTokenBlacklisted(JWT_TOKEN)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("cbl", auth.getPrincipal());
    }
}
