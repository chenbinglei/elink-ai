package com.sunmax.configure.util.city;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Maps;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.configure.dao.CityRecordDao;
import com.sunmax.configure.dto.ResponseDto;
import com.sunmax.configure.entity.CityRecordEntity;
import com.sunmax.configure.util.AESUtil;
import com.sunmax.configure.util.HMacMD5Util;
import com.sunmax.configure.util.PlatformConfig;
import com.sunmax.configure.vo.RequestCommonVo;
import com.sunmax.configure.vo.RequestVo;
import com.sunmax.configure.vo.city.CityMethodVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 市请求工具类
 */
@Slf4j
@Configuration
public class CityRequestUtil {

    //运营商token 运营商id -> token
    public static Map<String, String> tokenMap = Maps.newHashMap();

    public static int TOKEN_COUNT = 0;

    public static void queryToken(RequestCommonVo commonVo) {
        try {
            Map<String,Object> paramMap = Maps.newHashMap();
            paramMap.put("OperatorID", PlatformConfig.PLATFORM_ID);
            paramMap.put("OperatorSecret", commonVo.getPlatformSecret());
            String data = JSON.toJSONString(paramMap);
            String requestUrl = commonVo.getUrl() + CityMethodVo.QUERY_TOKEN;
            log.info("市明文数据（Data）：" + data);
            RequestVo request = new RequestVo();
            request.setOperatorId(PlatformConfig.PLATFORM_ID);
            request.setData(AESUtil.encrypt(commonVo.getDataSecret(), commonVo.getDataSecretIv(), data));
            request.setTimeStamp(HMacMD5Util.getTimeStamp());
            request.setSeq(HMacMD5Util.getSeq(request.getTimeStamp()));
            String dataSplice = HMacMD5Util.dataSplice(request.getData(), request.getTimeStamp(), request.getSeq());
//            log.info("市需要签名的内容：" + dataSplice + ",密钥：" + SIG_SECRET);
            request.setSig(HMacMD5Util.getHmacMd5HexString(commonVo.getSigSecret(), dataSplice));

//            log.info("市加密后数据（Data）：" + request.getData());
//            log.info("市解密后数据（Data）：" + AESUtil.desEncrypt(DATA_SECRET, DATA_SECRET_IV, request.getData()));
//            log.info("市HmacSHA1的签名内容：" + request.getSig());
            // 发送请求
            String tokenRequest = JSON.toJSONString(request);
//            log.info("市请求参数：{}", tokenRequest);
            log.info("市请求路径：{}", requestUrl);
            String response = HttpUtil.post(requestUrl, tokenRequest);
            ResponseDto result = JSON.parseObject(response, ResponseDto.class);
            log.info("市请求结束,结果：{}", result.getData());
            if (result.getRet() == 0 && StringUtil.isNotEmpty(result.getData())) {
                //解密data
                Map<String, String> resultMap = JSON.parseObject(AESUtil.desEncrypt(commonVo.getDataSecret(), commonVo.getDataSecretIv(), result.getData()), new TypeReference<Map<String, String>>(){});
                log.info("resultMap：" + resultMap);
                log.info("AccessToken：" + resultMap.get("AccessToken"));
                tokenMap.put(commonVo.getOperatorId(), resultMap.get("AccessToken"));
            }
        } catch (RuntimeException e) {
            log.error("获取数据失败: ", e);
        }
    }

    /**
     * 调用第三方接口
     */
    public static void sendData(RequestCommonVo commonVo, String methodName, String data, CityRecordDao cityRecordDao) {
        if (StringUtil.isEmpty(commonVo.getUrl())) {
            log.info("省平台请求路径为空");
            return;
        }
        //校验url后缀正确性
        String url = commonVo.getUrl();
        if (!Objects.equals(url.substring(url.length() - 1), FileUtil.SLASH)) {
            commonVo.setUrl(url + FileUtil.SLASH);
        }
        try {
            //token为空 重新获取一下
            if (!tokenMap.containsKey(commonVo.getOperatorId())) {
                queryToken(commonVo);
            }
            LocalDateTime createTime = LocalDateTime.now();
            String requestUrl = commonVo.getUrl() + methodName;
            log.info("市明文数据（Data）：" + data);
            RequestVo request = new RequestVo();
            request.setOperatorId(PlatformConfig.PLATFORM_ID);
            request.setData(AESUtil.encrypt(commonVo.getDataSecret(), commonVo.getDataSecretIv(), data));
            request.setTimeStamp(HMacMD5Util.getTimeStamp());
            request.setSeq(HMacMD5Util.getSeq(request.getTimeStamp()));
            String dataSplice = HMacMD5Util.dataSplice(request.getData(), request.getTimeStamp(), request.getSeq());
//          log.info("市需要签名的内容：" + dataSplice + ",密钥：" + SIG_SECRET);
            request.setSig(HMacMD5Util.getHmacMd5HexString(commonVo.getSigSecret(), dataSplice));

//          log.info("市加密后数据（Data）：" + request.getData());
//          log.info("市解密后数据（Data）：" + AESUtil.desEncrypt(DATA_SECRET, DATA_SECRET_IV, request.getData()));
//          log.info("市HmacSHA1的签名内容：" + request.getSig());
            // 发送请求
            String jsonRequest = JSON.toJSONString(request);
//          log.info("市请求参数：{}", tokenRequest);
            log.info("市请求路径：{}", requestUrl);
            String response = HttpRequest.post(requestUrl).header("Authorization", "Bearer " + tokenMap.get(commonVo.getOperatorId()))
                    .body(jsonRequest).execute().body();
            log.info("市响应结果: " + response);
            LocalDateTime updateTime = LocalDateTime.now();
            ResponseDto result = JSON.parseObject(response, ResponseDto.class);
            String responseData = null;
            if (StringUtil.isNotEmpty(result.getData())) {
                responseData = AESUtil.desEncrypt(commonVo.getDataSecret(), commonVo.getDataSecretIv(), result.getData());
                log.info("市响应数据: " + responseData);
            }
            //token错误 需要重新获取一下
            if (result.getRet() == 4002 && TOKEN_COUNT == 0) {
                queryToken(commonVo);
                TOKEN_COUNT = 1;
                sendData(commonVo, methodName, data, cityRecordDao);
            } else {
                TOKEN_COUNT = 0;
            }
            //保存订单推送记录
            if (Objects.equals(methodName, CityMethodVo.NOTIFICATION_CHARGE_ORDER_INFO)) {
                CityRecordEntity cityRecord = CityRecordEntity.builder().interfaceName(methodName).pushData(data)
                        .responseStatus(result.getRet()).responseData(responseData).build();
                cityRecord.setCreateTime(createTime);
                cityRecord.setUpdateTime(updateTime);
                cityRecordDao.save(cityRecord);
            }
        } catch (RuntimeException e) {
            log.error("市平台请求失败: ", e);
        }

    }

}
