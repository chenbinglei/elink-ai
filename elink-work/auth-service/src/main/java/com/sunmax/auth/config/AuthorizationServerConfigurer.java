package com.sunmax.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.sunmax.common.config.RedisTokenAuthenticationFilter;
import com.sunmax.common.config.SMAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sunmax.auth.dto.UserLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * 授权服务器配置 - 基于 Spring Authorization Server
 * 替代旧的 AuthorizationServerConfigurerAdapter
 *
 * 安全过滤链设计：
 * - Order(1): 授权服务器默认端点（/oauth2/token, /oauth2/jwks 等）
 * - Order(2): 业务API请求（登录、用户管理等自定义端点）
 */
@Configuration
@EnableWebSecurity
public class AuthorizationServerConfigurer {

    @Autowired
    private RedisTokenAuthenticationFilter redisTokenAuthenticationFilter;

    @Autowired
    private SMAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * 密码编码器 - BCrypt实现
     * 供OauthController等组件使用
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 已注册客户端仓库 - 从数据库读取客户端配置
     * 兼容旧版 oauth_client_details 表
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    /**
     * RSA密钥对 - 用于JWT签名和验证
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 授权服务器安全过滤链 - 处理标准OAuth2端点
     * 端点：/oauth2/token, /oauth2/jwks, /oauth2/authorize 等
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(
                    new org.springframework.security.web.authentication.HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED)
                )
            )
            .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * 业务API安全过滤链 - 处理自定义登录、用户管理等端点
     * 这些端点不走标准OAuth2协议，而是自定义REST API
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(request -> {
                // 使用 servletPath（不含 context-path）进行匹配，避免 context-path 干扰
                String path = request.getServletPath();
                // 排除标准OAuth2端点，由Order(1)的过滤链处理
                return !path.startsWith("/oauth2/") && !path.startsWith("/.well-known/");
            })
            .addFilterBefore(redisTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/doc.html").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/v3/api-docs").permitAll()
                .requestMatchers("/feign/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/prometheus").permitAll()
                .requestMatchers("/actuator/metrics").permitAll()
                .requestMatchers("/actuator/metrics/**").permitAll()
                .requestMatchers("/oauth/token").permitAll()
                .requestMatchers("/oauth/user/**").permitAll()
                .requestMatchers("/current-info").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            );

        return http.build();
    }

    /**
     * 授权服务器设置
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * JWT Token 自定义增强 - 在token中添加用户信息
     * 替代旧的 CustomAdditionalInformation (TokenEnhancer)
     * 当通过标准OAuth2端点获取token时，自动将用户信息写入JWT claims
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            if (context.getPrincipal() != null) {
                var principal = context.getPrincipal();
                context.getClaims().claim("sub", principal.getName());
                // 如果principal中包含UserLoginDto信息，添加自定义claims
                if (principal.getPrincipal() instanceof UserLoginDto) {
                    UserLoginDto user = (UserLoginDto) principal.getPrincipal();
                    context.getClaims().claim("id", user.getId());
                    context.getClaims().claim("userAccount", user.getUserAccount());
                    context.getClaims().claim("userRole", user.getUserRole());
                    context.getClaims().claim("phone", user.getPhone());
                    context.getClaims().claim("fullName", user.getFullName());
                    context.getClaims().claim("tenantId", user.getTenantId());
                    context.getClaims().claim("tenantName", user.getTenantName());
                    context.getClaims().claim("isDefaultAdmin", user.getIsDefaultAdmin());
                }
            }
        };
    }
}
