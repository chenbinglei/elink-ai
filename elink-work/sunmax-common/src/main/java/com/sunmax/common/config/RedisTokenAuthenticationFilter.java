package com.sunmax.common.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Redis Token 认证过滤器
 * 替代旧版的 OAuth2 JWT 资源服务器验证
 *
 * 工作原理：
 * 1. 从请求参数或Header中获取 access_token
 * 2. 从Redis中查找 token 对应的用户信息
 * 3. 构建Authentication对象放入SecurityContext
 * 4. 后续的权限校验由 UserResourceConfig 中的 access() 方法处理
 */
@Component
@Slf4j
public class RedisTokenAuthenticationFilter extends OncePerRequestFilter {

    /** Token在Redis中的key前缀，与OauthController保持一致 */
    private static final String ACCESS_TOKEN_PREFIX = "auth:access:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
            try {
                // 从Redis中查找token
                String userJson = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN_PREFIX + accessToken);
                if (StringUtil.isNotEmpty(userJson)) {
                    JSONObject userData = JSON.parseObject(userJson);
                    String userAccount = userData.getString("userAccount");

                    // 构建权限列表
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_USER")
                    );

                    // 构建Authentication对象，将用户信息作为details存储
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userAccount, null, authorities
                    );
                    authentication.setDetails(userData);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    if (log.isDebugEnabled()) {
                        log.debug("Redis Token认证成功: userAccount={}", userAccount);
                    }
                }
            } catch (Exception e) {
                log.warn("Redis Token认证异常: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取access_token
     * 支持以下方式：
     * 1. 请求参数 access_token
     * 2. Header Authorization: Bearer xxx
     */
    private String extractToken(HttpServletRequest request) {
        // 方式1: 从请求参数获取
        String token = request.getParameter("access_token");
        if (StringUtil.isNotEmpty(token)) {
            return token;
        }

        // 方式2: 从Authorization Header获取
        String header = request.getHeader("Authorization");
        if (StringUtil.isNotEmpty(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }
}
