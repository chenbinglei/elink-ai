package com.sunmax.crontab.config;

import com.sunmax.common.config.SMAccessDeniedHandler;
import com.sunmax.common.config.SMAuthenticationEntryPoint;
import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.StringUtil;
import com.sunmax.crontab.service.feign.SauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class UserResourceConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private SMAccessDeniedHandler accessDeniedHandler; //无权访问处理器

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                //测试开放所有接口
                .authorizeRequests()
                .antMatchers("/doc.html").authenticated()
                .antMatchers("/swagger-ui.html").authenticated() // 任意访问
                .antMatchers("/feign/**").permitAll()//服务间内部调用  不需要认证和权限//后面设置权限机制
                .antMatchers("/actuator/health").permitAll()//健康检查端点，供Docker healthcheck使用
                .antMatchers("/configFuncPoint/**").permitAll()//服务间内部调用  不需要认证和权限//后面设置权限机制
                .antMatchers("/*WebSocket/**").permitAll()//服务间内部调用  不需要认证和权限//后面设置权限机制
                .antMatchers("/*Websocket/**").permitAll()//服务间内部调用  不需要认证和权限//后面设置权限机制
                .antMatchers("/**").access("isAuthenticated() && @userResourceConfiguration.canAccess(request, authentication)")//需要认证和权限.antMatchers("/**").permitAll()
//                .antMatchers("/**").permitAll()
                .anyRequest()//其他所有需要认证
                .authenticated();
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);

    }

    // 之后引入的bean是为了解决no bean resolver registered的问题
    @Autowired
    private OAuth2WebSecurityExpressionHandler expressionHandler;

    @Bean
    public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {
        OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        // 配置资源 ID --对应sso的资源id
        resources.resourceId("backend-resources");
        resources.authenticationEntryPoint(new SMAuthenticationEntryPoint());
        resources.expressionHandler(expressionHandler);
    }

    @Autowired
    private SauthService sauthService;

    public boolean canAccess(HttpServletRequest request, Authentication authentication) {
        //根据用户账号获取权限数据
        String clientId = getClientId(authentication);
        String userAccount = authentication.getPrincipal().toString();
        if (StringUtil.isNotEmpty(userAccount) && StringUtil.isNotEmpty(clientId)) {
            List<PermissionInfoListDto> permissionList = sauthService.findPermissionByUserAccount(userAccount, clientId).getData();
            List<String> urls = permissionList.stream().filter(p -> Objects.equals(p.getType(), 2)).map(PermissionInfoListDto::getUrl)
                    .collect(Collectors.toList());
            String uri = request.getRequestURI();
            return !urls.isEmpty() && urls.contains(uri);
        }
        return false;
    }

    /**
     * 获取当前登录客户端id
     *
     * @param authentication
     * @return
     */
    public static String getClientId(Authentication authentication) {
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oauthAuthentication = (OAuth2Authentication) authentication;
            return oauthAuthentication.getOAuth2Request().getClientId();
        }
        return null;
    }
}
