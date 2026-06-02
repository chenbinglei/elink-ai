package com.sunmax.protocol.runner;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DevicePointDto;
import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.model.ChannelModel;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.LocalParamVo;
import com.sunmax.protocol.dto.platform.PlatformDataDto;
import com.sunmax.protocol.service.feign.DeviceService;
import com.sunmax.protocol.service.feign.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 云网关相关数据
 */
@Configuration
@Slf4j
public class ProtocolRunner implements ApplicationRunner {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SystemService systemService;

    /**
     * 设备点号缓存数据 设备id -> 设备点号信息
     */
    public static Map<String, DevicePointDto> devicePointMap = Maps.newConcurrentMap();

    /**
     * 设备云网关缓存数据 设备序列号 -> 通信状态
     */
    public static Map<String, Integer> gatewayRealMap = Maps.newConcurrentMap();

    /**
     * 平台V2G放电控制协议 平台运营商id -> V2G充放电协议数据
     */
    public static Map<String, PlatformDataDto> platformDataMap = Maps.newConcurrentMap();

    /**
     * 设备事件缓存数据
     */
    public static Map<String, Set<String>> deviceEventMap = Maps.newConcurrentMap();

    /**
     * 设备模型缓存数据
     */
    public static Map<String, String> deviceModelMap = Maps.newConcurrentMap();

    @Override
    public void run(ApplicationArguments args) {
        //初始化云网关数据
        initCloudGatewayData();
        //初始化平台V2G放电控制协议数据
        initPlatformV2GData();
        //初始化设备事件
        initDeviceEvent();
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void initScheduled() {
        initCloudGatewayData();
        initPlatformV2GData();
        initDeviceEvent();
    }

    public static PlatformDataDto getPlatformData(String pileCode) {
        Optional<PlatformDataDto> optional = platformDataMap.values().stream().filter(d -> d.getPileCodes().contains(pileCode)).findFirst();
        return optional.orElse(null);
    }

    /**
     * 初始化云网关数据
     */
    private void initCloudGatewayData() {
        //1.初始化模型下面的设备点号数据
        List<String> modelIds = Arrays.asList(LocalParamVo.YJY_KC_MODEL_ID, LocalParamVo.YJY_CC_MODEL_ID, LocalParamVo.YJY_SC_MODEL_ID, LocalParamVo.YJY_V2G_MODEL_ID);
        devicePointMap = deviceService.findAllDevicePointByModelIds(Sets.newHashSet(modelIds)).getData();
//        log.info("初始化设备点号数据:{}", devicePointMap);
        //2.初始化设备云网关数据
        List<DeviceBasicInfoDto> deviceList = deviceService.findAllDeviceInfoByTypeIds(Collections.singleton("32")).getData().values().stream()
                .filter(s -> StringUtil.isNotEmpty(s.getDeviceNumber())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceList)) {
            deviceList.forEach(device -> {
                String deviceNumber = device.getDeviceNumber();
                DeviceModel deviceModel = RedisDeviceUtil.getDevice(deviceNumber);
                if (deviceModel != null) {
                    //离线 默认给在线
                    if (StringUtil.isNotEmpty(deviceModel.getTxStatus()) && deviceModel.getTxStatus() == 88) {
                        deviceModel.setTxStatus(1);
                    }
                    RedisDeviceUtil.setDevice(deviceNumber, deviceModel);

                    if (StringUtil.isNotEmpty(deviceModel.getTxStatus())) {
                        gatewayRealMap.put(deviceNumber, deviceModel.getTxStatus());
                    }
                    RedisGeneralUtil.executePile(deviceNumber, () -> {
                        GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(deviceNumber);
                        if (gatewayRealModel != null) {
                            //更新网关状态
                            if (StringUtil.isEmpty(gatewayRealModel.getTerminalCode())) {
                                gatewayRealModel.setTerminalCode(deviceNumber);
                            }
                            gatewayRealModel.setDeviceStatus(1);
                            gatewayRealModel.setLastSendTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));

                            GatewayRealModel.ChannelRealModel channelRealModel;
                            if (gatewayRealModel.getChannelRealMap().containsKey(KeyUtil.MQTT)) {
                                channelRealModel = gatewayRealModel.getChannelRealMap().get(KeyUtil.MQTT);
                                channelRealModel.setTxStatus(0);
                            } else {
                                channelRealModel = new GatewayRealModel.ChannelRealModel();
                                channelRealModel.setProtocolType(KeyUtil.MQTT);
                                channelRealModel.setAccessProtocol(KeyUtil.MQTT);
                                channelRealModel.setTxStatus(0);
                            }

                            if (MapUtils.isNotEmpty(deviceModel.getChannelMap()) && deviceModel.getChannelMap().containsKey(KeyUtil.MQTT)) {
                                ChannelModel channelModel = deviceModel.getChannelMap().get(KeyUtil.MQTT);
                                channelRealModel.setIp(channelModel.getIp());
                                channelRealModel.setPort(channelModel.getPort());
                                channelRealModel.setPointTableMap(channelModel.getPointTableMap());
                            }

                            gatewayRealModel.getChannelRealMap().put(KeyUtil.MQTT, channelRealModel);

                            RedisGeneralUtil.setGatewayRealModel(deviceNumber, gatewayRealModel);
                        }
                    });
                }
            });
        }

//        log.info("初始化网关数据:{}", gatewayRealMap);
    }

    /**
     * 初始化平台V2G放电控制协议数据
     */
    private void initPlatformV2GData() {
        //根据协议编号查询数据转发通道数据
        List<PlatformDataForwardDto> platformDataList = systemService.getPlatformDataForwardList(Collections.singleton(ProtocolEnum.PLATFORM.getCode())).getData();
        if (CollectionUtils.isNotEmpty(platformDataList)) {
            //根据多个站点id查询电桩编号数据
            List<String> siteIds = platformDataList.stream().filter(p -> CollectionUtils.isNotEmpty(p.getSiteOperateList()))
                    .flatMap(p -> p.getSiteOperateList().stream().map(SiteOperateDto::getSiteId)
                            .filter(StringUtil::isNotEmpty)).collect(Collectors.toList());
            Map<String, List<DeviceBasicInfoDto>> siteDeviceMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1).getData();

            platformDataList.forEach(platformData -> {
                PlatformDataDto result = new PlatformDataDto();
                result.setDataForwardId(platformData.getId());
                result.setUrl(platformData.getAddress());
                result.setPlatformId(platformData.getPlatformId());
                result.setDataSecret(platformData.getDataSecret());
                result.setDataSecretIv(platformData.getDataSecretIv());
                Set<String> pileCodes = Sets.newHashSet();
                if (CollectionUtils.isNotEmpty(platformData.getSiteOperateList())) {
                    platformData.getSiteOperateList().forEach(siteOperate -> {
                        List<DeviceBasicInfoDto> deviceList = siteDeviceMap.get(siteOperate.getSiteId());
                        if (CollectionUtils.isNotEmpty(deviceList)) {
                            pileCodes.addAll(deviceList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toSet()));
                        }
                    });
                }
                result.setPileCodes(pileCodes);
                platformDataMap.put(platformData.getPlatformId(), result);
            });
        }
//        log.info("初始化平台V2G协议数据:{}", platformDataMap);
    }

    private void initDeviceEvent() {
        //查询设备下的事件数据
        deviceEventMap = deviceService.findDeviceEventIds(0).getData();
        //查询所有设备的模型id
        deviceModelMap = deviceService.findDeviceModelIds(null).getData();
    }

}
