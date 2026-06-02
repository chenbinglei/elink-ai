package com.sunmax.configure.util.interflow;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.configure.dto.ResponseDto;
import com.sunmax.configure.util.AESUtil;
import com.sunmax.configure.util.HMacMD5Util;
import com.sunmax.configure.util.PlatformConfig;
import com.sunmax.configure.vo.RequestCommonVo;
import com.sunmax.configure.vo.RequestVo;
import com.sunmax.configure.vo.interflow.InterflowMethodVo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * 城市充电请求工具类
 */
@Slf4j
public class InterflowRequestUtil {

    //互联互通运营商id -> token
    public static Map<String, String> tokenMap = Maps.newHashMap();

    public static int TOKEN_COUNT = 0;

    public static String queryToken(RequestCommonVo commonVo) {
        try {
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("OperatorID", PlatformConfig.PLATFORM_ID);
            paramMap.put("OperatorSecret", commonVo.getPlatformSecret());
            String data = JSON.toJSONString(paramMap);
            //校验url后缀正确性
            String url = commonVo.getUrl();
            if (!Objects.equals(url.substring(url.length() - 1), FileUtil.SLASH)) {
                commonVo.setUrl(url + FileUtil.SLASH);
            }
            String requestUrl = commonVo.getUrl() + InterflowMethodVo.QUERY_TOKEN;
            log.info("互联互通明文数据（Data）：{}", data);
            RequestVo request = new RequestVo();
            request.setOperatorId(PlatformConfig.PLATFORM_ID);
            request.setData(AESUtil.encrypt(commonVo.getDataSecret(), commonVo.getDataSecretIv(), data));
            request.setTimeStamp(HMacMD5Util.getTimeStamp());
            request.setSeq(HMacMD5Util.getSeq(request.getTimeStamp()));
            String dataSplice = HMacMD5Util.dataSplice(request.getData(), request.getTimeStamp(), request.getSeq());
            request.setSig(HMacMD5Util.getHmacMd5HexString(commonVo.getSigSecret(), dataSplice));
            // 发送请求
            String tokenRequest = JSON.toJSONString(request);
            log.info("互联互通请求路径：{}", requestUrl);
            String response = HttpUtil.post(requestUrl, tokenRequest);
            ResponseDto result = JSON.parseObject(response, ResponseDto.class);
            log.info("互联互通请求结束,结果：{}", result.getData());
            if (result.getRet() == 0 && StringUtil.isNotEmpty(result.getData())) {
                //解密data
                Map<String, String> resultMap = JSON.parseObject(AESUtil.desEncrypt(commonVo.getDataSecret(), commonVo.getDataSecretIv(), result.getData()), new TypeReference<Map<String, String>>() {
                });
                log.info("resultMap：{}", resultMap);
                log.info("AccessToken：{}", resultMap.get("AccessToken"));
                tokenMap.put(commonVo.getPlatformId(), resultMap.get("AccessToken"));
                return resultMap.get("AccessToken");
            }
        } catch (Exception e) {
            log.error("互联互通获取token数据失败: ", e);
        }
        return null;
    }

