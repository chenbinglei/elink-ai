package com.sunmax.common.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.security.JwtTokenService;
import com.sunmax.common.security.TokenBlacklistService;
import com.sunmax.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis Token 认证过滤器
 * 替代旧版的 OAuth2 JWT 资源服务器验证
 *
 * <p>双轨模式（方案B阶段二步骤 2.3）：</p>
 * <ol>
 *   <li><b>JWT 验签优先</b>：token 符合 JWT 格式（三段式 xxx.yyy.zzz）时，
 *       调用 {@link JwtTokenService#parseAndVerify(String)} 本地验签，
 *       再校验 {@link TokenBlacklistService} 黑名单，
 *       全部通过后从 JWT claim 构建 Authentication。</li>
 *   <li><b>失败回退 UUID</b>：JWT 验签失败、黑名单命中、或 token 不是 JWT 格式时，
 *       回退到原有 Redis 查询逻辑（兼容过渡期未过期的 UUID token）。</li>
 * </ol>
 *
 * <p>工作原理：</p>
 * <ol>
 *   <li>从请求参数或 Header 中获取 access_token</li>
 *   <li>JWT 模式：本地验签 + 黑名单校验 + claim 构建 Authentication</li>
 *   <li>UUID 模式：从 Redis 中查找 token 对应的用户信息</li>
 *   <li>构建 Authentication 对象放入 SecurityContext</li>
 *   <li>后续的权限校验由 UserResourceConfig 中的 access() 方法处理</li>
 * </ol>
 */
@Component
@Slf4j
public class RedisTokenAuthenticationFilter extends OncePerRequestFilter {

    /** Token在Redis中的key前缀，与OauthController保持一致 */
    private static final String ACCESS_TOKEN_PREFIX = "auth:access:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 如果SecurityContext中已有认证信息，跳过
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从请求参数或Header中获取access_token
        String accessToken = extractToken(request);

        if (StringUtil.isNotEmpty(accessToken)) {
            // 双轨认证：JWT 优先 → 失败回退 UUID
            boolean authenticated = false;
            if (isJwtFormat(accessToken)) {
                authenticated = tryJwtAuthentication(accessToken);
            }
            if (!authenticated) {
                tryRedisAuthentication(accessToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 判断 token 是否符合 JWT 格式（三段式 header.payload.signature）
     */
    private boolean isJwtFormat(String token) {
        if (StringUtil.isEmpty(token)) {
            return false;
        }
        String[] parts = token.split("\\.");
        return parts.length == 3
                && parts[0].length() > 0
                && parts[1].length() > 0
                && parts[2].length() > 0;
    }

    /**
     * 尝试 JWT 认证
     *
     * @return true=认证成功；false=验签失败/黑名单/过期，调用方应回退到 UUID 模式
     */
    private boolean tryJwtAuthentication(String accessToken) {
        try {
            // 1. JWT 本地验签
            JWTClaimsSet claims = jwtTokenService.parseAndVerify(accessToken);

            // 2. 过期校验
            if (jwtTokenService.isExpired(claims)) {
                log.debug("JWT 已过期，回退 UUID 模式");
                return false;
            }

            // 3. type 必须为 access
            String type = claims.getStringClaim("type");
            if (!JwtTokenService.TOKEN_TYPE_ACCESS.equals(type)) {
                log.warn("JWT 类型错误（非 access token），拒绝: type={}", type);
                return false;
            }

            // 4. 黑名单校验（登出/刷新后旧 token 被加入黑名单）
            if (tokenBlacklistService.isAccessTokenBlacklisted(accessToken)) {
                log.warn("JWT 在黑名单中（已登出/已刷新），拒绝");
                return false;
            }

            // 5. 从 claim 构建 Authentication
            String userAccount = claims.getSubject();
            Integer userRole = claims.getIntegerClaim("userRole");
            List<SimpleGrantedAuthority> authorities = mapToAuthorities(userRole);

            // 构建 details（与 UUID 模式保持一致，便于下游 UserResourceConfig.getClientId 取用）
            JSONObject userData = new JSONObject();
            userData.put("userAccount", userAccount);
            userData.put("id", claims.getStringClaim("id"));
            userData.put("userRole", userRole);
            userData.put("tenantId", claims.getStringClaim("tenantId"));
            userData.put("clientId", claims.getStringClaim("clientId"));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userAccount, null, authorities
            );
            authentication.setDetails(userData);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (log.isDebugEnabled()) {
                log.debug("JWT 认证成功: userAccount={}, authorities={}", userAccount, authorities);
            }
            return true;
        } catch (Exception e) {
            // 验签失败、解析异常等，回退到 UUID 模式
            if (log.isDebugEnabled()) {
                log.debug("JWT 验签失败，回退 UUID 模式: {}", e.getMessage());
            }
            return false;
        }
    }

    /**
     * UUID 模式认证（原有逻辑，保持兼容）
     */
    private void tryRedisAuthentication(String accessToken) {
        try {
            String redisKey = ACCESS_TOKEN_PREFIX + accessToken;
            String userJson = stringRedisTemplate.opsForValue().get(redisKey);
            if (StringUtil.isNotEmpty(userJson)) {
                JSONObject userData = JSON.parseObject(userJson);
                String userAccount = userData.getString("userAccount");

                // 按 userRole 映射真实角色
                // 0-平台管理员(ROLE_PLATFORM_ADMIN) 1-管理员(ROLE_ADMIN) 2-普通用户(ROLE_USER)
                List<SimpleGrantedAuthority> authorities = mapToAuthorities(userData.getInteger("userRole"));

                // 构建Authentication对象，将用户信息作为details存储（兼容 UserResourceConfig.getClientId）
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userAccount, null, authorities
                );
                authentication.setDetails(userData);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (log.isDebugEnabled()) {
                    log.debug("Redis Token认证成功: userAccount={}, authorities={}", userAccount, authorities);
                }
            }
        } catch (RuntimeException e) {
            log.warn("Redis Token认证异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 从请求中提取 access_token
     *
     * 支持两种传递方式（按优先级）：
     * 1. 请求参数 access_token（兼容旧前端、第三方调用、文件下载等场景）
     *    - 适用场景：浏览器直链下载、WebSocket 建立、第三方系统集成
     *    - 风险提示：token 会出现在 URL/日志中，仅建议在无法使用 Header 时使用
     * 2. Header Authorization: Bearer xxx（推荐方式）
     *    - 适用场景：常规业务接口调用（推荐所有前端项目统一采用）
     *    - 优势：token 不暴露在 URL，GET 请求 URL 不会过长，符合 OAuth2 标准
     *
     * 设计原则：
     * - 两种方式并存，参数优先级高于 Header（与历史行为保持一致）
     * - 前端项目（linkos/derms/tycvs）统一采用 Header 方式
     * - 第三方接口/特殊场景（文件下载等）允许使用参数方式
     */
    private String extractToken(HttpServletRequest request) {
        // 方式1: 请求参数 access_token（兼容方式）
        String token = request.getParameter("access_token");
        if (StringUtil.isNotEmpty(token)) {
            return token;
        }

        // 方式2: Authorization Header Bearer（推荐方式）
        String header = request.getHeader("Authorization");
        if (StringUtil.isNotEmpty(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }

    /**
     * 根据 userRole 映射为 Spring Security GrantedAuthority
     * 0-平台管理员(ROLE_PLATFORM_ADMIN)
     * 1-管理员(ROLE_ADMIN)
     * 2-普通用户(ROLE_USER)
     * 其他/空值 - 兜底为 ROLE_USER
     */
    private List<SimpleGrantedAuthority> mapToAuthorities(Integer userRole) {
        String role;
        if (userRole == null) {
            role = "ROLE_USER";
        } else {
            switch (userRole) {
                case 0:
                    role = "ROLE_PLATFORM_ADMIN";
                    break;
                case 1:
                    role = "ROLE_ADMIN";
                    break;
                case 2:
                default:
                    role = "ROLE_USER";
                    break;
            }
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(1);
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}
