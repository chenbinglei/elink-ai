package com.sunmax.auth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunmax.auth.service.WechatService;
import com.sunmax.common.config.wechat.*;
import com.sunmax.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WechatServiceImpl implements WechatService {

    @Override
    public Map<String, String> getAppletData(String code, String appletCode, String appletSecret, String encryptData, String iv) {
        //返回的集合
        Map<String, String> resultMap = new HashMap<>();
        //请求参数
        String params = "appid=" + appletCode + "&secret=" + appletSecret + "&js_code=" + code + "&grant_type=" + WechatPayConfig.GRANT_TYPE;
        String result = HttpRequestUtil.sendGet(WechatPayConfig.OPENID_URL, params);
        if (StringUtil.isNotEmpty(result)) {
            //解析相应内容（转换成json对象）
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && !jsonObject.isEmpty() && jsonObject.get("openid") != null) {
                if (StringUtil.isNotEmpty(jsonObject.get("openid"))) {
                    resultMap.put("appletId", jsonObject.getString("openid"));//微信小程序唯一id
                }
                if (StringUtil.isNotEmpty(jsonObject.getString("session_key"))) {
                    String sessionKey = jsonObject.getString("session_key");
                    JSONObject phoneObject = JSONObject.parseObject(WxPhoneUtil.decryptData(encryptData.replace("\\", ""),
                            sessionKey.replace("\\", ""), iv.replace("\\", "")));
                    if (!jsonObject.isEmpty() && StringUtil.isNotEmpty(phoneObject.getString("phoneNumber"))) {
                        resultMap.put("mobile", phoneObject.getString("phoneNumber"));//手机号
                    }
                }
            }
        }
        return resultMap;
    }
}
