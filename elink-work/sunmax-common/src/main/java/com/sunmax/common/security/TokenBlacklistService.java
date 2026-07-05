package com.sunmax.common.security;

import com.sunmax.common.util.StringUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单服务
 *
 * <p>用于方案B阶段二步骤 2.3/2.4：JWT 模式下，登出或刷新时将旧 token 加入黑名单，
 * 防止 JWT 在 Redis access_token 删除后仍能通过本地验签被使用。</p>
 *
 * <p>UUID 模式下不写入黑名单（OauthController 中所有黑名单调用在 {@code if (jwtEnabled)} 守卫内，
 * UUID token 通过 Redis 删除即失效，无需黑名单机制）。</p>
 *
 * <p>Redis Key 设计：</p>
 * <ul>
 *   <li>access token 黑名单：{@code auth:access:blacklist:{token}}</li>
 *   <li>refresh token 黑名单：{@code auth:refresh:blacklist:{token}}</li>
 * </ul>
 *
 * <p>TTL 策略：与对应 token 的剩余有效期一致，自动清理过期黑名单条目。</p>
 */
@Component
@Slf4j
public class TokenBlacklistService {

    /** Access Token 黑名单 Redis Key 前缀 */
    private static final String ACCESS_BLACKLIST_PREFIX = "auth:access:blacklist:";

    /** Refresh Token 黑名单 Redis Key 前缀 */
    private static final String REFRESH_BLACKLIST_PREFIX = "auth:refresh:blacklist:";

    /** 黑名单值（仅做存在性判断，内容固定为 1） */
    private static final String BLACKLIST_VALUE = "1";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    /** 记录黑名单加入次数 */
    private void recordBlacklistAdd(String type) {
        Counter.builder("jwt.blacklist.add")
                .tag("type", type)
                .description("Token 加入黑名单次数")
                .register(meterRegistry)
                .increment();
    }

    /** 记录黑名单检查结果 */
    private void recordBlacklistCheck(String type, boolean hit) {
        Counter.builder("jwt.blacklist.check")
                .tag("type", type)
                .tag("result", hit ? "hit" : "miss")
                .description("Token 黑名单检查次数及命中情况")
                .register(meterRegistry)
                .increment();
    }

    /**
     * 将 access_token 加入黑名单
     *
     * @param token      access_token 字符串（JWT 或 UUID）
     * @param ttlSeconds TTL（秒），建议与 Redis access_token 剩余有效期一致
     */
    public void blacklistAccessToken(String token, long ttlSeconds) {
        blacklist(ACCESS_BLACKLIST_PREFIX, token, ttlSeconds, "access");
        recordBlacklistAdd("access");
    }

    /**
     * 将 refresh_token 加入黑名单
     *
     * @param token      refresh_token 字符串（JWT 或 UUID）
     * @param ttlSeconds TTL（秒），建议与 Redis refresh_token 剩余有效期一致
     */
    public void blacklistRefreshToken(String token, long ttlSeconds) {
        blacklist(REFRESH_BLACKLIST_PREFIX, token, ttlSeconds, "refresh");
        recordBlacklistAdd("refresh");
    }

    /**
     * 检查 access_token 是否在黑名单中
     *
     * @param token access_token 字符串
     * @return true=在黑名单（已登出/已刷新，应拒绝）；false=不在黑名单
     */
    public boolean isAccessTokenBlacklisted(String token) {
        boolean blacklisted = isBlacklisted(ACCESS_BLACKLIST_PREFIX, token);
        recordBlacklistCheck("access", blacklisted);
        return blacklisted;
    }

    /**
     * 检查 refresh_token 是否在黑名单中
     *
     * @param token refresh_token 字符串
     * @return true=在黑名单；false=不在黑名单
     */
    public boolean isRefreshTokenBlacklisted(String token) {
        boolean blacklisted = isBlacklisted(REFRESH_BLACKLIST_PREFIX, token);
        recordBlacklistCheck("refresh", blacklisted);
        return blacklisted;
    }

    private void blacklist(String prefix, String token, long ttlSeconds, String type) {
        if (StringUtil.isEmpty(token)) {
            return;
        }
        if (ttlSeconds <= 0) {
            // TTL 非法时使用兜底值：access 3 天，refresh 30 天
            ttlSeconds = "access".equals(type) ? 3 * 24 * 60 * 60L : 30 * 24 * 60 * 60L;
        }
        String key = prefix + token;
        stringRedisTemplate.opsForValue().set(key, BLACKLIST_VALUE, ttlSeconds, TimeUnit.SECONDS);
        if (log.isDebugEnabled()) {
            log.debug("Token 加入黑名单: type={}, token={}, ttl={}s", type, token.substring(0, Math.min(16, token.length())), ttlSeconds);
        }
    }

    private boolean isBlacklisted(String prefix, String token) {
        if (StringUtil.isEmpty(token)) {
            return false;
        }
        Boolean exists = stringRedisTemplate.hasKey(prefix + token);
        return Boolean.TRUE.equals(exists);
    }
}
