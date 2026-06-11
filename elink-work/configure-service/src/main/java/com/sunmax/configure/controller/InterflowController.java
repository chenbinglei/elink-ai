package com.sunmax.configure.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.configure.GunStatusInfoDto;
import com.sunmax.common.dto.configure.PowerControlResDto;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.PowerControlParamVo;
import com.sunmax.configure.dto.ResponseDto;
import com.sunmax.configure.service.InterflowService;
import com.sunmax.configure.util.AESUtil;
import com.sunmax.configure.util.HttpResponseUtil;
import com.sunmax.configure.util.PlatformConfig;
import com.sunmax.configure.util.TokenUtil;
import com.sunmax.configure.vo.RequestVo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping("interflow")
@Tag(name = "互联互通管理层")
@Slf4j
public class InterflowController {

    @Autowired
    private InterflowService interflowService;

    @PostMapping("queryInterflowToken")
    @Operation(summary = "查询token")
    
    public ResponseResult<String> queryToken(String platformId) {
        return interflowService.queryToken(platformId);
    }

    @PostMapping("queryInterflowData")
    @Operation(summary = "查询互联互通接口数据")
    
    public ResponseDto queryInterflowData(String platformId, String methodName, String paramData) {
        return interflowService.queryInterflowData(platformId, methodName, paramData);
    }

    @PostMapping("notification_equip_charge_status")
    @Operation(summary = "接收推送充电状态信息")
    
    public String notificationEquipChargeStatus(@RequestBody RequestVo requestVo) {
        log.info("互联互通推送充电状态接口，原始参数：{}", JSON.toJSONString(requestVo));
        ResponseDto responseDto = HttpResponseUtil.checkData(requestVo, ProtocolEnum.INTERFLOW.getCode());
        try {
            log.info("推送充电状态接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (StringUtil.isNotEmpty(responseDto) && responseDto.getRet() == 0) {
                Map<String, Object> resultDataMap = interflowService.notificationEquipChargeStatus(requestVo.getOperatorId(), responseDto.getData());
                log.info("推送充电状态接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(resultDataMap));
                responseDto.setData(JSON.toJSONString(resultDataMap));
            }
            log.info("推送充电状态接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            assert responseDto != null;
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("推送充电状态接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("推送充电状态接口，系统错误：", e);
            return responseData;
        }
    }

    @PostMapping("notification_charge_order_info")
    @Operation(summary = "接收推送充电订单信息")
    
    public String notificationChargeOrderInfo(@RequestBody RequestVo requestVo) {
        log.info("推送充电订单接口，原始参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(requestVo));
        ResponseDto responseDto = HttpResponseUtil.checkData(requestVo, ProtocolEnum.INTERFLOW.getCode());
        try {
            log.info("推送充电订单接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (StringUtil.isNotEmpty(responseDto) && responseDto.getRet() == 0) {
                Map<String, Object> resultDataMap = interflowService.notificationChargeOrderInfo(requestVo.getOperatorId(), responseDto.getData());
                log.info("推送充电订单接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(resultDataMap));
                responseDto.setData(JSON.toJSONString(resultDataMap));
            }
            log.info("推送充电订单接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            assert responseDto != null;
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("推送充电订单接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("推送充电订单接口，系统错误：", e);
            return responseData;
        }
    }

    @PostMapping("notification_stationStatus")
    @Operation(summary = "接收推送设备状态变化信息")
    
    public String notificationStationStatus(@RequestBody RequestVo requestVo) {
        log.info("推送设备状态变化接口，原始参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(requestVo));
        ResponseDto responseDto = HttpResponseUtil.checkData(requestVo, ProtocolEnum.INTERFLOW.getCode());
        try {
            log.info("推送设备状态变化接口，解密后参数为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            if (StringUtil.isNotEmpty(responseDto) && responseDto.getRet() == 0) {
                Map<String, Object> resultDataMap = interflowService.notificationStationStatus(requestVo.getOperatorId(), responseDto.getData());
                log.info("推送设备状态变化接口，执行后返回数据为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(resultDataMap));
                responseDto.setData(JSON.toJSONString(resultDataMap));
            }
            log.info("推送设备状态变化接口，返回数据加密前为@@@@@@@@@@@@@@@@@@@：{}", JSON.toJSONString(responseDto));
            assert responseDto != null;
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.info("推送设备状态变化接口，返回数据加密后为@@@@@@@@@@@@@@@@@@@：{}", responseData);
            return responseData;
        } catch (RuntimeException e) {
            responseDto.setRet(500);
            responseDto.setMsg("系统错误");
            String responseData = HttpResponseUtil.responseData(responseDto);
            log.error("推送设备状态变化接口，系统错误：", e);
            return responseData;
        }
    }

    @PostMapping("queryStationStatus")
    @Operation(summary = "查询站点下设备接口状态")
    
    public ResponseResult<Map<String, List<GunStatusInfoDto>>> queryStationStatus(@RequestParam String platformId, @RequestBody List<String> stationIds) {
        return interflowService.queryStationStatus(platformId, stationIds);
    }

    @PostMapping("interflowPowerControl")
    @Operation(summary = "城市充电功率控制")
    
    public ResponseResult<List<PowerControlResDto>> interflowPowerControl(String pileCode, Integer gunCode, Double outPower, Integer controlDurationValue) {
        PowerControlParamVo powerControlParamVo = new PowerControlParamVo();
        powerControlParamVo.setPileCode(pileCode);
        powerControlParamVo.setGunCode(gunCode);
        powerControlParamVo.setOutPower(outPower);
        powerControlParamVo.setControlDurationValue(controlDurationValue);
        return interflowService.interflowBatchPowerControl(Collections.singletonList(powerControlParamVo));
    }

    @PostMapping(value = "query_token")
    @Operation(summary = "查询平台token")
    
    public String queryToken(@RequestBody RequestVo request) {
        log.info("城市充电查询token请求参数: {}", request);
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
        log.info("城市充电查询token接口响应数据: {}", dataMap);
        String encryptData = AESUtil.encrypt(PlatformConfig.DATA_SECRET, PlatformConfig.DATA_SECRET_IV, JSON.toJSONString(dataMap));
        String result = JSON.toJSONString(ResponseDto.ok(encryptData, null, null, null));
        log.info("城市充电查询token接口响应参数: {}", result);
        return result;
    }

    @PostMapping("queryStationsInfo")
    @Operation(summary = "查询所有站点信息")
    
    public ResponseResult<Void> queryStationsInfo(String platformId) {
        interflowService.queryStationsInfo(platformId);
        return ResponseResult.ok();
    }

    public static void main(String[] args) {
        String str = "{\"ChargeDetails\":[{\"DetailElecMoney\":1.80,\"DetailEndTime\":\"2025-11-20 15:28:20\",\"DetailPower\":0.260,\"DetailServiceMoney\":0.50,\"DetailStartTime\":\"2025-11-20 14:54:46\",\"ElecPrice\":0.4680,\"ServicePrice\":0.1300}],\"ConnectorID\":\"1169\",\"EndTime\":\"2025-11-20 15:28:21\",\"ParkingFeeDiscount\":0,\"StartChargeSeq\":\"MA7L4KFB0000000000010605895\",\"StartTime\":\"2025-11-20 14:54:32\",\"StopReason\":99,\"SumPeriod\":1,\"TotalElecMoney\":0.47,\"TotalMoney\":0.60,\"TotalPower\":0.26,\"TotalSeviceMoney\":0.13,\"Vin\":\"\"}";
        log.info("{}", JSON.parseObject(str));
    }

}
