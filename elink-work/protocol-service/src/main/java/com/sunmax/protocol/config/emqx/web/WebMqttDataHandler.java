package com.sunmax.protocol.config.emqx.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.constant.IEGConstant;
import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.dto.device.PileFaultDto;
import com.sunmax.common.dto.protocol.GateWayControlDto;
import com.sunmax.common.dto.protocol.GateWayPolicyDto;
import com.sunmax.common.dto.protocol.StationInfoDto;
import com.sunmax.common.dto.protocol.mqtt.UpdateInfoDto;
import com.sunmax.common.dto.protocol.mqtt.web.CtrlBoardInfo;
import com.sunmax.common.dto.protocol.mqtt.web.falut.ChargerRunFault;
import com.sunmax.common.dto.protocol.mqtt.web.falut.ElecModuleFault;
import com.sunmax.common.dto.protocol.mqtt.web.falut.ItfFault;
import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import com.sunmax.common.model.ChannelModel;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.PointTableModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.*;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.PlatformLogoVo;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.device.DeviceBatchUpdateVo;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.mqtt.web.command.CmdToPoFwResSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.command.CmdToPoUpdateSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.IEGTopicVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.ResponseSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.data.*;
import com.sunmax.common.vo.protocol.mqtt.web.from.*;
import com.sunmax.common.vo.protocol.mqtt.web.request.*;
import com.sunmax.common.vo.protocol.mqtt.web.response.*;
import com.sunmax.protocol.constant.SMV2gConstant;
import com.sunmax.protocol.dao.*;
import com.sunmax.protocol.demand.GatewayDemand;
import com.sunmax.protocol.demand.PileDemand;
import com.sunmax.protocol.entity.*;
import com.sunmax.protocol.model.GatewayDemandModel;
import com.sunmax.protocol.model.PileDemandModel;
import com.sunmax.protocol.service.PileCtrlService;
import com.sunmax.protocol.service.feign.DeviceService;
import com.sunmax.protocol.service.feign.TogetherService;
import com.sunmax.protocol.task.DeviceEventDataSink;
import com.sunmax.protocol.task.KeepAlivePollingTask;
import com.sunmax.protocol.util.FirmwareUtil;
import com.sunmax.protocol.util.PileRecordUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sunmax.protocol.service.impl.DispatchServiceImpl.stationInfoMap;

@Slf4j
@Configuration
public class WebMqttDataHandler {

    /**
     * 策略响应映射表 电桩编号+枪编号 -> 上次时间
     */
    private static final Map<String, LocalDateTime> strategyMap = Maps.newHashMap();


    private static AlarmRecordDao alarmRecordDao;
    private static OrderRecordDao orderRecordDao;
    private static TimeFrameQDao timeFrameQDao;
    private static ChargeTariffRecordDao chargeTariffRecordDao;
    private static TogetherService togetherService;

    private static DeviceService deviceService;


    @PostConstruct
    public void init() {
        alarmRecordDao = SpringBeanUtil.getBean(AlarmRecordDao.class);
        orderRecordDao = SpringBeanUtil.getBean(OrderRecordDao.class);
        timeFrameQDao = SpringBeanUtil.getBean(TimeFrameQDao.class);
        chargeTariffRecordDao = SpringBeanUtil.getBean(ChargeTariffRecordDao.class);
        togetherService = SpringBeanUtil.getBean(TogetherService.class);
        deviceService = SpringBeanUtil.getBean(DeviceService.class);
    }

