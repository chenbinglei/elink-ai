package com.sunmax.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("认证中心")
                        .description("认证中心")
                        .version("1.1"))
                .addSecurityItem(new SecurityRequirement().addList("OAuth2"))
                .schemaRequirement("OAuth2", new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(new OAuthFlows()
                                .password(new OAuthFlow()
                                        .tokenUrl("/sauth/oauth/token")
                                        .refreshUrl("/sauth/oauth/token")
                                        .scopes(new Scopes().addString("all", "all scope")))));
    }
}
