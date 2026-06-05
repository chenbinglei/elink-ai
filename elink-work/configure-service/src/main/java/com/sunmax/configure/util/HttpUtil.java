package com.sunmax.configure.util;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 发送GET请求
     *
     * @param url 请求的URL
     * @return 响应体内容
     * @throws IOException 如果请求失败或读取响应体时出错
     */
    public static String sendGet(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            if (response.body() == null) {
                return null;
            }
            return response.body().string();
        }
    }

    /**
     * 发送POST请求（JSON体）
     *
     * @param url  请求的URL
     * @param json JSON格式的请求体
     * @return 响应体内容
     * @throws IOException 如果请求失败或读取响应体时出错
     */
    public static String sendPost(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("错误的数据: " + response);
            }
            if (response.body() == null) {
                return null;
            }
            return response.body().string();
        }
    }

    // 可以根据需要添加其他HTTP方法（PUT, DELETE等）

    // 辅助方法：构建请求头
    public static Headers buildHeaders(String... headers) {
        Headers.Builder builder = new Headers.Builder();
        for (int i = 0; i < headers.length; i += 2) {
            builder.add(headers[i], headers[i + 1]);
        }
        return builder.build();
    }

    public static String jsonObjectToString(JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            // 对值进行URL编码（如果需要）
            // 注意：这里简单地调用了toString()，你可能需要根据值的类型进行更复杂的处理
            // 比如，如果值是null，你可能想要忽略它或将其转换为特定的字符串
            // 如果值是数组或另一个JSONObject，则需要递归处理
            String encodedValue = encodeValue(value.toString());
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(encodedValue);
        }

        return sb.toString();
    }

    // 简单的URL编码方法，注意处理UnsupportedEncodingException
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // 理论上不会抛出，因为UTF-8是支持的
            throw new RuntimeException(e);
        }
    }

}
