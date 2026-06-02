package com.sunmax.webapp.service;

import com.sunmax.common.util.ResponseResult;

import java.util.Map;

public interface WechatService {

    /**
     * 验证URL
     * @param params
     * @return
     */
    String verifyUrl(Map<String, String> params);

    /**
     * 获取微信签名
     */
    ResponseResult<Map<String,String>> getWechatSignature(String url, String appletKey);
}
