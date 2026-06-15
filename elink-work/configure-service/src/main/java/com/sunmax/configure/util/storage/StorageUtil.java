package com.sunmax.configure.util.storage;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.io.IOException;

@Slf4j
public class StorageUtil {

    /**
     * TOKEN数据
     */
    private static String TOKEN;

    /**
     * TOKEN次数
     */
    private static int TOKEN_COUNT = 0;

    /**
     * 推送失败数据
     */
    public static Map<String, Integer> failDataMap = Maps.newHashMap();

    /**
     * 创建Header
     *
     * @param appId   应用ID
     * @param url     请求地址
     * @param dataKey 数据密钥（明文）
     * @param dataSM4 消息体
     * @param token   身份认证Token
     * @return
     */
    private static Map<String, String> getHeader(String appId, String publicKey, String privateKey, String url, String dataKey, String dataSM4, String token) {
        Long timestamp = DateTime.now().getTime();
        // 随机签名
        String nonce = UUID.randomUUID().toString(true);
        // 加密数据秘钥（SM2）
        String dataKeySM2 = SmUtil.sm2(privateKey, publicKey).encryptHex(dataKey, KeyType.PublicKey);
        // 签名
        String sign = StorageUtil.getSign(appId, timestamp, nonce, dataKeySM2, url, dataSM4, token);
        // 设置请求Header
        Map<String, String> header = MapUtil.newHashMap(true);
        header.put(StorageConst.X_CA_APPID, appId);
        header.put(StorageConst.X_CA_TIMESTAMP, timestamp + "");
        header.put(StorageConst.X_CA_NONCE, nonce);
        header.put(StorageConst.X_CA_SIGN, sign);
        header.put(StorageConst.X_CA_KEY, dataKeySM2);
        if (StrUtil.isNotEmpty(token)) {
            header.put(StorageConst.AUTHORIZATION, token);
        }
        return header;
    }

    /**
     * 根据验签规则进行验签加密
     *
     * @param appId      应用ID
     * @param timestamp  时间戳
     * @param nonce      随机值
     * @param dataKeySM2 SM2加密后的密钥
     * @param url        请求地址
     * @param bodySM4    SM4加密的消息体（例："[SM4加密数据]")，需要进行SM3加密进行签名)
     * @param token      身份认证
     * @return 验签加密String(SM3)
     */
    private static String getSign(String appId, Long timestamp, String nonce, String dataKeySM2, String url, String bodySM4, String token) {
        String bodySM4SM3;
        StringBuilder signSB = new StringBuilder();
        signSB.append(appId);
        signSB.append(StorageConst.SIGN_SEGMENT).append(timestamp);
        signSB.append(StorageConst.SIGN_SEGMENT).append(nonce);
        signSB.append(StorageConst.SIGN_SEGMENT).append(dataKeySM2);
        signSB.append(StorageConst.SIGN_SEGMENT).append(url);
        if (StrUtil.isNotEmpty(bodySM4)) {
            JSONObject dataJson = new JSONObject().set("data", bodySM4);
            bodySM4SM3 = SmUtil.sm3().digestHex(dataJson.toString());
            signSB.append(StorageConst.SIGN_SEGMENT).append(bodySM4SM3);
        }
        if (StrUtil.isNotEmpty(token)) {
            signSB.append(StorageConst.SIGN_SEGMENT).append(token);
        }
        return SmUtil.sm3().digestHex(signSB.toString());
    }

    /**
     * 获取TOKEN数据
     */
    private static void getToken(String appId, String publicKey, String privateKey, String baseUrl) {
        // 随机生成数据加密密钥
        String dataKey = UUID.randomUUID().toString(true).substring(0, 16);
        // 设置请求Header
        Map<String, String> header = getHeader(appId, publicKey, privateKey, StorageConst.TOKEN_API, dataKey, null, TOKEN);
        // 测试请求
        HttpResponse httpResponse = HttpUtil.createPost(baseUrl + StorageConst.TOKEN_API).addHeaders(header).execute();
        // 输出返回值
        String resBody = httpResponse.body();
        if (JSONUtil.isJsonObj(resBody)) {
            JSONObject json = JSONUtil.parseObj(resBody);
            if (json.containsKey("retCode") && json.getInt("retCode") == 0 && StringUtil.isNotEmpty(json.getJSONObject("retData"))) {
                TOKEN = json.getJSONObject("retData").getStr("token");
            }
        }
    }

    /**
     * 推送数据
     */
    public static void sendData(String appId, String publicKey, String privateKey, String baseUrl, String url, String resourceNo, Object data) {
        try {
            //校验url后缀正确性
            if (!Objects.equals(baseUrl.substring(baseUrl.length() - 1), FileUtil.SLASH)) {
                baseUrl = baseUrl + FileUtil.SLASH;
            }
            log.info("推送浙江省储能平台入参明文数据, HttpRequest({}) -> {}", url, data);
            if (StringUtil.isEmpty(TOKEN)) {
                getToken(appId, publicKey, privateKey, baseUrl);
            }
            // 随机生成SM4数据加密密钥
            String dataKey = UUID.randomUUID().toString(true).substring(0, 16);
            // 消息体内容采用SM4加密
            String dataSM4 = SmUtil.sm4(dataKey.getBytes(StandardCharsets.UTF_8)).encryptHex(JSONUtil.toJsonStr(data));
            // 设置Header
            Map<String, String> header = getHeader(appId, publicKey, privateKey, url, dataKey, dataSM4, TOKEN);
            // 模拟请求接口，消息体放入post的from表单
            HttpResponse httpResponse = HttpUtil.createPost(baseUrl + url).addHeaders(header).form("data", dataSM4).execute();
            // 输出接口返回值
            log.info("推送浙江省储能平台响应出参, HttpResponse({}) -> {}", url, JSONUtil.toJsonStr(httpResponse.body()));
            // 输出返回值
            String resBody = httpResponse.body();
            if (JSONUtil.isJsonObj(resBody)) {
                JSONObject json = JSONUtil.parseObj(resBody);
                //token错误 需要重新获取一下
                if (json.containsKey("retCode")) {
                    //0-成功 201-成功(数据验证存在问题【仅实时数据验证】（会生成数据工单）) 4001-请求失败 4002-Token错误
                    // 4003-参数错误。token、data、timestamp、sign有缺失 4004-请求失效（timestamp超时） 5001-数据格式错误 5002-数据缺失 5003-服务器错误
                    Integer retCode = json.getInt("retCode");
                    switch (retCode) {
                        case 0:
                        case 4001:
                        case 4003:
                        case 4004:
                        case 5001:
                        case 5002:
                            failDataMap.remove(resourceNo);
                            break;
                        case 4002:
                            if (TOKEN_COUNT == 0) {
                                getToken(appId, publicKey, privateKey, baseUrl);
                                TOKEN_COUNT = 1;
                                sendData(appId, publicKey, privateKey, baseUrl, url, resourceNo, data);
                            }
                            failDataMap.remove(resourceNo);
                            break;
                        default:
                            failDataMap.put(resourceNo, retCode);
                            break;
                    }
                } else {
                    TOKEN_COUNT = 0;
                }
            }
        } catch (RuntimeException e) {
            log.error("推送数据异常", e);
            failDataMap.put(resourceNo, 5003);
        }
    }

}
