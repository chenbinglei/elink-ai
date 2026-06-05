package com.sunmax.webapp.util;

import cn.hutool.http.HttpRequest;
import com.google.gson.annotations.SerializedName;
import com.wechat.pay.java.core.util.PemUtil;
import okhttp3.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

/**
 * 发起转账
 */
public class TransferToUser {
    private static String HOST = "https://api.mch.weixin.qq.com";
    private static String METHOD = "POST";
    private static String PATH = "/v3/fund-app/mch-transfer/transfer-bills";

    public String post(TransferToUserRequest request) {
        String response = HttpRequest.post(HOST + PATH)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Wechatpay-Serial", certificateSerialNo)
                .header("Authorization", WxPayUtil.buildAuthorization(mchid, certificateSerialNo, privateKey, METHOD, PATH, WxPayUtil.toJson(request)))
                .body(WxPayUtil.toJson(request))
                .timeout(3000).execute().body();
        return response;
    }

    public TransferToUserResponse run(TransferToUserRequest request) {
        String uri = PATH;
        String reqBody = WxPayUtil.toJson(request);

        Request.Builder reqBuilder = new Request.Builder().url(HOST + uri);
        reqBuilder.addHeader("Accept", "application/json");
        reqBuilder.addHeader("Wechatpay-Serial", certificateSerialNo);
        reqBuilder.addHeader("Authorization", WxPayUtil.buildAuthorization(mchid, certificateSerialNo, privateKey, METHOD, PATH, WxPayUtil.toJson(request)));
        reqBuilder.addHeader("Content-Type", "application/json");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqBody);
        reqBuilder.method(METHOD, requestBody);
        Request httpRequest = reqBuilder.build();

        // 发送HTTP请求
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            String respBody = WxPayUtil.extractBody(httpResponse);
            if (httpResponse.code() >= 200 && httpResponse.code() < 300) {
                // 2XX 成功，验证应答签名
                WxPayUtil.validateResponse(this.wechatPayPublicKeyId, this.wechatPayPublicKey,
                        httpResponse.headers(), respBody);

                // 从HTTP应答报文构建返回数据
                return WxPayUtil.fromJson(respBody, TransferToUserResponse.class);
            } else {
                throw new WxPayUtil.ApiException(httpResponse.code(), respBody, httpResponse.headers());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Sending request to " + uri + " failed.", e);
        }
    }

    private final String mchid;
    private final String certificateSerialNo;
    private final PrivateKey privateKey;
    private final String wechatPayPublicKeyId;
    private PublicKey wechatPayPublicKey;

    public TransferToUser(String mchid, String certificateSerialNo, String privateKey, String wechatPayPublicKeyId) {
        this.mchid = mchid;
        this.certificateSerialNo = certificateSerialNo;
        this.privateKey = PemUtil.loadPrivateKeyFromString(privateKey);
        this.wechatPayPublicKeyId = wechatPayPublicKeyId;
    }

    public TransferToUser(String mchid, String certificateSerialNo, String privateKeyFilePath, String wechatPayPublicKeyId, String wechatPayPublicKeyFilePath) {
        this.mchid = mchid;
        this.certificateSerialNo = certificateSerialNo;
        this.privateKey = WxPayUtil.loadPrivateKeyFromPath(privateKeyFilePath);
        this.wechatPayPublicKeyId = wechatPayPublicKeyId;
        this.wechatPayPublicKey = WxPayUtil.loadPublicKeyFromPath(wechatPayPublicKeyFilePath);
    }

    public String encrypt(String plainText) {
        return WxPayUtil.encrypt(this.wechatPayPublicKey, plainText);
    }

    public static class TransferToUserResponse {
        @SerializedName("out_bill_no")
        public String outBillNo;

        @SerializedName("transfer_bill_no")
        public String transferBillNo;

        @SerializedName("create_time")
        public String createTime;

        @SerializedName("state")
        public TransferBillStatus state;

        @SerializedName("package_info")
        public String packageInfo;
    }

    public enum TransferBillStatus {
        @SerializedName("ACCEPTED")
        ACCEPTED,
        @SerializedName("PROCESSING")
        PROCESSING,
        @SerializedName("WAIT_USER_CONFIRM")
        WAIT_USER_CONFIRM,
        @SerializedName("TRANSFERING")
        TRANSFERING,
        @SerializedName("SUCCESS")
        SUCCESS,
        @SerializedName("FAIL")
        FAIL,
        @SerializedName("CANCELING")
        CANCELING,
        @SerializedName("CANCELLED")
        CANCELLED
    }

    public static class TransferSceneReportInfo {
        @SerializedName("info_type")
        public String infoType;

        @SerializedName("info_content")
        public String infoContent;
    }

    public static class TransferToUserRequest {
        @SerializedName("appid")
        public String appid;

        @SerializedName("out_bill_no")
        public String outBillNo;

        @SerializedName("transfer_scene_id")
        public String transferSceneId;

        @SerializedName("openid")
        public String openid;

        @SerializedName("user_name")
        public String userName;

        @SerializedName("transfer_amount")
        public Long transferAmount;

        @SerializedName("transfer_remark")
        public String transferRemark;

        @SerializedName("notify_url")
        public String notifyUrl;

        @SerializedName("user_recv_perception")
        public String userRecvPerception;

        @SerializedName("transfer_scene_report_infos")
        public List<TransferSceneReportInfo> transferSceneReportInfos;
    }

}
