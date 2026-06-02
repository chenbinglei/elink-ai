package com.sunmax.auth.config;

import com.sunmax.auth.dto.UserLoginDto;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xt
 * @brief 在token上添加额外的信息
 * @date 2021/6/1 15:39
 */
@Component
public class CustomAdditionalInformation implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        UserLoginDto user = (UserLoginDto) authentication.getPrincipal();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",user.getId());
        map.put("userAccount", user.getUserAccount());
        map.put("userRole", user.getUserRole());
        map.put("phone", user.getPhone());
        map.put("fullName", user.getFullName());
        map.put("tenantId", user.getTenantId());
        map.put("tenantName", user.getTenantName());
        map.put("isDefaultAdmin",user.getIsDefaultAdmin());
        map.put("checkMsg",user.getCheckMsg());
        map.put("checkCode",user.getCheckCode());
        map.put("userProfile",user.getUserProfile());
        map.put("menuList", user.getMenuList());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
        return accessToken;
    }
}
