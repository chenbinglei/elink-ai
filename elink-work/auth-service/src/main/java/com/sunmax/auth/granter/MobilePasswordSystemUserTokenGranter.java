package com.sunmax.auth.granter;

import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.auth.service.UserLoginService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * @author xt
 * @brief 平台系统用户 密码登陆
 * @date 2021/6/7 13:51
 */
public class MobilePasswordSystemUserTokenGranter extends AbstractCustomTokenGranter {

    protected UserLoginService userLoginService;

    public MobilePasswordSystemUserTokenGranter(UserLoginService userLoginService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, "sys_pwd");
        this.userLoginService = userLoginService;
    }

    @Override
    protected UserLoginDto getCustomUser(Map<String, String> parameters) {
        String userAccount = parameters.get("userAccount");
        String password = parameters.get("password");
        String clientId = parameters.get("client_id");//客户端id 用来标识登录那个平台
        return userLoginService.loadSysUserByAccountAndPassword(userAccount, password, clientId);
    }

}