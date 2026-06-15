package com.sunmax.protocol.util.platform;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.protocol.dto.platform.PlatformDataDto;
import com.sunmax.protocol.runner.ProtocolRunner;
import com.sunmax.protocol.vo.PlatformRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
public class PlatformUtil {

    //用户id
    public static String userId = "99999";

    //发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-平台对平台(V2G放电)
    public static Integer starter = 7;

    //电桩运行方式信息 key(电桩编号) -> 运行类型(0-充电 1-放电)
    public static Map<String, Integer> pileRunMap = Maps.newHashMap();

    /**
     * 加密数据
     *
     * @param pileCode 桩编号
     * @param data     需要加密的数据
     * @return 加密后的数据
     */
    public static String encryptData(String pileCode, String data) {
        PlatformDataDto platformData = ProtocolRunner.getPlatformData(pileCode);
        if (platformData == null) {
            log.error("平台数据转发通道未找到该桩编号,请去平台配置中心-数据转发配置一下");
            return null;
        }
        //加密数据
        return AESUtil.encrypt(platformData.getDataSecret(), platformData.getDataSecretIv(), data);
    }

    /**
     * 加密数据
     *
     * @param platformData 平台转发数据
     * @param data         需要加密的数据
     * @return 加密后的数据
     */
    public static String encryptData(PlatformDataDto platformData, String data) {
        if (platformData == null) {
            log.error("平台数据转发通道未找到该桩编号,请去平台配置中心-数据转发配置一下");
            return null;
        }
        //加密数据
        return AESUtil.encrypt(platformData.getDataSecret(), platformData.getDataSecretIv(), data);
    }

    /**
     * 解密数据
     *
     * @param platformId 平台id
     * @param data       加密的数据
     * @return 解密后的数据
     */
    public static JSONObject decodeData(String platformId, String data) {
        JSONObject result = new JSONObject();
        if (StringUtil.isEmpty(platformId)) {
            return result;
        }
        Map<String, PlatformDataDto> platformDataMap = ProtocolRunner.platformDataMap;
        if (platformDataMap.containsKey(platformId)) {
            PlatformDataDto platformData = platformDataMap.get(platformId);
            //解密数据
            String desEncrypt = AESUtil.desEncrypt(platformData.getDataSecret(), platformData.getDataSecretIv(), data);
            if (StringUtil.isNotEmpty(desEncrypt)) {
                result = JSONObject.parseObject(desEncrypt);
            }
        }
        return result;
    }

    public static Boolean post(String url, String methodName, PlatformRequestVo requestVo) {
        int count = 0;
//        String data = JSON.toJSONString(dataMap);
        if (!StringUtils.endsWith(url, FileUtil.SLASH)) {
            url = url + FileUtil.SLASH;
        }
        boolean response = PlatformUtil.callMethod(url, methodName, requestVo);
        while (!response && count < 3) {
            response = PlatformUtil.callMethod(url, methodName, requestVo);
            count++;
        }
        return response;
    }

    public static Boolean callMethod(String url, String methodName, PlatformRequestVo requestVo) {
        try {
            log.info("接口路径: " + url + methodName + ", 推送数据请求参数:" + JSON.toJSONString(requestVo));
            String response = HttpRequest.post(url + methodName).header("Content-Type", "application/json").body(JSON.toJSONString(requestVo))
                    .timeout(3000).execute().body();
            log.info("方法名: " + url + methodName + ", 推送数据响应结果: " + response);
            JSONObject responseResult = JSON.parseObject(response);
            if (StringUtil.isEmpty(responseResult.getString("success"))) {
                return false;
            }
            String platformId = requestVo.getPlatformId();
            Map<String, PlatformDataDto> platformDataMap = ProtocolRunner.platformDataMap;
            if (platformDataMap.containsKey(platformId) && platformDataMap.get(platformId) != null) {
                PlatformDataDto platformData = platformDataMap.get(platformId);
                //处理不同的返回结果处理
                String success = AESUtil.desEncrypt(platformData.getDataSecret(), platformData.getDataSecretIv(), responseResult.getString("success"));
                if (StringUtil.isNotEmpty(success)) {
                    return Boolean.parseBoolean(success); //加密的数据解密处理
                } else {
                    return Boolean.parseBoolean(responseResult.getString("success")); //未加密的数据处理
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

//    public static void main(String[] args) {
//        PlatformPileStartVo platformPileStartVo = new PlatformPileStartVo();
//        platformPileStartVo.setPileCode("3102620250121001");
//        platformPileStartVo.setGunCode(1);
//        platformPileStartVo.setStrategy(0);
//        platformPileStartVo.setStrategyCfg(100.0);
//        platformPileStartVo.setDirection(1);
//        platformPileStartVo.setAccountType(3);
//        platformPileStartVo.setAccountData("13112345678");
//        System.out.println(JSON.toJSONString(platformPileStartVo));
//
//        PlatformPileStopVo platformPileStopVo = new PlatformPileStopVo();
//        platformPileStopVo.setPileCode("3102620250121001");
//        platformPileStopVo.setGunCode(1);
//        platformPileStopVo.setSerialNum("31026202501210012508261731261101");
//        platformPileStopVo.setType(1);
//

    /// /        System.out.println(JSON.toJSONString(platformPileStopVo));
//
//        System.out.println(AESUtil.encrypt("sunmaxandwnkey00", "sunmaxandwniv000", JSON.toJSONString(platformPileStopVo)));
//
//        System.out.println(AESUtil.desEncrypt("sunmaxandwnkey00", "sunmaxandwniv000", "vM8IP23KVw6MDaFXqCw7xLJ0pGcZzkWXgISS4RNZWZh4ENWI9nuF6XNXr/WmJgRzZfvwOHvh0Bg7921TT/2SWGLeRfxKqexwMV5cYN9hxaFxoXWe1CwwnY1r1UY+UrpanAPpcxgBvVXAip54FtZdEZpeIV+YCZdNgN0qoR75Gzw="));
//    }
//    public static void main(String[] args) {
//        System.out.println(AESUtil.desEncrypt("1234567890wnvpp1", "1wnvpp1234567890", "DVb+JPAqEsXMnoHBcFGK0AMgjsEk6cfSh5c0noziXCL61DZIWpmZEYfuysdnfhoVIuOiUiKKfduAZoiMjO4sGuTLEVw4tbWTKf/h/MQ0p+vPvBKeA9DX/CwFf5v1qAPvZqWfnqwLrA4AGvlVBeKJ4A=="));
//    }

}
