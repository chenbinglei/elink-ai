package com.sunmax.auth.granter;

import com.sunmax.auth.dto.UserLoginDto;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

/**
 * 自定义Token授权基类
 *
 * @deprecated 旧版TokenGranter体系已废弃，新的认证流程由OauthController直接处理。
 * 各grant_type的认证逻辑已分别在对应的AuthenticationProvider实现类中完成。
 * 保留仅用于兼容性参考。
 */
@Deprecated
public abstract class AbstractCustomTokenGranter implements AuthenticationProvider {

    /**
     * 子类实现此方法获取自定义用户数据
     *
     * @param parameters 请求参数
     * @return 用户登录信息
     */
    protected abstract UserLoginDto getCustomUser(Map<String, String> parameters);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 子类应直接实现authenticate方法，不再通过getCustomUser间接调用
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
