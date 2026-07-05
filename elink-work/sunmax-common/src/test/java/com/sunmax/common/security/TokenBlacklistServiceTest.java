package com.sunmax.common.security;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TokenBlacklistService 单元测试")
class TokenBlacklistServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private MeterRegistry meterRegistry;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        try {
            java.lang.reflect.Field field = TokenBlacklistService.class.getDeclaredField("meterRegistry");
            field.setAccessible(true);
            field.set(tokenBlacklistService, meterRegistry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("1. access_token 加入黑名单 - 调用 Redis set 并设置 TTL")
    void blacklistAccessToken_writesToRedisWithTtl() {
        // when
        tokenBlacklistService.blacklistAccessToken("at-xxx", 3600L);

        // then
        verify(valueOperations).set(eq("auth:access:blacklist:at-xxx"), eq("1"), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("2. refresh_token 加入黑名单 - 调用 Redis set 并设置 TTL")
    void blacklistRefreshToken_writesToRedisWithTtl() {
        tokenBlacklistService.blacklistRefreshToken("rt-yyy", 86400L);

        verify(valueOperations).set(eq("auth:refresh:blacklist:rt-yyy"), eq("1"), eq(86400L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("3. isAccessTokenBlacklisted - Redis 存在返回 true")
    void isAccessTokenBlacklisted_exists_returnsTrue() {
        when(stringRedisTemplate.hasKey("auth:access:blacklist:at-xxx")).thenReturn(true);

        assertTrue(tokenBlacklistService.isAccessTokenBlacklisted("at-xxx"));
    }

    @Test
    @DisplayName("4. isAccessTokenBlacklisted - Redis 不存在返回 false")
    void isAccessTokenBlacklisted_notExists_returnsFalse() {
        when(stringRedisTemplate.hasKey("auth:access:blacklist:at-xxx")).thenReturn(false);

        assertFalse(tokenBlacklistService.isAccessTokenBlacklisted("at-xxx"));
    }

    @Test
    @DisplayName("5. isRefreshTokenBlacklisted - Redis 存在返回 true")
    void isRefreshTokenBlacklisted_exists_returnsTrue() {
        when(stringRedisTemplate.hasKey("auth:refresh:blacklist:rt-yyy")).thenReturn(true);

        assertTrue(tokenBlacklistService.isRefreshTokenBlacklisted("rt-yyy"));
    }

    @Test
    @DisplayName("6. isRefreshTokenBlacklisted - Redis 不存在返回 false")
    void isRefreshTokenBlacklisted_notExists_returnsFalse() {
        when(stringRedisTemplate.hasKey("auth:refresh:blacklist:rt-yyy")).thenReturn(false);

        assertFalse(tokenBlacklistService.isRefreshTokenBlacklisted("rt-yyy"));
    }

    @Test
    @DisplayName("7. TTL 非法（<=0）时使用兜底值 - access_token 3 天")
    void blacklistAccessToken_invalidTtl_usesFallback() {
        tokenBlacklistService.blacklistAccessToken("at-zzz", 0L);

        // 兜底 TTL 应为 3 天 = 259200 秒
        verify(valueOperations).set(eq("auth:access:blacklist:at-zzz"), eq("1"), eq(259200L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("8. TTL 非法（负数）时 refresh_token 兜底 30 天")
    void blacklistRefreshToken_invalidTtl_usesFallback() {
        tokenBlacklistService.blacklistRefreshToken("rt-www", -1L);

        verify(valueOperations).set(eq("auth:refresh:blacklist:rt-www"), eq("1"), eq(2592000L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("9. 空 token 不调用 Redis（避免空 key 写入）")
    void blacklist_emptyToken_skipsRedis() {
        tokenBlacklistService.blacklistAccessToken("", 3600L);
        tokenBlacklistService.blacklistRefreshToken(null, 3600L);

        verifyNoInteractions(valueOperations);
    }

    @Test
    @DisplayName("10. isBlacklisted - 空 token 返回 false，不查 Redis")
    void isBlacklisted_emptyToken_returnsFalseWithoutRedisCall() {
        assertFalse(tokenBlacklistService.isAccessTokenBlacklisted(""));
        assertFalse(tokenBlacklistService.isRefreshTokenBlacklisted(null));

        verify(stringRedisTemplate, never()).hasKey(anyString());
    }

    @Test
    @DisplayName("11. hasKey 返回 null（Redis 异常）- 兜底返回 false")
    void isBlacklisted_redisReturnsNull_returnsFalse() {
        when(stringRedisTemplate.hasKey("auth:access:blacklist:at-null")).thenReturn(null);

        assertFalse(tokenBlacklistService.isAccessTokenBlacklisted("at-null"));
    }
}
