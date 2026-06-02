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
// * 手机App 微信登录校验
// */
//public class MobileWechatCustomTokenGranter extends AbstractCustomTokenGranter {
//
//    protected CustomUserDetailsService userDetailsService;
//
//    public MobileWechatCustomTokenGranter(CustomUserDetailsService userDetailsService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
//        super(tokenServices, clientDetailsService, requestFactory, "wechat");
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected UserModel getCustomUser(Map<String, String> parameters) {
//        String wechatId = parameters.get("wechatId");//微信唯一标识 openId
//        String wechatName = parameters.get("wechatName");//微信名称
//        String mobile = parameters.get("mobile");//手机号
//        String smsCode = parameters.get("smscode");//验证码
//        return userDetailsService.loadUserByWechatIdAndMobile(wechatId,wechatName, mobile,smsCode);
//    }
//}
