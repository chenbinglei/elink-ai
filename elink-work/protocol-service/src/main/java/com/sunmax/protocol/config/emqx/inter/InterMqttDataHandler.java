package com.sunmax.protocol.config.emqx.inter;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.constant.CmdConstant;
import com.sunmax.common.dto.device.DevicePointDto;
import com.sunmax.common.dto.device.PileFaultDto;
import com.sunmax.common.dto.protocol.mqtt.UpdateInfoDto;
import com.sunmax.common.dto.protocol.mqtt.inter.*;
import com.sunmax.common.dto.protocol.mqtt.web.CtrlBoardInfo;
import com.sunmax.common.dto.protocol.mqtt.web.falut.ChargerRunFault;
import com.sunmax.common.dto.protocol.mqtt.web.falut.ElecModuleFault;
import com.sunmax.common.dto.protocol.mqtt.web.falut.ItfFault;
import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import com.sunmax.common.enums.GeneralFieldEnum;
import com.sunmax.common.model.ChannelModel;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.FunctionModel;
import com.sunmax.common.model.PointTableModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.*;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.PlatformLogoVo;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.device.DeviceBatchUpdateVo;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.mqtt.inter.*;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import com.sunmax.protocol.constant.SMV2gConstant;
import com.sunmax.protocol.dao.AlarmRecordDao;
import com.sunmax.protocol.dao.ChargeTariffRecordDao;
import com.sunmax.protocol.dao.OrderRecordDao;
import com.sunmax.protocol.dao.TimeFrameQDao;
import com.sunmax.protocol.demand.PileDemand;
import com.sunmax.protocol.entity.AlarmRecordEntity;
import com.sunmax.protocol.entity.ChargeTariffRecordEntity;
import com.sunmax.protocol.entity.OrderRecordEntity;
import com.sunmax.protocol.entity.TimeFrameQEntity;
import com.sunmax.protocol.model.PileDemandModel;
import com.sunmax.protocol.runner.ProtocolRunner;
import com.sunmax.protocol.service.PileCtrlService;
import com.sunmax.protocol.service.feign.DeviceService;
import com.sunmax.protocol.service.feign.TogetherService;
import com.sunmax.protocol.task.DeviceEventDataSink;
import com.sunmax.protocol.util.FirmwareUtil;
import com.sunmax.protocol.util.PileRecordUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.sunmax.protocol.task.KeepAlivePollingTask.heartBeatMap;

@Slf4j
@Configuration
public class InterMqttDataHandler {

    /**
     * 策略响应映射表 电桩编号+枪编号 -> 上次时间
     */
    private static final Map<String, LocalDateTime> strategyMap = Maps.newHashMap();

    private static AlarmRecordDao alarmRecordDao;
    private static OrderRecordDao orderRecordDao;
    private static TimeFrameQDao timeFrameQDao;

    private static TogetherService togetherService;
    private static ChargeTariffRecordDao chargeTariffRecordDao;

    private static DeviceService deviceService;

    private static CostFormat apply(EventRateReqPublicVo.TimeFrameRate timeFrameRate) {
        CostFormat costFormat = new CostFormat();
        if (StringUtil.isNotEmpty(timeFrameRate.getStartTime())) {
            costFormat.setStartTime(Math.toIntExact(timeFrameRate.getStartTime()));
        }
        if (StringUtil.isNotEmpty(timeFrameRate.getEndTime())) {
            costFormat.setEndTime(Math.toIntExact(timeFrameRate.getEndTime()));
        }
        if (StringUtil.isNotEmpty(timeFrameRate.getPrice())) {
            costFormat.setPrice(Double.valueOf(timeFrameRate.getPrice()));
        }
        if (StringUtil.isNotEmpty(timeFrameRate.getServiceCharger())) {
            costFormat.setServiceCharger(Double.valueOf(timeFrameRate.getServiceCharger()));
        }
        costFormat.setType(timeFrameRate.getType());
        return costFormat;
    }


    @PostConstruct
    public void init() {
        alarmRecordDao = SpringBeanUtil.getBean(AlarmRecordDao.class);
        orderRecordDao = SpringBeanUtil.getBean(OrderRecordDao.class);
        timeFrameQDao = SpringBeanUtil.getBean(TimeFrameQDao.class);
        togetherService = SpringBeanUtil.getBean(TogetherService.class);
        chargeTariffRecordDao = SpringBeanUtil.getBean(ChargeTariffRecordDao.class);
        deviceService = SpringBeanUtil.getBean(DeviceService.class);
    }

    /**
     * 费率请求处理
     *
     * @param pilesCode 电桩编号
     * @param rateReqVo 费率请求数据
     */
    public static void rateReq(String pilesCode, RateReqVo rateReqVo) {
        //费率响应处理
        try {
            Map<String, EventRateReqPublicVo> rateReqMap = togetherService.findSiteRateInfoByPileCodes(Collections.singletonList(pilesCode)).getData();
            if (MapUtils.isNotEmpty(rateReqMap) && rateReqMap.containsKey(pilesCode)) {
                EventRateReqPublicVo rateReqPublicVo = rateReqMap.get(pilesCode);
                RateResDto rateRes = new RateResDto();
                rateRes.setPilesCode(String.valueOf(Collections.singletonList(pilesCode)));
                if (StringUtil.isNotEmpty(rateReqVo.getType())) {
                    if (rateReqVo.getType() == 0) { //充电费率
                        rateRes.setCRateId(rateReqPublicVo.getCRateId());
                        rateRes.setCTimeFrameNum(rateReqPublicVo.getCTimeFrameNum());
                        rateRes.setCTimeFrameRate(rateReqPublicVo.getCTimeFrameRate().stream().map(InterMqttDataHandler::apply).sorted(Comparator.comparing(CostFormat::getStartTime)).collect(Collectors.toList()));
                    }
                    if (rateReqVo.getType() == 1) { //放电费率
                        rateRes.setDRateId(rateReqPublicVo.getDRateId());
                        rateRes.setDRateId(rateReqPublicVo.getDRateId());
                        rateRes.setDTimeFrameNum(rateReqPublicVo.getDTimeFrameNum());
                        rateRes.setDTimeFrameRate(rateReqPublicVo.getDTimeFrameRate().stream().map(InterMqttDataHandler::apply).sorted(Comparator.comparing(CostFormat::getStartTime)).collect(Collectors.toList()));
                    }
                    //费率响应数据发送
                    InterMqttConfig.sendToMqtt(pilesCode, CmdConstant.CMD_RATE_RES, rateRes);
                }
            } else {
                log.info("未找到该设备的费率数据, 桩编号:{}, 费率类型{}", pilesCode, rateReqVo.getType());
            }
        } catch (Exception e) {
            log.error("内网MQTT费率响应处理失败", e);
        }
    }

