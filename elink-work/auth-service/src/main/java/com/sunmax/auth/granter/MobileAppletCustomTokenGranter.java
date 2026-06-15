package com.sunmax.auth.granter;

import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.auth.service.UserLoginService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Map;

/**
 * 微信小程序登录认证提供者
 * 替代旧的 MobileAppletCustomTokenGranter
 * grant_type: applet
 */
public class MobileAppletCustomTokenGranter implements AuthenticationProvider {

    private final UserLoginService userLoginService;

    public MobileAppletCustomTokenGranter(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> params = (Map<String, String>) authentication.getDetails();
            String appletCode = params.get("code");
            String appletKey = params.get("appletKey");
            String encryptedData = params.get("encryptedData");
            String iv = params.get("iv");
            UserLoginDto user = userLoginService.loadUserByAppletCodeAndMobile(appletCode, appletKey, encryptedData, iv);
            if (user == null) {
                throw new BadCredentialsException("微信小程序登录失败");
            }
            return new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities() != null ? user.getAuthorities() : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }
        throw new BadCredentialsException("缺少微信小程序登录参数");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
