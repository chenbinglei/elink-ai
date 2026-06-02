package com.sunmax.common.config.wechat;

/**
 * 微信支付的配置类
 */
public class WechatPayConfig {

    //微信小程序授权code
    public static final String GRANT_TYPE = "authorization_code";

    //获取微信openid的链接
    public static final String OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session";
}
