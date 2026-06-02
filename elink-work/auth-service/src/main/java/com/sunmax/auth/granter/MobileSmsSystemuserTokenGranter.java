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
// *@brief 平台系统用户 验证码登陆
// *@author xt
// *@date 2021/6/7 13:53
// */
//public class MobileSmsSystemuserTokenGranter extends AbstractCustomTokenGranter {
//
//    protected CustomUserDetailsService userDetailsService;
//
//    public MobileSmsSystemuserTokenGranter(CustomUserDetailsService userDetailsService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
//        super(tokenServices, clientDetailsService, requestFactory, "sys_sms");
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected UserModel getCustomUser(Map<String, String> parameters) {
//        String mobile = parameters.get("mobile");
//        String smscode = parameters.get("smscode");
//        return userDetailsService.loadSysUserByMobileAndSmscode(mobile, smscode);
//    }
//
//}
