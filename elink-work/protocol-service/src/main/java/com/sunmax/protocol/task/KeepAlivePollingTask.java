package com.sunmax.protocol.task;

import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.constant.IEGConstant;
import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.model.ChannelModel;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.SunMaxUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.protocol.mqtt.web.common.IEGTopicVo;
import com.sunmax.protocol.config.emqx.web.WebMqttConfig;
import com.sunmax.protocol.dao.AlarmRecordDao;
import com.sunmax.protocol.entity.AlarmRecordEntity;
import com.sunmax.protocol.runner.ProtocolRunner;
import com.sunmax.protocol.util.PileRecordUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xt
 * @brief 能量网关保活线程
 * @date 2021/8/31 16:47
 */

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class KeepAlivePollingTask {

    /**
     * 充电桩心跳时间
     */
    public static Map<String, LocalDateTime> heartBeatMap = Maps.newHashMap();

    @Autowired
    private AlarmRecordDao alarmRecordDao;

    @Scheduled(fixedRate = 30000)
    public void run() {
        //生成网关告警
        List<AlarmRecordEntity> alarmRecordList = Lists.newArrayList();
        //校验网关设备状态
        for (String terminalKey : RedisUtil.keys(KeyUtil.GENERAL_GW_PREFIX + FileUtil.ASTERISK)) {
            String terminalCode = terminalKey.replace(KeyUtil.GENERAL_GW_PREFIX, FileUtil.separator);
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
            if (gatewayRealModel != null) {
                if (StringUtil.isEmpty(gatewayRealModel.getDeviceStatus()) || gatewayRealModel.getDeviceStatus() == 0 || gatewayRealModel.getDeviceStatus() == 88) {
                    continue;
                }
                //TODO 过滤云网关
                if (ProtocolRunner.gatewayRealMap.containsKey(terminalCode)) {
                    continue;
                }
                LocalDateTime curTime = LocalDateTime.now();
                //更新缓存状态
                if (StringUtil.isNotEmpty(gatewayRealModel.getLastBeatTime())) {
                    LocalDateTime lastBeatTime = DateUtil.strToLocalDateTime(gatewayRealModel.getLastBeatTime());
                    if (DateUtil.compareDiffBetweenMinutes(lastBeatTime, curTime) > 3) { //超过三分钟 判断离线
                        //更新网关通用设备状态以及缓存子设备状态
                        gatewayRealModel.setDeviceStatus(88);
                        if (gatewayRealModel.getChannelRealMap().containsKey(KeyUtil.MQTT)) {
                            GatewayRealModel.ChannelRealModel channelRealModel = gatewayRealModel.getChannelRealMap().get(KeyUtil.MQTT);
                            channelRealModel.setTxStatus(1);
                            gatewayRealModel.getChannelRealMap().put(KeyUtil.MQTT, channelRealModel);
                        }
                        RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
                        //更新平台设备状态
                        updatePlatformDeviceStatus(terminalCode, 88);
                        //生成网关离线记录
                        alarmRecordList.add(AlarmRecordEntity.builder()
                                .deviceCode(terminalCode)
                                .faultCode(65534)
                                .featureCode((long) IEGConstant.P_SMIGP)
                                .eventName("网关离线")
                                .eventLevel(5)
                                .alarmStatus(0)
                                .ignoreStatus(0)
                                .alarmType(1).build());
                        //更新网关下面的子设备状态
                        for (Map.Entry<String, Integer> subDevice : gatewayRealModel.getSubDeviceRealMap().entrySet()) {
                            if (subDevice.getValue() != 88) {
                                gatewayRealModel.getSubDeviceRealMap().put(subDevice.getKey(), 88);
                                //更新通用缓存网关子设备状态
                                pileOffLineMqtt(subDevice.getKey(), alarmRecordList);
                                //更新平台网关子设备设备状态
                                updatePlatformDeviceStatus(subDevice.getKey(), 88);
                            }
                        }
                    }
                }
                //保活消息
                if (StringUtil.isNotEmpty(gatewayRealModel.getLastSendTime())) {
                    LocalDateTime lastSendTime = DateUtil.strToLocalDateTime(gatewayRealModel.getLastSendTime());
                    if (DateUtil.compareDiffBetweenSecond(lastSendTime, curTime) > 50) {//距离上次发送报文大于50s 发送保活
                        //下发设备心跳参数
                        IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                                .mid("0006")
                                .param(null)
                                .type(IEGConstant.Type.EVENT_HEARTBEAT)
                                .timestamp(SunMaxUtil.getSysTime())
                                .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                                .build();
                        String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.DEVICE_RESPONSE;
                        WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
                    }
                }
            }
        }

        //校验电桩直连设备
        for (String pileKey : RedisUtil.keys(KeyUtil.GENERAL_PILE_PREFIX + FileUtil.ASTERISK)) {
            String pileCode = pileKey.replace(KeyUtil.GENERAL_PILE_PREFIX, FileUtil.separator);
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            if (pileRealModel != null) {
                //外网接入都是网关下面的桩 跳过
                if (StringUtil.isNotEmpty(pileRealModel.getMessageType()) && pileRealModel.getMessageType() == 1) {
                    continue;
                }
                if (StringUtil.isEmpty(pileRealModel.getWorkStatus()) || pileRealModel.getWorkStatus() == 0 || pileRealModel.getWorkStatus() == 88) {
                    continue;
                }
                LocalDateTime curTime = LocalDateTime.now();
                //更新缓存状态
                if (heartBeatMap.containsKey(pileCode)) {
                    LocalDateTime lastBeatTime = heartBeatMap.get(pileCode);
                    if (StringUtil.isNotEmpty(lastBeatTime) && DateUtil.compareDiffBetweenMinutes(lastBeatTime, curTime) > 3) { //超过三分钟 判断离线
                        //更新电桩离线状态
                        pileOffLineMqtt(pileCode, alarmRecordList);
                        //更新平台设备状态
                        updatePlatformDeviceStatus(pileCode, 88);
                    }
                } else {
                    //如果心跳缓存为空 默认给个心跳缓存 以免服务重启时丢失数据
                    heartBeatMap.put(pileCode, LocalDateTime.now());
                }
                //直连桩暂无保活消息
            }
        }

        //生成离线告警存库
        if (CollectionUtils.isNotEmpty(alarmRecordList)) {
            //根据设备编号和故障码查询未修复的告警记录
            Set<String> deviceCodes = alarmRecordList.stream().map(AlarmRecordEntity::getDeviceCode).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            Map<String, AlarmRecordEntity> alarmRecordMap = alarmRecordDao.findAllByDeviceCodeInAndFaultCodeAndAlarmStatus(deviceCodes, 65535, 0)
                    .stream().collect(Collectors.toMap(AlarmRecordEntity::getDeviceCode, Function.identity(), (k1, k2) -> k1));
            alarmRecordList = alarmRecordList.stream().filter(alarmRecord -> !alarmRecordMap.containsKey(alarmRecord.getDeviceCode()))
                    .peek(alarmRecord -> alarmRecord.setCreateTime(LocalDateTime.now())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(alarmRecordList)) {
                alarmRecordDao.saveAll(alarmRecordList);
            }
        }

    }

    //更新网关下面的电桩状态
    public static void pileOffLineMqtt(String pileCode, List<AlarmRecordEntity> alarmRecordList) {
        RedisGeneralUtil.executePile(pileCode, () -> {
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getPileCode())) {
                pileRealModel.setWorkStatus(88); //离线
                pileRealModel.setOriginalStatus(88); //离线
                pileRealModel.setOfflineTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> {
                    gunRealModel.setGunStatus(88); //离线
                    gunRealModel.setGunOriginalStatus(88); //离线
                    pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                });
                RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                //生成电桩离线记录
                alarmRecordList.add(AlarmRecordEntity.builder()
                        .deviceCode(pileCode)
                        .faultCode(65534)
                        .featureCode(pileRealModel.getFeatureCode())
                        .eventName("电桩离线")
                        .eventLevel(5)
                        .alarmStatus(0)
                        .ignoreStatus(0)
                        .alarmType(2).build());
                //更新电桩订单充电中的订单状态
                PileRecordUtil.updateOrderChargingStatus(pileCode, 4);
            }
        });
    }

    //更新平台设备状态
    public static void updatePlatformDeviceStatus(String deviceCode, Integer status) {
        DeviceModel device = RedisDeviceUtil.getDevice(deviceCode);
        if (device != null && StringUtil.isNotEmpty(device.getDeviceNumber()) && !Objects.equals(device.getTxStatus(), 0)) {
            device.setTxStatus(status);
            if (MapUtils.isNotEmpty(device.getChannelMap()) && device.getChannelMap().containsKey(KeyUtil.MQTT)) {
                ChannelModel channelModel = device.getChannelMap().get(KeyUtil.MQTT);
                channelModel.setTxStatus(1);
                device.getChannelMap().put(KeyUtil.MQTT, channelModel);
            }
            RedisDeviceUtil.setDevice(deviceCode, device);
        }
    }

}
