package com.sunmax.auth.config;

import com.sunmax.auth.filter.CustomClientCredentialsTokenEndpointFilter;
import com.sunmax.auth.granter.CustomRefreshTokenGranter;
import com.sunmax.auth.granter.MobileAppletCustomTokenGranter;
import com.sunmax.auth.granter.MobilePasswordSystemUserTokenGranter;
import com.sunmax.auth.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Author: xiuho
 * @CreateDate: 2021.3.18
 * @Description: 授权服务配置
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TokenStore tokenStore;

    /*@Autowired
    JwtAccessTokenConverter jwtAccessTokenConverter;*/

    @Autowired
    private CustomAdditionalInformation customAdditionalInformation;

    @Bean
    public ClientDetailsService clientDetailsService1() {
        return new JdbcClientDetailsService(dataSource);
    }

    //jwt
/*    @Bean
    AuthorizationServerTokenServices tokenServices() {
        //DefaultTokenServices services = new DefaultTokenServices();
        SMTokenService services = new SMTokenService();
        services.setClientDetailsService(clientDetailsService());
        services.setSupportRefreshToken(true);
        services.setTokenStore(tokenStore);
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter, customAdditionalInformation));
        services.setTokenEnhancer(tokenEnhancerChain);
        return services;
    }*/

    //redis
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        //todo 单点 多点登录
        //DefaultTokenServices services = new DefaultTokenServices();
        SMTokenService services = new SMTokenService();
        services.setClientDetailsService(clientDetailsService1());
        // token有效期自定义设置，3天
        services.setAccessTokenValiditySeconds(60 * 60 * 24 * 3);
        // refresh_token 30天
        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 30);
        //token永久有效
//        defaultTokenServices.setAccessTokenValiditySeconds(-1);
//        defaultTokenServices.setRefreshTokenValiditySeconds(-1);
        services.setSupportRefreshToken(true);
        services.setReuseRefreshToken(false);
        services.setTokenStore(tokenStore);
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(customAdditionalInformation));
        services.setTokenEnhancer(tokenEnhancerChain);
        return services;
    }

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private AuthorizationServerEndpointsConfiguration configuration;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService1());//clients信息存入到了数据库
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        String path = "/oauth/token";
        try {
            // 获取自定义映射路径，比如 ((AuthorizationServerEndpointsConfigurer) endpoints).pathMapping("/oauth/token", "/my/token");
            path = configuration.oauth2EndpointHandlerMapping().getServletPath(path);
        } catch (Exception e) {
        }
        CustomClientCredentialsTokenEndpointFilter endpointFilter = new CustomClientCredentialsTokenEndpointFilter(security, path);
        endpointFilter.afterPropertiesSet();
        endpointFilter.setAuthenticationEntryPoint(authenticationEntryPoint);

        security.authenticationEntryPoint(authenticationEntryPoint);
        security.addTokenEndpointAuthenticationFilter(endpointFilter);

        security.tokenKeyAccess("isAuthenticated()").checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> tokenGranters = getTokenGranters(
                endpoints.getAuthorizationCodeServices(),
                endpoints.getTokenStore(),
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory());
        endpoints.tokenGranter(new CompositeTokenGranter(tokenGranters));
        endpoints.tokenServices(tokenServices());
    }

    private List<TokenGranter> getTokenGranters(AuthorizationCodeServices authorizationCodeServices, TokenStore tokenStore, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        return new ArrayList<>(Arrays.asList(
                new CustomRefreshTokenGranter(tokenStore, tokenServices, clientDetailsService, requestFactory),
                new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetailsService, requestFactory),
                new MobilePasswordSystemUserTokenGranter(userLoginService, tokenServices, clientDetailsService, requestFactory),//系统用户密码登陆
                new MobileAppletCustomTokenGranter(userLoginService,tokenServices, clientDetailsService, requestFactory)//晟曼e充微信小程序登录
        ));
    }

}
