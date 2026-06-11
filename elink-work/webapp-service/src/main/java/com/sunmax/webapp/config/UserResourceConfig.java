package com.sunmax.webapp.config;

import com.sunmax.common.config.RedisTokenAuthenticationFilter;
import com.sunmax.common.config.SMAccessDeniedHandler;
import com.sunmax.common.config.SMAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .requestMatchers("/oauth/appletLogin").permitAll()
                .requestMatchers("/wechat/getWechatSignature").permitAll()
                .requestMatchers("/wechat/verifyUrl").permitAll()
                .requestMatchers("/image/**").permitAll()
                .requestMatchers("/sms/**").permitAll()
                .requestMatchers("/userTrade/**").permitAll()
                .requestMatchers("/*WebSocket/**").permitAll()
                .requestMatchers("/*Websocket/**").permitAll()
                .requestMatchers("/feign/**").permitAll()
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            );

        return http.build();
    }
}