    /**
     * 调用第三方接口
     */
    public static ResponseDto queryData(RequestCommonVo commonVo, String methodName, String data) {
        //返回的对象
        ResponseDto result = new ResponseDto();
        try {
            if (StringUtil.isEmpty(commonVo.getUrl())) {
                log.info("互联互通请求路径为空: {}", commonVo.getUrl());
                return result;
            }
            //校验url后缀正确性
            String url = commonVo.getUrl();
            if (!Objects.equals(url.substring(url.length() - 1), FileUtil.SLASH)) {
                commonVo.setUrl(url + FileUtil.SLASH);
            }
            //token为空 重新获取一下
            if (!tokenMap.containsKey(commonVo.getPlatformId())) {
                queryToken(commonVo);
            }
            String requestUrl = commonVo.getUrl() + methodName;
            RequestVo request = new RequestVo();
            request.setOperatorId(PlatformConfig.PLATFORM_ID);
            request.setData(AESUtil.encrypt(commonVo.getDataSecret(), commonVo.getDataSecretIv(), data));
            request.setTimeStamp(HMacMD5Util.getTimeStamp());
            request.setSeq(HMacMD5Util.getSeq(request.getTimeStamp()));
            String dataSplice = HMacMD5Util.dataSplice(request.getData(), request.getTimeStamp(), request.getSeq());
            request.setSig(HMacMD5Util.getHmacMd5HexString(commonVo.getSigSecret(), dataSplice));
            // 发送请求
            String tokenRequest = JSON.toJSONString(request);
            log.info("互联互通请求路径：{}", requestUrl);
            log.info("互联互通明文数据（Data）：{}", data);
            log.info("互联互通查询入参：{}", tokenRequest);
            String response = HttpRequest.post(requestUrl).header("Authorization", "Bearer " + tokenMap.get(commonVo.getPlatformId())).body(tokenRequest).execute().body();
            log.info("互联互通响应结果: {}", response);
            result = JSON.parseObject(response, ResponseDto.class);
            String responseData;
            if (StringUtil.isNotEmpty(result.getData())) {
                responseData = AESUtil.desEncrypt(commonVo.getDataSecret(), commonVo.getDataSecretIv(), result.getData());
                result.setData(responseData);
            }
            //token错误 需要重新获取一下
            if (result.getRet() == 4002 && TOKEN_COUNT == 0) {
                queryToken(commonVo);
                TOKEN_COUNT = 1;
                return queryData(commonVo, methodName, data);
            } else {
                TOKEN_COUNT = 0;
            }
        } catch (Exception e) {
            log.error("互联互通平台请求失败: ", e);
        }
        return result;
    }

//    public static void main(String[] args) {

