package com.sunmax.auth.granter;

import com.sunmax.auth.service.UserLoginService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 刷新令牌认证提供者
 *
 * @deprecated Spring Authorization Server 内置了 refresh_token grant 类型处理，
 * 此类不再需要。刷新令牌由 OauthController.handleRefreshToken() 通过Redis直接处理，
 * 无需经过AuthenticationProvider。
 * 保留仅用于兼容性参考。
 */
@Deprecated
public class CustomRefreshTokenGranter implements AuthenticationProvider {

    private final UserLoginService userLoginService;

    public CustomRefreshTokenGranter(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // refresh_token 由 OauthController.handleRefreshToken() 通过Redis直接处理
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
