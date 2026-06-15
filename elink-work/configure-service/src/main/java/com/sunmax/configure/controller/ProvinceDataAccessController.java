package com.sunmax.configure.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.StringUtil;
import com.sunmax.configure.dto.ResponseDto;
import com.sunmax.configure.service.ProvinceDataAccessService;
import com.sunmax.configure.util.AESUtil;
import com.sunmax.configure.util.HttpResponseUtil;
import com.sunmax.configure.util.PlatformConfig;
import com.sunmax.configure.util.TokenUtil;
import com.sunmax.configure.vo.RequestVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: yqz
 * @Date: 2023/9/2017:32
 * @version: 1.0
 * @注释: 省级数据接入管理
 */
@RestController
@CrossOrigin
@RequestMapping("/web/province/v1")
@Tag(name = "省级数据接入管理")
@Slf4j
public class ProvinceDataAccessController {

    @Autowired
    private ProvinceDataAccessService provinceDataAccessService;

    @PostMapping(value = "supervise_query_operator_info")
    @Operation(summary = "查询运营商信息")
    
    public String queryOperatorInfo(@RequestBody RequestVo request) {
        log.info("省级平台查询运营商信息原始入参：{}", request);
        ResponseDto responseDto = HttpResponseUtil.checkData(request, ProtocolEnum.PROVINCE.getCode());
        log.info("省级平台查询运营商信息解密后入参：{}", responseDto);
        try {
            if (responseDto.getRet() == 0) {
                JSONObject paramMap = JSON.parseObject(responseDto.getData());
                Integer pageNo = 1;
                Integer pageSize = 50;
                if (StringUtil.isNotEmpty(paramMap.getInteger("PageNo"))) {
                    pageNo = paramMap.getInteger("PageNo");
                }
                if (StringUtil.isNotEmpty(paramMap.getInteger("PageSize"))) {
                    pageSize = paramMap.getInteger("PageSize");
                }
                Map<String, Object> resultMap = provinceDataAccessService.queryOperatorInfo(pageNo, pageSize, responseDto.getOperatorInfoList());
                log.info("省级平台查询运营商信息响应数据：{}", JSON.toJSONString(resultMap));
                responseDto.setData(JSON.toJSONString(resultMap));
            }
            return HttpResponseUtil.responseData(responseDto);
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("省级调用查询运营商信息接口，系统错误：", e);
            return responseData;
        }
    }

/*    @PostMapping(value = "supervise_query_stations_info")
    @Operation(summary = "查询充电站信息")
    
    public Map<String, Object> findStationInfoListByTime(String LastQueryTime, Integer PageNo, Integer PageSize, String stationIDs) {
        Map<String, Object> objectMap = provinceDataAccessService.findStationInfoListByTime(LastQueryTime, PageNo, PageSize, stationIDs);
        return objectMap;
    }*/


    @PostMapping(value = "supervise_query_stations_info")
    @Operation(summary = "查询充电站信息")
    
