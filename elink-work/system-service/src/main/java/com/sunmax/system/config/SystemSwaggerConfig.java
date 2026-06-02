package com.sunmax.system.config;

import com.sunmax.common.enums.ResponseCodeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date
 * @Author:
 */
@Configuration
@EnableSwagger2
public class SystemSwaggerConfig {
    @Bean
    public Docket productApi() {
        //添加全局响应状态码
        List<ResponseMessage> responseList = Arrays.stream(ResponseCodeEnum.values()).map(responseCode ->
                new ResponseMessageBuilder().code(responseCode.getCode()).message(responseCode.getMessage())
                        .build()).collect(Collectors.toList());
        return new Docket(DocumentationType.SWAGGER_2)
                .globalResponseMessage(RequestMethod.GET, responseList)
                .globalResponseMessage(RequestMethod.POST, responseList)
                .globalResponseMessage(RequestMethod.PUT, responseList)
                .globalResponseMessage(RequestMethod.DELETE, responseList)
                .apiInfo(apiInfo())
                .select()
                //添加ApiOperiation注解的被扫描
                .apis(RequestHandlerSelectors.basePackage("com.sunmax.system.controller"))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(securityScheme()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("系统管理")
                .description("系统管理")
                .version("1.1")
                .build();
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[]{
                new AuthorizationScope("all", "all scope")
        };
    }

    private SecurityScheme securityScheme() {
        GrantType grant = new ResourceOwnerPasswordCredentialsGrant("http://localhost:50001/oauth/token");
        return new OAuthBuilder().name("OAuth2")
                .grantTypes(Collections.singletonList(grant))
                .scopes(Arrays.asList(scopes()))
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(new SecurityReference("OAuth2", scopes())))
                .forPaths(PathSelectors.any())
                .build();
    }

}
