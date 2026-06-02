package com.sunmax.webapp.config;

import com.sunmax.common.config.SMAccessDeniedHandler;
import com.sunmax.common.config.SMAuthenticationEntryPoint;
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
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;

import javax.servlet.http.HttpServletRequest;


@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class UserResourceConfig extends ResourceServerConfigurerAdapter {

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
                .antMatchers("/doc.html").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/oauth/appletLogin").permitAll()
                .antMatchers("/wechat/getWechatSignature").permitAll()
                .antMatchers("/wechat/verifyUrl").permitAll()
                .antMatchers("/image/**").permitAll()
                .antMatchers("/sms/**").permitAll()
                .antMatchers("/userTrade/**").permitAll()//用户交易相关的接口
                .antMatchers("/*WebSocket/**").permitAll()//WebSocket开放
                .antMatchers("/*Websocket/**").permitAll()//WebSocket开放
                .antMatchers("/feign/**").permitAll()//服务间内部调用  不需要认证和权限//后面设置权限机制
//                .antMatchers("/**").access("isAuthenticated() && @webAppUserResourceConfiguration.canAccess(request,authentication)")//需要认证和权限
                .antMatchers("/**").permitAll()
                .anyRequest()//其他所有需要认证
                .authenticated();
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);

    }
    // 之后引入的bean是为了解决no bean resolver registered的问题
    @Autowired
    private OAuth2WebSecurityExpressionHandler expressionHandler;
    @Bean
    public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler1(ApplicationContext applicationContext) {
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

//    @Autowired
//    private PermissionRemoteService permissionRemoteService;
//
    public boolean canAccess(HttpServletRequest request, Authentication authentication) {
//        //根据用户账号获取权限数据
//        String userAccount = authentication.getPrincipal().toString();
//        if (StringUtil.isNotEmpty(userAccount)) {
//            List<PermissionListDto> permissionList = permissionRemoteService.findPermissionByUserAccount(userAccount);
//            List<String> urls = permissionList.stream().filter(p -> Objects.equals(p.getType(),4)).map(PermissionListDto::getUrl)
//                    .collect(Collectors.toList());
//            String uri = request.getRequestURI();
//            return !urls.isEmpty() && urls.contains(uri);
//        }
//        return false;
        return true;
    }

}
