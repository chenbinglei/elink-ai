package com.sunmax.auth.service;

import java.util.Map;

public interface WechatService {

    /**
     * 获取微信用户的openId和手机号
     */
    Map<String,String> getAppletData(String code, String appletCode, String appletSecret, String encryptData, String iv);
}
