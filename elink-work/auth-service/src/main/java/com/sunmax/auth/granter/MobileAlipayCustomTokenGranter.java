/*
package com.sunmax.auth.granter;

import com.sunmax.auth.entity.UserEntity;
import com.sunmax.auth.service.UserLoginService;
import com.sunmax.common.util.StringUtil;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

*/
/**
 * 手机App 支付宝登录校验
 *//*

public class MobileAlipayCustomTokenGranter extends AbstractCustomTokenGranter {

    protected UserLoginService userLoginService;

    public MobileAlipayCustomTokenGranter(UserLoginService userLoginService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, "alipay");
        this.userLoginService = userLoginService;
    }

    @Override
    protected UserEntity getCustomUser(Map<String, String> parameters) {
        String encryptData = parameters.get("encryptData");//支付宝小程序加密手机号
        Integer appLogo = StringUtil.isNotEmpty(parameters.get("appLogo")) ? Integer.parseInt(parameters.get("appLogo")) : null;//支付宝App标识
        return userLoginService.loadUserByEncryptDataAndMobile(encryptData, appLogo);
    }
}
*/
