package com.sunmax.protocol.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.constant.IEGConstant;
import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.dto.protocol.GateWayControlDto;
import com.sunmax.common.dto.protocol.GateWayPolicyDto;
import com.sunmax.common.dto.protocol.PlatformSetDto;
import com.sunmax.common.dto.protocol.PlatformStatusDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.TimestampUtil;
import com.sunmax.common.vo.protocol.GateWayControlVo;
import com.sunmax.common.vo.protocol.GateWayPolicyVo;
import com.sunmax.common.vo.protocol.PlatformSetVo;
import com.sunmax.common.vo.protocol.PlatformStatusVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.SmMqttRequestVo;
import com.sunmax.common.vo.protocol.mqtt.web.from.*;
import com.sunmax.common.vo.protocol.mqtt.web.platform.LicensePublishVo;
import com.sunmax.common.vo.protocol.mqtt.web.platform.RebootPublicVo;
import com.sunmax.common.vo.protocol.mqtt.web.platform.SyncClockPublishVo;
import com.sunmax.protocol.config.emqx.web.WebMqttConfig;
import com.sunmax.protocol.constant.SMV2gConstant;
import com.sunmax.protocol.demand.GatewayDemand;
import com.sunmax.protocol.model.GatewayDemandModel;
import com.sunmax.protocol.service.GatewayCtrlService;
import com.sunmax.protocol.task.ControlTask;
import com.sunmax.protocol.util.MqttTopicUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class GatewayCtrlServiceImpl implements GatewayCtrlService {

    @Override
    public ResponseResult<GateWayControlDto> loadParamSet(GateWayControlVo gateWayControlVo) {
        //计算权重   命令码
        String keyId = gateWayControlVo.getDeviceCode() + IEGConstant.Param.LOAD_PARAMSET;
        //创建控制
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, gateWayControlVo.getDeviceCode());
        //下发主题
        MqttTopicUtil.sendLoadParamTopic(gateWayControlVo);
        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 10);
        //移除需求
        GatewayDemand.deleteDemand(keyId);
        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                case 0:
                    return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), GateWayControlDto.class));
                case 1:
                    return ResponseResult.error(SMV2gConstant.StatusFailed);
                case 255:
                    return ResponseResult.error(SMV2gConstant.StatusOther);
            }
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<GateWayPolicyDto> issuedPolicyParam(GateWayPolicyVo policyIssuedVo) {
        //返回的对象
        GateWayPolicyDto result = new GateWayPolicyDto();

        //计算权重   命令码
        String keyId = policyIssuedVo.getDeviceCode() + policyIssuedVo.getPolicyId() + IEGConstant.Param.GENERAL_PARAMGETSET;

        //创建控制
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, policyIssuedVo.getDeviceCode());

        //下发主题
        MqttTopicUtil.sendPolicyParamIssuedTopic(policyIssuedVo);

        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 10);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            BeanUtils.copyProperties(policyIssuedVo, result);
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    result.setIssuedStatus(recCode); //下发超时
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut, result);
                case 0:
                    return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), GateWayPolicyDto.class));
                case 1:
                    result.setIssuedStatus(recCode); //下发失败
                    return ResponseResult.error(SMV2gConstant.StatusFailed, result);
                case 255:
                    result.setIssuedStatus(recCode); //其他原因
                    return ResponseResult.error(SMV2gConstant.StatusOther, result);
            }
        }

        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<GateWayPolicyDto>> batchIssuedPolicyParam(List<GateWayPolicyVo> policyIssuedVos) {
        if (CollectionUtils.isEmpty(policyIssuedVos)) {
            return ResponseResult.ok(Lists.newArrayList());
        }

        try {
            ForkJoinPool pool = ForkJoinPool.commonPool();
            List<CompletableFuture<GateWayPolicyDto>> futures = policyIssuedVos.stream()
                    .map(policyIssuedVo -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return this.issuedPolicyParam(policyIssuedVo).getData();
                        } catch (Exception e) {
                            log.error("处理策略参数下发任务失败: " + policyIssuedVo.toString(), e);
                            return null;
                        }
                    }, pool))
                    .collect(Collectors.toList());

            List<GateWayPolicyDto> resultList = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return ResponseResult.ok(resultList);
        } catch (CompletionException e) {
            // 解包CompletionException，获取根本原因
            Throwable cause = e.getCause();
            log.error("批量下发网关通用策略参数失败", cause);
            return ResponseResult.error("批量下发网关通用策略参数过程中遇到错误: " + cause.getMessage());
        } catch (Exception e) {
            log.error("批量下发网关通用策略参数失败, 遇到未知错误", e);
            return ResponseResult.error("批量下发网关通用策略参数过程中遇到未知错误");
        }
    }


    @Override
    public ResponseResult<GatewayStatusSubscribeVo> gatewayStatus(String deviceCode) {
        //计算权重   命令码
        String keyId = deviceCode + WebTopicConstant.GATEWAY_STATUS_REQUEST;

        //创建网关状态需求
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);

        //下发网关控制策略通用主题参数
        SmMqttRequestVo smMqttRequestVo = SmMqttRequestVo.builder()
                .token(WebTopicConstant.GATEWAY_STATUS_REQUEST)
                .body(null)
                .timestamp(DateUtil.localDateTimeToStr(LocalDateTime.now()))
                .build();
        String topicName = WebTopicConstant.GATEWAY_REQUEST_PREFIX + WebTopicConstant.GATEWAY_STATUS_REQUEST + deviceCode;
        WebMqttConfig.sendToMqtt(deviceCode, topicName, smMqttRequestVo);

        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 2);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                case 0:
                    return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), GatewayStatusSubscribeVo.class));
                case 1:
                    return ResponseResult.error(SMV2gConstant.StatusFailed);
                case 255:
                    return ResponseResult.error(SMV2gConstant.StatusOther);
            }
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<GatewayStatusSubscribeVo>> batchGatewayStatus(List<String> deviceCodes) {
        //返回的集合
        List<GatewayStatusSubscribeVo> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(deviceCodes)) {
            try {
                //2.创建线程池
                final ExecutorService executor = Executors.newFixedThreadPool(deviceCodes.size());
                //3.执行查询网关状态命令，提交到任务里面
                deviceCodes.forEach(deviceCode -> executor.submit(() -> {
                    try {
                        GatewayStatusSubscribeVo gatewayStatusVo = gatewayStatus(deviceCode).getData();
                        if (gatewayStatusVo != null) {
                            gatewayStatusVo.setDevId(deviceCode);
                            resultList.add(gatewayStatusVo);
                        }
                    } catch (Exception e) {
                        log.error("查询网关" + deviceCode + "状态报错", e);
                    }
                }));
                // 关闭线程池
                executor.shutdown();
                do {
                    Thread.sleep(500);
                } while (!executor.isTerminated());
            } catch (Exception e) {
                log.error("查询网关状态任务报错", e);
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<ServiceListSubscribeVo>> serviceList(String deviceCode) {
        //创建需求key
        String keyId = deviceCode + WebTopicConstant.SERVICE_LIST_REQUEST;

        //创建网关状态需求
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);

        //下发网关控制策略通用主题参数
        SmMqttRequestVo smMqttRequestVo = SmMqttRequestVo.builder()
                .token(WebTopicConstant.SERVICE_LIST_REQUEST)
                .body(null)
                .timestamp(DateUtil.localDateTimeToStr(LocalDateTime.now()))
                .build();
        String topicName = WebTopicConstant.GATEWAY_REQUEST_PREFIX + WebTopicConstant.SERVICE_LIST_REQUEST + deviceCode;
        //下发参数
        WebMqttConfig.sendToMqtt(deviceCode, topicName, smMqttRequestVo);

        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 20);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                case 0:
                    return ResponseResult.ok(JSON.parseArray(asyncTaskResult.getString("recMsg"), ServiceListSubscribeVo.class));
                case 1:
                    return ResponseResult.error(SMV2gConstant.StatusFailed);
                case 255:
                    return ResponseResult.error(SMV2gConstant.StatusOther);
            }
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<SyncClockSubscribeVo> syncClock(String deviceCode, Integer type) {
        //创建需求key
        String keyId = deviceCode + WebTopicConstant.SYNC_CLOCK_REQUEST;

        //创建网关时钟同步需求
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);

        //下发网关时钟同步通用主题参数
        SmMqttRequestVo smMqttRequestVo = SmMqttRequestVo.builder()
                .token(WebTopicConstant.SYNC_CLOCK_REQUEST)
                .body(SyncClockPublishVo.builder().type(type).timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+0"))).build())
                .timestamp(DateUtil.localDateTimeToStr(LocalDateTime.now()))
                .build();
        String topicName = WebTopicConstant.GATEWAY_REQUEST_PREFIX + WebTopicConstant.SYNC_CLOCK_REQUEST + deviceCode;
        //下发参数
        WebMqttConfig.sendToMqtt(deviceCode, topicName, smMqttRequestVo);

        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 20);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                case 0:
                    return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), SyncClockSubscribeVo.class));
                case 1:
                    return ResponseResult.error(SMV2gConstant.StatusFailed);
                case 255:
                    return ResponseResult.error(SMV2gConstant.StatusOther);
            }
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<RebootSubscribeVo> reboot(String deviceCode, RebootPublicVo rebootPublicVo) {
        //创建需求key
        String keyId = deviceCode + WebTopicConstant.REBOOT_REQUEST;
        //创建网关重启需求
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);
        //下发网关重启通用主题参数
        SmMqttRequestVo smMqttRequestVo = SmMqttRequestVo.builder()
                .token(WebTopicConstant.REBOOT_REQUEST)
                .body(rebootPublicVo)
                .timestamp(DateUtil.localDateTimeToStr(LocalDateTime.now()))
                .build();
        String topicName = WebTopicConstant.GATEWAY_REQUEST_PREFIX + WebTopicConstant.REBOOT_REQUEST + deviceCode;
        //下发参数
        WebMqttConfig.sendToMqtt(deviceCode, topicName, smMqttRequestVo);

        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 20);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                case 0:
                    return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), RebootSubscribeVo.class));
                case 1:
                    return ResponseResult.error(SMV2gConstant.StatusFailed);
                case 255:
                    return ResponseResult.error(SMV2gConstant.StatusOther);
            }
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<LicenseSubscribeVo> licenseStatus(String deviceCode) {
        //创建需求key
        String keyId = deviceCode + WebTopicConstant.LICENSE_STATUS_REQUEST;
        //创建网关许可状态查询需求
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);

        //下发网关许可状态查询通用主题参数
        SmMqttRequestVo smMqttRequestVo = SmMqttRequestVo.builder()
                .token(WebTopicConstant.LICENSE_STATUS_REQUEST)
                .body(null)
                .timestamp(DateUtil.localDateTimeToStr(LocalDateTime.now()))
                .build();
        String topicName = WebTopicConstant.GATEWAY_REQUEST_PREFIX + WebTopicConstant.LICENSE_STATUS_REQUEST + deviceCode;
        //下发参数
        WebMqttConfig.sendToMqtt(deviceCode, topicName, smMqttRequestVo);
        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 20);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                case 0:
                    return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), LicenseSubscribeVo.class));
                case 1:
                    return ResponseResult.error(SMV2gConstant.StatusFailed);
                case 255:
                    return ResponseResult.error(SMV2gConstant.StatusOther);
            }
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<LicenseSubscribeVo> licenseKey(String deviceCode) {

        //创建需求key
        String keyId = deviceCode + WebTopicConstant.LICENSE_KET_REQUEST;

        //创建设备网关key需求
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);

        //下发设备网关key通用主题参数
        SmMqttRequestVo smMqttRequestVo = SmMqttRequestVo.builder()
                .token(WebTopicConstant.LICENSE_KET_REQUEST)
                .body(null)
                .timestamp(DateUtil.localDateTimeToStr(LocalDateTime.now()))
                .build();
        String topicName = WebTopicConstant.GATEWAY_REQUEST_PREFIX + WebTopicConstant.LICENSE_KET_REQUEST + deviceCode;
        WebMqttConfig.sendToMqtt(deviceCode, topicName, smMqttRequestVo);

        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 20);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                case 0:
                    return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), LicenseSubscribeVo.class));
                case 1:
                    return ResponseResult.error(SMV2gConstant.StatusFailed);
                case 255:
                    return ResponseResult.error(SMV2gConstant.StatusOther);
            }
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<LicenseSubscribeVo> license(String deviceCode, LicensePublishVo licensePublicVo) {

        //创建需求key
        String keyId = deviceCode + WebTopicConstant.LICENSE_REQUEST;

        //创建网关许可状态查询需求
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);
        //下发网关许可状态查询通用主题参数
        SmMqttRequestVo smMqttRequestVo = SmMqttRequestVo.builder()
                .token(WebTopicConstant.LICENSE_REQUEST)
                .body(licensePublicVo)
                .timestamp(DateUtil.localDateTimeToStr(LocalDateTime.now()))
                .build();
        String topicName = WebTopicConstant.GATEWAY_REQUEST_PREFIX + WebTopicConstant.LICENSE_REQUEST + deviceCode;
        WebMqttConfig.sendToMqtt(deviceCode, topicName, smMqttRequestVo);

        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 20);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                case 0:
                    return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), LicenseSubscribeVo.class));
                case 1:
                    return ResponseResult.error(SMV2gConstant.StatusFailed);
                case 255:
                    return ResponseResult.error(SMV2gConstant.StatusOther);
            }
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<PlatformSetDto> platformSet(String deviceCode, List<PlatformSetVo> platformSetVos) {
        //返回的对象
        PlatformSetDto result = new PlatformSetDto();
        result.setDeviceCode(deviceCode);
        result.setIssuedStatus(1);
        Integer policyId = TimestampUtil.getUniqueTimestamp();
        //计算权重   命令码
        String keyId = deviceCode + policyId + IEGConstant.Param.GENERAL_PARAMGETSET;

        //创建控制
        GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);

        //下发主题
        MqttTopicUtil.sendPlatformSetTopic(deviceCode, policyId, platformSetVos);

        //开启查询线程等待返回
        JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 10);

        //移除需求
        GatewayDemand.deleteDemand(keyId);

        //成功返回状态
        if (MapUtils.isNotEmpty(asyncTaskResult)) {
            Integer recCode = asyncTaskResult.getInteger("recCode");
            switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                case -1:
                    result.setIssuedStatus(recCode); //下发超时
                    return ResponseResult.error(SMV2gConstant.StatusTimeOut, result);
                case 0:
                    result.setIssuedStatus(recCode);
                    return ResponseResult.ok(result);
                case 1:
                    result.setIssuedStatus(recCode); //下发失败
                    return ResponseResult.error(SMV2gConstant.StatusFailed, result);
                case 255:
                    result.setIssuedStatus(recCode);
                    return ResponseResult.error(SMV2gConstant.StatusOther, result);
            }
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR, result);
    }

    @Override
    public ResponseResult<Map<String, PlatformSetDto>> batchPlatformSet(Map<String, List<PlatformSetVo>> platformSetVoMap) {
        //返回的对象
        Map<String, PlatformSetDto> resultMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(platformSetVoMap)) {
            try {
                //2.创建线程池
                final ExecutorService executor = Executors.newFixedThreadPool(platformSetVoMap.size());
                //3.执行查询网关状态命令，提交到任务里面
                platformSetVoMap.forEach((key, value) -> {
                    executor.submit(() -> {
                        try {
                            PlatformSetDto result = platformSet(key, value).getData();
                            if (result != null) {
                                resultMap.put(key, result);
                            }
                        } catch (Exception e) {
                            log.error("设置网关" + key + "平台策略报错", e);
                        }
                    });
                    // 关闭线程池
                    executor.shutdown();
                    do {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } while (!executor.isTerminated());
                });
            } catch (Exception e) {
                log.error("设置网关平台策略任务下发报错", e);
            }
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<PlatformStatusDto> platformStatus(String deviceCode, List<PlatformStatusVo> platformStatusVos) {
        //返回的对象
        PlatformStatusDto result = new PlatformStatusDto();
        result.setDeviceCode(deviceCode);
        result.setIssuedStatus(1);
        if (CollectionUtils.isNotEmpty(platformStatusVos)) {

            Map<String, String> platformLogoMap = platformStatusVos.stream().collect(Collectors
                    .toMap(PlatformStatusVo::getPlatformLogo, PlatformStatusVo::getProtocolDriver, (k1, k2) -> k1));

            Integer policyId = TimestampUtil.getUniqueTimestamp();
            //计算权重   命令码
            String keyId = deviceCode + policyId + IEGConstant.Param.GENERAL_PARAMGETSET;

            //创建控制
            GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);

            //下发主题
            MqttTopicUtil.sendPlatformStatusTopic(deviceCode, policyId, platformStatusVos);

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 10);

            //移除需求
            GatewayDemand.deleteDemand(keyId);

            //成功返回状态
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                    case -1:
                        result.setIssuedStatus(recCode); //下发超时
                        return ResponseResult.error(SMV2gConstant.StatusTimeOut, result);
                    case 0:
                        result.setIssuedStatus(recCode);
                        if (StringUtil.isNotEmpty(asyncTaskResult.getString("recMsg"))) {
                            GateWayPolicyDto gateWayPolicy = JSON.parseObject(asyncTaskResult.getString("recMsg"), GateWayPolicyDto.class);
                            if (gateWayPolicy != null && StringUtil.isNotEmpty(gateWayPolicy.getPolicyCfg())) {
                                JSONObject policyCfg = JSONObject.parseObject(JSON.toJSONString(gateWayPolicy.getPolicyCfg()));
                                JSONObject apiParams = policyCfg.getJSONObject("apiParams");
                                if (apiParams != null && CollectionUtils.isNotEmpty(apiParams.getJSONArray("comDrivers"))) {
                                    JSONArray comDrivers = apiParams.getJSONArray("comDrivers");
                                    Map<String, List<PlatformStatusDto.Pile>> platformPileMap = Maps.newHashMap();
                                    comDrivers.forEach(driver -> {
                                        JSONObject comDriver = JSON.parseObject(JSON.toJSONString(driver));
                                        JSONArray platforms = comDriver.getJSONArray("platforms");
                                        if (CollectionUtils.isNotEmpty(platforms)) {
                                            platforms.stream().filter(StringUtil::isNotEmpty).forEach(plat -> {
                                                JSONObject platformObj = JSON.parseObject(JSON.toJSONString(plat));
                                                JSONObject platform = platformObj.getJSONObject("paltform");
                                                JSONArray piles = platformObj.getJSONArray("piles");
                                                if (platform != null && StringUtil.isNotEmpty(platform.getString("company_id"))
                                                        && CollectionUtils.isNotEmpty(piles)) {
                                                    String platformLogo = platform.getString("company_id");
                                                    if (platformLogoMap.containsKey(platformLogo)) {
                                                        platformPileMap.put(platformLogo, piles.stream().map(pile -> {
                                                            PlatformStatusDto.Pile pileDto = new PlatformStatusDto.Pile();
                                                            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(pile));
                                                            pileDto.setDevId(jsonObject.getString("devId"));
                                                            pileDto.setFlag(jsonObject.getBoolean("flag"));
                                                            return pileDto;
                                                        }).collect(Collectors.toList()));
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    result.setPlatformPileMap(platformPileMap);
                                }
                            }
                        }
                        return ResponseResult.ok(result);
                    case 1:
                        result.setIssuedStatus(recCode); //下发失败
                        return ResponseResult.error(SMV2gConstant.StatusFailed, result);
                    case 255:
                        result.setIssuedStatus(recCode);
                        return ResponseResult.error(SMV2gConstant.StatusOther, result);
                }
            }
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR, result);
    }

}
