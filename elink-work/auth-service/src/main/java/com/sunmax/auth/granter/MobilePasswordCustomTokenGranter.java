package com.sunmax.auth.granter;//package com.sunmax.auth.granter;
//
//import com.sunmax.auth.entity.UserModel;
//import com.sunmax.auth.service.CustomUserDetailsService;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
//import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
//
//import java.util.Map;
//
///**
// * @Author: xiuho
// * @CreateDate: 2021.3.18
// * @Description:
// */
//public class MobilePasswordCustomTokenGranter extends AbstractCustomTokenGranter {
//
//    protected CustomUserDetailsService userDetailsService;
//
//    public MobilePasswordCustomTokenGranter(CustomUserDetailsService userDetailsService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
//        super(tokenServices, clientDetailsService, requestFactory, "pwd");
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected UserModel getCustomUser(Map<String, String> parameters) {
//        String mobile = parameters.get("mobile");
//        String password = parameters.get("password");
//        return userDetailsService.loadUserByMobileAndPassword(mobile, password);
//    }
//
//}