    public String findStationInfoListByTime(@RequestBody RequestVo request) {
        log.info("省级调用查询充电站信息接口，原始参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(request));
        ResponseDto responseDto = HttpResponseUtil.checkData(request, ProtocolEnum.PROVINCE.getCode());
        try {
            log.info("省级调用查询充电站信息接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (responseDto.getRet() == 0) {
                String data = responseDto.getData();
                Map<String, Object> requestDataMap = JSON.parseObject(data, new TypeReference<Map<String, Object>>() {
                });
                String LastQueryTime = null;
                int PageNo = 1;
                int PageSize = 50;
                String stationIDs = null;
                if (StringUtil.isNotEmpty(requestDataMap.get("LastQueryTime"))) {
                    LastQueryTime = String.valueOf(requestDataMap.get("LastQueryTime"));
                }
                if (StringUtil.isNotEmpty(requestDataMap.get("PageNo"))) {
                    PageNo = Integer.parseInt(String.valueOf(requestDataMap.get("PageNo")));
                }
                if (StringUtil.isNotEmpty(requestDataMap.get("PageSize"))) {
                    PageSize = Integer.parseInt(String.valueOf(requestDataMap.get("PageSize")));
                }
                if (StringUtil.isNotEmpty(requestDataMap.get("StationIDs"))) {
                    stationIDs = String.valueOf(requestDataMap.get("StationIDs"));
                }
                List<SiteOperateDto> siteOperateList = responseDto.getSiteOperateList();
                Map<String, Object> objectMap = provinceDataAccessService.findStationInfoListByTime(LastQueryTime, PageNo, PageSize, stationIDs, siteOperateList);
                log.info("省级调用查询充电站信息接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(objectMap));
                responseDto.setData(JSON.toJSONString(objectMap));
            }
            log.info("省级调用查询充电站信息接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("省级调用查询充电站信息接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("省级调用查询充电站信息接口，系统错误：", e);
            return responseData;
        }
    }

    /*    @PostMapping(value = "supervise_query_station_status")
        @Operation(summary = "查询充电站接口状态")
        
        public Map<String, Object> queryStationStatus(String StationIDs) {
            Map<String, Object> objectMap = provinceDataAccessService.queryStationStatus(StationIDs);
            return objectMap;
        }*/
    @PostMapping(value = "supervise_query_station_status")
    @Operation(summary = "查询充电站接口状态")
    
    public String queryStationStatus(@RequestBody RequestVo request) {
        log.info("省级调用查询充电站接口状态接口，原始参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(request));
        ResponseDto responseDto = HttpResponseUtil.checkData(request, ProtocolEnum.PROVINCE.getCode());
        try {
            log.info("省级调用查询充电站接口状态接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (responseDto.getRet() == 0) {
                String data = responseDto.getData();
                Map<String, Object> requestDataMap = JSON.parseObject(data, new TypeReference<Map<String, Object>>() {
                });
                List<SiteOperateDto> siteOperateList = Lists.newArrayList();
                if (StringUtil.isNotEmpty(requestDataMap.get("StationIDs"))) {
                    List<String> cityStationIdList = JSON.parseArray(String.valueOf(requestDataMap.get("StationIDs")), String.class);
                    //数据转发配置返回的站点id列表
                    siteOperateList = responseDto.getSiteOperateList();
                    if (CollectionUtils.isNotEmpty(siteOperateList)) {
                        //在数据转发配置站点id列表中过滤出符合的站点id列表
                        siteOperateList = siteOperateList.stream().filter(item -> cityStationIdList.stream().anyMatch(stationId -> stationId.equals(item.getSiteId()))).collect(Collectors.toList());
                    }
                }

                Map<String, Object> objectMap = provinceDataAccessService.queryStationStatus(siteOperateList);
                log.info("省级调用查询充电站接口状态接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(objectMap));
                responseDto.setData(JSON.toJSONString(objectMap));
            }
            log.info("省级调用查询充电站接口状态接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("省级调用查询充电站接口状态接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("省级调用查询充电站接口状态接口，系统错误：", e);
            return responseData;
        }
    }

    @PostMapping(value = "query_token")
    @Operation(summary = "省查询平台token")
    
    public String queryToken(@RequestBody RequestVo request) {
        log.info("省查询token请求参数{}", request);
        String data = request.getData();
        String plainData = AESUtil.desEncrypt(PlatformConfig.DATA_SECRET, PlatformConfig.DATA_SECRET_IV, data);
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("OperatorID", request.getOperatorId());
        if (StringUtil.isNotEmpty(plainData)) {
            Map<String, String> info = JSON.parseObject(plainData, new TypeReference<Map<String, String>>() {});
            String operatorId = info.get("OperatorID");
            String operatorSecret = info.get("OperatorSecret");
            dataMap.put("OperatorID", operatorId);
            if (StringUtils.isBlank(operatorId) || StringUtils.isBlank(operatorSecret)) {
                dataMap.put("FailReason", 1);
                dataMap.put("SuccStat", 1);
                dataMap.put("TokenAvailableTime", 0);
                dataMap.put("AccessToken", "");
            } else {
                //验证密钥正确性
                if (Objects.equals(operatorSecret, PlatformConfig.PLATFORM_SECRET)) {
                    dataMap.put("AccessToken", TokenUtil.generateToken(operatorId));
                    dataMap.put("TokenAvailableTime", 7200);
                    dataMap.put("FailReason", 0);
                    dataMap.put("SuccStat", 0);
                } else {
                    dataMap.put("FailReason", 2);
                    dataMap.put("SuccStat", 1);
                    dataMap.put("TokenAvailableTime", 0);
                    dataMap.put("AccessToken", "");
                }
            }
        } else {
            dataMap.put("FailReason", 1);
            dataMap.put("SuccStat", 1);
            dataMap.put("TokenAvailableTime", 0);
            dataMap.put("AccessToken", "");
        }
        //加密
        String encryptData = AESUtil.encrypt(PlatformConfig.DATA_SECRET, PlatformConfig.DATA_SECRET_IV, JSON.toJSONString(dataMap));
        log.info("省查询token接口响应数据: {}", dataMap);
        String result = JSON.toJSONString(ResponseDto.ok(encryptData, null, null, null));
        log.info("省查询token接口响应参数: {}", result);
        return result;
    }

}
