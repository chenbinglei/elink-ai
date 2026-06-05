package com.sunmax.crontab.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.configure.PowerControlResDto;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.device.SiteDeviceDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.dto.system.DataConfigDto;
import com.sunmax.common.dto.system.DataForwardDto;
import com.sunmax.common.dto.system.dynamic.MqttConfigDto;
import com.sunmax.common.dto.system.dynamic.MqttForwardDto;
import com.sunmax.common.enums.HdCodeEnum;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.crontab.PowerControlParamVo;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import com.sunmax.crontab.config.mqtt.MqttClientManager;
import com.sunmax.crontab.dto.HdPowerCtrlDto;
import com.sunmax.crontab.dto.StationAdjustPowerDto;
import com.sunmax.crontab.service.HdDataService;
import com.sunmax.crontab.service.TogetherFeignService;
import com.sunmax.crontab.service.feign.*;
import com.sunmax.crontab.util.ConstantUtil;
import com.sunmax.crontab.util.StrategyUtil;
import com.sunmax.crontab.vo.mqtt.HDSetVo;
import com.sunmax.crontab.vo.mqtt.HDTopicVo;
import com.sunmax.crontab.vo.mqtt.NodeVarParamVo;
import com.sunmax.crontab.vo.mqtt.StationPayLoadVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HdDataServiceImpl implements HdDataService {

    @Resource
    private DeviceService deviceService;

    @Resource
    private SystemService systemService;

    @Resource
    private TogetherService togetherService;

    @Resource
    private ProtocolService protocolService;

    @Resource
    private ConfigService configService;

    @Resource
    private TogetherFeignService togetherFeignService;

    @Override
    public void stationTask() {
        //获取所有站点转发数据
        List<DataForwardDto> dataForwardList = systemService.getDataForwardList(1, 1).getData();
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            //获取全部站点数据配置
            List<DataConfigDto> dataConfigList = dataForwardList.stream().flatMap(d -> d.getDataConfigList().stream()).collect(Collectors.toList());

            //获取充电站采集数据
            Map<String, StationPayLoadVo> chgStatInfoMap = Maps.newHashMap();
            List<DataConfigDto> chargeStationList = dataConfigList.stream().filter(d -> StringUtil.isNotEmpty(d.getDynamicMqttConfigs())
                            && d.getDynamicMqttConfigs().getIdentifier().contains(HdCodeEnum.CHG_STAT_INFO_ACQ.getCode()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(chargeStationList)) {
                Set<String> siteIds = chargeStationList.stream().map(DataConfigDto::getSiteId).collect(Collectors.toSet());
                //获取充电站采集信息
                chgStatInfoMap = this.getChgStatInfoAcq(siteIds);
            }
            for (DataForwardDto dataForward : dataForwardList) {
                MqttForwardDto mqttForward = JSON.parseObject(dataForward.getDynamicFields(), MqttForwardDto.class);
                String clientId = mqttForward.getClientId();
                String topic = FileUtil.SLASH + mqttForward.getVendor() + FileUtil.SLASH + mqttForward.getGwSn() + "/service";

                //推送充电站信息
                List<DataConfigDto> siteConfigList = dataForward.getDataConfigList().stream().filter(d -> StringUtil.isNotEmpty(d.getDynamicMqttConfigs())
                                && d.getDynamicMqttConfigs().getIdentifier().contains(HdCodeEnum.CHG_STAT_INFO_ACQ.getCode()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(siteConfigList)) {
                    for (DataConfigDto dataConfig : siteConfigList) {
                        HDTopicVo result = new HDTopicVo();
                        MqttConfigDto mqttConfig = dataConfig.getDynamicMqttConfigs();
                        result.setSn(mqttConfig.getDeviceSn());
//                        result.setMi(String.valueOf(MiUtil.getUniqueTimestamp()));
                        result.setTime(System.currentTimeMillis());
                        result.setIdentifier(HdCodeEnum.CHG_STAT_INFO_ACQ.getCode());
                        if (chgStatInfoMap.containsKey(dataConfig.getSiteId())) {
                            result.setTags(chgStatInfoMap.get(dataConfig.getSiteId()));
                            try {
                                if (MqttClientManager.clients.containsKey(clientId)) {
                                    //推送充电站信息
                                    MqttClientManager.publish(clientId, topic, result);
                                } else {
                                    log.error("找不到对应的客户端id: " + clientId);
                                }
                            } catch (Exception e) {
                                log.error("推送充电站信息失败", e);
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    public ResponseResult<HdPowerCtrlDto> hdPowerCtrl(String clientId, HDSetVo hdSetVo) {
        if (!Objects.equals(hdSetVo.getIdentifier(), HdCodeEnum.POWER_CTL.getCode())) {
            return ResponseResult.paramError("当前只支持功率控制,暂不支持此指令");
        }
        if (hdSetVo.getParams() == null || StringUtil.isEmpty(hdSetVo.getParams().getTarget())) {
            return ResponseResult.paramError("目标值为空,不允许调控");
        }
        //根据客户端id查询数据转发的站点配置数据
        DataForwardDto dataForward = systemService.findDataForwardByClientId(clientId).getData();
        //根据站点设备编号查询站点id
        if (CollectionUtils.isEmpty(dataForward.getDataConfigList())) {
            return ResponseResult.paramError("晟曼平台未配置充电站信息,暂不支持调控");
        }
        Optional<DataConfigDto> optional = dataForward.getDataConfigList().stream().filter(dataConfig -> StringUtil.isNotEmpty(dataConfig.getDynamicMqttConfigs())
                && dataConfig.getDynamicMqttConfigs().getDeviceSn().equals(hdSetVo.getSn())).collect(Collectors.toList()).stream().findFirst();
        if (optional.isPresent()) {
            DataConfigDto dataConfig = optional.get();
            String siteId = dataConfig.getSiteId();
            if (StringUtil.isEmpty(siteId)) {
                return ResponseResult.paramError("晟曼平台未获取到该充电站站点id,暂不支持调控");
            }
            List<StrategyTaskVo> strategyTaskList = togetherService.findStrategyHandTaskList(siteId).getData();
            if (CollectionUtils.isEmpty(strategyTaskList)) {
                return ResponseResult.paramError("晟曼平台未获取到该充电站策略任务数据,暂不支持调控");
            }
            StrategyTaskVo strategyTask = strategyTaskList.get(0);
            //根据多个站点id查询来源类型
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData();
            Integer sourceType;
            if (siteInfoMap.containsKey(siteId) && StringUtil.isNotEmpty(siteInfoMap.get(siteId).getSourceType())) {
                sourceType = siteInfoMap.get(siteId).getSourceType();
            } else {
                sourceType = 1;
            }
            strategyTask.setTargetP(DoubleUtil.getToBigDecimal(new BigDecimal(hdSetVo.getParams().getTarget().toString()).multiply(new BigDecimal("0.001"))));
            strategyTask.setControlTime(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().getEpochSecond());
            strategyTask.setValidTime(31536000);
            //功率控制策略计算
            List<PilePowerCtrlVo> pilePowerCtrlVos = StrategyUtil.controlPowerCalculate(strategyTask);
            //对需要调的电桩功率进行调控
            if (CollectionUtils.isNotEmpty(pilePowerCtrlVos)) {
                //入参，出参参数
                String params = null;
                String result = null;
                //晟曼平台接入控制
                if (sourceType == 1) {
                    params = JSON.toJSONString(pilePowerCtrlVos);
                    List<PileResultDto> pileResultList = protocolService.batchPilePowerCtrlResult(pilePowerCtrlVos).getData();
                    result = JSON.toJSONString(pileResultList);
                    if (CollectionUtils.isEmpty(pileResultList)) {
                        return ResponseResult.paramError("未获取到调控结果");
                    }
                    List<PileResultDto> failList = pileResultList.stream().filter(pileResult -> pileResult.getResult() != 0).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(failList)) {
                        return ResponseResult.paramError("调控失败,失败原因:" + failList.stream().map(p -> {
                            //执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
                            switch (p.getResult()) {
                                case -1:
                                    return "桩编号:" + p.getPileCode() + "枪编号:" + p.getGunCode() + "执行超时";
                                case 1:
                                    return "桩编号:" + p.getPileCode() + "枪编号:" + p.getGunCode() + "执行失败";
                                case 255:
                                    return "桩编号:" + p.getPileCode() + "枪编号:" + p.getGunCode() + "其他原因";
                                case 500:
                                    return "桩编号:" + p.getPileCode() + "枪编号:" + p.getGunCode() + "平台处理报错";
                                default:
                                    return "桩编号:" + p.getPileCode() + "枪编号:" + p.getGunCode() + "未知错误";
                            }
                        }).collect(Collectors.joining(FileUtil.COMMA)));
                    }
                }
                //城市充电接入控制
                if (sourceType == 2) {
                    List<PowerControlParamVo> powerControlVos = pilePowerCtrlVos.stream().map(pilePowerCtrlVo -> {
                        PowerControlParamVo pilePowerCtrl = new PowerControlParamVo();
                        pilePowerCtrl.setPileCode(pilePowerCtrlVo.getPileCode());
                        if (StringUtil.isNotEmpty(pilePowerCtrlVo.getGunCode())) {
                            pilePowerCtrl.setGunCode(Integer.parseInt(pilePowerCtrlVo.getGunCode()));
                        }
                        pilePowerCtrl.setOutPower(pilePowerCtrlVo.getOutPower());
                        return pilePowerCtrl;
                    }).collect(Collectors.toList());
                    params = JSON.toJSONString(powerControlVos);
                    List<PowerControlResDto> pileResultList = configService.interflowBatchPowerControl(powerControlVos).getData();
                    result = JSON.toJSONString(pileResultList);
                    if (CollectionUtils.isEmpty(pileResultList)) {
                        return ResponseResult.paramError("未获取到调控结果");
                    }
                    List<PowerControlResDto> failList = pileResultList.stream().filter(pileResult -> pileResult.getStatus() != 0).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(failList)) {
                        return ResponseResult.paramError("调控失败,失败原因:" + failList.stream().map(p -> {
                            //返回结果 0-接收下发 1-下发失败
                            switch (p.getStatus()) {
                                case 0:
                                    return "桩编号:" + p.getPileCode() + "枪编号:" + p.getGunCode() + "下发成功";
                                case 1:
                                    return "桩编号:" + p.getPileCode() + "枪编号:" + p.getGunCode() + "下发失败";
                                default:
                                    return "桩编号:" + p.getPileCode() + "枪编号:" + p.getGunCode() + "未知错误";
                            }
                        }).collect(Collectors.joining(FileUtil.COMMA)));
                    }
                }
                return ResponseResult.ok(HdPowerCtrlDto.builder().strategyTask(strategyTask).sourceType(sourceType).params(params).result(result).build());
            } else {
                return ResponseResult.paramError("目前不需要调控");
            }
        }
        return ResponseResult.paramError("晟曼平台未获取到该充电站配置数据,暂不支持调控");
    }

    private Map<String, StationPayLoadVo> getChgStatInfoAcq(Set<String> siteIds) {
        //定义充电站采集信息
        Map<String, StationPayLoadVo> resultMap = Maps.newHashMap();
        //根据多个站点id获取站点设备数据
        List<SiteDeviceDto> siteDeviceList = deviceService.getSiteDeviceList(siteIds).getData();
        //查询所有进行中的自动策略数据
        Map<String, StrategyTaskVo> siteStrategyMap = togetherService.findStrategyHandTaskList(null)
                .getData().stream().collect(Collectors.toMap(StrategyTaskVo::getSiteId, a -> a, (k1, k2) -> k1));
        if (CollectionUtils.isNotEmpty(siteDeviceList)) {
            //获取常量字段
            List<String> nodeCodes = new ArrayList<>(ConstantUtil.getConstantValues(NodeVarParamVo.class));
            siteDeviceList.forEach(site -> {
                StationPayLoadVo stationPayLoadVo = new StationPayLoadVo();
                //获取站点系统变量值
                this.getStationPayLoad(site.getId(), nodeCodes, stationPayLoadVo);
                //实时总有功功率
                if (StringUtil.isEmpty(stationPayLoadVo.getSignedActivePower())) {
                    stationPayLoadVo.setSignedActivePower(0.0);
                }
                //获取该站点下面所有电桩策略数据
                if (siteStrategyMap.containsKey(site.getId())) {
                    StrategyTaskVo strategyTaskVo = siteStrategyMap.get(site.getId());
                    StationAdjustPowerDto stationAdjustPower = StrategyUtil.getStationAdjustPower(strategyTaskVo.getStrategyPileMap(), BigDecimal.ZERO);
                    stationPayLoadVo.setMaxChargeUp(DoubleUtil.getToDouble(stationAdjustPower.getChargeUpAdjustP().doubleValue() * 1000));
                    stationPayLoadVo.setMaxChargeDown(DoubleUtil.getToDouble(stationAdjustPower.getChargeDownAdjustP().doubleValue() * 1000));
                    stationPayLoadVo.setMaxDischargeUp(DoubleUtil.getToDouble(stationAdjustPower.getDischargeUpAdjustP().doubleValue() * 1000));
                    stationPayLoadVo.setMaxDischargeDown(DoubleUtil.getToDouble(stationAdjustPower.getDischargeDownAdjustP().doubleValue() * 1000));
                }
                resultMap.put(site.getId(), stationPayLoadVo);
            });
        }
        return resultMap;
    }

    //获取站点电表相关系统变量数据
    private void getStationPayLoad(String siteId, List<String> nodeCodes, StationPayLoadVo result) {
        Map<String, Double> resultMap = Maps.newHashMap();
        Map<String, Map<String, LocalCacheDto>> localCacheMap = togetherFeignService.findNodeCacheByVarCodes(nodeCodes, Collections.singletonList(siteId)).getData()
                .entrySet().stream().filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (Map.Entry<String, Map<String, LocalCacheDto>> entry : localCacheMap.entrySet()) {
            String key = entry.getKey();
            Map<String, LocalCacheDto> value = entry.getValue();
            if (value.containsKey(siteId) && value.get(siteId) != null && value.get(siteId).getResultValue() != null) {
                resultMap.put(key, DoubleUtil.getToDouble(value.get(siteId).getResultValue(), 5));
            }
        }
        try {
            BeanUtils.populate(result, resultMap);
        } catch (Exception e) {
            log.error("数据转换错误", e);
        }
    }

}