    public static void iegLinkUp(EventLinkUpSubscribeVo eventLinkUpVo) {
        try {
            String terminalCode = eventLinkUpVo.getDevSN();
            RedisGeneralUtil.executeGateway(terminalCode, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
                if (gatewayRealModel != null) {
                    //更新网关状态
                    if (StringUtil.isEmpty(gatewayRealModel.getTerminalCode())) {
                        gatewayRealModel.setTerminalCode(terminalCode);
                    }
                    gatewayRealModel.setDeviceStatus(1);
                    gatewayRealModel.setLastSendTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    GatewayRealModel.ChannelRealModel channelRealModel;
                    if (gatewayRealModel.getChannelRealMap().containsKey(KeyUtil.MQTT)) {
                        channelRealModel = gatewayRealModel.getChannelRealMap().get(KeyUtil.MQTT);
                    } else {
                        channelRealModel = new GatewayRealModel.ChannelRealModel();
                        channelRealModel.setProtocolType(KeyUtil.MQTT);
                        channelRealModel.setAccessProtocol(KeyUtil.MQTT);
                    }
                    channelRealModel.setIp(eventLinkUpVo.getIp());
                    channelRealModel.setPort(eventLinkUpVo.getHarderVer());
                    channelRealModel.setTxStatus(0);
                    gatewayRealModel.getChannelRealMap().put(KeyUtil.MQTT, channelRealModel);
                    RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
                }
            });
            if (StringUtil.isNotEmpty(terminalCode)) {
                //查询是否存在未修复的离线告警,如果存在则修复
                List<AlarmRecordEntity> alarmRecordList = alarmRecordDao.findAllByDeviceCodeAndFaultCodeInAndAlarmStatus(terminalCode,
                        Collections.singleton(65534), 0);
                if (CollectionUtils.isNotEmpty(alarmRecordList)) {
                    PileRecordUtil.updateAlarmStatus(alarmRecordList, alarmRecordDao);
                }
            }
            //上线应答主题参数
            IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                    .mid("1900")
                    .param(null) //参数暂时给空
                    .type(IEGConstant.Type.EVENT_LINK_UP)
                    .timestamp(SunMaxUtil.getSysTime())
                    .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                    .build();
            String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.DEVICE_RESPONSE;
            WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
        } catch (Exception e) {
            log.error("网关上线应答响应数据失败", e);
        }
    }

    public static void iegLinkDown(EventLinkDownSubscribeVo eventLinkDownVo) {
        //网关下线处理
        try {
            String terminalCode = eventLinkDownVo.getDevSN();
            //告警记录列表
            List<AlarmRecordEntity> alarmRecordList = Lists.newArrayList();

            List<AlarmRecordEntity> finalAlarmRecordList = alarmRecordList;
            RedisGeneralUtil.executeGateway(terminalCode, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
                if (gatewayRealModel != null) {
                    //更新网关状态
                    if (StringUtil.isEmpty(gatewayRealModel.getTerminalCode())) {
                        gatewayRealModel.setTerminalCode(terminalCode);
                    }
                    //更新网关通用设备状态以及缓存子设备状态
                    gatewayRealModel.setDeviceStatus(88);
                    if (gatewayRealModel.getChannelRealMap().containsKey(KeyUtil.MQTT)) {
                        GatewayRealModel.ChannelRealModel channelRealModel = gatewayRealModel.getChannelRealMap().get(KeyUtil.MQTT);
                        channelRealModel.setTxStatus(1);
                        gatewayRealModel.getChannelRealMap().put(KeyUtil.MQTT, channelRealModel);
                    }
                    RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);

                    //更新平台网关设备状态
                    KeepAlivePollingTask.updatePlatformDeviceStatus(terminalCode, 88);

                    //生成网关离线记录
                    finalAlarmRecordList.add(AlarmRecordEntity.builder()
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
                            KeepAlivePollingTask.pileOffLineMqtt(subDevice.getKey(), finalAlarmRecordList);
                            //更新平台网关子设备设备状态
                            KeepAlivePollingTask.updatePlatformDeviceStatus(subDevice.getKey(), 88);
                        }
                    }

                }
            });
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
        } catch (Exception e) {
            log.error("网关下线处理异常失败", e);
        }
    }

    /**
     * 更新子设备
     *
     * @param terminalCode    网关设备编号
     * @param cmdToPoUpdateVo 更新子设备数据
     */
    public static void pilesLinkUp(String terminalCode, CmdToPoUpdateSubscribeVo cmdToPoUpdateVo) {
        try {
            RedisGeneralUtil.executePile(terminalCode, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
                if (gatewayRealModel != null && CollectionUtils.isNotEmpty(cmdToPoUpdateVo.getDeviceStatuses())) {
                    //更新网关子设备状态
                    if (MapUtils.isEmpty(gatewayRealModel.getSubDeviceRealMap())) {
                        gatewayRealModel.setSubDeviceRealMap(Maps.newConcurrentMap());
                    }
                    Map<String, Integer> subDeviceRealMap = gatewayRealModel.getSubDeviceRealMap();
                    cmdToPoUpdateVo.getDeviceStatuses().forEach(subDevice -> {
                        String deviceCode = subDevice.getDeviceId();
                        if (StringUtil.isNotEmpty(deviceCode)) {
                            int workState;
                            if (Objects.equals(subDevice.getStatus(), StaticParamVo.ONLINE)) { //在线
                                workState = 1;
                            } else if (Objects.equals(subDevice.getStatus(), StaticParamVo.OFFLINE)) { //离线
                                workState = 88;
                            } else {
                                workState = -1;
                            }
                            subDeviceRealMap.put(deviceCode, workState);
                            RedisGeneralUtil.executePile(deviceCode, () -> {
                                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(deviceCode);
                                if (pileRealModel != null) {
                                    //更新电桩和枪的状态
                                    pileRealModel.setTerminalCode(terminalCode);
                                    pileRealModel.setWorkStatus(workState);
                                    pileRealModel.setFeatureCode(subDevice.getFeatureCode());
                                    pileRealModel.setMessageType(1); //外网
                                    //电桩离线 增加离线时间
                                    if (workState == 88) {
                                        pileRealModel.setOfflineTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                                    }
                                    if (MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                                        pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> {
                                            if (workState == 1) {
                                                gunRealModel.setGunStatus(0);
                                            } else {
                                                gunRealModel.setGunStatus(workState);
                                            }
                                            pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                                        });
                                    }
                                    RedisGeneralUtil.setPileRealModel(deviceCode, pileRealModel);
                                    //查询是否存在未修复的离线告警
                                    List<AlarmRecordEntity> alarmRecordList = alarmRecordDao.findAllByDeviceCodeAndFaultCodeInAndAlarmStatus(deviceCode, Collections.singleton(65534), 0);
                                    if (workState == 1) { //在线 修复未修复的电桩
                                        //如果存在则修复
                                        if (CollectionUtils.isNotEmpty(alarmRecordList)) {
                                            PileRecordUtil.updateAlarmStatus(alarmRecordList, alarmRecordDao);
                                        }
                                        //桩重新上线 如果是复位的订单 把订单状态改为订单挂起
                                        if (subDevice.getReason() == 1) {
                                            PileRecordUtil.updateOrderChargingStatus(deviceCode, 4);
                                        }
                                    } else if (workState == 88) { //离线 生成电桩告警
                                        if (CollectionUtils.isEmpty(alarmRecordList)) {
                                            //生成电桩离线记录
                                            AlarmRecordEntity alarmRecord = AlarmRecordEntity.builder()
                                                    .deviceCode(deviceCode)
                                                    .faultCode(65534)
                                                    .featureCode(pileRealModel.getFeatureCode())
                                                    .eventName("电桩离线")
                                                    .eventLevel(5)
                                                    .alarmStatus(0)
                                                    .ignoreStatus(0)
                                                    .alarmType(2).build();
                                            alarmRecord.setCreateTime(LocalDateTime.now());
                                            alarmRecordDao.save(alarmRecord);
                                        }
                                        //把充电中的订单状态改为订单挂起
                                        PileRecordUtil.updateOrderChargingStatus(deviceCode, 4);
                                    }
                                }
                            });
                        }
                    });
                    //更新网关通用实时数据
                    gatewayRealModel.setSubDeviceRealMap(subDeviceRealMap);
                    RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
                }
            });
        } catch (Exception e) {
            log.error("更新子设备失败", e);
        }
    }

    /**
     * 费率响应处理
     *
     * @param terminalCode   终端编号
     * @param eventRateReqVo 费率请求数据
     */
    public static void rateReq(String terminalCode, EventRateReqSubscribeVo eventRateReqVo) {
        //费率响应处理
        try {
            List<String> pileCodes = eventRateReqVo.getPilesCode();
            if (StringUtil.isNotEmpty(pileCodes)) {
                //查询充电桩的费率模型
                Map<String, EventRateReqPublicVo> billTemplateMap = togetherService.findSiteRateInfoByPileCodes(pileCodes).getData();
                pileCodes.forEach(pileCode -> {
                    EventRateReqPublicVo billTemplate = billTemplateMap.get(pileCode);
                    if (billTemplate != null) {
                        JSONObject result = JSONObject.parseObject(JSONObject.toJSONString(billTemplate));
                        result.put("pilesCode", Collections.singletonList(pileCode));
                        //发送
                        IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                                .mid("1001")
                                .param(result)
                                .type(IEGConstant.Type.EVENT_RATE_REQ)
                                .timestamp(SunMaxUtil.getSysTime())
                                .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                                .build();
                        String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.DEVICE_RESPONSE;
                        WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
                    }
                });
            }
        } catch (Exception e) {
            log.error("外网MQTT费率响应处理失败", e);
        }
    }

    /**
     * 固件数据块响应
     *
     * @param terminalCode     网关设备编号
     * @param fwReqSubscribeVo 固件升级请求数据
     */
    public static void firmwareDataRes(String terminalCode, FwReqSubscribeVo fwReqSubscribeVo) {
        try {
            //1.收到电桩固件数据请求，做数据块响应
            Integer deviceType = fwReqSubscribeVo.getDeviceType();
            Integer firmMainVCode = fwReqSubscribeVo.getMajorNo();//电桩发上来的固件主版本号
            Integer firmSecVCode = fwReqSubscribeVo.getChildNo();//电桩发上来的固件次版本号
            Integer dataBlockNum = fwReqSubscribeVo.getDataFlag();//数据块标号
            Integer dataBlockSize = fwReqSubscribeVo.getDataLen();//数据块大小

            IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                    .mid(String.valueOf(SMV2gConstant.SMV2G_MSG8003))
                    .type(IEGConstant.Type.CMD_RESPONSE_FW_DATA)
                    .timestamp(SunMaxUtil.getSysTime())
                    .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                    .build();
            RedisGeneralUtil.executeGateway(terminalCode, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
                if (gatewayRealModel != null && gatewayRealModel.getUpdateInfoMap() != null) {
                    //2.根据充电桩编号获取缓存中的固件包数据
                    UpdateInfoDto updateInfoDto = gatewayRealModel.getUpdateInfoMap().get(deviceType);
                    if (updateInfoDto != null) {
                        //请求第一块时 更新设备任务数据
                        if (dataBlockNum == 0 && gatewayRealModel.getUpdatePileMap().containsKey(deviceType)) {
                            DeviceBatchUpdateVo deviceUpdateVo = new DeviceBatchUpdateVo();
                            Map<String, List<DeviceBatchUpdateVo.DeviceUpdateInfo>> deviceUpdateInfoMap = Maps.newHashMap();
                            List<DeviceBatchUpdateVo.DeviceUpdateInfo> deviceUpdateInfoList = gatewayRealModel.getUpdatePileMap().get(deviceType).stream()
                                    .map(pilesCode -> DeviceBatchUpdateVo.DeviceUpdateInfo.builder().deviceCode(pilesCode).status(2).build()).collect(Collectors.toList());
                            deviceUpdateInfoMap.put(updateInfoDto.getTaskId(), deviceUpdateInfoList);
                            deviceUpdateVo.setDeviceUpdateInfoMap(deviceUpdateInfoMap);
                            deviceUpdateVo.setUpgradeTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            deviceUpdateVo.setTaskStatus(2);
                            deviceService.batchUpdateDeviceTask(deviceUpdateVo);
                        }
                        //3.获取内存中的固件主版本号，固件次版本号，固件路径
                        Integer firmwareMajorVersion = updateInfoDto.getFirmwareMajorVersion();
                        Integer firmwareMinorVersion = updateInfoDto.getFirmwareMinorVersion();
                        String firmwarePath = updateInfoDto.getFirmwarePath();
                        //4.比较一下电桩上来的固件版本号和缓存中充电桩的版本号是否一致
                        if (firmMainVCode.equals(firmwareMajorVersion) && firmSecVCode.equals(firmwareMinorVersion)) {
                            //更新升级进度
                            updateInfoDto.setDataBlockLabel(dataBlockNum + 1);//数据块标号 从1开始
                            updateInfoDto.setStatus(-1);
                            updateInfoDto.setMessage("固件包下发正在进行中");
                            //对应的数据块信息
                            byte[] dataBlockBytes;
                            //从缓存中读取固件块数据
                            List<byte[]> firmwareList = FirmwareUtil.getFirmwareByPilesCode(terminalCode, deviceType);
                            if (Objects.nonNull(firmwareList) && !firmwareList.isEmpty()) {
                                dataBlockBytes = firmwareList.get(dataBlockNum);//数据块
                                updateInfoDto.setDataBlockSum(firmwareList.size());//数据块总数
                            } else {
                                //根据文件路径和数据块大小拆分成数据块
                                dataBlockBytes = FirmwareUtil.parseFile(terminalCode, deviceType, firmwarePath, dataBlockNum, dataBlockSize);
                                if (Objects.nonNull(dataBlockBytes)) {
                                    updateInfoDto.setDataBlockSum(FirmwareUtil.getFirmwareByPilesCode(terminalCode, deviceType).size());
                                }
                            }
                            gatewayRealModel.getUpdateInfoMap().put(deviceType, updateInfoDto);
                            RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
                            //更新网关子设备升级信息
                            if (gatewayRealModel.getUpdatePileMap().containsKey(deviceType)) {
                                gatewayRealModel.getUpdatePileMap().get(deviceType).forEach(pileCode ->
                                        RedisGeneralUtil.executePile(pileCode, () -> {
                                            //网关子设备保存设备升级信息
                                            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                                            if (pileRealModel != null) {
                                                pileRealModel.getUpdateInfoMap().put(deviceType, updateInfoDto);
                                                RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                                            }
                                        }));
                            }

                            //固件数据响应
                            FwReqResPublishVo fwReqResPublishVo = new FwReqResPublishVo();
                            fwReqResPublishVo.setBetaNo(fwReqSubscribeVo.getBetaNo());
                            fwReqResPublishVo.setMajorNo(fwReqSubscribeVo.getMajorNo());
                            fwReqResPublishVo.setChildNo(fwReqSubscribeVo.getChildNo());
                            fwReqResPublishVo.setDeviceType(fwReqSubscribeVo.getDeviceType());
                            fwReqResPublishVo.setDataFlag(fwReqSubscribeVo.getDataFlag());
                            fwReqResPublishVo.setDataLen(fwReqSubscribeVo.getDataLen());
                            fwReqResPublishVo.setDataBlock(Base64.getEncoder().encodeToString(dataBlockBytes));

                            iegTopicVo.setParam(JSON.toJSON(fwReqResPublishVo));
                        }
                    }
                }
            });
            //下发启动主题
            String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.DEVICE_COMMAND;
            WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
        } catch (Exception e) {
            log.error("外网MQTT固件数据块响应处理失败", e);
        }
    }

    /**
     * 网关接收完成上报
     *
     * @param terminalCode 网关设备编号
     * @param fwGwOverVo   固件升级请求数据
     */
    public static void firmwareIegReceiveOver(String terminalCode, FwGwOverSubscribeVo fwGwOverVo) {
        try {
            log.info("网关固件数据校验结果:{}", fwGwOverVo.getFailReason());
            //更新设备升级状态
            DeviceBatchUpdateVo deviceUpdateVo = new DeviceBatchUpdateVo();
            RedisGeneralUtil.executeGateway(terminalCode, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
                //固件接收完成
                //收到固件接收完成 无论成功失败 都要清掉内存里面的数据
                FirmwareUtil.clearFirmwareByPilesCode(terminalCode, fwGwOverVo.getDeviceType());
                if (StringUtil.isNotEmpty(fwGwOverVo.getFailReason()) && gatewayRealModel != null) {
                    Map<Integer, UpdateInfoDto> updateInfoMap = gatewayRealModel.getUpdateInfoMap();
                    if (updateInfoMap.containsKey(fwGwOverVo.getDeviceType())) {
                        UpdateInfoDto updateInfoDto = updateInfoMap.get(fwGwOverVo.getDeviceType());
                        updateInfoDto.setStatus(fwGwOverVo.getFailReason());
                        if (fwGwOverVo.getFailReason() == 0) {
                            updateInfoDto.setMessage("固件包下发完成");
                        } else {
                            updateInfoDto.setMessage("固件包下发失败");

                            //网关固件包接收失败 更新所有子设备升级失败状态
                            Map<String, List<DeviceBatchUpdateVo.DeviceUpdateInfo>> deviceUpdateInfoMap = Maps.newHashMap();
                            Map<Integer, Set<String>> updatePileMap = gatewayRealModel.getUpdatePileMap();
                            if (updatePileMap.containsKey(fwGwOverVo.getDeviceType())) {
                                updatePileMap.get(fwGwOverVo.getDeviceType()).forEach(pileCode -> {
                                    RedisGeneralUtil.executePile(pileCode, () -> {
                                        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                                        if (pileRealModel != null) {
                                            //清空网关子设备升级信息
                                            pileRealModel.getUpdateInfoMap().remove(fwGwOverVo.getDeviceType());
                                            RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);

                                        }
                                    });
                                    List<DeviceBatchUpdateVo.DeviceUpdateInfo> deviceUpdateInfoList;
                                    if (deviceUpdateInfoMap.containsKey(updateInfoDto.getTaskId())) {
                                        deviceUpdateInfoList = deviceUpdateInfoMap.get(updateInfoDto.getTaskId());
                                    } else {
                                        deviceUpdateInfoList = Lists.newArrayList();
                                    }
                                    deviceUpdateInfoList.add(DeviceBatchUpdateVo.DeviceUpdateInfo.builder().deviceCode(pileCode).status(3).failReason(fwGwOverVo.getFailReason()).build());
                                    deviceUpdateInfoMap.put(updateInfoDto.getTaskId(), deviceUpdateInfoList);
                                });
                            }

                            deviceUpdateVo.setDeviceUpdateInfoMap(deviceUpdateInfoMap);
                            deviceUpdateVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                        }
                        gatewayRealModel.getUpdateInfoMap().remove(fwGwOverVo.getDeviceType());
                        RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);

                    }
                }
            });
            //更新设备升级状态
            if (MapUtils.isNotEmpty(deviceUpdateVo.getDeviceUpdateInfoMap()) && StringUtil.isNotEmpty(deviceUpdateVo.getEndTime())) {
                deviceService.batchUpdateDeviceTask(deviceUpdateVo);
            }
        } catch (Exception e) {
            log.error("外网MQTT网关接收完成上报处理失败", e);
        }
    }

    /**
     * 电桩接收完成上报
     *
     * @param fwPileOverVo 电桩接收完成上报数据
     */
    public static void firmwarePileReceiveOver(FwPileOverSubscribeVo fwPileOverVo) {
        List<FwPileOverSubscribeVo.FwPileUpdateInfo> reasonList = fwPileOverVo.getReasonList();
        try {
            Map<String, List<DeviceBatchUpdateVo.DeviceUpdateInfo>> deviceUpdateInfoMap = Maps.newHashMap();
            //批量更新充电桩升级信息
            for (FwPileOverSubscribeVo.FwPileUpdateInfo fwPileUpdateInfo : reasonList) {
                String pileCode = fwPileUpdateInfo.getPilesCode();
                RedisGeneralUtil.executePile(pileCode, () -> {
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                    if (pileRealModel != null) {
                        UpdateInfoDto updateInfoDto = pileRealModel.getUpdateInfoMap().get(fwPileOverVo.getDeviceType());
                        if (updateInfoDto != null) {
                            if (StringUtil.isNotEmpty(fwPileUpdateInfo.getFailReason())) {
                                log.info("网关电桩固件数据校验结果:{}", fwPileUpdateInfo.getFailReason());
                                updateInfoDto.setStatus(fwPileUpdateInfo.getFailReason());
                                int status;
                                Integer failReason = null;
                                if (fwPileUpdateInfo.getFailReason() == 0) {
                                    updateInfoDto.setMessage("固件包下发完成");
                                    status = 4;
                                } else {
                                    updateInfoDto.setMessage("固件包下发失败");
                                    status = 3;
                                    failReason = fwPileUpdateInfo.getFailReason();
                                }
                                pileRealModel.getUpdateInfoMap().put(fwPileOverVo.getDeviceType(), updateInfoDto);
                                RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);

                                List<DeviceBatchUpdateVo.DeviceUpdateInfo> deviceUpdateInfoList;
                                if (deviceUpdateInfoMap.containsKey(updateInfoDto.getTaskId())) {
                                    deviceUpdateInfoList = deviceUpdateInfoMap.get(updateInfoDto.getTaskId());
                                } else {
                                    deviceUpdateInfoList = Lists.newArrayList();
                                }
                                deviceUpdateInfoList.add(DeviceBatchUpdateVo.DeviceUpdateInfo.builder().deviceCode(pileCode).status(status).failReason(failReason).build());
                                deviceUpdateInfoMap.put(updateInfoDto.getTaskId(), deviceUpdateInfoList);
                            }
                        }
                    }
                });
            }
            //更新设备升级状态
            DeviceBatchUpdateVo deviceUpdateVo = new DeviceBatchUpdateVo();
            deviceUpdateVo.setDeviceUpdateInfoMap(deviceUpdateInfoMap);
            deviceUpdateVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            deviceService.batchUpdateDeviceTask(deviceUpdateVo);
        } catch (Exception e) {
            log.error("外网MQTT电桩接收完成上报数据", e);
        }
    }

    /**
     * 鉴权请求 做鉴权响应
     *
     * @param terminalCode       网关设备编号
     * @param eventAuthRequestVo 鉴权请求数据
     */
    public static void authenticationRes(String terminalCode, EventAuthRequestSubscribeVo eventAuthRequestVo) {
        try {
            UserAccount userAccount = eventAuthRequestVo.getUserAccount();
            //鉴权响应
            EventAuthResponsePublicVo eventAuthResponseVo = new EventAuthResponsePublicVo();
            eventAuthResponseVo.setPilesCode(eventAuthRequestVo.getPilesCode());
            eventAuthResponseVo.setGunCode(eventAuthRequestVo.getGunCode());
            eventAuthResponseVo.setBalanceType(1);
            eventAuthResponseVo.setBalanceNum(500000.0);
            eventAuthResponseVo.setUserAccount(userAccount);
            eventAuthResponseVo.setFailReason(255); //账号非法
            //账号类型 1-充/放电卡 2-VIN码 3-手机号
            if (StringUtil.isNotEmpty(userAccount.getAccountType())) {
                switch (userAccount.getAccountType()) {
                    case 1: //充放电卡
                        break;
                    case 2: //vin码校验
                    case 3: //手机号
                        Boolean isData = togetherService.checkAccountCode(eventAuthRequestVo.getPilesCode(), userAccount.getAccountData()).getData();
                        if (isData) {
                            eventAuthResponseVo.setFailReason(0);
                        }
                        break;
                }
            } else {
                eventAuthResponseVo.setFailReason(1); //账号非法
            }
            //发送消息
            //鉴权响应主题参数
            IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                    .mid(String.valueOf(SMV2gConstant.SMV2G_MSG3001))
                    .param(JSON.toJSON(eventAuthResponseVo))
                    .type(IEGConstant.Type.EVENT_AUTHENTICATION_RESPONSE)
                    .timestamp(SunMaxUtil.getSysTime())
                    .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                    .build();
            String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.DEVICE_RESPONSE;
            //发送数据
            WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
        } catch (Exception e) {
            log.error("外网MQTT处理鉴权请求数据报错", e);
        }
    }

    /**
     * 策略请求 做策略响应
     *
     * @param terminalCode           网关设备编号
     * @param eventStrategySettingVo 策略请求数据
     */
    public static void strategyRes(String terminalCode, EventStrategySettingSubscribeVo eventStrategySettingVo) {
        try {
            //策略设置响应
            EventStrategyConfirmPublicVo eventStrategyConfirmVo = new EventStrategyConfirmPublicVo();
            eventStrategyConfirmVo.setPilesCode(eventStrategySettingVo.getPilesCode());
            eventStrategyConfirmVo.setGunCode(eventStrategySettingVo.getGunCode());
            eventStrategyConfirmVo.setRunMode(eventStrategySettingVo.getRunMode());
            eventStrategyConfirmVo.setFailReason(0);
            eventStrategyConfirmVo.setFailDetail(0);
            //判断是否大于30秒
            boolean isPileStart = false;
            String strategyKey = eventStrategySettingVo.getPilesCode() + eventStrategySettingVo.getGunCode();
            if (strategyMap.containsKey(strategyKey)) {
                LocalDateTime startTime = strategyMap.get(strategyKey);
                //判断是否大于30秒
                if (DateUtil.compareDiffBetweenSecond(startTime, LocalDateTime.now()) >= 30) {
                    isPileStart = true;
                } else {
                    eventStrategyConfirmVo.setFailReason(255);
                }
            } else {
                isPileStart = true;
            }
            strategyMap.put(strategyKey, LocalDateTime.now());
            //策略设置响应主题参数
            IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                    .mid(String.valueOf(SMV2gConstant.SMV2G_MSG3003))
                    .param(JSON.toJSON(eventStrategyConfirmVo))
                    .type(IEGConstant.Type.EVENT_STRATEGY_CONFIRM)
                    .timestamp(SunMaxUtil.getSysTime())
                    .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                    .build();
            String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.DEVICE_RESPONSE;
            //发送数据
            WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);


            //下发启动命令
            if (isPileStart) {
                PileStartVo pileStartVo = new PileStartVo();

                pileStartVo.setPileCode(eventStrategySettingVo.getPilesCode());
                pileStartVo.setGunCode(String.valueOf(eventStrategySettingVo.getGunCode()));
                Strategy strategy = eventStrategySettingVo.getStrategy();
                if (strategy != null) {
                    pileStartVo.setStrategy(strategy.getStrategyType());
                    pileStartVo.setStrategyCfg(PileRecordUtil.getStrategyCfg(eventStrategySettingVo.getRunMode(), strategy.getStrategyType(), strategy.getStrategyCfg()));
                }
                pileStartVo.setStarter(4);
                pileStartVo.setType(0);
                pileStartVo.setRunMode(eventStrategySettingVo.getRunMode());
                if (eventStrategySettingVo.getUserAccount() != null) {
                    Integer accountType = eventStrategySettingVo.getUserAccount().getAccountType();
                    if (StringUtil.isNotEmpty(accountType)) {
                        if (accountType == 1) {
                            pileStartVo.setStarter(3);
                        }
                    }
                    pileStartVo.setAccountType(accountType);
                    pileStartVo.setAccountData(eventStrategySettingVo.getUserAccount().getAccountData());
                }
                pileStartVo.setPrepayMoney(new BigDecimal(5000));
                SpringBeanUtil.getBean(PileCtrlService.class).pileStart(pileStartVo);
            }
        } catch (Exception e) {
            log.error("外网MQTT处理策略响应数据报错", e);
        }
    }

    /**
     * 控制板信息响应
     *
     * @param cmdToPoFwResVo 控制板信息响应数据
     */
    public static void ctrlBoardInfoRes(CmdToPoFwResSubscribeVo cmdToPoFwResVo) {
        try {
            if (CollectionUtils.isNotEmpty(cmdToPoFwResVo.getFwInfos())) {
                Map<String, List<DeviceBatchUpdateVo.DeviceUpdateInfo>> deviceUpdateInfoMap = Maps.newHashMap();
                for (int i = 0; i < cmdToPoFwResVo.getFwInfos().size(); i++) {
                    CmdToPoFwResSubscribeVo.FwInfo fwInfo = cmdToPoFwResVo.getFwInfos().get(i);
                    String pileCode = fwInfo.getPilesCode();
                    String keyId = pileCode + SMV2gConstant.SUB_MODEL_INFO_REQ;
                    if (PileDemand.demandModelMap.containsKey(keyId)) {
                        PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                        demandModel.setRecFlag(true);
                        demandModel.setRecCode(0);//执行成功
                        PileDemand.demandModelMap.put(keyId, demandModel);
                    }
                    RedisGeneralUtil.executePile(pileCode, () -> {
                        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                        if (pileRealModel != null) {
                            //获取上报的控制板信息
                            Map<Integer, CtrlBoardInfo> ctrlBoardInfoMap = getStringCtrlBoardInfoMap(fwInfo);

                            //更新固件
                            pileRealModel.getUpdateInfoMap().forEach((key, value) -> {
                                DeviceBatchUpdateVo.DeviceUpdateInfo deviceUpdateInfo = new DeviceBatchUpdateVo.DeviceUpdateInfo();
                                deviceUpdateInfo.setDeviceCode(pileCode);
                                deviceUpdateInfo.setStatus(5);
                                deviceUpdateInfo.setFailReason(255);
                                if (ctrlBoardInfoMap.containsKey(key)) {
                                    CtrlBoardInfo ctrlBoardInfo = ctrlBoardInfoMap.get(key);
                                    String firmwareVersion1 = value.getFirmwareMajorVersion() + FileUtil.POINT + value.getFirmwareMinorVersion();
                                    String firmwareVersion2 = ctrlBoardInfo.getFirmMainVCode() + FileUtil.POINT + ctrlBoardInfo.getFirmSecVCode();
                                    if (Objects.equals(firmwareVersion1, firmwareVersion2)) {
                                        deviceUpdateInfo.setStatus(6);
                                        deviceUpdateInfo.setFailReason(0);
                                    } else { //控制板信息上报不一致
                                        deviceUpdateInfo.setFailReason(256);
                                    }
                                }
                                List<DeviceBatchUpdateVo.DeviceUpdateInfo> deviceUpdateInfoList;
                                if (deviceUpdateInfoMap.containsKey(value.getTaskId())) {
                                    deviceUpdateInfoList = deviceUpdateInfoMap.get(value.getTaskId());
                                } else {
                                    deviceUpdateInfoList = Lists.newArrayList();
                                }
                                deviceUpdateInfoList.add(deviceUpdateInfo);
                                deviceUpdateInfoMap.put(value.getTaskId(), deviceUpdateInfoList);
                            });

                            pileRealModel.setCtrlBoardInfoMap(ctrlBoardInfoMap);
                            pileRealModel.getUpdateInfoMap().clear(); //清空更新信息
                            RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                        }
                    });
                }
                //更新设备升级状态
                if (MapUtils.isNotEmpty(deviceUpdateInfoMap)) {
                    DeviceBatchUpdateVo deviceUpdateVo = new DeviceBatchUpdateVo();
                    deviceUpdateVo.setDeviceUpdateInfoMap(deviceUpdateInfoMap);
                    deviceUpdateVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    deviceService.batchUpdateDeviceTask(deviceUpdateVo);
                }
            }
        } catch (Exception e) {
            log.error("外网MQTT处理控制板响应数据报错", e);
        }
    }

    @NotNull
    private static Map<Integer, CtrlBoardInfo> getStringCtrlBoardInfoMap(CmdToPoFwResSubscribeVo.FwInfo fwInfo) {
        Map<Integer, CtrlBoardInfo> ctrlBoardInfoMap = new HashMap<>();
        for (CmdToPoFwResSubscribeVo.CbInfo cbInfo : fwInfo.getCbInfos()) {
            CtrlBoardInfo ctrlBoardInfo = new CtrlBoardInfo();
            ctrlBoardInfo.setCtrlBoardType(cbInfo.getCbType());//控制板类型
            ctrlBoardInfo.setFirmInnerVCode(cbInfo.getFwBetaNo());//内测版本号
            ctrlBoardInfo.setFirmMainVCode(cbInfo.getFwMajorNo());//主版本号
            ctrlBoardInfo.setFirmSecVCode(cbInfo.getFwChildNo());//次版本号
            ctrlBoardInfo.setFirmwareName(cbInfo.getFwName());//固件名称
            ctrlBoardInfo.setHardMainVCode(cbInfo.getHMajorNo());//硬件主版本
            ctrlBoardInfo.setHardSecVCode(cbInfo.getHChildNo());//硬件次版本
            ctrlBoardInfo.setHardwareName(cbInfo.getHardwareName());//硬件名称
            ctrlBoardInfo.setSeqNum(cbInfo.getSeq());
            ctrlBoardInfoMap.put(ctrlBoardInfo.getCtrlBoardType(), ctrlBoardInfo);
        }
        return ctrlBoardInfoMap;
    }

    /**
     * 功率控制日志
     *
     * @param terminalCode 终端编号
     * @param gatewayLogVo 功率控制日志数据
     */
    public static void updateGateWayCtrlLog(String terminalCode, GatewayLogSubscribeVo gatewayLogVo) {
        try {
            ControlRecordDao controlRecordDao = SpringBeanUtil.getBean(ControlRecordDao.class);
            ControlRecordEntity controlRecordEntity = new ControlRecordEntity();
            controlRecordEntity.setDeviceCode(terminalCode);
            controlRecordEntity.setOperateContent("网关功率控制");
            controlRecordEntity.setControlParam(JSON.toJSONString(gatewayLogVo));
            controlRecordEntity.setControlType(2);
            controlRecordEntity.setCreateTime(new Date(1000 * gatewayLogVo.getCmdTime()).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime());
            controlRecordEntity.setUpdateTime(LocalDateTime.now());
            controlRecordDao.save(controlRecordEntity);
        } catch (Exception e) {
            log.error("外网MQTT功率控制日志处理", e);
        }
    }

    /**
     * 处理离线交易记录
     *
     * @param terminalCode        终端编号
     * @param pileOffLineRecordVo 离线交易记录数据
     */
    public static void offLineRecordRes(String terminalCode, PileOffLineRecordSubscribeVo pileOffLineRecordVo) {
        if (pileOffLineRecordVo.getOffLineChargeRecord() != null) {
            for (int i = 0; i < pileOffLineRecordVo.getOffLineChargeRecord().size(); i++) {
                PileRecordSubscribeVo pileRecordSubscribeVo = pileOffLineRecordVo.getOffLineChargeRecord().get(i);
                recordRes(terminalCode, pileRecordSubscribeVo);//保存充电记录
            }
        }
    }

    /**
     * 更新电桩状态
     *
     * @param pileStateMap 电桩状态数据
     */
    public static void updateStatus(String terminalCode, Map<String, PileStateSubscribeVo> pileStateMap) {
        try {

            //查询设备未修复的告警记录数据
            List<AlarmRecordEntity> updateAlarmList = Lists.newArrayList();
            Set<String> deviceCodes = Sets.newHashSet();
            deviceCodes.add(terminalCode);
            if (MapUtils.isNotEmpty(pileStateMap)) {
                deviceCodes.addAll(pileStateMap.keySet());
            }
            List<AlarmRecordEntity> alarmRecordList = alarmRecordDao.findAllByDeviceCodeInAndFaultCodeAndAlarmStatus(deviceCodes, 65534, 0);

            RedisGeneralUtil.executeGateway(terminalCode, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
                AtomicReference<Boolean> isGateway = new AtomicReference<>(false);
                pileStateMap.forEach((pileCode, pileStateVo) ->
                        RedisGeneralUtil.executePile(pileCode, () -> {
                            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                            if (pileRealModel != null) {
                                if (StringUtil.isEmpty(pileRealModel.getPileCode())) {
                                    pileRealModel.setPileCode(pileCode);
                                }

                                //更新电桩和枪的状态
                                if (StringUtil.isEmpty(pileRealModel.getTerminalCode()) || !Objects.equals(pileRealModel.getTerminalCode(), terminalCode)) {
                                    pileRealModel.setTerminalCode(terminalCode);
                                }
                                if (StringUtil.isEmpty(pileRealModel.getMessageType()) || !Objects.equals(pileRealModel.getMessageType(), 1)) {
                                    pileRealModel.setMessageType(1); //外网
                                }
                                Integer workState = WebMqttUtil.convertPileState(pileStateVo.getWorkState());
                                //校验网关子设备状态
                                if (gatewayRealModel != null) {
                                    if (gatewayRealModel.getSubDeviceRealMap().containsKey(pileCode)) {
                                        Integer lastWorkState = gatewayRealModel.getSubDeviceRealMap().get(pileCode);
                                        if (!Objects.equals(lastWorkState, workState)) {
                                            gatewayRealModel.getSubDeviceRealMap().put(pileCode, workState);
                                            isGateway.set(true);
                                        }
                                    } else {
                                        gatewayRealModel.getSubDeviceRealMap().put(pileCode, workState);
                                        isGateway.set(true);
                                    }
                                }
                                pileRealModel.setWorkStatus(workState);
                                pileRealModel.setOriginalStatus(pileStateVo.getWorkState());
                                pileRealModel.setResetTimes(pileStateVo.getResetTimes());
                                if (workState == 1) { //在线 修复未修复的电桩
                                    //查询是否存在未修复的离线告警,如果存在则修复
                                    updateAlarmList.addAll(alarmRecordList.stream().filter(a -> Objects.equals(a.getDeviceCode(), pileCode)).collect(Collectors.toList()));
                                }
                                Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                                pileStateVo.getGun_state().forEach(gunState -> {
                                    PileRealModel.GunRealModel gunRealModel = new PileRealModel.GunRealModel();
                                    if (StringUtil.isNotEmpty(gunState.getGunCode())) {
                                        String gunCode = String.valueOf(gunState.getGunCode());
                                        if (gunRealModelMap.containsKey(gunCode)) {
                                            gunRealModel = gunRealModelMap.get(gunCode);
                                        }
                                        if (StringUtil.isEmpty(gunRealModel.getGunCode())) {
                                            gunRealModel.setGunCode(gunCode);
                                        }
                                        gunRealModel.setGunStatus(WebMqttUtil.convertGunState(pileStateVo.getWorkState(), gunState.getWorkState(), gunState.getVehicleConnState()));
                                        gunRealModel.setGunOriginalStatus(gunState.getWorkState());
//                                    gunRealModel.setParkingLockState(gunState.getParkingLockState());
                                        gunRealModel.setVehicleConnState(gunState.getVehicleConnState());
//                                    gunRealModel.setGunLockState(gunState.getGunLockState());
//                                    gunRealModel.setK1k2State(gunState.getK1k2State());
                                        gunRealModelMap.put(gunCode, gunRealModel);
                                    }
                                });
                                pileRealModel.setGunRealModelMap(gunRealModelMap);
                                pileRealModel.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                                RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                            }
                        }));
                if (gatewayRealModel != null) {
                    if (StringUtil.isEmpty(gatewayRealModel.getDeviceStatus()) || gatewayRealModel.getDeviceStatus() == 88) {
                        gatewayRealModel.setDeviceStatus(1);
                        isGateway.set(true);
                        //查询是否存在未修复的离线告警,如果存在则修复
                        updateAlarmList.addAll(alarmRecordList.stream().filter(a -> Objects.equals(a.getDeviceCode(), terminalCode)).collect(Collectors.toList()));
                    }
                }
                //网关状态或子设备状态发生改变 进行保存
                if (isGateway.get()) {
                    RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
                }
            });
            //更新未修复的告警记录数据
            if (CollectionUtils.isNotEmpty(updateAlarmList)) {
                PileRecordUtil.updateAlarmStatus(updateAlarmList, alarmRecordDao);
            }
        } catch (Exception e) {
            log.error("外网MQTT更新网关状态报错", e);
        }

    }

    /**
     * 解析电桩故障
     *
     * @param pileFaultMap 电桩故障数据
     */
    public static void parseFault(Map<String, PileFaultSubscribeVo> pileFaultMap) {
        //定义添加和修改的故障
        List<AlarmRecordEntity> addAlarmRecordList = Lists.newArrayList();
        // 定义需要更新的故障 Map (设备编号 -> (故障码，模块地址))
        Map<String, Map<Integer, Set<Integer>>> updateFaultWithModuleMap = Maps.newConcurrentMap();
        // 定义故障码和电力模块地址 Map (故障码 -> 模块地址 Set，多个模块可能上报相同故障码)
        Map<Integer, Set<Integer>> moduleFaultAddrMap = Maps.newConcurrentMap();

        for (String pileCode : pileFaultMap.keySet()) {
            PileFaultSubscribeVo pileFaultVo = JSON.parseObject(JSON.toJSONString(pileFaultMap.get(pileCode)), PileFaultSubscribeVo.class);
            try {
                //收到电桩故障数据
                RedisGeneralUtil.executePile(pileCode, () -> {
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                    if (pileRealModel != null) {
                        Set<Integer> pileFaultCodes = Sets.newHashSet();//电桩最新故障状态码
                        Map<Integer, Set<Integer>> itfFaultCodeMap = Maps.newHashMap();//电桩最新充电接口故障状态码
                        Set<Integer> lastPileFaultCodes = pileRealModel.getFaultCodes();//电桩上一次故障状态码
                        Map<Integer, Set<Integer>> lastItfFaultCodeMap = Maps.newHashMap();//电桩上一次充电接口故障状态码
                        ChargerRunFault chargerRunFault = new ChargerRunFault();
                        chargerRunFault.setChargerComFault(pileFaultVo.getGeneralFault());
                        chargerRunFault.setChargerStopFault(pileFaultVo.getEmergencyStopFault());
                        chargerRunFault.setTcuReportFault(pileFaultVo.getTcuReportFault());
                        List<ItfFault> itfFaults = new ArrayList<>();
                        for (PileFaultSubscribeVo.GunFaultVo gunFaultVo : pileFaultVo.getGun_fault()) {
                            ItfFault itfFault = new ItfFault();
                            itfFault.setGunCode(gunFaultVo.getGunCode());
                            itfFault.setCcuReport(gunFaultVo.getCcuReportFault());
                            itfFault.setVehicleBmsFault(gunFaultVo.getBmsReportFault());
                            itfFault.setCcuTimeoutFault(gunFaultVo.getCcuTimeOutFault());
                            itfFault.setBmsTimeoutFault(gunFaultVo.getBmsTimeOutFault());
                            itfFault.setIdmFault(gunFaultVo.getIdmFault());
                            itfFaults.add(itfFault);
                        }
                        List<ElecModuleFault> elecModuleFaults = new ArrayList<>();
                        for (PileFaultSubscribeVo.ModuleFaultVo moduleFaultVo : pileFaultVo.getEmodule_fault()) {
                            ElecModuleFault elecModuleFault = new ElecModuleFault();
                            elecModuleFault.setModuleAddr(moduleFaultVo.getAddr());
                            elecModuleFault.setPcuReportFault(moduleFaultVo.getPcuReportFault());
                            elecModuleFault.setPcuTimeout(moduleFaultVo.getPcuTimeOutFalut());
                            elecModuleFaults.add(elecModuleFault);
                        }
                        //电桩故障
                        if (StringUtil.isNotEmpty(chargerRunFault.getChargerStopFault()) && chargerRunFault.getChargerStopFault() >= 0) {
                            pileFaultCodes.addAll(FaultParseUtil.parseEmergcyFault(chargerRunFault.getChargerStopFault()));//急停故障
                        }
                        if (StringUtil.isNotEmpty(chargerRunFault.getChargerComFault()) && chargerRunFault.getChargerComFault() >= 0) {
                            pileFaultCodes.addAll(FaultParseUtil.parseGeneralFault(chargerRunFault.getChargerComFault()));//通用故障
                        }
                        if (StringUtil.isNotEmpty(chargerRunFault.getTcuReportFault()) && chargerRunFault.getTcuReportFault() >= 0) {
                            pileFaultCodes.addAll(FaultParseUtil.parseTcuFault(chargerRunFault.getTcuReportFault()));//Tcu上报故障
                        }
                        //充电接口故障
                        if (CollectionUtils.isNotEmpty(itfFaults)) {
                            for (ItfFault fault : itfFaults) {
                                Set<Integer> itfFaultCodes = Sets.newHashSet();
                                if (StringUtil.isNotEmpty(fault.getCcuReport()) && fault.getCcuReport() >= 0) {
                                    itfFaultCodes.addAll(FaultParseUtil.parseCcuFault(fault.getCcuReport()));
                                }
                                if (StringUtil.isNotEmpty(fault.getVehicleBmsFault()) && fault.getVehicleBmsFault() >= 0) {
                                    itfFaultCodes.addAll(FaultParseUtil.parseBmsFault(fault.getVehicleBmsFault()));
                                }
                                if (StringUtil.isNotEmpty(fault.getCcuTimeoutFault()) && fault.getCcuTimeoutFault() >= 0) {
                                    itfFaultCodes.addAll(FaultParseUtil.parseCcuTimeOutFault(fault.getCcuTimeoutFault()));
                                }
                                if (StringUtil.isNotEmpty(fault.getBmsTimeoutFault()) && fault.getBmsTimeoutFault() >= 0) {
                                    itfFaultCodes.addAll(FaultParseUtil.parseBmsTimeOutFault(fault.getBmsTimeoutFault()));
                                }
                                if (StringUtil.isNotEmpty(fault.getIdmFault()) && fault.getIdmFault() >= 0) {
                                    itfFaultCodes.addAll(FaultParseUtil.parseIMDFault(fault.getIdmFault()));
                                }
                                itfFaultCodeMap.put(fault.getGunCode(), itfFaultCodes);
                                //更新充放电接口故障
                                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(fault.getGunCode().toString());
                                if (gunRealModel != null) {
                                    lastItfFaultCodeMap.put(fault.getGunCode(), gunRealModel.getFaultCodes());
                                    gunRealModel.setFaultCodes(itfFaultCodes);
                                    pileRealModel.getGunRealModelMap().put(fault.getGunCode().toString(), gunRealModel);
                                }
                            }
                        } else {
                            for (String gunCode : pileRealModel.getGunRealModelMap().keySet()) {
                                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                                if (gunRealModel != null) {
                                    lastItfFaultCodeMap.put(Integer.parseInt(gunCode), gunRealModel.getFaultCodes());
                                    gunRealModel.setFaultCodes(Sets.newHashSet());
                                    pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                                }
                            }
                        }
                        //电力模块故障 (故障码 + 模块地址 -> 生成单独的告警记录)
                        if (CollectionUtils.isNotEmpty(elecModuleFaults)) {
                            for (ElecModuleFault moduleFault : elecModuleFaults) {
                                if (StringUtil.isNotEmpty(moduleFault.getPcuReportFault()) && moduleFault.getPcuReportFault() >= 0) {
                                    List<Integer> pcuReportFaultList = FaultParseUtil.parsePcuFault(moduleFault.getPcuReportFault());
                                    // 同一故障码可能来自多个模块，每个模块单独生成告警记录
                                    for (Integer faultCode : pcuReportFaultList) {
                                        pileFaultCodes.add(faultCode);
                                        // 记录该故障码对应的所有模块地址
                                        moduleFaultAddrMap.computeIfAbsent(faultCode, k -> Sets.newConcurrentHashSet())
                                                .add(moduleFault.getModuleAddr());
                                    }
                                }
                            }
                        }

                        //对比电桩故障码 保存入库
                        //根据设备编号和多个故障码查询设备电桩事件数据
                        DeviceService deviceService = SpringBeanUtil.getBean(DeviceService.class);
                        Set<Integer> faultCodes = Stream.of(pileFaultCodes, itfFaultCodeMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()))
                                .flatMap(Collection::stream).collect(Collectors.toSet());
                        Map<Integer, PileFaultDto> faultEventMap = deviceService.findPileFaultList(pileCode, faultCodes).getData();

                        // 获取上一次电力模块故障码对应的模块地址（从 Redis 缓存中读取）
                        Map<Integer, Set<Integer>> lastModuleFaultAddrMapSnapshot = Maps.newHashMap();
                        Map<Integer, Set<Integer>> lastModuleFaultAddrMapCache = pileRealModel.getModuleFaultAddrMap();
                        if (lastModuleFaultAddrMapCache != null) {
                            lastModuleFaultAddrMapCache.forEach((k, v) -> lastModuleFaultAddrMapSnapshot.put(k, Sets.newHashSet(v)));
                        }

                        //电桩故障相关转化 - 新增故障记录
                        if (CollectionUtils.isNotEmpty(lastPileFaultCodes)) {
                            // 处理故障码：包括完全新增、模块新增、模块恢复、完全恢复
                            for (Integer pileFaultCode : pileFaultCodes) {
                                Set<Integer> currentModuleAdds = moduleFaultAddrMap.get(pileFaultCode);
                                if (!lastPileFaultCodes.contains(pileFaultCode)) {
                                    // 情况 1：故障码完全新增，为所有模块地址生成告警记录
                                    if (CollectionUtils.isNotEmpty(currentModuleAdds)) {
                                        for (Integer moduleAddr : currentModuleAdds) {
                                            addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
                                                    moduleAddr, faultEventMap));
                                        }
                                    } else {
                                        // 非电力模块故障
                                        addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
                                                null, faultEventMap));
                                    }
                                } else {
                                    // 情况 2：故障码已存在，检查是否有新增模块地址
                                    Set<Integer> lastModuleAdds = lastModuleFaultAddrMapSnapshot.get(pileFaultCode);
                                    if (CollectionUtils.isNotEmpty(currentModuleAdds)) {
                                        Set<Integer> newModuleAdds = Sets.difference(currentModuleAdds,
                                                lastModuleAdds != null ? lastModuleAdds : Sets.newHashSet()).immutableCopy();
                                        // 为新增的模块地址生成告警记录
                                        for (Integer moduleAddr : newModuleAdds) {
                                            addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
                                                    moduleAddr, faultEventMap));
                                        }
                                    }
                                }
                            }
                            // 处理恢复的故障：包括部分恢复和完全恢复
                            for (Integer lastPileFaultCode : lastPileFaultCodes) {
                                Set<Integer> lastModuleAdds = lastModuleFaultAddrMapSnapshot.get(lastPileFaultCode);
                                if (!pileFaultCodes.contains(lastPileFaultCode)) {
                                    // 情况 3：故障码完全恢复，更新该故障码的所有告警记录
                                    updateFaultWithModuleMap.computeIfAbsent(pileCode, k -> Maps.newConcurrentMap())
                                            .put(lastPileFaultCode, lastModuleAdds != null ? lastModuleAdds : Sets.newHashSet());
                                } else {
                                    // 情况 4：故障码仍存在，检查是否有模块恢复
                                    Set<Integer> currentModuleAdds = moduleFaultAddrMap.get(lastPileFaultCode);
                                    if (CollectionUtils.isNotEmpty(lastModuleAdds)) {
                                        // 找出恢复的模块地址（在上次中存在但在本次中不存在）
                                        Set<Integer> recoveredModuleAdds = Sets.difference(lastModuleAdds,
                                                currentModuleAdds != null ? currentModuleAdds : Sets.newHashSet()).immutableCopy();
                                        if (CollectionUtils.isNotEmpty(recoveredModuleAdds)) {
                                            // 只有恢复的模块需要更新告警记录
                                            updateFaultWithModuleMap.computeIfAbsent(pileCode, k -> Maps.newConcurrentMap())
                                                    .put(lastPileFaultCode, recoveredModuleAdds);
                                        }
                                    }
                                }
                            }
                        } else {
                            // 首次上电或无上次故障数据：为每个故障码的每个模块地址单独生成告警记录
                            for (Integer pileFaultCode : pileFaultCodes) {
                                Set<Integer> moduleAdds = moduleFaultAddrMap.get(pileFaultCode);
                                if (CollectionUtils.isNotEmpty(moduleAdds)) {
                                    for (Integer moduleAddr : moduleAdds) {
                                        addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
                                                moduleAddr, faultEventMap));
                                    }
                                } else {
                                    addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
                                            null, faultEventMap));
                                }
                            }
                        }

                        //电枪故障相关转换
                        if (MapUtils.isNotEmpty(itfFaultCodeMap)) {
                            for (Map.Entry<Integer, Set<Integer>> itfFault : itfFaultCodeMap.entrySet()) {
                                Integer gunCode = itfFault.getKey();
                                Set<Integer> newItfFaults = itfFault.getValue();
                                if (lastItfFaultCodeMap.containsKey(gunCode)) {
                                    Set<Integer> lastItfFaults = lastItfFaultCodeMap.get(gunCode);
                                    if (CollectionUtils.isNotEmpty(lastItfFaults)) {
                                        //新产生的故障
                                        for (Integer faultCode : newItfFaults) {
                                            if (!lastItfFaults.contains(faultCode)) { //新产生的故障
                                                Set<Integer> moduleAdds = moduleFaultAddrMap.get(faultCode);
                                                if (CollectionUtils.isNotEmpty(moduleAdds)) {
                                                    for (Integer moduleAddr : moduleAdds) {
                                                        addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, gunCode.toString(), faultCode, pileRealModel.getFeatureCode(),
                                                                moduleAddr, faultEventMap));
                                                    }
                                                } else {
                                                    addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, gunCode.toString(), faultCode, pileRealModel.getFeatureCode(),
                                                            null, faultEventMap));
                                                }
                                            }
                                        }
                                        //恢复的枪故障：故障码完全恢复时更新
                                        for (Integer faultCode : lastItfFaults) {
                                            if (!newItfFaults.contains(faultCode)) {
                                                updateFaultWithModuleMap.computeIfAbsent(pileCode, k -> Maps.newConcurrentMap())
                                                        .put(faultCode, Sets.newHashSet()); // 空模块地址集合表示非电力模块故障
                                            }
                                        }
                                    } else {
                                        //新产生的故障
                                        for (Integer faultCode : newItfFaults) {
                                            Set<Integer> moduleAdds = moduleFaultAddrMap.get(faultCode);
                                            if (CollectionUtils.isNotEmpty(moduleAdds)) {
                                                for (Integer moduleAddr : moduleAdds) {
                                                    addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, gunCode.toString(), faultCode, pileRealModel.getFeatureCode(),
                                                            moduleAddr, faultEventMap));
                                                }
                                            } else {
                                                addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pileCode, gunCode.toString(), faultCode, pileRealModel.getFeatureCode(),
                                                        null, faultEventMap));
                                            }
                                        }
                                    }
                                }

                            }
                        } else { //恢复的枪故障
                            for (Map.Entry<Integer, Set<Integer>> itfFault : lastItfFaultCodeMap.entrySet()) {
                                Set<Integer> lastItfFaults = itfFault.getValue();
                                for (Integer faultCode : lastItfFaults) {
                                    updateFaultWithModuleMap.computeIfAbsent(pileCode, k -> Maps.newConcurrentMap())
                                            .put(faultCode, Sets.newHashSet());
                                }
                            }
                        }

                        pileRealModel.setFaultCodes(pileFaultCodes);//保存故障
                        // 更新 Redis 缓存中的电力模块故障信息，供下次故障上报对比
                        pileRealModel.getModuleFaultAddrMap().clear();
                        moduleFaultAddrMap.forEach((k, v) -> pileRealModel.getModuleFaultAddrMap().put(k, Sets.newHashSet(v)));
                        RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                    }
                });
                //告警数据存库
                if (CollectionUtils.isNotEmpty(addAlarmRecordList)) {
                    alarmRecordDao.batchInsert(addAlarmRecordList);
                }
                //更新告警记录：根据故障码和模块地址精确更新
                List<AlarmRecordEntity> updateAlarmRecordList = Lists.newArrayList();
                if (MapUtils.isNotEmpty(updateFaultWithModuleMap)) {
                    updateFaultWithModuleMap.forEach((deviceCode, faultModuleMap) -> {
                        //根据故障码更新告警记录
                        Set<Integer> faultCodes = faultModuleMap.entrySet().stream().filter(s -> CollectionUtils.isEmpty(s.getValue()))
                                .map(Map.Entry::getKey).collect(Collectors.toSet());
                        if (CollectionUtils.isNotEmpty(faultCodes)) {
                            updateAlarmRecordList.addAll(alarmRecordDao.findAllByDeviceCodeAndFaultCodeInAndAlarmStatus(deviceCode, faultCodes, 0));
                        }
                        //根据故障码和模块地址告警记录
                        faultModuleMap.entrySet().stream().filter(s -> CollectionUtils.isNotEmpty(s.getValue())).forEach(entry ->
                                updateAlarmRecordList.addAll(alarmRecordDao.findAllByDeviceCodeAndFaultCodeAndModuleAddrInAndAlarmStatus(deviceCode, entry.getKey(), entry.getValue(), 0)));
                    });
                }
                if (CollectionUtils.isNotEmpty(updateAlarmRecordList)) {
                    PileRecordUtil.updateAlarmStatus(updateAlarmRecordList, alarmRecordDao);
                }
            } catch (Exception e) {
                log.error("外网MQTT解析电桩故障失败", e);
            }
        }

    }

    /**
     * 解析电桩数据
     *
     * @param pileDataMap 电桩数据
     */
    public static void parseData(Map<String, PileDataSubscribeVo> pileDataMap) {
        pileDataMap.forEach((pileCode, pileDataVo) -> {
            try {
                RedisGeneralUtil.executePile(pileCode, () -> {
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                    if (pileRealModel != null) {//有缓存 则更新
                        //更新充电枪口运行数据
                        if (CollectionUtils.isNotEmpty(pileDataVo.getGun_data())) {
                            for (PileDataSubscribeVo.GunDataVo gunDataVo : pileDataVo.getGun_data()) {
                                String gunCode = String.valueOf(gunDataVo.getGunCode());
                                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                                if (gunRealModel != null) {
                                    //枪工作中的状态
//                            List<Integer> workStates = Arrays.asList(1, 2, 4, 5);
//                            if (workStates.contains(gunRealModel.getGunStatus())) {
                                    gunRealModel.setBatterySoc(gunDataVo.getSoc());
                                    gunRealModel.setRunMode(gunDataVo.getRunmode());
                                    gunRealModel.setGunTemp1(null);
                                    if (StringUtil.isNotEmpty(gunDataVo.getGunTemp1())) {
                                        gunRealModel.setGunTemp1(DoubleUtil.getToDouble(gunDataVo.getGunTemp1() * 0.1));
                                    }
                                    gunRealModel.setGunTemp2(null);
                                    if (StringUtil.isNotEmpty(gunDataVo.getGunTemp2())) {
                                        gunRealModel.setGunTemp2(DoubleUtil.getToDouble(gunDataVo.getGunTemp2() * 0.1));
                                    }
                                    gunRealModel.setOutVolt(gunDataVo.getOutVolt());
                                    if (StringUtil.isNotEmpty(gunDataVo.getOutVolt())) {
                                        gunRealModel.setOutVolt(DoubleUtil.getToDouble(gunDataVo.getOutVolt() * 0.1, 4));
                                    }
                                    gunRealModel.setOutCurrent(gunDataVo.getOutCurrent());
                                    if (StringUtil.isNotEmpty(gunDataVo.getOutCurrent())) {
                                        gunRealModel.setOutCurrent(DoubleUtil.getToDouble(gunDataVo.getOutCurrent() * 0.01, 4));
                                    }
                                    gunRealModel.setOutPower(null);
                                    if (StringUtil.isNotEmpty(gunRealModel.getOutVolt()) && StringUtil.isNotEmpty(gunRealModel.getOutCurrent())) {
                                        gunRealModel.setOutPower(DoubleUtil.getToDouble(gunRealModel.getOutVolt() * gunRealModel.getOutCurrent() * 0.001, 4));
                                    }
                                    gunRealModel.setReqVolt(gunDataVo.getReqVolt());
                                    if (StringUtil.isNotEmpty(gunDataVo.getReqVolt())) {
                                        gunRealModel.setReqVolt(DoubleUtil.getToDouble(gunDataVo.getReqVolt() * 0.1, 4));
                                    }
                                    gunRealModel.setReqCurrent(gunDataVo.getReqCurrent());
                                    if (StringUtil.isNotEmpty(gunDataVo.getReqCurrent())) {
                                        gunRealModel.setReqCurrent(DoubleUtil.getToDouble(gunDataVo.getReqCurrent() * 0.01, 4));
                                    }
                                    gunRealModel.setReqPower(null);
                                    if (StringUtil.isNotEmpty(gunRealModel.getReqVolt()) && StringUtil.isNotEmpty(gunRealModel.getReqCurrent())) {
                                        gunRealModel.setReqPower(DoubleUtil.getToDouble(gunRealModel.getReqVolt() * gunRealModel.getReqCurrent() * 0.001, 4));
                                    }
                                    gunRealModel.setDirMeterNum(null);
                                    if (StringUtil.isNotEmpty(gunDataVo.getDirMeterNum())) {
                                        gunRealModel.setDirMeterNum(DoubleUtil.getToDouble(gunDataVo.getDirMeterNum() * 0.001, 4));
                                    }
                                    gunRealModel.setAlterMeterNum(null);
                                    if (StringUtil.isNotEmpty(gunDataVo.getAlterMeterNum())) {
                                        gunRealModel.setAlterMeterNum(DoubleUtil.getToDouble(gunDataVo.getAlterMeterNum() * 0.001, 4));
                                    }
                                    gunRealModel.setRunTime(gunDataVo.getRunTime());
                                    gunRealModel.setRemainTime(gunDataVo.getRemainTime());
                                    gunRealModel.setTotalQt(null);
                                    if (StringUtil.isNotEmpty(gunDataVo.getTotalQt())) {
                                        gunRealModel.setTotalQt(DoubleUtil.getAbsDouble(gunDataVo.getTotalQt() * 0.001, 4));
                                    }
                                    gunRealModel.setTotalCost(null);
                                    if (StringUtil.isNotEmpty(gunDataVo.getTotalCost())) {
                                        gunRealModel.setTotalCost(DoubleUtil.getAbsBigDecimal(gunDataVo.getTotalCost().multiply(new BigDecimal("0.001")), 4));
                                    }
                                    pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                                }
//                        }
                            }
                        } else {
                            //防止数据存储错误
                            if (MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                                List<Integer> gunStatusList = Arrays.asList(1, 2, 4, 5);
                                Map<String, PileRealModel.GunRealModel> gunRealModelMap = Maps.newConcurrentMap();
                                pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> {
                                    Integer gunStatus = gunRealModel.getGunStatus();
                                    if (!gunStatusList.contains(gunStatus)) {
                                        gunRealModel.setBatterySoc(null);
                                        gunRealModel.setGunTemp1(null);
                                        gunRealModel.setGunTemp2(null);
                                        gunRealModel.setOutVolt(null);
                                        gunRealModel.setOutCurrent(null);
                                        gunRealModel.setOutPower(null);
                                        gunRealModel.setReqVolt(null);
                                        gunRealModel.setReqCurrent(null);
                                        gunRealModel.setReqPower(null);
                                        gunRealModel.setDirMeterNum(null);
                                        gunRealModel.setAlterMeterNum(null);
                                        gunRealModel.setRunTime(null);
                                        gunRealModel.setRemainTime(null);
                                        gunRealModel.setTotalQt(null);
                                        gunRealModel.setTotalCost(null);
                                        gunRealModel.setBatteryTempMax(null);
                                        gunRealModel.setBatteryTempMaxNo(null);
                                        gunRealModel.setBatteryTempMin(null);
                                        gunRealModel.setBatteryTempMinNo(null);
                                        gunRealModel.setBatteryVoltageMaxGn(null);
                                        gunRealModel.setBatteryVoltageMax(null);
                                        gunRealModel.setBatteryVoltageMinGn(null);
                                        gunRealModel.setBatteryVoltageMin(null);
                                    }
                                    gunRealModelMap.put(gunCode, gunRealModel);
                                });
                                pileRealModel.setGunRealModelMap(gunRealModelMap);
                            }
                        }
                        double rechargePower = pileRealModel.getGunRealModelMap().values().stream().filter(gun ->
                                        (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) &&
                                                StringUtil.isNotEmpty(gun.getGunStatus()) && StringUtil.isNotEmpty(gun.getOutPower()) &&
                                                (Objects.equals(gun.getGunStatus(), 1) || Objects.equals(gun.getGunStatus(), 2)))
                                .mapToDouble(PileRealModel.GunRealModel::getOutPower).sum();
                        double dischargePower = pileRealModel.getGunRealModelMap().values().stream().filter(gun ->
                                        (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) &&
                                                StringUtil.isNotEmpty(gun.getGunStatus()) && StringUtil.isNotEmpty(gun.getOutPower()) &&
                                                (Objects.equals(gun.getGunStatus(), 4) || Objects.equals(gun.getGunStatus(), 5)))
                                .mapToDouble(PileRealModel.GunRealModel::getOutPower).sum();
                        pileRealModel.setTotalPower(rechargePower + dischargePower);
                        pileRealModel.setRecChargePower(rechargePower);
                        pileRealModel.setDisChargePower(dischargePower);
                        if (StringUtil.isNotEmpty(pileDataVo.getInnerTemp())) {
                            pileRealModel.setInnerTemp(pileDataVo.getInnerTemp() * 0.1);
                        }
                        pileRealModel.setPowerModMaxTemp(null);
                        if (StringUtil.isNotEmpty(pileDataVo.getPowerModMaxTemp())) {
                            pileRealModel.setPowerModMaxTemp(pileDataVo.getPowerModMaxTemp() * 0.1);
                        }
                        pileRealModel.setMaxTempMod(pileDataVo.getMaxTempMod());
                        pileRealModel.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                        RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                    }
                });
            } catch (Exception e) {
                log.error("外网MQTT解析电桩数据失败", e);
            }
        });
    }

    /**
     * 解析电桩日志上报
     *
     * @param pileLogMap 电桩日志上报数据
     */
    public static void parsePileLog(Map<String, PileLogSubscribeVo> pileLogMap) {
        try {
            //保存日志数据上报
            pileLogMap.forEach((pileCode, devLogReportVo) -> {
                if (StringUtil.isNotEmpty(devLogReportVo.getGunCode())) {
                    PileRecordUtil.saveMqttRecord(pileCode, String.valueOf(devLogReportVo.getGunCode()), 2, 8, 1, IEGConstant.Param.PILE_LOG, devLogReportVo);
                }
            });
        } catch (Exception e) {
            log.error("内网MQTT日志数据上报失败", e);
        }
    }

    /**
     * 解析遥信数据
     *
     * @param terminalCode 设备编号
     * @param iegMqtt      遥测数据
     */
    public static void parseIegYcYx(String terminalCode, ZFPointsSubscribeVo iegMqtt) {
        try {
            Map<String, PointTableModel> pointTableMap = Maps.newHashMap();
            RedisGeneralUtil.executeGateway(terminalCode, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
                if (gatewayRealModel != null) {
                    if (StringUtil.isEmpty(gatewayRealModel.getDeviceStatus()) || gatewayRealModel.getDeviceStatus() == 88) {
                        gatewayRealModel.setDeviceStatus(1);
                    }
                    GatewayRealModel.ChannelRealModel channelRealModel;
                    if (gatewayRealModel.getChannelRealMap().containsKey(KeyUtil.MQTT)) {
                        channelRealModel = gatewayRealModel.getChannelRealMap().get(KeyUtil.MQTT);
                    } else {
                        channelRealModel = new GatewayRealModel.ChannelRealModel();
                        channelRealModel.setProtocolType(KeyUtil.MQTT);
                        channelRealModel.setAccessProtocol(KeyUtil.MQTT);
                    }
                    channelRealModel.setTxStatus(0);
                    pointTableMap.putAll(channelRealModel.getPointTableMap());
                    if (iegMqtt != null) {
                        //字符串数组数据
                        if (CollectionUtils.isNotEmpty(iegMqtt.getIegGroupList())) {
                            List<ZFPointsSubscribeVo.PointVo> iegGroupList = iegMqtt.getIegGroupList();
                            for (ZFPointsSubscribeVo.PointVo pointVo : iegGroupList) {
                                PointTableModel pointTable = new PointTableModel();
                                if (StringUtil.isNotEmpty(pointVo.getPId())) {
                                    pointTable.setDataId(Long.valueOf(pointVo.getPId()));
                                }
                                pointTable.setDataType(8);
                                pointTable.setDataValue(pointVo.getVal());
                                pointTable.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                                pointTableMap.put(KeyUtil.POINT_KEY + pointVo.getPId(), pointTable);
                            }
                        }
                        //单个点号数据
                        if (CollectionUtils.isNotEmpty(iegMqtt.getIegPointList())) {
                            List<ZFPointsSubscribeVo.PointVo> iegPointList = iegMqtt.getIegPointList();
                            for (ZFPointsSubscribeVo.PointVo pointVo : iegPointList) {
                                PointTableModel pointTable = new PointTableModel();
                                if (StringUtil.isNotEmpty(pointVo.getPId())) {
                                    pointTable.setDataId(Long.valueOf(pointVo.getPId()));
                                }
                                pointTable.setDataType(WebMqttUtil.convertFunction(pointVo.getAppType()));
                                pointTable.setDataValue(pointVo.getVal());
                                pointTable.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                                pointTableMap.put(KeyUtil.POINT_KEY + pointVo.getPId(), pointTable);
                            }
                        }
                    }
                    channelRealModel.setPointTableMap(pointTableMap);
                    gatewayRealModel.getChannelRealMap().put(KeyUtil.MQTT, channelRealModel);
                    RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);

                }
            });
            //解析网关下面的告警
            DeviceModel deviceModel = RedisDeviceUtil.getDevice(terminalCode);
            if (deviceModel != null && StringUtil.isNotEmpty(deviceModel.getTxStatus()) && deviceModel.getTxStatus() != 0) {

                //解析网关设备自身的告警
                if (MapUtils.isNotEmpty(deviceModel.getFunctionMap())) {
                    DeviceEventDataSink.parseDeviceEvent(deviceModel.getModelId(), deviceModel.getDeviceId(), deviceModel.getFunctionMap(), deviceService);
                }
                //解析网关子设备下面的告警
                ChannelModel channelModel = deviceModel.getChannelMap().get(KeyUtil.MQTT);
                if (channelModel != null && MapUtils.isNotEmpty(channelModel.getFunctionPointMap())) {
                    Map<String, Map<String, PointTableModel>> subDeviceMap = Maps.newHashMap();
                    Map<String, String> functionPointMap = channelModel.getFunctionPointMap();
                    for (Map.Entry<String, String> functionPoint : functionPointMap.entrySet()) {
                        String subDeviceId = functionPoint.getKey().split(FileUtil.COLON)[0];
                        String functionLogo = functionPoint.getKey().split(FileUtil.COLON)[1];
                        Map<String, PointTableModel> realDataModelMap = Maps.newHashMap();
                        if (subDeviceMap.containsKey(subDeviceId)) {
                            realDataModelMap = subDeviceMap.get(subDeviceId);
                        }
                        if (pointTableMap.containsKey(functionPoint.getValue())) {
                            realDataModelMap.put(functionLogo, pointTableMap.get(functionPoint.getValue()));
                        }
                        subDeviceMap.put(subDeviceId, realDataModelMap);
                    }
                    //解析设备事件数据
                    subDeviceMap.forEach((key, value) -> DeviceEventDataSink.parseSubDeviceEvent(null, key, value, deviceService));
                }
            }
        } catch (Exception e) {
            log.error("外网MQTT解析电桩ieg遥信遥测数据失败", e);
        }
    }

    /**
     * 整站信息上报
     *
     * @param terminalCode  设备编号
     * @param stationInfoVo 整站信息数据
     */
    public static void gatewayStationInfo(String terminalCode, StationInfoSubscribeVo stationInfoVo) {
        if (StringUtil.isNotEmpty(terminalCode) && stationInfoVo != null) {
            StationInfoDto stationInfo = new StationInfoDto();
            BeanUtils.copyProperties(stationInfoVo, stationInfo);
            if (CollectionUtils.isNotEmpty(stationInfoVo.getPileInfoList())) {
                List<StationInfoSubscribeVo.PileInfoDto> pileInfoList = stationInfoVo.getPileInfoList().stream().map(pileInfoVo -> {
                    StationInfoSubscribeVo.PileInfoDto pileInfo = new StationInfoSubscribeVo.PileInfoDto();
                    BeanUtils.copyProperties(pileInfoVo, pileInfo);
                    if (CollectionUtils.isNotEmpty(pileInfoVo.getItfInfoList())) {
                        pileInfo.setItfInfoList(pileInfoVo.getItfInfoList().stream().map(itfInfoVo -> {
                            StationInfoSubscribeVo.ItfInfoDto itfInfo = new StationInfoSubscribeVo.ItfInfoDto();
                            BeanUtils.copyProperties(itfInfoVo, itfInfo);
                            if (StringUtil.isNotEmpty(itfInfoVo.getItfCode())) {
                                itfInfo.setItfCode(String.valueOf(itfInfoVo.getItfCode()));
                            }
                            return itfInfo;
                        }).collect(Collectors.toList()));
                    }
                    return pileInfo;
                }).collect(Collectors.toList());
                stationInfo.setPileInfoList(pileInfoList);
            }
            stationInfoMap.put(terminalCode, stationInfo);
        }
    }

    /**
     * 费率下发响应处理
     *
     * @param pileRateSetResponseVo 费率下发响应参数
     */
    public static void rateSetRes(PileRateSetResponseSubscribeVo pileRateSetResponseVo) {
        //费率下发响应处理
        try {
            String keyId = pileRateSetResponseVo.getPilesCode() + pileRateSetResponseVo.getType() + pileRateSetResponseVo.getRateId() + SMV2gConstant.RATESET; //计算权重

            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
            }
        } catch (Exception e) {
            log.error("外网MQTT费率下发响应处理报错", e);
        }
    }

    /**
     * 启动响应处理
     *
     * @param pileStartResponseVo 启动响应参数
     */
    public static void startCmdRes(PileStartResponseSubscribeVo pileStartResponseVo) {
        try {
            //保存启动响应记录
            String gunCode = StringUtil.isNotEmpty(pileStartResponseVo.getGunCode()) ? String.valueOf(pileStartResponseVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pileStartResponseVo.getPilesCode(), gunCode, 2, 2, 1, IEGConstant.Param.PILE_START_RESPONSE, pileStartResponseVo);

            //收到启动命令响应后的处理
            String keyId = pileStartResponseVo.getPilesCode() + pileStartResponseVo.getGunCode() + SMV2gConstant.STARTCMD; //计算权重
            if (PileDemand.demandModelMap.containsKey(keyId)) {//启动命令执行失败
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                if (pileStartResponseVo.getFaileReason() != SMV2gConstant.SUNMAX_START_OK && pileStartResponseVo.getFaileReason() != SMV2gConstant.SUNMAX_APPOINTMENT_OK) {
                    demandModel.setRecFlag(true);
                    demandModel.setRecCode(pileStartResponseVo.getResponseResult());
                    demandModel.setRecMsg(pileStartResponseVo.getFaileReason());
                    demandModel.setSerialNum(pileStartResponseVo.getRecordId());
                    //更新订单异常状态数据
                    OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(pileStartResponseVo.getRecordId());
                    if (orderRecord != null) {
                        Integer originalState = orderRecord.getOrderStatus();
                        if (StringUtil.isNotEmpty(pileStartResponseVo.getFaileReason())) {
                            orderRecord.setStopReason(String.valueOf(pileStartResponseVo.getFaileReason()));
                        }
                        if (StringUtil.isNotEmpty(pileStartResponseVo.getStopDetail())) {
                            orderRecord.setStopDetailReason(pileStartResponseVo.getStopDetail());
                        }
                        if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                            orderRecord.setSiteId(PileRecordUtil.getSiteId(orderRecord.getPileCode()));
                        }
                        orderRecord.setOrderStatus(3);
                        orderRecord.setAbnormalCode(JSON.toJSONString(Arrays.asList(4, 5)));
                        orderRecordDao.save(orderRecord);
                        //启动失败 校验是否退款 保存结算记录
                        PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, new BigDecimal("0.0"), new BigDecimal("0.0"));
                    }
                }
            }
        } catch (Exception e) {
            log.error("外网MQTT启动响应处理报错", e);
        }
    }

    /**
     * 启动事件处理
     *
     * @param pileStartEventVo 启动事件参数
     */
    public static void startEvent(PileStartEventSubscribeVo pileStartEventVo) {
//        log.info("启动事件入参:{}", pileStartEventVo);
        try {
            //保存启动事件记录
            String pilesCode = pileStartEventVo.getPilesCode();
            String gunCode = StringUtil.isNotEmpty(pileStartEventVo.getGunCode()) ? String.valueOf(pileStartEventVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pilesCode, gunCode, 2, 3, 1, IEGConstant.Param.PILE_START_EVENT, pileStartEventVo);

            Integer eventResult = pileStartEventVo.getEventResult();
            //收到启动事件后的处理
            String keyId = pilesCode + gunCode + SMV2gConstant.STARTCMD;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                switch (eventResult) { //事件结果 0-启动成功 1-启动失败 2-预约成功 3-预约失败 255-其它故障
                    case SMV2gConstant.SUNMAX_START_OK:
                    case SMV2gConstant.SUNMAX_APPOINTMENT_OK:
                        demandModel.setRecCode(0);
                        break;
                    case SMV2gConstant.SUNMAX_START_FAIL:
                    case SMV2gConstant.SUNMAX_APPOINTMENT_FAIL:
                        demandModel.setRecCode(1);
                        break;
                    case SMV2gConstant.SUNMAX_APPOINTMENT_OTHER:
                        demandModel.setRecCode(255);
                        break;
                }
                //更新异常订单
                if (demandModel.getRecCode() == 1 || demandModel.getRecCode() == 255) {
                    //更新订单异常状态数据
                    OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(pileStartEventVo.getRecordId());
                    if (orderRecord != null) {
                        Integer originalState = orderRecord.getOrderStatus();
                        String startTime = null;
                        if (StringUtil.isNotEmpty(pileStartEventVo.getStartTime())) {
                            startTime = SunMaxUtil.timeStamp8Date(pileStartEventVo.getStartTime());
                        }
                        if (StringUtil.isNotEmpty(pileStartEventVo.getFaileReason())) {
                            orderRecord.setStopReason(String.valueOf(pileStartEventVo.getFaileReason()));
                        }
                        if (StringUtil.isNotEmpty(pileStartEventVo.getStopDetail())) {
                            orderRecord.setStopDetailReason(pileStartEventVo.getStopDetail());
                        }
                        if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                            orderRecord.setSiteId(PileRecordUtil.getSiteId(orderRecord.getPileCode()));
                        }
                        orderRecord.setOrderStatus(3);
                        orderRecord.setStartTime(startTime);
                        orderRecord.setAbnormalCode(JSON.toJSONString(Arrays.asList(4, 5)));
                        orderRecordDao.save(orderRecord);
                        //启动失败 校验是否退款 保存结算记录
                        PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, new BigDecimal("0.0"), new BigDecimal("0.0"));
                    }
                }
                demandModel.setRecFlag(true);
                demandModel.setRecMsg(pileStartEventVo.getFaileReason());
                demandModel.setSerialNum(pileStartEventVo.getRecordId());
            }
            if (eventResult == SMV2gConstant.SUNMAX_START_OK) { //启动成功
                RedisGeneralUtil.executePile(pilesCode, () -> {
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                    if (pileRealModel != null && pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                        PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                        //存取启动时间
                        gunRealModel.setStartTime(SunMaxUtil.timeStamp8Date(pileStartEventVo.getStartTime()));
                        if (pileStartEventVo.getBmsInfo() != null && pileStartEventVo.getBmsInfo().getCurSoc() != null) {
                            gunRealModel.setStartSoc(pileStartEventVo.getBmsInfo().getCurSoc());//记录开始充电时刻SOC
                        }
                        if (pileStartEventVo.getBmsInfo() != null && pileStartEventVo.getBmsInfo().getBcs_RemainingTime() != null) {
                            gunRealModel.setRemainTime(pileStartEventVo.getBmsInfo().getBcs_RemainingTime());
                        }
                        pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                    }
                });
                //更新订单状态 在途订单
                OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(pileStartEventVo.getRecordId());
                if (orderRecord != null) {
                    String startTime = null;
                    if (StringUtil.isNotEmpty(pileStartEventVo.getStartTime())) {
                        startTime = SunMaxUtil.timeStamp8Date(pileStartEventVo.getStartTime());
                    }
                    if (pileStartEventVo.getBmsInfo() != null && pileStartEventVo.getBmsInfo().getCurSoc() != null) {
                        orderRecord.setStartSoc(pileStartEventVo.getBmsInfo().getCurSoc());//记录开始充电时刻SOC
                    }
                    if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                        orderRecord.setSiteId(PileRecordUtil.getSiteId(orderRecord.getPileCode()));
                    }
                    orderRecord.setOrderStatus(1);
                    orderRecord.setStartTime(startTime);
                    orderRecordDao.save(orderRecord);
                }
            }
            if (eventResult == SMV2gConstant.SUNMAX_APPOINTMENT_OK) { //预约成功
                RedisGeneralUtil.executePile(pilesCode, () -> {
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                    if (pileRealModel != null && pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                        PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                        //获取预约启动时间
                        gunRealModel.setStartTime(SunMaxUtil.timeStamp8Date(pileStartEventVo.getStartTime()));
                        if (pileStartEventVo.getBmsInfo() != null && pileStartEventVo.getBmsInfo().getCurSoc() != null) {
                            gunRealModel.setStartSoc(pileStartEventVo.getBmsInfo().getCurSoc());//记录开始充电时刻SOC
                        }
                        if (pileStartEventVo.getBmsInfo() != null && pileStartEventVo.getBmsInfo().getBcs_RemainingTime() != null) {
                            gunRealModel.setRemainTime(pileStartEventVo.getBmsInfo().getBcs_RemainingTime());
                        }
                        pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                    }
                });
                //更新订单状态 预约订单
                OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(pileStartEventVo.getRecordId());
                if (orderRecord != null) {
                    if (pileStartEventVo.getBmsInfo() != null && pileStartEventVo.getBmsInfo().getCurSoc() != null) {
                        orderRecord.setStartSoc(pileStartEventVo.getBmsInfo().getCurSoc());//记录开始充电时刻SOC
                    }
                    if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                        orderRecord.setSiteId(PileRecordUtil.getSiteId(orderRecord.getPileCode()));
                    }
                    orderRecord.setOrderStatus(6);
                    orderRecordDao.save(orderRecord);
                }
            }
        } catch (Exception e) {
            log.error("外网MQTT启动事件处理报错", e);
        }

    }

    /**
     * 停止响应处理
     *
     * @param pileStopResponseVo 停止响应参数
     */
    public static void stopCmdRes(PileStopResponseSubscribeVo pileStopResponseVo) {
        try {
            //保存停止响应记录
            String gunCode = StringUtil.isNotEmpty(pileStopResponseVo.getGunCode()) ? String.valueOf(pileStopResponseVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pileStopResponseVo.getPilesCode(), gunCode, 2, 5, 1, IEGConstant.Param.PILE_STOP_RESPONSE, pileStopResponseVo);

            //收到命令响应后的处理
            String keyId = pileStopResponseVo.getPilesCode() + pileStopResponseVo.getGunCode() + SMV2gConstant.STOPCMD;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(pileStopResponseVo.getResponseResult());
                demandModel.setRecMsg(pileStopResponseVo.getFaileReason());
                demandModel.setSerialNum(pileStopResponseVo.getRecordId());
            }
        } catch (Exception e) {
            log.error("外网MQTT停止响应处理报错", e);
        }
    }

    /**
     * 停止事件处理
     *
     * @param pileStopEventVo 停止事件参数
     */
    public static void stopEvent(PileStopEventSubscribeVo pileStopEventVo) {
        try {
            //保存停止事件记录
            String gunCode = StringUtil.isNotEmpty(pileStopEventVo.getGunCode()) ? String.valueOf(pileStopEventVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pileStopEventVo.getPilesCode(), gunCode, 2, 6, 1, IEGConstant.Param.PILE_STOP_EVENT, pileStopEventVo);

            //收到命令响应后的处理
            String keyId = pileStopEventVo.getPilesCode() + pileStopEventVo.getGunCode() + SMV2gConstant.STARTCMD;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);
                demandModel.setRecMsg(pileStopEventVo.getFaileReason());
                demandModel.setSerialNum(pileStopEventVo.getRecordId());
            }
        } catch (Exception e) {
            log.error("外网MQTT停止事件处理报错", e);
        }
    }

    /**
     * 保存BMS信息
     *
     * @param pileBmsInfoVo BMS信息数据
     */
    public static void saveBmsInfo(PileBmsInfoSubscribeVo pileBmsInfoVo) {
        try {
            String pileCode = pileBmsInfoVo.getPilesCode();
            String gunCode = String.valueOf(pileBmsInfoVo.getGunCode());
            PileBmsInfoSubscribeVo.BmsInfoVo bmsInfo = pileBmsInfoVo.getBmsInfo();
            RedisGeneralUtil.executePile(pileCode, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                if (pileRealModel != null && bmsInfo != null) {
                    PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                    if (gunRealModel != null) {
                        gunRealModel.setBatteryTempMax(bmsInfo.getBsm_BatteryTempMax());
                        gunRealModel.setBatteryTempMaxNo(bmsInfo.getBsm_BatteryTempMaxNo());
                        gunRealModel.setBatteryTempMin(bmsInfo.getBsm_BatteryTempMin());
                        gunRealModel.setBatteryTempMinNo(bmsInfo.getBsm_BatteryTempMinNo());
                        gunRealModel.setBatteryVoltageMaxGn(bmsInfo.getBcs_BatteryVoltageMaxGN());
                        gunRealModel.setBatteryVoltageMax(bmsInfo.getBcs_BatteryVoltageMax());
                        gunRealModel.setBatteryVoltageMinGn(bmsInfo.getBcs_BatteryVoltageMinGN());
                        gunRealModel.setBatteryVoltageMin(bmsInfo.getBcs_BatteryVoltageMin());
                        if (StringUtil.isNotEmpty(bmsInfo.getBcs_BatteryVoltageMax())) {
                            gunRealModel.setBatteryVoltageMax(DoubleUtil.getToDouble(bmsInfo.getBcs_BatteryVoltageMax() * 0.01, 4));
                        }
                        if (StringUtil.isNotEmpty(bmsInfo.getBcs_BatteryVoltageMin())) {
                            gunRealModel.setBatteryVoltageMin(DoubleUtil.getToDouble(bmsInfo.getBcs_BatteryVoltageMin() * 0.01, 4));
                        }

                        if (StringUtil.isNotEmpty(bmsInfo.getBcpAllowChargeCellVmax())) {
                            gunRealModel.setBcpAllowChargeCellVMax(DoubleUtil.getToDouble(bmsInfo.getBcpAllowChargeCellVmax() * 0.01));
                        }
                        if (StringUtil.isNotEmpty(bmsInfo.getBcpAllowChargeCurrentMax())) {
                            gunRealModel.setBcpAllowChargeCurrentMax(DoubleUtil.getToDouble(bmsInfo.getBcpAllowChargeCurrentMax() * 0.1));
                        }
                        if (StringUtil.isNotEmpty(bmsInfo.getBcpBatteryNorminalCap())) {
                            gunRealModel.setBcpBatteryNominalCap(DoubleUtil.getToDouble(bmsInfo.getBcpBatteryNorminalCap() * 0.1));
                        }
                        if (StringUtil.isNotEmpty(bmsInfo.getBcpAllowChargeVmax())) {
                            gunRealModel.setBcpAllowChargeVMax(DoubleUtil.getToDouble(bmsInfo.getBcpAllowChargeVmax() * 0.1));
                        }
                        gunRealModel.setBcpAllowTempMax(bmsInfo.getBcpAllowTempMax());
                        gunRealModel.setBatteryType(bmsInfo.getBatteryType());

                        pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
//                    Map<String, Object> bmsInfoMap = new HashMap<>();
//                    bmsInfoMap.put("ts", System.currentTimeMillis());
//                    bmsInfoMap.put("recordId", gunRealModel.getSerialNum());
//                    bmsInfoMap.put("bcl_VoltageRequire", bmsInfo.getBmsRequestVolt());//电压需求 0.1v
//                    bmsInfoMap.put("bcl_CurrentRequire", bmsInfo.getBmsRequestCurrent());//电流需求 0.1a
//                    bmsInfoMap.put("bcl_ChargeMode", bmsInfo.getChargeMode());//充电模式
//                    bmsInfoMap.put("bcs_ChargeVoltMeasurement", bmsInfo.getBmsMeasureVolt());//电压测量值
//                    bmsInfoMap.put("bcs_ChargeCurrentMeasurement", bmsInfo.getBmsMeasureCurrent());//电流测量值
//                    bmsInfoMap.put("bcs_BatteryVoltageMax", bmsInfo.getBcs_BatteryVoltageMax());//最高单体蓄电池电压 0.01v
//                    bmsInfoMap.put("bcs_BatteryVoltageMaxGN", bmsInfo.getBcs_BatteryVoltageMaxGN());//最高动力电池组号
//                    bmsInfoMap.put("bcs_BatteryVoltageMin", bmsInfo.getBcs_BatteryVoltageMin());//最低动力电池电压
//                    bmsInfoMap.put("bcs_BatteryVoltageMinGN", bmsInfo.getBcs_BatteryVoltageMinGN());//最低动力电池所在组号
//                    bmsInfoMap.put("bcs_CurSoc", bmsInfo.getCurSoc());//当前SOC
//                    bmsInfoMap.put("bcs_RemainingTime", bmsInfo.getBcs_RemainingTime());//充放电剩余时间
//                    bmsInfoMap.put("bsm_BatteryVoltageMaxNo", bmsInfo.getBsm_BatteryVoltageMaxNo());//最高单体动力蓄电池电压所在编号
//                    //bmsInfoMap.put("bsm_BatteryVoltageMinNo",bmsInfo.getBsm_BatteryVoltageMinNo());//最低单体动力蓄电池电压所在编号
//                    bmsInfoMap.put("bsm_BatteryTempMax", bmsInfo.getBsm_BatteryTempMax());//最高动力蓄电池温度
//                    bmsInfoMap.put("bsm_BatteryTempMaxNo", bmsInfo.getBsm_BatteryTempMaxNo());//最高动力蓄电池温度所在编号
//                    bmsInfoMap.put("bsm_BatteryTempMin", bmsInfo.getBsm_BatteryTempMin());//最低动力蓄电池温度
//                    bmsInfoMap.put("bsm_BatteryTempMinNo", bmsInfo.getBsm_BatteryTempMinNo());//最低动力蓄电池温度所在编号
//                    bmsInfoMap.put("bsm_BatteryVoltageState", bmsInfo.getBsm_BatteryVoltageState());//动力蓄电池电压状态
//                    bmsInfoMap.put("bsm_SocState", bmsInfo.getBsm_SocState());//动力蓄电池SOC荷电状态
//                    bmsInfoMap.put("bsm_BatteryCurrentState", bmsInfo.getBsm_BatteryCurrentState());//动力蓄电池充电电流状态
//                    bmsInfoMap.put("bsm_BatteryTempState", bmsInfo.getBsm_BatteryTempState());//动力蓄电池充温度状态
//                    bmsInfoMap.put("bsm_InsulationState", bmsInfo.getBsm_InsulationState());//动力蓄电池绝缘状态
//                    bmsInfoMap.put("bsm_BatteryOutputConnectorState", bmsInfo.getBsm_BatteryOutputConnectorState());//动力蓄电池组输出连接器连接状态
//                    bmsInfoMap.put("bsm_ChargeEnable", bmsInfo.getBsm_ChargeEnable());//充放电允许
                        //TODO bms数据入库 后期调整
//                    TaosOperateService taosOperateService = SpringBeanUtil.getBean(TaosOperateService.class);
//                    taosOperateService.insertCpBmsData(pileCode, gunCode, bmsInfoMap);
//                }
                    }
                }
            });
        } catch (Exception e) {
            log.error("外网MQTT上报BMS数据处理报错", e);
        }
    }

    /**
     * 功率控制响应
     *
     * @param pileCtrlResponseVo 功率控制响应参数
     */
    public static void powerCtrlRes(PileCtrlResponseSubscribeVo pileCtrlResponseVo) {
        try {
            //收到命令响应后的处理
            String keyId = pileCtrlResponseVo.getPilesCode() + pileCtrlResponseVo.getGunCode() + SMV2gConstant.POWERCTRL;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(pileCtrlResponseVo.getResponseResult());
            }
        } catch (Exception e) {
            log.error("外网MQTT功率控制响应处理报错", e);
        }
    }

    /**
     * 记录上报
     *
     * @param terminalCode 终端编号
     * @param pileRecordVo 记录上报数据
     */
    public static void recordRes(String terminalCode, PileRecordSubscribeVo pileRecordVo) {

        try {
            //保存记录上报数据
            String gunCode = StringUtil.isNotEmpty(pileRecordVo.getGunCode()) ? String.valueOf(pileRecordVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pileRecordVo.getPilesCode(), gunCode, 2, 7, 1, IEGConstant.Param.PILE_RECORD_REQUEST, pileRecordVo);

            if (StringUtil.isEmpty(pileRecordVo.getPlatformId()) || Objects.equals(pileRecordVo.getPlatformId(), PlatformLogoVo.SUNMAX_LOGO)) {
                //新记录确认
                JSONObject newParam = new JSONObject();
                newParam.put("pileCode", pileRecordVo.getPilesCode());
                newParam.put("recordId", pileRecordVo.getRecordId());
                newParam.put("recordSeq", pileRecordVo.getRecordSeq());
                newParam.put("reportType", pileRecordVo.getReportType());
                newParam.put("gunCode", pileRecordVo.getGunCode());
                IEGTopicVo newIegTopicVo = IEGTopicVo.builder()
                        .mid("84012")
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.PILE_RECORD_CONFIRM).paras(newParam).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.SERVICE_COMMAND;
                //发送数据
                WebMqttConfig.sendToMqtt(terminalCode, topicName, newIegTopicVo);
            }

            //定义订单记录实体类
            OrderRecordEntity orderRecord = new OrderRecordEntity();
            //定义时段电量记录实体类
            TimeFrameQEntity timeFrameQt = null;

            //是否挂单
            boolean isRepair = false;
            Integer originalState = null;
            //保存充放电记录
            OrderRecordEntity orderRecordEntity = orderRecordDao.findByOrderNum(pileRecordVo.getRecordId());
            if (orderRecordEntity == null) {
                orderRecord.setId(pileRecordVo.getRecordId());
                orderRecord.setOrderNum(pileRecordVo.getRecordId());
                orderRecord.setIsOrderly(2);
                orderRecord.setType(1);
            } else {
                //订单记录是挂起的话 说明需要补单
                if (StringUtil.isNotEmpty(orderRecordEntity.getOrderStatus()) && orderRecordEntity.getOrderStatus() == 4) {
                    isRepair = true;
                }
                orderRecord = orderRecordEntity;
                originalState = orderRecord.getOrderStatus();
            }
            orderRecord.setOrderStatus(2);
            //预约取消相关状态
            if (pileRecordVo.getStopReason() == 0x1005 || pileRecordVo.getStopReason() == 0x1011) {
                orderRecord.setOrderStatus(5); //预约取消
            }
            if (StringUtil.isEmpty(orderRecord.getPrepayMoney())) {
                orderRecord.setPrepayMoney(new BigDecimal("0.0"));
            }
            if (StringUtil.isEmpty(pileRecordVo.getRunMode())) {
                orderRecord.setRunMode(pileRecordVo.getRunMode());
            }
            if (pileRecordVo.getUserAccount() != null) {
                UserAccount userAccount = pileRecordVo.getUserAccount();
                if (StringUtil.isEmpty(orderRecord.getAccountType())) {
                    orderRecord.setAccountType(userAccount.getAccountType());
                }
                if (StringUtil.isEmpty(orderRecord.getAccountData())) {
                    orderRecord.setAccountData(userAccount.getAccountData());
                }
            }
            if (StringUtil.isEmpty(orderRecord.getStarter())) {
                orderRecord.setStarter(pileRecordVo.getStarter());
            }
            if (StringUtil.isEmpty(orderRecord.getPileCode())) {
                orderRecord.setPileCode(pileRecordVo.getPilesCode());
            }
            if (StringUtil.isEmpty(orderRecord.getGunCode())) {
                orderRecord.setGunCode(pileRecordVo.getGunCode());
            }
            if (StringUtil.isEmpty(orderRecord.getRunMode())) {
                orderRecord.setRunMode(pileRecordVo.getRunMode());
            }
            if (StringUtil.isEmpty(orderRecord.getPlatformLogo())) {
                orderRecord.setPlatformLogo(pileRecordVo.getPlatformId());
            }
            if (pileRecordVo.getStrategy() != null) {
                Strategy strategy = pileRecordVo.getStrategy();
                if (StringUtil.isEmpty(orderRecord.getStrategyType())) {
                    orderRecord.setStrategyType(strategy.getStrategyType());
                }
                if (StringUtil.isEmpty(orderRecord.getStrategyCfg())) {
                    orderRecord.setStrategyCfg(PileRecordUtil.getStrategyCfg(pileRecordVo.getRunMode(), strategy.getStrategyType(), strategy.getStrategyCfg()));
                }
                if (StringUtil.isNotEmpty(strategy.getStartTime())) {
                    orderRecord.setStrategyTime(SunMaxUtil.timeStamp8Date(strategy.getStartTime()));
                }
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getStartTime())) {
                orderRecord.setStartTime(SunMaxUtil.timeStamp8Date(pileRecordVo.getStartTime()));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getEndTime())) {
                orderRecord.setEndTime(SunMaxUtil.timeStamp8Date(pileRecordVo.getEndTime()));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getStartDCMeters())) {
                orderRecord.setStartDirMeter(DoubleUtil.getAbsDouble((double) pileRecordVo.getStartDCMeters() / 1000, 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getEndDCMeters())) {
                orderRecord.setEndDirMeter(DoubleUtil.getAbsDouble((double) pileRecordVo.getEndDCMeters() / 1000, 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getTotalQ())) { //总电量
                orderRecord.setTotalQt(DoubleUtil.getAbsDouble(pileRecordVo.getTotalQ() / 1000, 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getTotalCost())) { //总费用
                orderRecord.setTotalCost(DoubleUtil.getAbsBigDecimal(pileRecordVo.getTotalCost().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getTotalElecFee())) { //总电费
                orderRecord.setTotalElect(DoubleUtil.getAbsBigDecimal(pileRecordVo.getTotalElecFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getTotalServiceFee())) { //总服务费
                orderRecord.setTotalFee(DoubleUtil.getAbsBigDecimal(pileRecordVo.getTotalServiceFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getSharpElecFee())) { //尖电费
                orderRecord.setJElect(DoubleUtil.getAbsBigDecimal(pileRecordVo.getSharpElecFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getSharpServiceFee())) { //尖服务费
                orderRecord.setJFee(DoubleUtil.getAbsBigDecimal(pileRecordVo.getSharpServiceFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getSharpQ())) { //尖电量
                orderRecord.setJQt(DoubleUtil.getAbsDouble(pileRecordVo.getSharpQ() / 1000, 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getPeakElecFee())) { //峰电费
                orderRecord.setFElect(DoubleUtil.getAbsBigDecimal(pileRecordVo.getPeakElecFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getPeakServiceFee())) { //峰服务费
                orderRecord.setFFee(DoubleUtil.getAbsBigDecimal(pileRecordVo.getPeakServiceFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getPeakQ())) { //峰电量
                orderRecord.setFQt(DoubleUtil.getAbsDouble(pileRecordVo.getPeakQ() / 1000, 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getFlatElecFee())) { //平电费
                orderRecord.setPElect(DoubleUtil.getAbsBigDecimal(pileRecordVo.getFlatElecFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getFlatServiceFee())) { //平服务费
                orderRecord.setPFee(DoubleUtil.getAbsBigDecimal(pileRecordVo.getFlatServiceFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getFlatQ())) { //平电量
                orderRecord.setPQt(DoubleUtil.getAbsDouble(pileRecordVo.getFlatQ() / 1000, 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getValleyElecFee())) { //谷电费
                orderRecord.setGElect(DoubleUtil.getAbsBigDecimal(pileRecordVo.getValleyElecFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getValleyServiceFee())) { //谷服务费
                orderRecord.setGFee(DoubleUtil.getAbsBigDecimal(pileRecordVo.getValleyServiceFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getValleyQ())) { //谷电量
                orderRecord.setGQt(DoubleUtil.getAbsDouble(pileRecordVo.getValleyQ() / 1000, 4));
            }
            if (StringUtil.isEmpty(orderRecord.getStartSoc()) || orderRecord.getStartSoc() == 0) {
                orderRecord.setStartSoc(pileRecordVo.getStartSoc());
            }
            if (StringUtil.isEmpty(orderRecord.getEndSoc()) || orderRecord.getEndSoc() == 0) {
                orderRecord.setEndSoc(pileRecordVo.getEndSoc());
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getStopReason()) && StringUtil.isEmpty(orderRecord.getStopReason())) {
                orderRecord.setStopReason(String.valueOf(pileRecordVo.getStopReason()));
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getStopDetail()) && StringUtil.isEmpty(orderRecord.getStopDetailReason())) {
                orderRecord.setStopDetailReason(String.valueOf(pileRecordVo.getStopDetail()));
            }
            if (StringUtil.isEmpty(orderRecord.getBusVin())) {
                orderRecord.setBusVin(pileRecordVo.getBusVin());
            }
            if (StringUtil.isNotEmpty(pileRecordVo.getRateTemplateId()) && StringUtil.isEmpty(orderRecord.getRateTemplateId())) {
                orderRecord.setRateTemplateId(pileRecordVo.getRateTemplateId());
            }
            if (StringUtil.isEmpty(orderRecord.getTimeFrameNum())) {
                orderRecord.setTimeFrameNum(pileRecordVo.getTimeFrameNum());
            }
            if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                orderRecord.setSiteId(PileRecordUtil.getSiteId(orderRecord.getPileCode()));
            }
            String timeFrameQtId = UUID.randomUUID().toString().replace(FileUtil.BAR, FileUtil.separator).toLowerCase();
            orderRecord.setTimeFrameId(timeFrameQtId);
            if (CollectionUtils.isNotEmpty(pileRecordVo.getTimeFrameQ())) {
                //存储电桩电费，服务费，尖峰平谷相关价格
//            PileRecordUtil.getPileChargeElect(orderRecord, pileRecordVo.getStartTime(), pileRecordVo.getTimeFrameQ());
                //保存时段电量的数据
                timeFrameQt = PileRecordUtil.getTimeFrameQtData(timeFrameQtId, pileRecordVo.getRecordId(), pileRecordVo.getStartTime(), pileRecordVo.getTimeFrameQ());
            }

            //保存时段电量数据
            if (timeFrameQt != null) {
                timeFrameQDao.save(timeFrameQt);
            }

            //晟曼平台启动的订单 进行结算
            if (Objects.equals(orderRecord.getPlatformLogo(), PlatformLogoVo.SUNMAX_LOGO)) {
                //保存计费详情相关的数据
                List<ChargeTariffRecordEntity> tariffRecordList = PileRecordUtil.saveChargeTariffRecordList(orderRecord, chargeTariffRecordDao);
                //保存正常结算记录数据
                if (CollectionUtils.isNotEmpty(tariffRecordList) && orderRecord.getOrderStatus() != 4) {
                    BigDecimal electMoney = tariffRecordList.stream().map(ChargeTariffRecordEntity::getElectMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal serviceMoney = tariffRecordList.stream().map(ChargeTariffRecordEntity::getServiceMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                    log.info("外网订单号:{}, 结算电费金额:{}, 服务费金额:{}, 原始状态:{}, 结算状态:{},", orderRecord.getOrderNum(), electMoney, serviceMoney, originalState, orderRecord.getOrderStatus());
                    PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, electMoney, serviceMoney);
                } else if (orderRecord.getOrderStatus() == 5) { //订单取消 正常退款
                    PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, new BigDecimal("0.0"), new BigDecimal("0.0"));
                }

                //更新补单数据
                if (isRepair && orderRecord.getOrderStatus() != 4) {
                    PileRecordUtil.batchUpdateRepairOrderRecord(Collections.singletonList(orderRecord));
                }
            } else {
                PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, new BigDecimal("0.0"), new BigDecimal("0.0"));
            }

            //校验订单是否异常
            //1-时间异常：订单时长大于24h
            //2-大额订单：上报的订单总金额大于1000元
            //3-电量异常：电量大于500kwh
            //4-无效订单：电量小于1或者订单时长小于1分钟
            //5-费用异常：订单金额为0
            Set<Integer> abnormalCode = Sets.newHashSet();
            if (StringUtil.isNotEmpty(orderRecord.getStartTime()) && StringUtil.isNotEmpty(orderRecord.getEndTime())) {
                LocalDateTime startTime = DateUtil.strToLocalDateTime(orderRecord.getStartTime());
                LocalDateTime endTime = DateUtil.strToLocalDateTime(orderRecord.getEndTime());
                if (DateUtil.compareDiffBetweenHours(startTime, endTime) > 24) {
                    abnormalCode.add(1);
                }
                if (DateUtil.compareDiffBetweenSecond(startTime, endTime) < 60) {
                    abnormalCode.add(4);
                }
            }
            if (StringUtil.isNotEmpty(orderRecord.getTotalCost())) {
                if (orderRecord.getTotalCost().compareTo(new BigDecimal(1000)) > 0) {
                    abnormalCode.add(2);
                }
                if (orderRecord.getTotalCost().compareTo(BigDecimal.ZERO) == 0) {
                    abnormalCode.add(5);
                }
            }
            if (StringUtil.isNotEmpty(orderRecord.getTotalQt())) {
                if (orderRecord.getTotalQt() > 500) {
                    abnormalCode.add(3);
                }
                if (orderRecord.getTotalQt() < 1) {
                    abnormalCode.add(4);
                }
            }
            if (CollectionUtils.isNotEmpty(abnormalCode)) {
                orderRecord.setAbnormalCode(JSON.toJSONString(abnormalCode));
            } else if (StringUtil.isNotEmpty(orderRecord.getAbnormalCode())) {
                orderRecord.setAbnormalCode(null);
            }

            //保存订单记录数据
            orderRecordDao.save(orderRecord);
        } catch (Exception e) {
            log.error("外网MQTT保存订单记录数据处理报错", e);
        }
    }


    /**
     * 网关参数下发响应处理
     *
     * @param terminalCode             网关设备编号
     * @param gwLoadParamSetResponseVo 网关参数下发响应参数
     */
    public static void gwLoadParamSetResponse(String terminalCode, GwLoadParamSetResponseSubscribeVo gwLoadParamSetResponseVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + IEGConstant.Param.LOAD_PARAMSET;
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                GateWayControlDto gateWayControlDto = new GateWayControlDto();
                gateWayControlDto.getControlValueList().add(new GateWayControlDto.ControlValue(1, gwLoadParamSetResponseVo.getEnable().toString()));
                gateWayControlDto.getControlValueList().add(new GateWayControlDto.ControlValue(2, gwLoadParamSetResponseVo.getMax_load().toString()));
                gateWayControlDto.getControlValueList().add(new GateWayControlDto.ControlValue(3, gwLoadParamSetResponseVo.getLoadWaveUpCfg().toString()));
                gateWayControlDto.getControlValueList().add(new GateWayControlDto.ControlValue(4, gwLoadParamSetResponseVo.getPile_maxPower().toString()));
                gateWayControlDto.getControlValueList().add(new GateWayControlDto.ControlValue(5, gwLoadParamSetResponseVo.getMonitorPeriod().toString()));
                demandModel.setRecMsg(gateWayControlDto);
            }
        } catch (Exception e) {
            log.error("外网MQTT网关参数下发响应处理失败", e);
        }
    }

    /**
     * 网关通用参数下发响应处理
     *
     * @param terminalCode             网关设备编号
     * @param gwGeneralParamResponseVo 网关通用参数下发响应参数
     */
    public static void gwGeneralParamResponse(String terminalCode, GwGeneralParamResponseSubscribeVo gwGeneralParamResponseVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + gwGeneralParamResponseVo.getPolicyId() + IEGConstant.Param.GENERAL_PARAMGETSET;
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                if (StringUtil.isEmpty(gwGeneralParamResponseVo.getCmdType())) {
                    demandModel.setRecCode(1);//执行失败
                }
                GateWayPolicyDto gateWayPolicyDto = new GateWayPolicyDto();
                demandModel.setRecCode(0);//执行成功
                switch (gwGeneralParamResponseVo.getCmdType()) {
                    case "response":
                        gateWayPolicyDto.setIssuedStatus(0); //下发成功
                        break;
                    case "response_invaild_policy":
                        gateWayPolicyDto.setIssuedStatus(9);//无效策略
                        break;
                    default:
                        gateWayPolicyDto.setIssuedStatus(255);//其它原因-未知错误
                        break;
                }
                gateWayPolicyDto.setDeviceCode(terminalCode);
                gateWayPolicyDto.setName(gwGeneralParamResponseVo.getName());
                gateWayPolicyDto.setPolicyCfg(gwGeneralParamResponseVo.getPolicyCfg());
                gateWayPolicyDto.setPolicyPeriod(gwGeneralParamResponseVo.getPolicyPeriod());
                gateWayPolicyDto.setCmdType(gwGeneralParamResponseVo.getCmdType());
                demandModel.setRecMsg(JSON.toJSON(gateWayPolicyDto));
            }
        } catch (Exception e) {
            log.error("外网MQTT网关通用参数下发响应处理失败", e);
        }
    }

    /**
     * 调控需求下发响应
     *
     * @param terminalCode             网关设备编号
     * @param gwPVControlSetResponseVo 调控需求下发响应参数
     */
    public static void gatewayPeakValleyControlSetResponse(String terminalCode, GwPVControlSetResponseSubscribeVo gwPVControlSetResponseVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + IEGConstant.Param.GATEWAY_PEAK_VALLEY_CONTROL_SET; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(StringUtil.isNotEmpty(gwPVControlSetResponseVo.getResult()) ? gwPVControlSetResponseVo.getResult() - 1 : 1);
            }
        } catch (Exception e) {
            log.error("外网MQTT调控需求下发响应失败", e);
        }
    }

    /**
     * 调控需求终止下发响应
     *
     * @param terminalCode              网关设备编号
     * @param gwPVControlStopResponseVo 调控需求终止下发响应参数
     */
    public static void gatewayPeakValleyControlStopResponse(String terminalCode, GwPVControlStopResponseSubscribeVo gwPVControlStopResponseVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + IEGConstant.Param.GATEWAY_PEAK_VALLEY_CONTROL_STOP; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(StringUtil.isNotEmpty(gwPVControlStopResponseVo.getResult()) ? gwPVControlStopResponseVo.getResult() - 1 : 1);
            }
        } catch (Exception e) {
            log.error("外网MQTT调控需求终止下发响应失败", e);
        }
    }

    /**
     * 电桩复位响应数据
     *
     * @param pileResetResponseSubscribeVo 电桩复位响应
     */
    public static void pileResetResponse(PileResetResponseSubscribeVo pileResetResponseSubscribeVo) {
        try {
            PileRecordUtil.saveMqttRecord(pileResetResponseSubscribeVo.getPilesCode(), null, 2, 10, 1, IEGConstant.Param.PILE_RESET_RESPONSE, pileResetResponseSubscribeVo);
            //收到此命令 立即响应需求
            String keyId = pileResetResponseSubscribeVo.getPilesCode() + pileResetResponseSubscribeVo.getType() + SMV2gConstant.PILE_RESET;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(pileResetResponseSubscribeVo.getResult());//执行结果
            }
        } catch (Exception e) {
            log.error("外网MQTT电桩复位响应失败", e);
        }
    }

    /**
     * 网关状态查询响应
     *
     * @param terminalCode    网关设备编号
     * @param gatewayStatusVo 网关状态响应数据
     */
    public static void gatewayStatus(String terminalCode, GatewayStatusSubscribeVo gatewayStatusVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + WebTopicConstant.GATEWAY_STATUS_REQUEST; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(JSON.toJSON(gatewayStatusVo));
            }
        } catch (Exception e) {
            log.error("外网MQTT网关状态查询响应失败", e);
        }
    }

    /**
     * 服务列表查询响应
     *
     * @param terminalCode 网关设备编号
     * @param serviceList  服务列表数据
     */
    public static void serviceList(String terminalCode, List<ServiceListSubscribeVo> serviceList) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + WebTopicConstant.SERVICE_LIST_REQUEST; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(JSON.toJSON(serviceList));
            }
        } catch (Exception e) {
            log.error("外网MQTT服务列表查询响应失败", e);
        }
    }

    /**
     * 同步时钟响应
     *
     * @param terminalCode 网关设备编号
     * @param syncClockVo  同步时钟数据
     */
    public static void syncClock(String terminalCode, SyncClockSubscribeVo syncClockVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + WebTopicConstant.SYNC_CLOCK_REQUEST; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(JSON.toJSON(syncClockVo));
            }
        } catch (Exception e) {
            log.error("外网MQTT同步时钟响应失败", e);
        }
    }

    /**
     * 网关重启响应
     *
     * @param terminalCode 网关设备编号
     * @param rebootVo     网关重启响应数据
     */
    public static void reboot(String terminalCode, RebootSubscribeVo rebootVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + WebTopicConstant.REBOOT_REQUEST; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(JSON.toJSON(rebootVo));
            }
        } catch (Exception e) {
            log.error("外网MQTT网关重启响应失败", e);
        }
    }

    /**
     * 许可状态查询响应
     *
     * @param terminalCode    网关设备编号
     * @param licenseStatusVo 许可状态响应数据
     */
    public static void licenseStatus(String terminalCode, LicenseSubscribeVo licenseStatusVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + WebTopicConstant.LICENSE_STATUS_REQUEST; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(JSON.toJSON(licenseStatusVo));
            }
        } catch (Exception e) {
            log.error("外网MQTT许可状态查询响应失败", e);
        }
    }

    /**
     * 获取设备key响应
     *
     * @param terminalCode 网关设备编号
     * @param licenseKeyVo 设备key响应数据
     */
    public static void licenseKey(String terminalCode, LicenseSubscribeVo licenseKeyVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + WebTopicConstant.LICENSE_KET_REQUEST; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(JSON.toJSON(licenseKeyVo));
            }
        } catch (Exception e) {
            log.error("外网MQTT获取设备key响应失败", e);
        }
    }

    /**
     * 下发许可响应
     *
     * @param terminalCode 网关设备编号
     * @param licenseVo    下发许可响应数据
     */
    public static void license(String terminalCode, LicenseSubscribeVo licenseVo) {
        try {
            //收到此命令 立即响应需求
            String keyId = terminalCode + WebTopicConstant.LICENSE_REQUEST; //计算权重
            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                GatewayDemandModel demandModel = GatewayDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(JSON.toJSON(licenseVo));
            }
        } catch (Exception e) {
            log.error("外网MQTT下发许可响应失败", e);
        }
    }

}
