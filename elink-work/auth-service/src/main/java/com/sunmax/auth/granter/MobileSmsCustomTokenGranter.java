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
//public class MobileSmsCustomTokenGranter extends AbstractCustomTokenGranter {
//
//    protected CustomUserDetailsService userDetailsService;
//
//    public MobileSmsCustomTokenGranter(CustomUserDetailsService userDetailsService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
//        super(tokenServices, clientDetailsService, requestFactory, "sms");
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected UserModel getCustomUser(Map<String, String> parameters) {
//        String mobile = parameters.get("mobile");
//        String smscode = parameters.get("smscode");
//        return userDetailsService.loadUserByMobileAndSmscode(mobile, smscode);
//    }
//
//}
