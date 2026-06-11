package com.sunmax.webapp.util;
import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.config.wechat.HttpRequestUtil;
import com.sunmax.webapp.dto.AccessTokenDto;
import com.sunmax.webapp.dto.JsapiTicketDto;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
public class CommonUtil {

    public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    public final static String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

    public static String createNonceStr() {
        return UUID.randomUUID().toString();
    }

    public static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    //    public static String byteToHex(final byte[] hash) {
//        Formatter formatter = new Formatter();
//        for (byte b : hash)
//        {
//            formatter.format("%02x", b);
//        }
//        String result = formatter.toString();
//        formatter.close();
//        return result;
//    }
    public static String SHA1(String str) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA"); //如果是SHA加密只需要将"SHA-1"改成"SHA"即可
            digest.update(str.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexStr = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte b : messageDigest) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexStr.append(0);
                }
                hexStr.append(shaHex);
            }
            return hexStr.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) throws Exception {
        JSONObject jsonObject = null;
        StringBuilder buffer = new StringBuilder();
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = new TrustManager[10];
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();

        URL url = new URL(requestUrl);
        HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
                .openConnection();
        httpUrlConn.setSSLSocketFactory(ssf);

        httpUrlConn.setDoOutput(true);
        httpUrlConn.setDoInput(true);
        httpUrlConn.setUseCaches(false);
        // 设置请求方式（GET/POST）
        httpUrlConn.setRequestMethod(requestMethod);

        if ("GET".equalsIgnoreCase(requestMethod))
            httpUrlConn.connect();

        // 当有数据需要提交时
        if (null != outputStr) {
            OutputStream outputStream = httpUrlConn.getOutputStream();
            // 注意编码格式，防止中文乱码
            outputStream.write(outputStr.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        }

        // 将返回的输入流转换成字符串
        InputStream inputStream = httpUrlConn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        // 释放资源
        inputStream.close();
        inputStream = null;
        httpUrlConn.disconnect();
        jsonObject = JSONObject.parseObject(buffer.toString());
        return jsonObject;
    }

    public static AccessTokenDto getAccessToken(String appid, String appsecret) throws Exception {
        AccessTokenDto accessToken = null;

        String requestUrl = access_token_url.replace("APPID", appid).replace(
                "APPSECRET", appsecret);
        JSONObject jsonObject = JSONObject.parseObject(HttpRequestUtil.sendGet(requestUrl, null));
        // 如果请求成功
        if (null != jsonObject) {
            try {
                accessToken = new AccessTokenDto();
                accessToken.setToken(jsonObject.getString("access_token"));
                accessToken.setExpiresIn(jsonObject.getInteger("expires_in"));
            } catch (JSONException e) {
                accessToken = null;
            }
        }
        return accessToken;
    }

    public static JsapiTicketDto getJsapiTicket(String tocken) throws Exception {
        JsapiTicketDto jsapiTicket = null;
        String requestUrl = jsapi_ticket_url.replace("ACCESS_TOKEN", tocken);
        JSONObject jsonObject = JSONObject.parseObject(HttpRequestUtil.sendPost(requestUrl, null));
        // 如果请求成功
        if (null != jsonObject) {
            try {
                jsapiTicket = new JsapiTicketDto();
                jsapiTicket.setTicket(jsonObject.getString("ticket"));
                jsapiTicket.setExpiresIn(jsonObject.getInteger("expires_in"));
            } catch (JSONException e) {
                jsapiTicket = null;
                // 获取token失败
                log.error(e.getMessage(), e);
            }
        }
        return jsapiTicket;
    }

}
