package com.sunmax.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import com.fasterxml.jackson.databind.Module;

/**
 * Token存储配置 - 使用Redis存储OAuth2授权信息
 * 替代旧的RedisTokenStore
 */
@Configuration
public class AccessTokenConfig {

    @Bean
    public Module oauth2AuthorizationServerJackson2Module() {
        return new OAuth2AuthorizationServerJackson2Module();
    }
}
