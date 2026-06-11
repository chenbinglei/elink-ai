package com.sunmax.configure.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.StringUtil;
import com.sunmax.configure.dto.ResponseDto;
import com.sunmax.configure.service.CityDataAccessService;
import com.sunmax.configure.util.AESUtil;
import com.sunmax.configure.util.HttpResponseUtil;
import com.sunmax.configure.util.PlatformConfig;
import com.sunmax.configure.util.TokenUtil;
import com.sunmax.configure.vo.RequestVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释:
 */
@RestController
@CrossOrigin
@RequestMapping("/web/city/v1")
@Tag(name = "市级数据接入管理")
@Slf4j
public class CityDataAccessController {

    @Autowired
    private CityDataAccessService cityDataAccessService;

    @PostMapping("query_stations_info")
    @Operation(summary = "查询充电站信息")
    
    public String findStationInfoListByTime(@RequestBody RequestVo requestVo) {
        log.info("市级调用查询充电站信息接口，原始参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(requestVo));
        ResponseDto responseDto = HttpResponseUtil.checkData(requestVo, ProtocolEnum.CITY.getCode());
        try {
            log.info("市级调用查询充电站信息接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (responseDto.getRet() == 0) {
                String data = responseDto.getData();
                Map<String, Object> requestDataMap = JSON.parseObject(data, new TypeReference<Map<String, Object>>() {});
                String LastQueryTime = null;
                int PageNo = 1;
                int PageSize = 10;
                if (StringUtil.isNotEmpty(requestDataMap.get("LastQueryTime"))) {
                    LastQueryTime = String.valueOf(requestDataMap.get("LastQueryTime"));
                }
                if (StringUtil.isNotEmpty(requestDataMap.get("PageNo"))) {
                    PageNo = Integer.parseInt(String.valueOf(requestDataMap.get("PageNo")));
                }
                if (StringUtil.isNotEmpty(requestDataMap.get("PageSize"))) {
                    PageSize = Integer.parseInt(String.valueOf(requestDataMap.get("PageSize")));
                }
                List<SiteOperateDto> siteOperateList = responseDto.getSiteOperateList();
                Map<String, Object> objectMap = cityDataAccessService.queryStationsInfo(LastQueryTime, PageNo, PageSize, siteOperateList);
                log.info("市级调用查询充电站信息接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(objectMap));
                responseDto.setData(JSON.toJSONString(objectMap));
            }
            log.info("市级调用查询充电站信息接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("市级调用查询充电站信息接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("市级调用查询充电站信息接口，系统错误：", e);
            return responseData;
        }
    }

    @PostMapping("query_station_status")
    @Operation(summary = "查询充电站接口状态")
    
    public String queryStationStatus(@RequestBody RequestVo requestVo) {
        log.info("市级调用查询充电站接口状态接口，原始参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(requestVo));
        ResponseDto responseDto = HttpResponseUtil.checkData(requestVo, ProtocolEnum.CITY.getCode());
        try {
            log.info("市级调用查询充电站接口状态接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (responseDto.getRet() == 0) {
                String data = responseDto.getData();
                Map<String, Object> requestDataMap = JSON.parseObject(data, new TypeReference<Map<String, Object>>() {});
                List<String> stationIdList = null;
                if (StringUtil.isNotEmpty(requestDataMap.get("StationIDs"))) {
                    List<String> cityStationIdList = JSON.parseArray(String.valueOf(requestDataMap.get("StationIDs")), String.class);
                    //数据转发配置返回的站点id列表
                    List<SiteOperateDto> siteOperateList = responseDto.getSiteOperateList();
                    if (CollectionUtils.isNotEmpty(siteOperateList)) {
                        List<String> siteIdList = siteOperateList.stream().map(SiteOperateDto::getSiteId).distinct().collect(Collectors.toList());
                        //在数据转发配置站点id列表中过滤出符合的站点id列表
                        stationIdList = siteIdList.stream().filter(cityStationIdList::contains).collect(Collectors.toList());
                    }
                }
                Map<String, Object> objectMap = cityDataAccessService.queryStationStatus(stationIdList);
                log.info("市级调用查询充电站接口状态接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(objectMap));
                responseDto.setData(JSON.toJSONString(objectMap));
            }
            log.info("市级调用查询充电站接口状态接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("市级调用查询充电站接口状态接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("市级调用查询充电站接口状态接口，系统错误：", e);
            return responseData;
        }
    }

    @PostMapping("query_equip_charge_status")
    @Operation(summary = "查询充电状态")
    
    public String queryEquipChargeStatus(@RequestBody RequestVo requestVo) {
        log.info("市级调用查询充电状态接口，原始参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(requestVo));
        ResponseDto responseDto = HttpResponseUtil.checkData(requestVo, ProtocolEnum.CITY.getCode());
        try {
            log.info("市级调用查询充电状态接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (responseDto.getRet() == 0) {
                String data = responseDto.getData();
                //数据转发配置返回的站点id列表
                List<SiteOperateDto> siteOperateList = responseDto.getSiteOperateList();
                List<String> siteIdList = siteOperateList.stream().map(SiteOperateDto::getSiteId).distinct().collect(Collectors.toList());
                Map<String, Object> requestDataMap = JSON.parseObject(data, new TypeReference<Map<String, Object>>() {});
                String StartChargeSeq = null;
                if (StringUtil.isNotEmpty(requestDataMap.get("StartChargeSeq"))) {
                    StartChargeSeq = String.valueOf(requestDataMap.get("StartChargeSeq"));
                }
                Map<String, Object> objectMap = cityDataAccessService.queryEquipChargeStatus(StartChargeSeq, siteIdList);
                log.info("市级调用查询充电状态接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(objectMap));
                responseDto.setData(JSON.toJSONString(objectMap));
            }
            log.info("市级调用查询充电状态接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("市级调用查询充电状态接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("市级调用查询充电状态接口，系统错误：", e);
            return responseData;
        }
    }

    @PostMapping("query_station_stats")
    @Operation(summary = "查询统计信息")
    
    public String queryStationStats(@RequestBody RequestVo requestVo) {
        log.info("市级调用查询统计信息接口，原始参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(requestVo));
        ResponseDto responseDto = HttpResponseUtil.checkData(requestVo, ProtocolEnum.CITY.getCode());
        try {
            log.info("市级调用查询统计信息接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (responseDto.getRet() == 0) {
                String data = responseDto.getData();
                Map<String, Object> requestDataMap = JSON.parseObject(data, new TypeReference<Map<String, Object>>() {});
                String stationId = null;
                String startTime = null;
                String endTime = null;
                if (StringUtil.isNotEmpty(requestDataMap.get("StationID"))) {
                    String cityStationId = String.valueOf(requestDataMap.get("StationID"));
                    //数据转发配置返回的站点id列表
                    List<SiteOperateDto> siteOperateList = responseDto.getSiteOperateList();
                    List<String> siteIdList = siteOperateList.stream().map(SiteOperateDto::getSiteId).distinct().collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(siteIdList) && siteIdList.stream().anyMatch(s -> s.endsWith(cityStationId))) {
                        stationId = cityStationId;
                    }
                }
                if (StringUtil.isNotEmpty(requestDataMap.get("StartTime"))) {
                    startTime = String.valueOf(requestDataMap.get("StartTime"));
                }
                if (StringUtil.isNotEmpty(requestDataMap.get("EndTime"))) {
                    endTime = String.valueOf(requestDataMap.get("EndTime"));
                }
                Map<String, Object> objectMap = cityDataAccessService.queryStationStats(stationId, startTime, endTime);
                log.info("市级调用查询统计信息接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：" + JSON.toJSONString(objectMap));
                responseDto.setData(JSON.toJSONString(objectMap));
            }
            log.info("市级调用查询统计信息接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("市级调用查询统计信息接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("市级调用查询统计信息接口，系统错误：", e);
            return responseData;
        }
    }

    @PostMapping(value = "query_token")
    @Operation(summary = "市查询平台token")
    
    public String queryToken(@RequestBody RequestVo request) {
        log.info("市查询token请求参数{}", request);
        String data = request.getData();
        String plainData = AESUtil.desEncrypt(PlatformConfig.DATA_SECRET, PlatformConfig.DATA_SECRET_IV, data);
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("OperatorID", request.getOperatorId());
        if (StringUtil.isNotEmpty(plainData)) {
            Map<String, String> info = JSON.parseObject(plainData, new TypeReference<Map<String, String>>() {});
            String operatorId = info.get("OperatorID");
            String operatorSecret = info.get("OperatorSecret");
            dataMap.put("OperatorID", operatorId);
            if (StringUtil.isEmpty(operatorId) || StringUtil.isEmpty(operatorSecret)) {
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
        log.info("市查询token接口响应数据: {}", dataMap);
        String encryptData = AESUtil.encrypt(PlatformConfig.DATA_SECRET, PlatformConfig.DATA_SECRET_IV, JSON.toJSONString(dataMap));
        String result = JSON.toJSONString(ResponseDto.ok(encryptData, null, null, null));
        log.info("市查询token接口响应参数: {}", result);
        return result;
    }

}
