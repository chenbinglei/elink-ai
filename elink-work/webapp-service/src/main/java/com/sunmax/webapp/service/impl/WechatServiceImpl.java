package com.sunmax.webapp.service.impl;

import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.webapp.dto.AccessTokenDto;
import com.sunmax.webapp.dto.JsapiTicketDto;
import com.sunmax.webapp.service.WechatService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.util.CommonUtil;
import com.sunmax.webapp.util.WXMsgPushUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WechatServiceImpl implements WechatService {

    @Autowired
    private SystemService systemService;

    @Override
    public String verifyUrl(Map<String, String> params) {
        // 微信发送的请求中 会有四个参数
        // 微信加密签名，signature结合了开发者填写的 token 参数和请求中的 timestamp 参数、nonce参数。
        String signature = params.get("signature");
        // 随机字符串
        String echostr = params.get("echostr");
        // 时间戳
        String timestamp = params.get("timestamp");
        // 随机数
        String nonce = params.get("nonce");

        // 消息推送配置中的 Token(令牌)
        String token = "smsunmax";

        try {
            // 验证
            String msgSignature = WXMsgPushUtil.getSHA1(token, timestamp, nonce);

            // 验证失败
            if (!signature.equals(msgSignature)) {
                return "false";
            }
        } catch (Exception e) {
            log.error("验证微信URL失败", e);
            return "false";
        }
        // 验证成功 将 echostr 原格式返回 ，即可完成验证
        return echostr;
    }

    @Override
    public ResponseResult<Map<String, String>> getWechatSignature(String url, String appletKey) {
        Map<String, String> resultMap = new HashMap<>();
        if (StringUtil.isEmpty(appletKey)) {
            return ResponseResult.error("未获取到小程序标识");
        }
        AppletDto appletDto = systemService.findAppletByAppletCode(appletKey).getData();
        if (appletDto == null || StringUtil.isEmpty(appletDto.getTencentCode()) || StringUtil.isEmpty(appletDto.getAppletSecret())) {
            return ResponseResult.paramError(ResponseResult.APPLET_ERROR);
        }
        //获取随机字符串
        String nonceStr = CommonUtil.createNonceStr();
        //获取时间戳
        String timestamp = CommonUtil.createTimestamp();
        AccessTokenDto accessToken = new AccessTokenDto();
        try {
            //获取token
            accessToken = CommonUtil.getAccessToken(appletDto.getTencentCode(), appletDto.getTencentSecret());
        } catch (Exception e) {
            log.error("获取token失败", e);
        }
        JsapiTicketDto jsapiTicket = new JsapiTicketDto();
        try {
            //获取jsapiTicket
            if (accessToken != null && StringUtil.isNotEmpty(accessToken.getToken())) {
                jsapiTicket = CommonUtil.getJsapiTicket(accessToken.getToken());
            }
        } catch (Exception e) {
            log.error("获取jsapiTicket失败", e);
        }
        if (jsapiTicket != null && StringUtil.isNotEmpty(jsapiTicket.getTicket())) {
            // 注意这里参数名必须全部小写，且必须有序
            String param = "jsapi_ticket=" + jsapiTicket.getTicket() + "&noncestr=" + nonceStr
                    + "&timestamp=" + timestamp + "&url=" + url;
            String signature = null;
            //                MessageDigest crypt = MessageDigest.getInstance("SHA-1");
//                crypt.reset();
//                crypt.update(param.getBytes(StandardCharsets.UTF_8));
            signature = CommonUtil.SHA1(param);
            //String signature = DigestUtils.sha1Hex(param);
            resultMap.put("url", url);
            //注意这里 要加上自己的appId
            resultMap.put("appId", appletDto.getTencentCode());
            resultMap.put("jsapi_ticket", jsapiTicket.getTicket());
            resultMap.put("nonceStr", nonceStr);
            resultMap.put("timestamp", timestamp);
            resultMap.put("signature", signature);
        }
        return ResponseResult.ok(resultMap);
    }

    public static void main(String[] args) {
        Map<String, String> resultMap = new HashMap<>();
        //获取随机字符串
        String nonceStr = CommonUtil.createNonceStr();
        //获取时间戳
        String timestamp = CommonUtil.createTimestamp();
        AccessTokenDto accessToken = new AccessTokenDto();
        try {
            //获取token
            accessToken = CommonUtil.getAccessToken("wxa44b789e73c6af4f", "58db37cd67b04bf000aedbe227bdf83d");
        } catch (Exception e) {
            log.error("获取token失败", e);
        }
        JsapiTicketDto jsapiTicket = new JsapiTicketDto();
        try {
            //获取jsapiTicket
            if (accessToken != null && StringUtil.isNotEmpty(accessToken.getToken())) {
                jsapiTicket = CommonUtil.getJsapiTicket(accessToken.getToken());
            }
        } catch (Exception e) {
            log.error("获取jsapiTicket失败", e);
        }
        if (jsapiTicket != null && StringUtil.isNotEmpty(jsapiTicket.getTicket())) {
            // 注意这里参数名必须全部小写，且必须有序
            String param = "jsapi_ticket=" + jsapiTicket.getTicket() + "&noncestr=" + nonceStr
                    + "&timestamp=" + timestamp + "&url=" + "https://sdccs.sunmaxxtech.com/";
            String signature = null;
            //                MessageDigest crypt = MessageDigest.getInstance("SHA-1");
//                crypt.reset();
//                crypt.update(param.getBytes(StandardCharsets.UTF_8));
            signature = CommonUtil.SHA1(param);
            //String signature = DigestUtils.sha1Hex(param);
            resultMap.put("url", "https://sdccs.sunmaxxtech.com/");
            //注意这里 要加上自己的appId
            resultMap.put("appId", "wxa44b789e73c6af4f");
            resultMap.put("jsapi_ticket", jsapiTicket.getTicket());
            resultMap.put("nonceStr", nonceStr);
            resultMap.put("timestamp", timestamp);
            resultMap.put("signature", signature);
        }
        log.info("{}", resultMap);
    }

}
