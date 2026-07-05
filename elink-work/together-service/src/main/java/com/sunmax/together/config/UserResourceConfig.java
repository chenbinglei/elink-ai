package com.sunmax.together.config;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.config.RedisTokenAuthenticationFilter;
import com.sunmax.common.config.SMAccessDeniedHandler;
import com.sunmax.common.config.SMAuthenticationEntryPoint;
import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.service.feign.SauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class UserResourceConfig {

    @Autowired
    private SMAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private SMAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private RedisTokenAuthenticationFilter redisTokenAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(redisTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/doc.html").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/v3/api-docs").permitAll()
                .requestMatchers("/*WebSocket/**").permitAll()
                .requestMatchers("/customSystem/**").permitAll()
                .requestMatchers("/feign/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/prometheus").authenticated()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/**").access((authSupplier, context) -> {
                    HttpServletRequest request = context.getRequest();
                    Authentication authObj = authSupplier.get();
                    if (authObj == null || !authObj.isAuthenticated()) {
                        return new org.springframework.security.authorization.AuthorizationDecision(false);
                    }
                    return new org.springframework.security.authorization.AuthorizationDecision(canAccess(request, authObj));
                })
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            );

        return http.build();
    }

    @Autowired
    private SauthService sauthService;

    public boolean canAccess(HttpServletRequest request, Authentication authentication) {
        String clientId = getClientId(authentication);
        String userAccount = authentication.getPrincipal().toString();
        if (StringUtil.isEmpty(userAccount)) {
            return false;
        }
        if (StringUtil.isEmpty(clientId)) {
            log.warn("认证信息缺少clientId，拒绝访问: userAccount={}, uri={}", userAccount, request.getRequestURI());
            return false;
        }
        try {
            List<PermissionInfoListDto> permissionList = sauthService.findPermissionByUserAccount(userAccount, clientId).getData();
            List<String> urls = permissionList.stream().filter(p -> Objects.equals(p.getType(), 2)).map(PermissionInfoListDto::getUrl)
                    .collect(Collectors.toList());
            String uri = request.getRequestURI();
            return !urls.isEmpty() && urls.contains(uri);
        } catch (Exception e) {
            log.warn("权限查询异常，拒绝访问: userAccount={}, uri={}, error={}", userAccount, request.getRequestURI(), e.getMessage());
            return false;
        }
    }

    public static String getClientId(Authentication authentication) {
        if (authentication.getDetails() instanceof JSONObject) {
            JSONObject details = (JSONObject) authentication.getDetails();
            return details.getString("clientId");
        }
        return null;
    }
}