    /// /        System.out.println(AESUtil.desEncrypt(DATA_SECRET, DATA_SECRET_IV, "/PUISUKwEm+ApOZ3uqqfxg=="));
//        try {
//            Map<String, Object> paramMap = Maps.newHashMap();
//            paramMap.put("OperatorID", "313744932");
//            paramMap.put("OperatorSecret", "78a51a478cff445f");
//            String data = JSON.toJSONString(paramMap);
//            String requestUrl = "http://36.137.133.136:18089/xndc-aggregator-service/CEC/1.0/" + InterflowMethodVo.QUERY_TOKEN;
//            log.info("互联互通明文数据（Data）：{}", data);
//            RequestVo request = new RequestVo();
//            request.setOperatorId("313744932");
//            request.setData(AESUtil.encrypt("d334caea1e8645e6", "00a18bed28d94a42", data));
//            request.setTimeStamp(HMacMD5Util.getTimeStamp());
//            request.setSeq(HMacMD5Util.getSeq(request.getTimeStamp()));
//            String dataSplice = HMacMD5Util.dataSplice(request.getData(), request.getTimeStamp(), request.getSeq());
//            request.setSig(HMacMD5Util.getHmacMd5HexString("c0e8b99f51a34ad9", dataSplice));
//            // 发送请求
//            String tokenRequest = JSON.toJSONString(request);
//            log.info("中电联请求路径：{}", requestUrl);
//            log.info("中电联请求入参：{}", tokenRequest);
//            String response = HttpUtil.post(requestUrl, tokenRequest);
//            log.info("中电联响应结果: {}", response);
//            ResponseDto result = JSON.parseObject(response, ResponseDto.class);
//            log.info("中电联请求结束,结果：{}", result.getData());
//            if (result.getRet() == 0 && StringUtil.isNotEmpty(result.getData())) {
//                //解密data
//                Map<String, String> resultMap = JSON.parseObject(AESUtil.desEncrypt("d334caea1e8645e6", "00a18bed28d94a42", result.getData()), new TypeReference<Map<String, String>>() {
//                });
//                log.info("resultMap：{}", resultMap);
//                log.info("AccessToken：{}", resultMap.get("AccessToken"));
//                System.out.println(resultMap.get("AccessToken"));
//            }
//        } catch (Exception e) {
//            log.error("获取数据失败: ", e);
//        }
//    }
    public static void main(String[] args) {
        String data = "XqEJ9Jl11YxaNW6PTSMC8jmgcTdKlTxwNaLf7tThvJWmVsxhFDgqBjeqcduLeGXEcIjZP0L9WMfWLIL1s4CPHpbH+mOOrRgoe9Fn6xG6GXZiVSdoAVjeU72DGn8c4QTv4lDLGX6Q7pwLGrdg951nfw4fBXgJl9EyufROAT8akC6j/YDiMVrBSNV5KAovEHpy4nhzHAMnv1C+qqJ0vzX8sJLW/nUc0sovC8+5E76o0MaMS2MIApshh3q9W/i4wSttDDsN2Sz8Rj/56BocUc5X6EacJm1QWKEZBdexiZN7B5WNtHBHGbzzQKu0491TqAP/BALJyCU9jfUjuWrAJUX7Q0umOi5Nw0sY+71MpesPzsi6bjASCKjP4WyoXlJ/TCasBfaZ9TV5KjxXXFkxio9Cz8IPIHhh8IP7EjBAKt7R+qEUWmtX+CdmTvVgXWf7e8QCTXdZje896MhEkpvmDcJRE5M79UGwX38O94e43vIOkA4EfZOAEWDKWeYFDU1GNkm9ksKgPlI/TgvksCLSEXrZVwht5VwP5QR1qRXfUEvKWPIhsKFXJSjIMEhQEA7Dx6u4gv8HThIqH1rXshhVhRa8gAysC/4dPOLeFPjuQwJGEMnPyuWrwcNCJxwyiGkXoa+YlnhJHABRYiaRI3lDODj24QL7krKpp57wJ5lLNQakEegsYpiNoyDmzvxtfBX5YiOAd3Lm93TY9h7IaRnWoR/apd2WGmO7uDVULUzrTJsWbIl6152bgGTEv6P2DxYKIyISzqw/ELwpdC1JytlmdFi9xFKs9WlieJGzl5zoEwkbLiPiQWKNMKhO39AZyIV/e/QiJ0kofAyh5+2eocGxN9NQyZ9H03lyIDdRaOgSMCyiUV2kBEZdJ1pPrhrpNuc2yqyV2W47mi3BK2H1SvrrfNMNuKHrII4DzyWKEZedP5HvJ0QaFQpORCvMXAW8VZoAjtU6cpkpdU4YD+IpZQe4fBtgIJbvnV2UUXHFI49Ub4w2vuXkeZLR8mO5SC/lEhDSKHSaYoMCo2JBNce0KMJ19RLFes66GaF9EpWS+s87jYKnQwyfaAKKvd0OOwrJkOKYH4ZDnzCtdfhMsrWVpmgZ/T693wsZM5qIrMlH7wiE7Nvg3br3k4aBVrRhGEz0/dxhPkbthz0mXpofary1NwK+fFyg5CBWW4pKJn6XaRjHIw0kplC8pmltwQJGNt+ya92zh+7kxWQLHvHNJS6WfT5MqsDq7OodIXj6gnCW0FLuc+07/3lPkZ63q2OQ2GyyNH2l3cmHPGpgTvIM49gZzBVH7xvvkvO0wFYzOEjsVD8BhB8OyQ1+KInjajuOHuhqhyfu80GqHQV4hmw8RzYZD2YJlhLuunvsxvyH+UCcTmrt4nxHr+nYRVzpFqWjO7wgqMY9xtWllCtQDQK0GlZsw5lpi0a7sTXV9MMjoibAsbjuLuN2KKUvonmf1IwVtN5So37H4g+mGrVLcfB/KSJypHTz3/lij+mWxPruq15/2qLbIZrTdkFKfpw8yI2H1qDzuj34LLeY";
        String result = AESUtil.desEncrypt("A7649A3C1BDBBCF8", "3750FB6AB984277F", data);
        System.out.println(result);
    }

}