    /**
     * 子设备上线通知
     *
     * @param pilesCode         电桩编号
     * @param subLinkUpNotifyVo 更新子设备数据
     */
    public static void subLinkUpNotify(String pilesCode, SubLinkUpNotifyVo subLinkUpNotifyVo) {
        try {
            RedisGeneralUtil.executePile(pilesCode, () -> {
                int workState;
                Integer originalStatus;
                if (Objects.equals(subLinkUpNotifyVo.getStatus(), StaticParamVo.ONLINE)) { //在线
                    workState = 1;
                    originalStatus = 0;
                } else if (Objects.equals(subLinkUpNotifyVo.getStatus(), StaticParamVo.OFFLINE)) { //离线
                    workState = 88;
                    originalStatus = 88;
                } else {
                    workState = -1;
                    originalStatus = null;
                }
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                if (pileRealModel != null) {
                    //更新电桩和枪的状态
                    pileRealModel.setWorkStatus(workState);
                    pileRealModel.setOriginalStatus(originalStatus);
                    pileRealModel.setFeatureCode(subLinkUpNotifyVo.getFeatureCode());
                    pileRealModel.setMessageType(2); //内网
                    //电桩离线 增加离线时间
                    if (workState == 88) {
                        pileRealModel.setOfflineTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    }
                    if (MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                        pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> {
                            if (workState == 1) {
                                gunRealModel.setGunStatus(0);
                                gunRealModel.setGunOriginalStatus(0);
                            } else {
                                gunRealModel.setGunStatus(workState);
                                gunRealModel.setGunOriginalStatus(originalStatus);
                            }
                            pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        });
                    }
                    RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                    //查询是否存在未修复的离线告警
                    List<AlarmRecordEntity> alarmRecordList = alarmRecordDao.findAllByDeviceCodeAndFaultCodeInAndAlarmStatus(pilesCode, Collections.singleton(65534), 0);
                    if (workState == 1) { //在线 修复未修复的电桩
                        //如果存在则修复
                        if (CollectionUtils.isNotEmpty(alarmRecordList)) {
                            PileRecordUtil.updateAlarmStatus(alarmRecordList, alarmRecordDao);
                        }
                        //桩重新上线 如果是复位的订单 把订单状态改为订单挂起
                        if (subLinkUpNotifyVo.getReason() == 1) {
                            PileRecordUtil.updateOrderChargingStatus(pilesCode, 4);
                        }
                        //下发二维码
                        PileRecordUtil.pileSetQr(null, pilesCode);
                    } else if (workState == 88) { //离线 生成电桩告警
                        //如果不存在则产生故障
                        if (CollectionUtils.isEmpty(alarmRecordList)) {
                            //生成电桩离线记录
                            AlarmRecordEntity alarmRecord = AlarmRecordEntity.builder().deviceCode(pilesCode).faultCode(65534).featureCode(pileRealModel.getFeatureCode()).eventName("电桩离线").eventLevel(5).alarmStatus(0).ignoreStatus(0).alarmType(2).build();
                            alarmRecord.setCreateTime(LocalDateTime.now());
                            alarmRecordDao.save(alarmRecord);
                        }
                        //把充电中的订单状态改为订单挂起
                        PileRecordUtil.updateOrderChargingStatus(pilesCode, 4);
                    }
                }
            });
        } catch (Exception e) {
            log.error("内网MQTT更新子设备失败", e);
        }
    }

    /**
     * 硬件控制板软硬件信息通知
     *
     * @param pilesCode      电桩编号
     * @param cbInfoNotifyVo 硬件控制板软硬件信息
     */
    public static void cbInfoNotify(String pilesCode, CbInfoNotifyVo cbInfoNotifyVo) {
        try {
            RedisGeneralUtil.executePile(pilesCode, () -> {
                Map<String, List<DeviceBatchUpdateVo.DeviceUpdateInfo>> deviceUpdateInfoMap = Maps.newHashMap();
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                if (pileRealModel != null) {
                    //获取上报的控制板信息
                    Map<Integer, CtrlBoardInfo> ctrlBoardInfoMap = getStringCtrlBoardInfoMap(cbInfoNotifyVo);
                    //更新固件
                    pileRealModel.getUpdateInfoMap().forEach((key, value) -> {
                        DeviceBatchUpdateVo.DeviceUpdateInfo deviceUpdateInfo = new DeviceBatchUpdateVo.DeviceUpdateInfo();
                        deviceUpdateInfo.setDeviceCode(pilesCode);
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
//                    RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                    // 将设备的实时模型存储到Redis
                    pileRealModel.setPileCode(pilesCode);
                    RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
//                    log.info("硬件控制板软硬件信息通知响应结果, 桩编号:{}, 硬件控制板软硬件信息:{}", pilesCode, pileRealModel);
                }
                //更新设备升级状态
                if (MapUtils.isNotEmpty(deviceUpdateInfoMap)) {
                    DeviceBatchUpdateVo deviceUpdateVo = new DeviceBatchUpdateVo();
                    deviceUpdateVo.setDeviceUpdateInfoMap(deviceUpdateInfoMap);
                    deviceUpdateVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    deviceService.batchUpdateDeviceTask(deviceUpdateVo);
                }
            });
        } catch (Exception e) {
            log.error("内网MQTT硬件控制板软硬件信息通知处理失败", e);
        }
    }

    @NotNull
    static Map<Integer, CtrlBoardInfo> getStringCtrlBoardInfoMap(CbInfoNotifyVo cbInfoNotifyVo) {
        Map<Integer, CtrlBoardInfo> ctrlBoardInfoMap = new HashMap<>();
        List<CbInfoNotifyVo.ControlBoardInfo> cbInfos = cbInfoNotifyVo.getCbInfos();
        if (CollectionUtils.isNotEmpty(cbInfos)) {
            for (CbInfoNotifyVo.ControlBoardInfo cbInfo : cbInfos) {
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
        }
        return ctrlBoardInfoMap;
    }

    /**
     * 启动响应处理
     *
     * @param pilesCode  电桩编号
     * @param startResVo 启动响应处理参数
     */
    public static void startRes(String pilesCode, StartResVo startResVo) {
        try {
            //保存启动响应记录
            String gunCode = StringUtil.isNotEmpty(startResVo.getGunCode()) ? String.valueOf(startResVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pilesCode, gunCode, 2, 2, 2, CmdConstant.CMD_START_RES, startResVo);

            //收到启动命令响应后的处理
            String keyId = pilesCode + startResVo.getGunCode() + SMV2gConstant.STARTCMD; //计算权重
            if (PileDemand.demandModelMap.containsKey(keyId)) {//启动命令执行失败
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                if (startResVo.getResponseResult() != SMV2gConstant.SUNMAX_START_OK && startResVo.getResponseResult() != SMV2gConstant.SUNMAX_APPOINTMENT_OK) {
                    //更新订单异常状态数据
                    OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(startResVo.getRecordId());
                    if (orderRecord != null) {
                        Integer originalState = orderRecord.getOrderStatus();
                        if (StringUtil.isNotEmpty(startResVo.getFailReason())) {
                            orderRecord.setStopReason(String.valueOf(startResVo.getFailReason()));
                        }
                        if (StringUtil.isNotEmpty(startResVo.getStopDetail())) {
                            orderRecord.setStopDetailReason(startResVo.getStopDetail());
                        }
                        orderRecord.setOrderStatus(3);
                        orderRecord.setAbnormalCode(JSON.toJSONString(Arrays.asList(4, 5)));
                        if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                            orderRecord.setSiteId(PileRecordUtil.getSiteId(pilesCode));
                        }
                        orderRecordDao.save(orderRecord);
                        //启动失败 校验是否退款 保存结算记录
                        PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, new BigDecimal("0.0"), new BigDecimal("0.0"));
                    }
                    demandModel.setRecFlag(true);
                    demandModel.setRecCode(startResVo.getResponseResult());
                    demandModel.setRecMsg(startResVo.getFailReason());
                    demandModel.setSerialNum(startResVo.getRecordId());
                }
            }
        } catch (Exception e) {
            log.error("内网MQTT启动响应处理报错", e);
        }
    }

    /**
     * 启动事件处理
     *
     * @param pilesCode    电桩编号
     * @param startEventVo 启动事件处理参数
     */
    public static void startEvent(String pilesCode, StartEventVo startEventVo) {
        try {
            //保存启动事件记录
            String gunCode = StringUtil.isNotEmpty(startEventVo.getGunCode()) ? String.valueOf(startEventVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pilesCode, gunCode, 2, 3, 2, CmdConstant.CMD_START_EVENT, startEventVo);

            Integer eventResult = startEventVo.getEventResult();
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
                    OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(startEventVo.getRecordId());
                    if (orderRecord != null) {
                        Integer originalState = orderRecord.getOrderStatus();
                        String startTime = null;
                        if (StringUtil.isNotEmpty(startEventVo.getStartTime())) {
                            startTime = SunMaxUtil.timeStamp0Date(startEventVo.getStartTime());
                        }
                        if (StringUtil.isNotEmpty(startEventVo.getFailReason())) {
                            orderRecord.setStopReason(String.valueOf(startEventVo.getFailReason()));
                        }
                        if (StringUtil.isNotEmpty(startEventVo.getStopDetail())) {
                            orderRecord.setStopDetailReason(startEventVo.getStopDetail());
                        }
                        orderRecord.setOrderStatus(3);
                        orderRecord.setStartTime(startTime);
                        orderRecord.setAbnormalCode(JSON.toJSONString(Arrays.asList(4, 5)));
                        if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                            orderRecord.setSiteId(PileRecordUtil.getSiteId(pilesCode));
                        }
                        orderRecordDao.save(orderRecord);
                        //启动失败 校验是否退款 保存结算记录
                        PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, new BigDecimal("0.0"), new BigDecimal("0.0"));
                    }
                }
                demandModel.setRecFlag(true);
                demandModel.setRecMsg(startEventVo.getFailReason());
                demandModel.setSerialNum(startEventVo.getRecordId());
            }
            if (eventResult == SMV2gConstant.SUNMAX_START_OK) { //启动成功
                RedisGeneralUtil.executePile(pilesCode, () -> {
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                    if (pileRealModel != null && pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                        PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                        //存取启动时间
                        gunRealModel.setStartTime(SunMaxUtil.timeStamp0Date(startEventVo.getStartTime()));
                        //BMS信息
                        if (startEventVo.getBmsInfo() != null && startEventVo.getBmsInfo().getCurSoc() != null) {
                            gunRealModel.setStartSoc(startEventVo.getBmsInfo().getCurSoc());//记录开始充电时刻SOC
                        }
                        if (startEventVo.getBmsInfo() != null && startEventVo.getBmsInfo().getBcs_RemainingTime() != null) {
                            gunRealModel.setRemainTime(startEventVo.getBmsInfo().getBcs_RemainingTime());
                        }
                        pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                    }
                });
                //更新订单状态 在途订单
                OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(startEventVo.getRecordId());
                if (orderRecord != null) {
                    String startTime = null;
                    if (StringUtil.isNotEmpty(startEventVo.getStartTime())) {
                        startTime = SunMaxUtil.timeStamp0Date(startEventVo.getStartTime());
                    }
                    //起始SOC
                    if (startEventVo.getBmsInfo() != null && startEventVo.getBmsInfo().getCurSoc() != null) {
                        orderRecord.setStartSoc(startEventVo.getBmsInfo().getCurSoc());//记录开始充电时刻SOC
                    }
                    if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                        orderRecord.setSiteId(PileRecordUtil.getSiteId(pilesCode));
                    }
                    if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                        orderRecord.setSiteId(PileRecordUtil.getSiteId(pilesCode));
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
                        gunRealModel.setStartTime(SunMaxUtil.timeStamp0Date(startEventVo.getStartTime()));
                        //BMS信息
                        if (startEventVo.getBmsInfo() != null && startEventVo.getBmsInfo().getCurSoc() != null) {
                            gunRealModel.setStartSoc(startEventVo.getBmsInfo().getCurSoc());//记录开始充电时刻SOC
                        }
                        if (startEventVo.getBmsInfo() != null && startEventVo.getBmsInfo().getBcs_RemainingTime() != null) {
                            gunRealModel.setRemainTime(startEventVo.getBmsInfo().getBcs_RemainingTime());
                        }
                        pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                    }
                });
                //更新订单状态 预约订单
                OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(startEventVo.getRecordId());
                if (orderRecord != null) {
                    //起始SOC
                    if (startEventVo.getBmsInfo() != null && startEventVo.getBmsInfo().getCurSoc() != null) {
                        orderRecord.setStartSoc(startEventVo.getBmsInfo().getCurSoc());//记录开始充电时刻SOC
                    }
                    if (StringUtil.isEmpty(orderRecord.getSiteId())) {
                        orderRecord.setSiteId(PileRecordUtil.getSiteId(pilesCode));
                    }
                    orderRecord.setOrderStatus(6);
                    orderRecordDao.save(orderRecord);
                }
            }
        } catch (Exception e) {
            log.error("内网MQTT启动事件处理报错", e);
        }
    }

    /**
     * 停止响应处理
     *
     * @param pilesCode 电桩编号
     * @param stopResVo 停止响应处理参数
     */
    public static void stopRes(String pilesCode, StopResVo stopResVo) {
        try {
            //保存停止响应记录
            String gunCode = StringUtil.isNotEmpty(stopResVo.getGunCode()) ? String.valueOf(stopResVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(stopResVo.getPilesCode(), gunCode, 2, 5, 2, CmdConstant.CMD_STOP_RES, stopResVo);

            //收到命令响应后的处理
            String keyId = pilesCode + stopResVo.getGunCode() + SMV2gConstant.STOPCMD;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(stopResVo.getResponseResult());
                demandModel.setRecMsg(stopResVo.getFailReason());
                demandModel.setSerialNum(stopResVo.getRecordId());
            }
        } catch (Exception e) {
            log.error("内网MQTT停止响应处理报错", e);
        }
    }

    /**
     * 停止事件处理
     *
     * @param pilesCode   电桩编号
     * @param stopEventVo 停止事件处理参数
     */
    public static void stopEvent(String pilesCode, StopEventVo stopEventVo) {
        try {
            //保存停止事件记录
            String gunCode = StringUtil.isNotEmpty(stopEventVo.getGunCode()) ? String.valueOf(stopEventVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pilesCode, gunCode, 2, 6, 2, CmdConstant.CMD_STOP_EVENT, stopEventVo);

            //收到命令响应后的处理
            String keyId = pilesCode + stopEventVo.getGunCode() + SMV2gConstant.STARTCMD;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);
                demandModel.setRecMsg(stopEventVo.getFailReason());
                demandModel.setSerialNum(stopEventVo.getRecordId());
            }
        } catch (Exception e) {
            log.error("内网MQTT停止事件处理报错", e);
        }
    }

    /**
     * 功率控制响应
     *
     * @param pilesCode         电桩编号
     * @param powerControlResVo 功率控制处理参数
     */
    public static void powerControlRes(String pilesCode, PowerControlResVo powerControlResVo) {
        try {
            //收到命令响应后的处理
            String keyId = pilesCode + powerControlResVo.getGunCode() + SMV2gConstant.POWERCTRL;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(powerControlResVo.getResponseResult());
            }
        } catch (Exception e) {
            log.error("内网MQTT功率控制响应处理报错", e);
        }
    }

    /**
     * 电桩数据上报
     *
     * @param pilesCode        电桩编号
     * @param pileDataReportVo 电桩数据处理参数
     */
    public static void pileDataReport(String pilesCode, PileDataReportVo pileDataReportVo) {
        try {
            RedisGeneralUtil.executePile(pilesCode, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                if (pileRealModel != null) {//有缓存 则更新

                    //更新充电枪口运行数据
                    if (CollectionUtils.isNotEmpty(pileDataReportVo.getGun_data())) {
                        for (PileDataReportVo.ItfRunData itfRunData : pileDataReportVo.getGun_data()) {
                            String gunCode = String.valueOf(itfRunData.getGunCode());
                            PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                            if (gunRealModel != null) {
                                //枪工作中的状态
//                          List<Integer> workStates = Arrays.asList(1, 2, 4, 5);
//                          if (workStates.contains(gunRealModel.getGunStatus())) {
                                gunRealModel.setBatterySoc(itfRunData.getSoc());
                                gunRealModel.setRunMode(itfRunData.getRunmode());
                                gunRealModel.setGunTemp1(null);
                                if (StringUtil.isNotEmpty(itfRunData.getGunTemp1())) {
                                    gunRealModel.setGunTemp1(DoubleUtil.getToDouble(itfRunData.getGunTemp1() * 0.1));
                                }
                                gunRealModel.setGunTemp2(null);
                                if (StringUtil.isNotEmpty(itfRunData.getGunTemp2())) {
                                    gunRealModel.setGunTemp2(DoubleUtil.getToDouble(itfRunData.getGunTemp2() * 0.1));
                                }
                                gunRealModel.setOutVolt(null);
                                if (StringUtil.isNotEmpty(itfRunData.getOutVolt())) {
                                    gunRealModel.setOutVolt(DoubleUtil.getToDouble(itfRunData.getOutVolt() * 0.1, 4));
                                }
                                gunRealModel.setOutCurrent(null);
                                if (StringUtil.isNotEmpty(itfRunData.getOutCurrent())) {
                                    gunRealModel.setOutCurrent(DoubleUtil.getToDouble(itfRunData.getOutCurrent() * 0.01, 4));
                                }
                                gunRealModel.setOutPower(null);
                                if (StringUtil.isNotEmpty(gunRealModel.getOutVolt()) && StringUtil.isNotEmpty(gunRealModel.getOutCurrent())) {
                                    gunRealModel.setOutPower(DoubleUtil.getToDouble(gunRealModel.getOutVolt() * gunRealModel.getOutCurrent() * 0.001, 4));
                                }
                                gunRealModel.setReqVolt(null);
                                if (StringUtil.isNotEmpty(itfRunData.getReqVolt())) {
                                    gunRealModel.setReqVolt(DoubleUtil.getToDouble(itfRunData.getReqVolt() * 0.1, 4));
                                }
                                gunRealModel.setReqCurrent(null);
                                if (StringUtil.isNotEmpty(itfRunData.getReqCurrent())) {
                                    gunRealModel.setReqCurrent(DoubleUtil.getToDouble(itfRunData.getReqCurrent() * 0.01, 4));
                                }
                                gunRealModel.setReqPower(null);
                                if (StringUtil.isNotEmpty(gunRealModel.getReqVolt()) && StringUtil.isNotEmpty(gunRealModel.getReqCurrent())) {
                                    gunRealModel.setReqPower(DoubleUtil.getToDouble(gunRealModel.getReqVolt() * gunRealModel.getReqCurrent() * 0.001, 4));
                                }
                                gunRealModel.setDirMeterNum(null);
                                if (StringUtil.isNotEmpty(itfRunData.getDirMeterNum())) {
                                    gunRealModel.setDirMeterNum(DoubleUtil.getToDouble(itfRunData.getDirMeterNum() * 0.001, 4));
                                }
                                gunRealModel.setAlterMeterNum(null);
                                if (StringUtil.isNotEmpty(itfRunData.getAlterMeterNum())) {
                                    gunRealModel.setAlterMeterNum(DoubleUtil.getToDouble(itfRunData.getAlterMeterNum() * 0.001, 4));
                                }
                                gunRealModel.setRunTime(itfRunData.getRunTime());
                                gunRealModel.setRemainTime(itfRunData.getRemainTime());
                                gunRealModel.setTotalQt(null);
                                if (StringUtil.isNotEmpty(itfRunData.getTotalQt())) {
                                    gunRealModel.setTotalQt(DoubleUtil.getAbsDouble(itfRunData.getTotalQt() * 0.001, 4));
                                }
                                gunRealModel.setTotalCost(null);
                                if (StringUtil.isNotEmpty(itfRunData.getTotalCost())) {
                                    gunRealModel.setTotalCost(DoubleUtil.getAbsBigDecimal(new BigDecimal(itfRunData.getTotalCost()).multiply(new BigDecimal("0.001")), 4));
                                }
                                pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
//                        }
                            }
                        }
                    } else {
                        //防止数据存储有误
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
                    double rechargePower = pileRealModel.getGunRealModelMap().values().stream().filter(gun -> (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) && StringUtil.isNotEmpty(gun.getGunStatus()) && StringUtil.isNotEmpty(gun.getOutPower()) && (Objects.equals(gun.getGunStatus(), 1) || Objects.equals(gun.getGunStatus(), 2))).mapToDouble(PileRealModel.GunRealModel::getOutPower).sum();
                    double dischargePower = pileRealModel.getGunRealModelMap().values().stream().filter(gun -> (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) && StringUtil.isNotEmpty(gun.getGunStatus()) && StringUtil.isNotEmpty(gun.getOutPower()) && (Objects.equals(gun.getGunStatus(), 4) || Objects.equals(gun.getGunStatus(), 5))).mapToDouble(PileRealModel.GunRealModel::getOutPower).sum();
                    pileRealModel.setTotalPower(rechargePower + dischargePower);
                    pileRealModel.setRecChargePower(rechargePower);
                    pileRealModel.setDisChargePower(dischargePower);
                    pileRealModel.setInnerTemp(null);
                    if (StringUtil.isNotEmpty(pileDataReportVo.getInnerTemp())) {
                        pileRealModel.setInnerTemp(pileDataReportVo.getInnerTemp() * 0.1);
                    }
                    pileRealModel.setPowerModMaxTemp(null);
                    if (StringUtil.isNotEmpty(pileDataReportVo.getPowerModMaxTemp())) {
                        pileRealModel.setPowerModMaxTemp(pileDataReportVo.getPowerModMaxTemp() * 0.1);
                    }
                    pileRealModel.setMaxTempMod(pileDataReportVo.getMaxTempMod());
                    pileRealModel.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                }
            });
        } catch (Exception e) {
            log.error("内网MQTT解析电桩数据失败", e);
        }
    }


    /**
     * 电桩故障上报
     *
     * @param pilesCode   电桩编号
     * @param pileFaultVo 电桩故障处理参数
     */
    public static void pileFaultReport(String pilesCode, PileFaultReportVo pileFaultVo) {
        try {
            //定义添加和修改的故障
            List<AlarmRecordEntity> addAlarmRecordList = Lists.newArrayList();
            // 定义需要更新的故障 Map (设备编号 -> (故障码，模块地址))
            Map<String, Map<Integer, Set<Integer>>> updateFaultWithModuleMap = Maps.newConcurrentMap();
            // 定义故障码和电力模块地址 Map (故障码 -> 模块地址 Set，多个模块可能上报相同故障码)
            Map<Integer, Set<Integer>> moduleFaultAddrMap = Maps.newConcurrentMap();
            Set<Integer> pileFaultCodes = Sets.newHashSet();//电桩最新故障状态码
            Map<Integer, Set<Integer>> itfFaultCodeMap = Maps.newHashMap();//电桩最新充电接口故障状态码
            Map<Integer, Set<Integer>> lastItfFaultCodeMap = Maps.newHashMap();//电桩上一次充电接口故障状态码
            ChargerRunFault chargerRunFault = new ChargerRunFault();
            chargerRunFault.setChargerComFault(pileFaultVo.getGeneralFault());
            chargerRunFault.setChargerStopFault(pileFaultVo.getEmergencyStopFault());
            chargerRunFault.setTcuReportFault(pileFaultVo.getTcuReportFault());
            RedisGeneralUtil.executePile(pilesCode, () -> {
                //收到电桩故障数据
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                if (pileRealModel != null) {
                    Set<Integer> lastPileFaultCodes = pileRealModel.getFaultCodes();//电桩上一次故障状态码
                    List<ItfFault> itfFaults = new ArrayList<>();
                    for (PileFaultReportVo.ItfRunFault gunFaultVo : pileFaultVo.getGun_fault()) {
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
                    for (PileFaultReportVo.EleModuleFault moduleFaultVo : pileFaultVo.getEmodule_fault()) {
                        ElecModuleFault elecModuleFault = new ElecModuleFault();
                        elecModuleFault.setModuleAddr(moduleFaultVo.getAddr());
                        elecModuleFault.setPcuReportFault(moduleFaultVo.getPcuReportFault());
                        elecModuleFault.setPcuTimeout(moduleFaultVo.getPcuTimeOutFault());
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
                    //根据设备编号和多个故障码查询设备电桩事件数据
                    DeviceService deviceService = SpringBeanUtil.getBean(DeviceService.class);
                    Set<Integer> faultCodes = Stream.of(pileFaultCodes, itfFaultCodeMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet())).flatMap(Collection::stream).collect(Collectors.toSet());
                    Map<Integer, PileFaultDto> faultEventMap = deviceService.findPileFaultList(pilesCode, faultCodes).getData();

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
                                        addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
                                                moduleAddr, faultEventMap));
                                    }
                                } else {
                                    // 非电力模块故障
                                    addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
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
                                        addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
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
                                updateFaultWithModuleMap.computeIfAbsent(pilesCode, k -> Maps.newConcurrentMap())
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
                                        updateFaultWithModuleMap.computeIfAbsent(pilesCode, k -> Maps.newConcurrentMap())
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
                                    addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
                                            moduleAddr, faultEventMap));
                                }
                            } else {
                                addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, null, pileFaultCode, pileRealModel.getFeatureCode(),
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
                                        if (!lastItfFaults.contains(faultCode)) { // 新产生的故障
                                            Set<Integer> moduleAdds = moduleFaultAddrMap.get(faultCode);
                                            if (CollectionUtils.isNotEmpty(moduleAdds)) {
                                                for (Integer moduleAddr : moduleAdds) {
                                                    addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, gunCode.toString(), faultCode, pileRealModel.getFeatureCode(),
                                                            moduleAddr, faultEventMap));
                                                }
                                            } else {
                                                addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, gunCode.toString(), faultCode, pileRealModel.getFeatureCode(),
                                                        null, faultEventMap));
                                            }
                                        }
                                    }
                                    //恢复的枪故障：故障码完全恢复时更新
                                    for (Integer faultCode : lastItfFaults) {
                                        if (!newItfFaults.contains(faultCode)) {
                                            updateFaultWithModuleMap.computeIfAbsent(pilesCode, k -> Maps.newConcurrentMap())
                                                    .put(faultCode, Sets.newHashSet()); // 空模块地址集合表示非电力模块故障
                                        }
                                    }
                                } else {
                                    //新产生的故障
                                    for (Integer faultCode : newItfFaults) {
                                        Set<Integer> moduleAdds = moduleFaultAddrMap.get(faultCode);
                                        if (CollectionUtils.isNotEmpty(moduleAdds)) {
                                            for (Integer moduleAddr : moduleAdds) {
                                                addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, gunCode.toString(), faultCode, pileRealModel.getFeatureCode(),
                                                        moduleAddr, faultEventMap));
                                            }
                                        } else {
                                            addAlarmRecordList.add(PileRecordUtil.addAlarmRecord(pilesCode, gunCode.toString(), faultCode, pileRealModel.getFeatureCode(),
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
                                updateFaultWithModuleMap.computeIfAbsent(pilesCode, k -> Maps.newConcurrentMap()).put(faultCode, Sets.newHashSet());
                            }
                        }
                    }

                    // 更新 Redis 缓存中的电力模块故障信息，供下次故障上报对比
                    pileRealModel.getModuleFaultAddrMap().clear();
                    moduleFaultAddrMap.forEach((k, v) -> pileRealModel.getModuleFaultAddrMap().put(k, Sets.newHashSet(v)));

                    pileRealModel.setFaultCodes(pileFaultCodes);// 保存故障
                    RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                }
            });

            //告警数据存库
            if (CollectionUtils.isNotEmpty(addAlarmRecordList)) {
                alarmRecordDao.batchInsert(addAlarmRecordList);
            }

            //更新告警记录：根据故障码和模块地址精确更新
            List<AlarmRecordEntity> updateAlarmRecordList = Lists.newArrayList();
            if (MapUtils.isNotEmpty(updateFaultWithModuleMap)) {
//                updateFaultWithModuleMap.forEach((deviceCode, faultModuleMap) ->
//                        faultModuleMap.forEach((faultCode, moduleAdds) -> {
//                            if (CollectionUtils.isEmpty(moduleAdds)) { // 非电力模块故障，只根据故障码查询
//                                updateAlarmRecordList.addAll(alarmRecordDao.findAllByDeviceCodeAndFaultCodeAndAlarmStatus(deviceCode, faultCode, 0));
//                            } else { // 电力模块故障，根据故障码和模块地址查询
//                                for (Integer moduleAddr : moduleAdds) {
//                                    updateAlarmRecordList.addAll(alarmRecordDao.findAllByDeviceCodeAndFaultCodeAndModuleAddrAndAlarmStatus(deviceCode, faultCode, moduleAddr, 0));
//                                }
//                            }
//                        }));
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
            log.error("内网MQTT解析电桩故障失败", e);
        }
    }

    /**
     * 电桩状态上报
     *
     * @param pilesCode    电桩编号
     * @param pileStatusVo 电桩状态处理参数
     */
    public static void pileStatusReport(String pilesCode, PileStatusReportVo pileStatusVo) {
        try {
            Integer workState = WebMqttUtil.convertPileState(pileStatusVo.getWorkState());
            if (workState == 1) { //在线 修复未修复的电桩
                //查询是否存在未修复的离线告警,如果存在则修复
                List<AlarmRecordEntity> alarmRecordList = alarmRecordDao.findAllByDeviceCodeAndFaultCodeInAndAlarmStatus(pilesCode, Collections.singleton(65534), 0);
                if (CollectionUtils.isNotEmpty(alarmRecordList)) {
                    PileRecordUtil.updateAlarmStatus(alarmRecordList, alarmRecordDao);
                }
            }

            RedisGeneralUtil.executePile(pilesCode, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                if (pileRealModel != null) {
                    if (StringUtil.isEmpty(pileRealModel.getPileCode())) {
                        pileRealModel.setPileCode(pilesCode);
                    }
                    if (StringUtil.isEmpty(pileRealModel.getMessageType()) || !Objects.equals(pileRealModel.getMessageType(), 2)) {
                        pileRealModel.setMessageType(2); //内网
                        pileRealModel.setTerminalCode(null);
                    }
                    pileRealModel.setWorkStatus(workState);
                    pileRealModel.setOriginalStatus(pileStatusVo.getWorkState());
                    pileRealModel.setResetTimes(pileStatusVo.getResetTimes());
                    Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                    pileStatusVo.getGun_state().forEach(gunState -> {
                        PileRealModel.GunRealModel gunRealModel = new PileRealModel.GunRealModel();
                        if (StringUtil.isNotEmpty(gunState.getGuncode())) {
                            String gunCode = String.valueOf(gunState.getGuncode());
                            if (gunRealModelMap.containsKey(gunCode)) {
                                gunRealModel = gunRealModelMap.get(gunCode);
                            }
                            if (StringUtil.isEmpty(gunRealModel.getGunCode())) {
                                gunRealModel.setGunCode(gunCode);
                            }
                            gunRealModel.setGunStatus(WebMqttUtil.convertGunState(pileStatusVo.getWorkState(), gunState.getWorkState(), gunState.getVehicleConnState()));
                            gunRealModel.setGunOriginalStatus(gunState.getWorkState());
                            gunRealModel.setParkingLockState(gunState.getParkingLockState());
                            gunRealModel.setVehicleConnState(gunState.getVehicleConnState());
                            gunRealModel.setGunLockState(gunState.getGunLockState());
                            gunRealModel.setK1k2State(gunState.getK1k2State());
                            gunRealModelMap.put(gunCode, gunRealModel);
                        }
                    });
                    pileRealModel.setGunRealModelMap(gunRealModelMap);
                    pileRealModel.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                }
            });

        } catch (Exception e) {
            log.error("内网MQTT更新电桩状态报错", e);
        }
    }

    /**
     * 电桩记录上报
     *
     * @param pilesCode    电桩编号
     * @param pileRecordVo 电桩记录处理参数
     */
    public static void pileRecordReport(String pilesCode, PileRecordReportVo pileRecordVo) {
        //保存记录上报数据
        String gunCode = StringUtil.isNotEmpty(pileRecordVo.getGunCode()) ? String.valueOf(pileRecordVo.getGunCode()) : null;
        PileRecordUtil.saveMqttRecord(pileRecordVo.getPilesCode(), gunCode, 2, 7, 2, CmdConstant.CMD_PILE_RECORD_REPORT, pileRecordVo);

        //电桩记录上报确认处理
        PileRecordConfirmDto pileRecordConfirm = new PileRecordConfirmDto();
        pileRecordConfirm.setPilesCode(pilesCode);
        pileRecordConfirm.setRecordId(pileRecordVo.getRecordId());
        pileRecordConfirm.setRecordSeq(pileRecordVo.getRecordSeq());
        pileRecordConfirm.setReportType(pileRecordVo.getReportType());
        pileRecordConfirm.setGunCode(pileRecordVo.getGunCode());
        //发送数据
        InterMqttConfig.sendToMqtt(pilesCode, CmdConstant.CMD_PILE_RECORD_CONFIRM, pileRecordConfirm);

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
        if (StringUtil.isEmpty(orderRecord.getPrepayMoney())) {
            orderRecord.setPrepayMoney(new BigDecimal("0.0"));
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
        if (StringUtil.isEmpty(orderRecord.getPlatformLogo()) && StringUtil.isNotEmpty(pileRecordVo.getPlatformId())) {
            orderRecord.setPlatformLogo(pileRecordVo.getPlatformId());
        }
        if (StringUtil.isEmpty(orderRecord.getSiteId())) {
            orderRecord.setSiteId(PileRecordUtil.getSiteId(pilesCode));
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
                orderRecord.setStrategyTime(SunMaxUtil.timeStamp0Date(strategy.getStartTime()));
            }
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getStartTime())) {
            orderRecord.setStartTime(SunMaxUtil.timeStamp0Date(pileRecordVo.getStartTime()));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getEndTime())) {
            orderRecord.setEndTime(SunMaxUtil.timeStamp0Date(pileRecordVo.getEndTime()));
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
        if (StringUtil.isNotEmpty(pileRecordVo.getSharpPrice())) { //尖电费
            orderRecord.setJElect(DoubleUtil.getAbsBigDecimal(new BigDecimal(pileRecordVo.getSharpPrice()).divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getSharpService())) { //尖服务费
            orderRecord.setJFee(DoubleUtil.getAbsBigDecimal(new BigDecimal(pileRecordVo.getSharpService()).divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getSharpQ())) { //尖电量
            orderRecord.setJQt(DoubleUtil.getAbsDouble(pileRecordVo.getSharpQ() * 0.001, 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getPeakPrice())) { //峰电费
            orderRecord.setFElect(DoubleUtil.getAbsBigDecimal(new BigDecimal(pileRecordVo.getPeakPrice()).divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getPeakService())) { //峰服务费
            orderRecord.setFFee(DoubleUtil.getAbsBigDecimal(new BigDecimal(pileRecordVo.getPeakService()).divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getPeakQ())) { //峰电量
            orderRecord.setFQt(DoubleUtil.getAbsDouble(pileRecordVo.getPeakQ() * 0.001, 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getFlatPrice())) { //平电费
            orderRecord.setPElect(DoubleUtil.getAbsBigDecimal(new BigDecimal(pileRecordVo.getFlatPrice()).divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getFlatService())) { //平服务费
            orderRecord.setPFee(DoubleUtil.getAbsBigDecimal(new BigDecimal(pileRecordVo.getFlatService()).divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getFlatQ())) { //平电量
            orderRecord.setPQt(DoubleUtil.getAbsDouble(pileRecordVo.getFlatQ() * 0.001, 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getValleyPrice())) { //谷电费
            orderRecord.setGElect(DoubleUtil.getAbsBigDecimal(new BigDecimal(pileRecordVo.getValleyPrice()).divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getValleyService())) { //谷服务费
            orderRecord.setGFee(DoubleUtil.getAbsBigDecimal(new BigDecimal(pileRecordVo.getValleyService()).divide(new BigDecimal(1000), 4, RoundingMode.UP), 4));
        }
        if (StringUtil.isNotEmpty(pileRecordVo.getValleyQ())) { //谷电量
            orderRecord.setGQt(DoubleUtil.getAbsDouble(pileRecordVo.getValleyQ() * 0.001, 4));
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
        if (Objects.equals(orderRecord.getPlatformLogo(), PlatformLogoVo.SUNMAX_LOGO) && orderRecord.getTotalQt() > 0) {
            //保存计费详情相关的数据
            List<ChargeTariffRecordEntity> tariffRecordList = PileRecordUtil.saveChargeTariffRecordList(orderRecord, chargeTariffRecordDao);
            //保存正常结算记录数据
            if (CollectionUtils.isNotEmpty(tariffRecordList) && orderRecord.getOrderStatus() != 4) {
                BigDecimal electMoney = tariffRecordList.stream().map(ChargeTariffRecordEntity::getElectMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal serviceMoney = tariffRecordList.stream().map(ChargeTariffRecordEntity::getServiceMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                log.info("内网订单号:{}, 结算电费金额:{}, 服务费金额:{}, 原始状态:{}, 结算状态:{}", orderRecord.getOrderNum(), electMoney, serviceMoney, originalState, orderRecord.getOrderStatus());
                PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, electMoney, serviceMoney);
            } else if (orderRecord.getOrderStatus() == 5) { //订单取消 正常退款
                PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, new BigDecimal("0.0"), new BigDecimal("0.0"));
            }
        } else {
            PileRecordUtil.updateOrderSettleRecord(orderRecord, originalState, new BigDecimal("0.0"), new BigDecimal("0.0"));
        }

        //更新补单数据
        if (isRepair && orderRecord.getOrderStatus() != 4) {
            PileRecordUtil.batchUpdateRepairOrderRecord(Collections.singletonList(orderRecord));
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
        }
        //保存订单记录数据
        orderRecordDao.save(orderRecord);
    }

    /**
     * 鉴权请求处理
     *
     * @param pilesCode           电桩编号
     * @param authenticationReqVo 鉴权请求处理参数
     */
    public static void authenticationReq(String pilesCode, AuthenticationReqVo authenticationReqVo) {
        //鉴权响应处理
        try {
            UserAccount userAccount = authenticationReqVo.getUserAccount();
            AuthenticationResDto authenticationRes = new AuthenticationResDto();
            authenticationRes.setPilesCode(pilesCode);
            authenticationRes.setGunCode(authenticationReqVo.getGunCode());
            authenticationRes.setBalanceType(1);
            authenticationRes.setBalanceNum(500000);
            authenticationRes.setUserAccount(userAccount);
            authenticationRes.setFailReason(255); //账号非法
            //账号类型 1-充/放电卡 2-VIN码 3-手机号
            if (StringUtil.isNotEmpty(userAccount.getAccountType())) {
                switch (userAccount.getAccountType()) {
                    case 1: //充放电卡
                        break;
                    case 2: //vin码校验
                    case 3: //手机号
                        Boolean isData = SpringBeanUtil.getBean(TogetherService.class).checkAccountCode(pilesCode, userAccount.getAccountData()).getData();
                        if (isData) {
                            authenticationRes.setFailReason(0);
                        }
                        break;
                }
            } else {
                authenticationRes.setFailReason(1); //账号非法
            }
            //发送数据
            InterMqttConfig.sendToMqtt(pilesCode, CmdConstant.CMD_AuthenticationResponse, authenticationRes);
        } catch (Exception e) {
            log.error("内网MQTT处理鉴权请求数据报错", e);
        }
    }

    /**
     * 策略设置处理
     *
     * @param pilesCode            电桩编号
     * @param strategySettingReqVo 策略设置处理参数
     */
    public static void strategySettingReq(String pilesCode, StrategySettingReqVo strategySettingReqVo) {
        //策略设置响应
        try {
            StrategySettingResDto strategySettingRes = new StrategySettingResDto();
            strategySettingRes.setPilesCode(pilesCode);
            strategySettingRes.setGunCode(strategySettingReqVo.getGunCode());
            strategySettingRes.setRunMode(strategySettingReqVo.getRunMode());
            strategySettingRes.setFailReason(0);
            strategySettingRes.setFailDetail(0);
            //判断是否大于30秒
            boolean isPileStart = false;
            String strategyKey = pilesCode + strategySettingReqVo.getGunCode();
            if (strategyMap.containsKey(strategyKey)) {
                LocalDateTime startTime = strategyMap.get(strategyKey);
                //判断是否大于30秒
                if (DateUtil.compareDiffBetweenSecond(startTime, LocalDateTime.now()) >= 30) {
                    isPileStart = true;
                } else {
                    strategySettingRes.setFailReason(255);
                }
            } else {
                isPileStart = true;
            }
            strategyMap.put(strategyKey, LocalDateTime.now());
            //发送数据
            InterMqttConfig.sendToMqtt(pilesCode, CmdConstant.CMD_StrategySettingResponse, strategySettingRes);

            //下发启动命令
            if (isPileStart) {
                PileStartVo pileStartVo = new PileStartVo();

                pileStartVo.setPileCode(strategySettingRes.getPilesCode());
                pileStartVo.setGunCode(String.valueOf(strategySettingRes.getGunCode()));
                Strategy strategy = strategySettingReqVo.getStrategy();
                if (strategy != null) {
                    pileStartVo.setStrategy(strategy.getStrategyType());
                    pileStartVo.setStrategyCfg(PileRecordUtil.getStrategyCfg(strategySettingReqVo.getRunMode(), strategy.getStrategyType(), strategy.getStrategyCfg()));
                }
                pileStartVo.setStarter(4);
                pileStartVo.setType(0);
                pileStartVo.setRunMode(strategySettingRes.getRunMode());
                if (strategySettingReqVo.getUserAccount() != null) {
                    Integer accountType = strategySettingReqVo.getUserAccount().getAccountType();
                    if (StringUtil.isNotEmpty(accountType)) {
                        if (accountType == 1) {
                            pileStartVo.setStarter(3);
                        }
                    }
                    pileStartVo.setAccountType(accountType);
                    pileStartVo.setAccountData(strategySettingReqVo.getUserAccount().getAccountData());
                }
                pileStartVo.setPrepayMoney(new BigDecimal(5000));
                SpringBeanUtil.getBean(PileCtrlService.class).pileStart(pileStartVo);
            }
        } catch (Exception e) {
            log.error("外网MQTT处理策略响应数据报错", e);
        }
    }

    /**
     * 设备升级-数据块请求
     *
     * @param pilesCode         电桩编号
     * @param devDataBlockReqVo 数据块请求处理参数
     */
    public static void devDataBlockReq(String pilesCode, DevDataBlockReqVo devDataBlockReqVo) {
        try {
            Integer deviceType = devDataBlockReqVo.getDeviceType();
            Integer firmMainVCode = devDataBlockReqVo.getMajorNo();//电桩发上来的固件主版本号
            Integer firmSecVCode = devDataBlockReqVo.getChildNo();//电桩发上来的固件次版本号
            Integer dataBlockNum = devDataBlockReqVo.getDataFlag();//数据块标号
            Integer dataBlockSize = devDataBlockReqVo.getDataLen();//数据块大小
            RedisGeneralUtil.executePile(pilesCode, () -> {
                //收到电桩固件数据请求，做数据块响应
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                if (pileRealModel != null) {
                    //2.根据充电桩编号获取缓存中的固件包数据
                    UpdateInfoDto updateInfoDto = pileRealModel.getUpdateInfoMap().get(deviceType);
                    if (updateInfoDto != null) {
                        if (dataBlockNum == 0) { //请求第一块时 更新设备任务数据
                            DeviceBatchUpdateVo deviceUpdateVo = new DeviceBatchUpdateVo();
                            Map<String, List<DeviceBatchUpdateVo.DeviceUpdateInfo>> deviceUpdateInfoMap = Maps.newHashMap();
                            deviceUpdateInfoMap.put(updateInfoDto.getTaskId(), Collections.singletonList(DeviceBatchUpdateVo.DeviceUpdateInfo.builder().deviceCode(pilesCode).status(2).build()));
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
//                        UpdateWebSocket.externalSendMessage(pileCode);
                            //对应的数据块信息
                            byte[] dataBlockBytes = null;
                            //从缓存中读取固件块数据
                            List<byte[]> firmwareList = FirmwareUtil.getFirmwareByPilesCode(pilesCode, deviceType);
                            if (Objects.nonNull(firmwareList) && !firmwareList.isEmpty()) {
                                dataBlockBytes = firmwareList.get(dataBlockNum);//数据块
                                updateInfoDto.setDataBlockSum(firmwareList.size());//数据块总数
                            } else {
                                //根据文件路径和数据块大小拆分成数据块
                                dataBlockBytes = FirmwareUtil.parseFile(pilesCode, deviceType, firmwarePath, dataBlockNum, dataBlockSize);
                                if (Objects.nonNull(dataBlockBytes)) {
                                    updateInfoDto.setDataBlockSum(FirmwareUtil.getFirmwareByPilesCode(pilesCode, deviceType).size());
                                }
                            }
                            pileRealModel.getUpdateInfoMap().put(deviceType, updateInfoDto);
                            RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                            //固件数据响应
                            DevDataBlockResDto devDataBlockRes = new DevDataBlockResDto();
                            devDataBlockRes.setDeviceType(deviceType);
                            devDataBlockRes.setMajorNo(devDataBlockReqVo.getMajorNo());
                            devDataBlockRes.setChildNo(devDataBlockReqVo.getChildNo());
                            devDataBlockRes.setBetaNo(devDataBlockReqVo.getBetaNo());
                            devDataBlockRes.setDataFlag(devDataBlockReqVo.getDataFlag());
                            devDataBlockRes.setDataLen(devDataBlockReqVo.getDataLen());
                            devDataBlockRes.setDataBlock(SunMaxUtil.toHexString(dataBlockBytes));
                            //发送数据
                            InterMqttConfig.sendToMqtt(pilesCode, CmdConstant.CMD_DevDataBlockResponse, devDataBlockRes);
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.error("内网MQTT固件块请求响应结果处理报错", e);
        }
    }

    /**
     * 设备升级-升级结果上报
     *
     * @param pilesCode         电桩编号
     * @param devUpdateReportVo 设备升级结果处理参数
     */
    public static void devUpdateReport(String pilesCode, DevUpdateReportVo devUpdateReportVo) {
        try {
            //更新设备升级状态
            DeviceBatchUpdateVo deviceUpdateVo = new DeviceBatchUpdateVo();

            RedisGeneralUtil.executePile(pilesCode, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                if (pileRealModel != null) {
                    //收到固件接收完成 无论成功失败 都要清掉内存里面的数据
                    FirmwareUtil.clearFirmwareByPilesCode(pilesCode, devUpdateReportVo.getDeviceType());
                    UpdateInfoDto updateInfoDto = pileRealModel.getUpdateInfoMap().get(devUpdateReportVo.getDeviceType());
                    if (updateInfoDto != null) {
                        if (StringUtil.isNotEmpty(devUpdateReportVo.getFailReason())) {
                            log.info("固件数据校验结果:{}", devUpdateReportVo.getFailReason());
                            updateInfoDto.setStatus(devUpdateReportVo.getFailReason());
                            int status;
                            Integer failReason = null;
                            if (devUpdateReportVo.getFailReason() == 0) {
                                updateInfoDto.setMessage("固件包下发完成");
                                status = 4;
                            } else {
                                updateInfoDto.setMessage("固件包下发失败");
                                status = 3;
                                failReason = devUpdateReportVo.getFailReason();
                            }
                            pileRealModel.getUpdateInfoMap().put(devUpdateReportVo.getDeviceType(), updateInfoDto);
                            RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);

                            Map<String, List<DeviceBatchUpdateVo.DeviceUpdateInfo>> deviceUpdateInfoMap = Maps.newHashMap();
                            deviceUpdateInfoMap.put(updateInfoDto.getTaskId(), Collections.singletonList(DeviceBatchUpdateVo.DeviceUpdateInfo.builder().deviceCode(pilesCode).status(status).failReason(failReason).build()));
                            deviceUpdateVo.setDeviceUpdateInfoMap(deviceUpdateInfoMap);
                            deviceUpdateVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                        }
                    }
                }
            });
            //更新设备升级状态
            deviceService.batchUpdateDeviceTask(deviceUpdateVo);
        } catch (Exception e) {
            log.error("内网MQTT设备升级-升级结果上报报错", e);
        }

    }

    /**
     * 费率下发响应
     *
     * @param pilesCode    电桩编号
     * @param rateSetResVo 费率下发处理参数
     */
    public static void rateSetRes(String pilesCode, RateSetResVo rateSetResVo) {
        //费率下发响应处理
        try {
            String keyId = pilesCode + rateSetResVo.getType() + rateSetResVo.getRateId() + SMV2gConstant.RATESET; //计算权重
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
            }
        } catch (Exception e) {
            log.error("内网MQTT费率下发响应处理报错", e);
        }
    }

    /**
     * 电桩运行数据请求响应
     *
     * @param pilesCode     电桩编号
     * @param pileDataResVo 电桩运行数据处理参数
     */
    public static void pileDataRes(String pilesCode, PileDataResVo pileDataResVo) {

    }

    /**
     * 车辆信息请求响应
     *
     * @param pilesCode        电桩编号
     * @param vehicleInfoResVo 车辆信息处理参数
     */
    public static void vehicleInfoRes(String pilesCode, VehicleInfoResVo vehicleInfoResVo) {
        //车辆信息请求响应处理
        try {
            String keyId = pilesCode + vehicleInfoResVo.getGunCode() + SMV2gConstant.VEHICLE_INFO_REQUEST; //计算权重

            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(0);//执行成功
                demandModel.setRecMsg(JSON.toJSONString(vehicleInfoResVo));
            }
        } catch (Exception e) {
            log.error("内网MQTT费率下发响应处理报错", e);
        }
    }

    /**
     * 电桩记录查询命令响应
     *
     * @param pilesCode             电桩编号
     * @param pileRecordReportResVo 电桩记录查询处理参数
     */
    public static void pileRecordReportRes(String pilesCode, PileRecordReportResVo pileRecordReportResVo) {

    }

    /**
     * 电桩记录查询结果响应
     *
     * @param pilesCode                 电桩编号
     * @param pileRecordReportInfoResVo 电桩记录查询结果处理参数
     */
    public static void pileRecordReportInfoRes(String pilesCode, PileRecordReportInfoResVo pileRecordReportInfoResVo) {

    }

    /**
     * 充电bms信息
     *
     * @param pilesCode           电桩编号
     * @param pileBmsInfoReportVo bms信息处理参数
     */
    public static void pileBmsInfoReport(String pilesCode, PileBmsInfoReportVo pileBmsInfoReportVo) {
        try {
            RedisGeneralUtil.executePile(pilesCode, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilesCode);
                if (pileRealModel != null) {
                    String gunCode = String.valueOf(pileBmsInfoReportVo.getGunCode());
                    PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                    if (gunRealModel != null) {
                        gunRealModel.setBatteryTempMax(pileBmsInfoReportVo.getBsm_BatteryTempMax());
                        gunRealModel.setBatteryTempMaxNo(pileBmsInfoReportVo.getBsm_BatteryTempMaxNo());
                        gunRealModel.setBatteryTempMin(pileBmsInfoReportVo.getBsm_BatteryTempMin());
                        gunRealModel.setBatteryTempMinNo(pileBmsInfoReportVo.getBsm_BatteryTempMinNo());
                        gunRealModel.setBatteryVoltageMaxGn(pileBmsInfoReportVo.getBcs_BatteryVoltageMaxGN());
                        if (StringUtil.isNotEmpty(pileBmsInfoReportVo.getBcs_BatteryVoltageMax())) {
                            gunRealModel.setBatteryVoltageMax(DoubleUtil.getToDouble(pileBmsInfoReportVo.getBcs_BatteryVoltageMax() * 0.01));
                        }
                        gunRealModel.setBatteryVoltageMinGn(pileBmsInfoReportVo.getBcs_BatteryVoltageMinGN());
                        if (StringUtil.isNotEmpty(pileBmsInfoReportVo.getBcs_BatteryVoltageMin())) {
                            gunRealModel.setBatteryVoltageMin(DoubleUtil.getToDouble(pileBmsInfoReportVo.getBcs_BatteryVoltageMin() * 0.01));
                        }
                        if (StringUtil.isNotEmpty(pileBmsInfoReportVo.getBcs_BatteryVoltageMax())) {
                            gunRealModel.setBatteryVoltageMax(DoubleUtil.getToDouble(pileBmsInfoReportVo.getBcs_BatteryVoltageMax() * 0.01));
                        }
                        if (StringUtil.isNotEmpty(pileBmsInfoReportVo.getBcs_BatteryVoltageMin())) {
                            gunRealModel.setBatteryVoltageMin(DoubleUtil.getToDouble(pileBmsInfoReportVo.getBcs_BatteryVoltageMin() * 0.01));
                        }

                        if (StringUtil.isNotEmpty(pileBmsInfoReportVo.getBcpAllowChargeCellVmax())) {
                            gunRealModel.setBcpAllowChargeCellVMax(DoubleUtil.getToDouble(pileBmsInfoReportVo.getBcpAllowChargeCellVmax() * 0.01));
                        }
                        if (StringUtil.isNotEmpty(pileBmsInfoReportVo.getBcpAllowChargeCurrentMax())) {
                            gunRealModel.setBcpAllowChargeCurrentMax(DoubleUtil.getToDouble(pileBmsInfoReportVo.getBcpAllowChargeCurrentMax() * 0.1));
                        }
                        if (StringUtil.isNotEmpty(pileBmsInfoReportVo.getBcpBatteryNorminalCap())) {
                            gunRealModel.setBcpBatteryNominalCap(DoubleUtil.getToDouble(pileBmsInfoReportVo.getBcpBatteryNorminalCap() * 0.1));
                        }
                        if (StringUtil.isNotEmpty(pileBmsInfoReportVo.getBcpAllowChargeVmax())) {
                            gunRealModel.setBcpAllowChargeVMax(DoubleUtil.getToDouble(pileBmsInfoReportVo.getBcpAllowChargeVmax() * 0.1));
                        }
                        gunRealModel.setBcpAllowTempMax(pileBmsInfoReportVo.getBcpAllowTempMax());
                        gunRealModel.setBatteryType(pileBmsInfoReportVo.getBatteryType());

                        pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        RedisGeneralUtil.setPileRealModel(pilesCode, pileRealModel);
                    }
                }
            });
        } catch (Exception e) {
            log.error("内网MQTT充电bms信息处理报错", e);
        }
    }

    /**
     * modbus信息上报
     *
     * @param deviceId           设备id
     * @param modbusInfoReportVo modbus信息上报
     */
    public static void modbusInfoReport(String deviceId, ModbusInfoReportVo modbusInfoReportVo) {
        try {
            //转换遥信遥测数据
            Map<Integer, ModbusInfoReportVo.PointVo> groupMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(modbusInfoReportVo.getGroupList())) {
                groupMap = modbusInfoReportVo.getGroupList().stream().collect(Collectors.toMap(ModbusInfoReportVo.PointVo::getPId, a -> a, (k1, k2) -> k1));
            }
            Map<Integer, ModbusInfoReportVo.PointVo> pointMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(modbusInfoReportVo.getPointList())) {
                pointMap = modbusInfoReportVo.getPointList().stream().collect(Collectors.toMap(ModbusInfoReportVo.PointVo::getPId, a -> a, (k1, k2) -> k1));
            }
//            log.info("转发点号数据:{}", pointMap);
            //获取云网关和电桩相关缓存里面的数据
            Map<String, Integer> gatewayRealMap = ProtocolRunner.gatewayRealMap;
            Map<String, DevicePointDto> devicePointMap = ProtocolRunner.devicePointMap;
            //解析云网关设备接入的数据
            for (String terminalCode : gatewayRealMap.keySet()) {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
                if (gatewayRealModel != null && gatewayRealModel.getChannelRealMap().containsKey(KeyUtil.MQTT)) {
                    GatewayRealModel.ChannelRealModel channelRealModel = gatewayRealModel.getChannelRealMap().get(KeyUtil.MQTT);
                    Map<String, PointTableModel> pointTableMap = channelRealModel.getPointTableMap();
                    if (MapUtils.isNotEmpty(groupMap) || MapUtils.isNotEmpty(pointMap)) {
                        for (Map.Entry<String, PointTableModel> entry : pointTableMap.entrySet()) {
                            PointTableModel pointTable = entry.getValue();
                            if (groupMap.containsKey(pointTable.getDataId().intValue())) {
                                ModbusInfoReportVo.PointVo pointVo = groupMap.get(pointTable.getDataId().intValue());
                                pointTable.setDataType(8);
                                pointTable.setDataValue(pointVo.getVal());
                                pointTable.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            }
                            if (pointMap.containsKey(pointTable.getDataId().intValue())) {
                                ModbusInfoReportVo.PointVo pointVo = pointMap.get(pointTable.getDataId().intValue());
                                pointTable.setDataType(WebMqttUtil.convertFunction(pointVo.getAppType()));
                                pointTable.setDataValue(pointVo.getVal());
                                pointTable.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            }
                            pointTableMap.put(entry.getKey(), pointTable);
                        }
                    }
                    //暂时不考虑加锁
                    RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);

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
                }

            }
            //解析电桩相关的数据
            for (DevicePointDto devicePoint : devicePointMap.values()) {
                if (StringUtil.isNotEmpty(devicePoint.getDeviceNumber()) && MapUtils.isNotEmpty(devicePoint.getFunctionPointDataMap())) {
                    //对设备点号数据分组 功能点标识->设备点号数据
                    Map<String, List<DevicePointDto.FunctionPointData>> functionPointMap = devicePoint.getFunctionPointDataMap().values().stream()
                            .filter(s -> StringUtil.isNotEmpty(s.getDataId())).collect(Collectors
                                    .groupingBy(DevicePointDto.FunctionPointData::getFunctionLogo));
                    DeviceModel deviceModel = RedisDeviceUtil.getDevice(devicePoint.getDeviceNumber());
                    if (deviceModel != null && StringUtil.isNotEmpty(deviceModel.getTxStatus()) && deviceModel.getTxStatus() != 0) {
                        for (Map.Entry<String, FunctionModel> entry : deviceModel.getFunctionMap().entrySet()) {
                            String functionLogo = entry.getKey();
                            FunctionModel value = entry.getValue();
                            if (functionPointMap.containsKey(functionLogo)) {
                                //定义数组Map数据
                                Map<Integer, Object> valueMap = Maps.newHashMap();
                                for (DevicePointDto.FunctionPointData pointData : functionPointMap.get(functionLogo)) {
                                    //功能点 不是数组类型的
                                    if (StringUtil.isEmpty(pointData.getFunctionIndex()) && pointMap.containsKey(pointData.getDataId().intValue())) {
                                        value.setDataValue(pointMap.get(pointData.getDataId().intValue()).getVal());
                                    }
                                    //功能点 数组类型
                                    if (StringUtil.isNotEmpty(pointData.getFunctionIndex()) && pointMap.containsKey(pointData.getDataId().intValue())) {
                                        Object objectValue = pointMap.get(pointData.getDataId().intValue()).getVal();
                                        if (StringUtil.isNotEmpty(objectValue) && StringUtil.isNotEmpty(pointData.getFieldCode())) {
                                            double dataValue = Double.parseDouble(String.valueOf(objectValue));
                                            GeneralFieldEnum generalField = GeneralFieldEnum.getByFieldCode(pointData.getFieldCode());
                                            if (generalField != null) {
                                                switch (generalField) {
                                                    //充电枪工作状态
                                                    case GUN_STATUS:
                                                        int i = (int) dataValue;
                                                        if (i == 0 || i == 5) {
                                                            objectValue = 0; //空闲
                                                        }
                                                        if (Arrays.asList(1, 2, 3).contains(i)) {
                                                            objectValue = 1; //充电准备
                                                        }
                                                        if (i == 4) {
                                                            objectValue = 2; //充电中
                                                        }
                                                        if (Arrays.asList(6, 7, 8).contains(i)) {
                                                            objectValue = 255; //故障
                                                        }
                                                        break;
                                                    case OUT_VOLT:
                                                    case OUT_CURRENT:
                                                    case REQ_VOLT:
                                                    case REQ_CURRENT:
                                                        objectValue = DoubleUtil.getToDouble(dataValue * 0.1, 4);
                                                        break;
                                                    case BATTERY_SOC:
                                                        objectValue = (int) dataValue;
                                                        break;
                                                    case RUN_TIME:
                                                        objectValue = (int) DoubleUtil.getToDouble(dataValue * 60).doubleValue();
                                                        break;
                                                    case TOTAL_QT:
                                                        objectValue = DoubleUtil.getToDouble(dataValue * 0.001);
                                                        break;
                                                    default:
                                                        objectValue = dataValue;
                                                        break;

                                                }
                                            }
                                        }
                                        valueMap.put(pointData.getFunctionIndex(), objectValue);
                                    }
                                }

                                if (MapUtils.isNotEmpty(valueMap)) {
                                    //缓存里面没数据 赋值
                                    List<Object> valueList = Lists.newArrayList();
                                    if (StringUtil.isEmpty(value.getDataValue())) {
                                        for (int i = 0; i < devicePoint.getGunCodeList().size(); i++) {
                                            valueList.add(i, valueMap.getOrDefault(i, null));
                                        }
                                    } else {
                                        List<Object> oldValueList = JSON.parseArray(String.valueOf(value.getDataValue()), Object.class);
                                        Map<Integer, Object> oldValueMap = IntStream.range(0, oldValueList.size()).boxed()
                                                .collect(HashMap::new, (m, i) -> m.put(i, oldValueList.get(i)), HashMap::putAll);
                                        for (int i = 0; i < devicePoint.getGunCodeList().size(); i++) {
                                            valueList.add(i, valueMap.getOrDefault(i, oldValueMap.getOrDefault(i, null)));
                                        }
                                    }
                                    if (CollectionUtils.isNotEmpty(valueList)) {
                                        value.setDataValue(JSON.toJSONString(valueList));
                                    }
                                }
                                deviceModel.getFunctionMap().put(functionLogo, value);
                            }
                        }
                        RedisDeviceUtil.setDevice(devicePoint.getDeviceNumber(), deviceModel);

                        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(devicePoint.getDeviceNumber());
                        if (pileRealModel != null) {
                            Map<String, String> fieldValueMap = deviceModel.getFunctionMap().values().stream().filter(s ->
                                            StringUtil.isNotEmpty(s.getFieldCode()) && StringUtil.isNotEmpty(s.getDataValue()))
                                    .collect(Collectors.toMap(FunctionModel::getFieldCode, s -> String.valueOf(s.getDataValue()),
                                            (k1, k2) -> k1));
                            pileRealModel.setPileCode(devicePoint.getDeviceNumber());
                            pileRealModel.setWorkStatus(1);
                            pileRealModel.setMessageType(1);
//                        pileRealModel.setTerminalCode();
                            pileRealModel.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));

                            Map<String, PileRealModel.GunRealModel> gunRealModelMap = Maps.newHashMap();
                            for (int i = 0; i < devicePoint.getGunCodeList().size(); i++) {
                                PileRealModel.GunRealModel gunRealModel = new PileRealModel.GunRealModel();
                                String gunCode = devicePoint.getGunCodeList().get(i);
                                gunRealModel.setGunCode(gunCode);
                                //枪状态
                                gunRealModel.setGunStatus(-1);
                                String gunStatus = getFieldValue(fieldValueMap, i, GeneralFieldEnum.GUN_STATUS.getFieldCode());
                                if (StringUtil.isNotEmpty(gunStatus)) {
                                    gunRealModel.setGunStatus(Integer.valueOf(gunStatus));
                                    gunRealModel.setGunOriginalStatus(Integer.valueOf(gunStatus));
                                }
                                //需求电压
                                gunRealModel.setReqVolt(null);
                                String reqVolt = getFieldValue(fieldValueMap, i, GeneralFieldEnum.REQ_VOLT.getFieldCode());
                                if (StringUtil.isNotEmpty(reqVolt)) {
                                    gunRealModel.setReqVolt(DoubleUtil.getToDouble(Double.parseDouble(reqVolt), 4));
                                }
                                //需求电流
                                gunRealModel.setReqCurrent(null);
                                String reqCurrent = getFieldValue(fieldValueMap, i, GeneralFieldEnum.REQ_CURRENT.getFieldCode());
                                if (StringUtil.isNotEmpty(reqCurrent)) {
                                    gunRealModel.setReqCurrent(DoubleUtil.getToDouble(Double.parseDouble(reqCurrent), 4));
                                }
                                //计算需求功率
                                gunRealModel.setReqPower(null);
                                if (StringUtil.isNotEmpty(gunRealModel.getReqVolt()) && StringUtil.isNotEmpty(gunRealModel.getReqCurrent())) {
                                    gunRealModel.setReqPower(DoubleUtil.getToDouble(gunRealModel.getReqVolt() * gunRealModel.getReqCurrent() * 0.001, 4));
                                }
                                //输出电压
                                gunRealModel.setOutVolt(null);
                                String outVolt = getFieldValue(fieldValueMap, i, GeneralFieldEnum.OUT_VOLT.getFieldCode());
                                if (StringUtil.isNotEmpty(outVolt)) {
                                    gunRealModel.setOutVolt(DoubleUtil.getToDouble(Double.parseDouble(outVolt), 4));
                                }
                                //输出电流
                                gunRealModel.setOutCurrent(null);
                                String outCurrent = getFieldValue(fieldValueMap, i, GeneralFieldEnum.OUT_CURRENT.getFieldCode());
                                if (StringUtil.isNotEmpty(outCurrent)) {
                                    gunRealModel.setOutCurrent(DoubleUtil.getToDouble(Double.parseDouble(outCurrent), 4));
                                }
                                //计算输出功率
                                gunRealModel.setOutPower(null);
                                if (StringUtil.isNotEmpty(gunRealModel.getOutVolt()) && StringUtil.isNotEmpty(gunRealModel.getOutCurrent())) {
                                    gunRealModel.setOutPower(DoubleUtil.getToDouble(gunRealModel.getOutVolt() * gunRealModel.getOutCurrent() * 0.001, 4));
                                }
                                //车SOC
                                gunRealModel.setBatterySoc(null);
                                String batterySoc = getFieldValue(fieldValueMap, i, GeneralFieldEnum.BATTERY_SOC.getFieldCode());
                                if (StringUtil.isNotEmpty(batterySoc)) {
                                    gunRealModel.setBatterySoc((int) Double.parseDouble(batterySoc));
                                }
                                //已充电量
                                gunRealModel.setTotalQt(null);
                                String totalQt = getFieldValue(fieldValueMap, i, GeneralFieldEnum.TOTAL_QT.getFieldCode());
                                if (StringUtil.isNotEmpty(totalQt)) {
                                    gunRealModel.setTotalQt(DoubleUtil.getToDouble(Double.parseDouble(totalQt)));
                                }
                                //运行时间
                                gunRealModel.setRunTime(null);
                                String runTime = getFieldValue(fieldValueMap, i, GeneralFieldEnum.RUN_TIME.getFieldCode());
                                if (StringUtil.isNotEmpty(runTime)) {
                                    gunRealModel.setRunTime((int) Double.parseDouble(runTime));
                                }
                                gunRealModelMap.put(gunCode, gunRealModel);
                            }
                            pileRealModel.setGunRealModelMap(gunRealModelMap);

                            List<PileRealModel.GunRealModel> gunRealModelList = pileRealModel.getGunRealModelMap().values().stream()
                                    .filter(gun -> (StringUtil.isNotEmpty(pileRealModel.getWorkStatus())
                                            && pileRealModel.getWorkStatus() != 88) && StringUtil.isNotEmpty(gun.getGunStatus())
                                            && StringUtil.isNotEmpty(gun.getOutPower())).collect(Collectors.toList());
                            double rechargePower = gunRealModelList.stream().filter(gun -> Objects.equals(gun.getGunStatus(), 1)
                                    || Objects.equals(gun.getGunStatus(), 2)).mapToDouble(PileRealModel.GunRealModel::getOutPower).sum();
                            double dischargePower = gunRealModelList.stream().filter(gun -> Objects.equals(gun.getGunStatus(), 4)
                                    || Objects.equals(gun.getGunStatus(), 5)).mapToDouble(PileRealModel.GunRealModel::getOutPower).sum();
                            pileRealModel.setRecChargePower(rechargePower);
                            pileRealModel.setDisChargePower(dischargePower);
                            pileRealModel.setTotalPower(rechargePower + dischargePower);
                            pileRealModel.setDateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            //暂时不做加锁处理
                            RedisGeneralUtil.setPileRealModel(devicePoint.getDeviceNumber(), pileRealModel);
                        }

                    }
                }
            }
        } catch (Exception e) {
            log.error("内网MQTT解析ieg遥信遥测数据失败", e);
        }

    }

    /**
     * 心跳数据
     *
     * @param devHeartBeatVo 心跳参数
     */
    public static void pileHeartBeat(DevHeartBeatVo devHeartBeatVo) {
        heartBeatMap.put(devHeartBeatVo.getDeviceId(), LocalDateTime.now());
    }

    /**
     * 日志数据上报
     *
     * @param pilesCode       充电桩编号
     * @param pileLogReportVo 日志数据参数
     */
    public static void pileLogReport(String pilesCode, PileLogReportVo pileLogReportVo) {
        try {
            //保存日志数据上报
            String gunCode = StringUtil.isNotEmpty(pileLogReportVo.getGunCode()) ? String.valueOf(pileLogReportVo.getGunCode()) : null;
            PileRecordUtil.saveMqttRecord(pilesCode, gunCode, 2, 8, 2, CmdConstant.CMD_PILE_LOGREPORT, pileLogReportVo);
        } catch (Exception e) {
            log.error("内网MQTT日志数据上报失败", e);
        }
    }

    /**
     * 电桩复位响应
     *
     * @param pilesCode         充电桩编号
     * @param pileResetResultVo 电桩复位响应
     */
    public static void pileResetResult(String pilesCode, PileResetResultVo pileResetResultVo) {
        try {
            PileRecordUtil.saveMqttRecord(pilesCode, null, 2, 10, 2, CmdConstant.CMD_PILE_RESET_RESULT, pileResetResultVo);
            //收到此命令 立即响应需求
            String keyId = pilesCode + pileResetResultVo.getType() + SMV2gConstant.PILE_RESET;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(pileResetResultVo.getResult());//执行结果
            }
        } catch (Exception e) {
            log.error("内网MQTT电桩复位响应失败", e);
        }
    }

    /**
     * 电桩设置二维码响应
     *
     * @param pileSetQrResVo 电桩设置二维码响应参数
     */
    public static void pileSetQrRes(PileSetQrResVo pileSetQrResVo) {
        try {
            String pilesCode = pileSetQrResVo.getPilesCode();
            PileRecordUtil.saveMqttRecord(pilesCode, null, 2, 12, 2, CmdConstant.CMD_PILE_SETQR_RES, pileSetQrResVo);
            //收到此命令 立即响应需求
            String keyId = pilesCode + SMV2gConstant.PILE_SETQR;
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                PileDemandModel demandModel = PileDemand.demandModelMap.get(keyId);
                demandModel.setRecFlag(true);
                demandModel.setRecCode(pileSetQrResVo.getResult());//执行结果 0-成功 1-失败
            }
        } catch (Exception e) {
            log.error("设置二维码前缀响应失败", e);
        }
    }

    private static String getFieldValue(Map<String, String> fieldValueMap, Integer i, String fieldCode) {
        if (fieldValueMap.containsKey(fieldCode)) {
            List<String> valueList = JSON.parseArray(fieldValueMap.get(fieldCode), String.class);
            if (CollectionUtils.isNotEmpty(valueList) && valueList.size() > i) {
                return valueList.get(i);
            }
        }
        return null;
    }

}
