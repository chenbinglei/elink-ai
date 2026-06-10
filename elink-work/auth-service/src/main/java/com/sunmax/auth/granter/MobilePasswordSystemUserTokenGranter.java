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

/**
 * 平台系统用户密码登录认证提供者
 * 替代旧的 MobilePasswordSystemUserTokenGranter
 * grant_type: sys_pwd
 */
public class MobilePasswordSystemUserTokenGranter implements AuthenticationProvider {

    private final UserLoginService userLoginService;

    public MobilePasswordSystemUserTokenGranter(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userAccount = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserLoginDto user = userLoginService.loadSysUserByAccountAndPassword(userAccount, password, "sys_pwd");
        if (user == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        return new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities() != null ? user.getAuthorities() : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
