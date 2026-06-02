package com.sunmax.auth.granter;

import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.auth.service.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * 微信小程序登录 微信小程序登录校验
 */
@Slf4j
public class MobileAppletCustomTokenGranter extends AbstractCustomTokenGranter {

    protected UserLoginService userLoginService;

    public MobileAppletCustomTokenGranter(UserLoginService userLoginService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, "applet");
        this.userLoginService = userLoginService;
    }

    @Override
    protected UserLoginDto getCustomUser(Map<String, String> parameters) {
        String appletCode = parameters.get("code");//微信小程序code
        String appletKey = parameters.get("appletKey");//微信小程序Id
        String encryptedData = parameters.get("encryptedData");
        String iv = parameters.get("iv");
        return userLoginService.loadUserByAppletCodeAndMobile(appletCode, appletKey, encryptedData, iv);
    }
}
