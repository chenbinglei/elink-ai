package com.sunmax.protocol.util.platform;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.SunMaxUtil;
import com.sunmax.common.vo.protocol.mqtt.inter.*;
import com.sunmax.protocol.constant.SMV2gConstant;
import com.sunmax.protocol.dto.platform.PlatformDataDto;
import com.sunmax.protocol.runner.ProtocolRunner;
import com.sunmax.protocol.vo.PlatformRequestVo;
import com.sunmax.protocol.vo.platform.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 平台V2G协议控制处理
 */
@Slf4j
public class SmInterControlHandler {

    /**
     * 启动命令响应的处理逻辑。
     * <p>
     * 当接收到特定的命令响应时，此方法用于更新相关的需求模型状态。
     *
     * @param startResVo 包含响应信息的模型对象，其中包括了通用消息模型和特定的枪支代码等信息。
     */
    public static void startRes(String pilesCode, StartResVo startResVo) {
        // 根据响应信息和枪支代码等生成唯一的键值，用于在需求模型映射中查找对应的模型
        String keyId = pilesCode + startResVo.getGunCode() + SMV2gConstant.STARTCMD;

        // 检查映射中是否存在对应键值的需求模型，如果存在，则进行状态更新
        if (PlatformControlDemand.demandModelMap.containsKey(keyId)) {
            PlatformDemandModel demandModel = PlatformControlDemand.demandModelMap.get(keyId);
            // 更新需求模型的状态，标记为已接收，并记录失败原因和详细信息（如果有）
            demandModel.setRecFlag(true);
            demandModel.setRecCode(startResVo.getResponseResult());
            if (StringUtil.isNotEmpty(startResVo.getFailReason())) {
                demandModel.setRecMsg(startResVo.getFailReason());
            }
            demandModel.setSerialNum(startResVo.getRecordId());
            log.info("内网MQTT平台V2G协议启动响应需求:{}", demandModel);
        }
    }

    /**
     * 停止响应的处理方法。
     * 该方法用于处理停止命令的响应，主要逻辑是根据给定的停止响应参数更新需求模型（demandModel）的状态。
     *
     * @param stopResVo 停止响应的参数模型，包含枪支代码、错误原因和详细错误信息等。
     */
    public static void stopRes(String pilesCode, StopResVo stopResVo) {
        // 根据消息模型中的通用消息部分获取堆栈代码，并结合枪支代码和停止命令的标识生成唯一键
        String keyId = pilesCode + stopResVo.getGunCode() + SMV2gConstant.STOPCMD;

        // 检查是否存在对应的需求模型
        if (PlatformControlDemand.demandModelMap.containsKey(keyId)) {
            // 如果存在，则获取该需求模型并更新其状态，标记为已接收，记录失败原因和详细信息
            PlatformDemandModel demandModel = PlatformControlDemand.demandModelMap.get(keyId);
            demandModel.setRecFlag(true);
            demandModel.setRecCode(stopResVo.getResponseResult());
            if (StringUtil.isNotEmpty(stopResVo.getFailReason())) {
                demandModel.setRecMsg(stopResVo.getFailReason());
            }
            demandModel.setSerialNum(stopResVo.getRecordId());
            log.info("内网MQTT平台V2G协议停止响应需求:{}", demandModel);
        }
    }

    /**
     * 功率控制响应的处理方法。
     * 该方法用于处理功率控制的响应，主要逻辑是根据给定的功率控制响应参数更新需求模型（demandModel）的状态。
     *
     * @param powerControlResVo 功率控制响应的参数模型，包含枪支代码、错误原因和详细错误信息等。
     */
    public static void powerControlRes(String pilesCode, PowerControlResVo powerControlResVo) {
        // 根据消息模型中的通用消息部分获取堆栈代码，并结合枪支代码和停止命令的标识生成唯一键
        String keyId = pilesCode + powerControlResVo.getGunCode() + SMV2gConstant.POWERCTRL;

        // 检查是否存在对应的需求模型
        if (PlatformControlDemand.demandModelMap.containsKey(keyId)) {
            // 如果存在，则获取该需求模型并更新其状态，标记为已接收，记录失败原因和详细信息
            PlatformDemandModel demandModel = PlatformControlDemand.demandModelMap.get(keyId);
            demandModel.setRecFlag(true);
            demandModel.setRecCode(powerControlResVo.getResponseResult());
            log.info("内网MQTT平台V2G协议功率控制响应需求:{}", demandModel);
        }
    }

    /**
     * 推送平台V2G协议启动事件处理方法。
     * 该方法用于处理枪机启动事件，首先从Redis中获取枪机状态模型，然后检查指定枪机是否正在运行，
     * 如果是，则创建一个启动事件的视图对象并准备调用平台V2G协议提供的接口
     *
     * @param startEventVo 包含枪机信息和启动事件数据的模型对象。
     */
    public static void pushStartEvent(StartEventVo startEventVo) {
        try {
            // 获取堆垛机代码和枪机代码
            String pileCode = startEventVo.getPilesCode();
            String gunCode = String.valueOf(startEventVo.getGunCode());

            // 从Redis获取电桩状态模型
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            if (pileRealModel != null) {

                // 检查枪机状态模型中是否包含该枪机代码
                if (pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                    PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);

                    // 如果枪机正在运行，并且启动者信息符合特定值，则处理启动事件
                    if (StringUtil.isNotEmpty(gunRealModel.getStarter()) && Objects.equals(gunRealModel.getStarter(), PlatformUtil.starter)) {
                        // 准备启动事件的相关信息
                        PlatformStartEventVo result = new PlatformStartEventVo();
                        result.setPileCode(pileCode);
                        result.setGunCode(startEventVo.getGunCode());
                        if (StringUtil.isNotEmpty(startEventVo.getStartTime())) {
                            result.setStartTime(SunMaxUtil.timeStamp0Date(startEventVo.getStartTime()));
                        }
                        if (StringUtil.isNotEmpty(startEventVo.getEventResult())) {
                            result.setFailReason(startEventVo.getEventResult());
                        }
                        if (StringUtil.isNotEmpty(startEventVo.getFailReason())) {
                            result.setFailDetailReason(startEventVo.getFailReason());
                        }
                        result.setStopDetail(startEventVo.getStopDetail());
                        result.setSerialNum(startEventVo.getRecordId());
                        // 调用平台V2G协议提供的接口，发送启动事件信息
                        log.info("内网MQTT推送平台V2G协议启动事件:{}", result);
                        PlatformDataDto platformData = ProtocolRunner.getPlatformData(pileCode);
                        if (platformData != null) {
                            PlatformRequestVo requestVo = new PlatformRequestVo();
                            requestVo.setPlatformId(platformData.getPlatformId());
                            requestVo.setData(PlatformUtil.encryptData(platformData, JSON.toJSONString(result)));
                            log.info("内网MQTT推送平台V2G协议启动事件响应结果:{}", PlatformUtil.post(platformData.getUrl(), "pushStartEvent", requestVo));
                        } else {
                            log.error("内网MQTT推送平台V2G协议启动事件失败，未找到对应平台ID");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 记录启动事件发送失败的错误日志
            log.error("内网MQTT平台V2G协议启动事件发送失败", e);
        }
    }

    /**
     * 推送电桩状态处理方法。
     * 该方法用于处理电桩状态推送，首先从Redis中获取电桩状态模型，然后检查指定电桩是否正在运行，
     * 如果是，则创建一个电桩状态的视图对象并准备调用平台V2G协议提供的接口。
     *
     * @param pileStatusVo 包含电桩信息和电桩状态数据的模型对象。
     *                     该对象应包含电桩的运行状态和接口状态等详细信息。
     */
    public static void pushPileStatus(String pileCode, PileStatusReportVo pileStatusVo) {

        try {
            // 从Redis获取当前充电桩的状态信息
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            if (PlatformUtil.pileRunMap.containsKey(pileCode) && pileRealModel != null) {
                // 创建电桩状态视图对象
                PlatformPileStatusVo pileStatus = new PlatformPileStatusVo();
                pileStatus.setPileCode(pileCode);
                pileStatus.setWorkStatus(pileStatusVo.getWorkState());

                // 遍历接口状态，构建电桩状态视图的接口数据列表
                AtomicBoolean isRun = new AtomicBoolean(false);
                List<Integer> gunWorkStates = Arrays.asList(1, 2, 4, 5);
                List<PlatformPileStatusVo.ItfData> itfDataList = pileStatusVo.getGun_state().stream().map(itfRunState -> {
                    PlatformPileStatusVo.ItfData result = new PlatformPileStatusVo.ItfData();
                    // 检查并设置枪的工作状态
                    if (!isRun.get()) {
                        if (gunWorkStates.contains(itfRunState.getWorkState())) {
                            isRun.set(true);
                        }
                    }
                    // 填充接口数据
                    result.setGunCode(itfRunState.getGuncode());
                    result.setGunWorkStatus(itfRunState.getWorkState());
                    result.setParkingStatus(itfRunState.getParkingLockState());
                    result.setVehicleConnStatus(itfRunState.getVehicleConnState());
                    // 如果是运行状态且有对应枪的状态数据，填充详细运行数据
                    String gunCode = String.valueOf(itfRunState.getGuncode());
                    if (gunWorkStates.contains(itfRunState.getWorkState()) && pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                        PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                        if (StringUtil.isNotEmpty(gunRealModel.getStarter()) && Objects.equals(gunRealModel.getStarter(), PlatformUtil.starter)) {
                            result.setSerialNum(gunRealModel.getSerialNum());
                            // 填充详细的电桩运行数据
                            result.setBatterySoc(gunRealModel.getBatterySoc());
                            result.setRunMode(gunRealModel.getRunMode());
                            result.setOutVolt(DoubleUtil.getToDouble(gunRealModel.getOutVolt(), 4));
                            result.setOutCurrent(DoubleUtil.getToDouble(gunRealModel.getOutCurrent(), 4));
                            result.setReqVolt(DoubleUtil.getToDouble(gunRealModel.getReqVolt(), 4));
                            result.setReqCurrent(DoubleUtil.getToDouble(gunRealModel.getReqCurrent(), 4));
                            result.setDirMeterNum(DoubleUtil.getToDouble(gunRealModel.getDirMeterNum(), 4));
                            result.setAlterMeterNum(DoubleUtil.getToDouble(gunRealModel.getAlterMeterNum(), 4));
                            result.setRunTime(gunRealModel.getRunTime());
                            result.setRemainTime(gunRealModel.getRemainTime());
                            result.setTotalQt(DoubleUtil.getToDouble(gunRealModel.getTotalQt(), 4));
                            result.setTotalCost(DoubleUtil.getToBigDecimal(gunRealModel.getTotalCost(), 4));
                        }
                    }
                    return result;
                }).collect(Collectors.toList());

                pileStatus.setItfDataList(itfDataList);
                // 将电桩状态视图对象转换为JSON字符串并发送给指定的电桩

                // 如果电桩处于运行状态，准备推送数据；否则，从运行映射中移除电桩
                if (isRun.get()) {
                    // 调用平台V2G协议提供的接口，发送电桩状态信息
                    log.info("内网MQTT推送平台V2G协议电桩状态及数据:{}", pileStatus);
                    PlatformDataDto platformData = ProtocolRunner.getPlatformData(pileCode);
                    if (platformData != null) {
                        PlatformRequestVo requestVo = new PlatformRequestVo();
                        requestVo.setPlatformId(platformData.getPlatformId());
                        requestVo.setData(PlatformUtil.encryptData(platformData, JSON.toJSONString(pileStatus)));
                        log.info("内网MQTT推送平台V2G协议电桩状态及数据响应结果:{}", PlatformUtil.post(platformData.getUrl(), "pushPileStatus", requestVo));
                    } else {
                        log.error("内网MQTT推送平台V2G协议电桩状态及数据失败，未找到对应平台ID");
                    }
                } else {
                    PlatformUtil.pileRunMap.remove(pileCode);
                    log.info("内网MQTT平台V2G协议从运行映射中移除电桩:{}", pileCode);
                }
            }

        } catch (Exception e) {
            // 处理异常，记录推送失败的错误日志
            log.error("内网MQTT推送平台V2G协议电桩状态数据失败: ", e);
        }
    }


    /**
     * 推送电桩停止事件处理方法。
     * 该方法用于处理电桩停止事件的推送，首先从Redis中获取电桩状态模型，然后检查指定电桩是否正在运行，
     * 如果是，则创建一个电桩停止事件的视图对象并准备调用平台V2G协议提供的接口。
     *
     * @param stopEventVo 包含电桩信息和电桩停止事件数据的模型对象。
     *                    其中包含了电桩编码、枪编码、停止详细原因、结束时间戳和序列号等信息。
     */
    public static void pushStopEvent(StopEventVo stopEventVo) {
        try {
            // 从消息模型中提取充电桩编码和枪编码
            String pileCode = stopEventVo.getPilesCode();
            String gunCode = String.valueOf(stopEventVo.getGunCode());

            // 从Redis获取当前充电桩的状态信息
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            if (pileRealModel != null) {

                // 检查枪编码是否存在于状态信息中，并确认电桩正在运行
                if (pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                    PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                    if (gunRealModel != null && StringUtil.isNotEmpty(gunRealModel.getStarter()) && Objects.equals(gunRealModel.getStarter(), PlatformUtil.starter)) {
                        // 准备电桩停止事件的视图对象
                        PlatformStopEventVo result = new PlatformStopEventVo();
                        result.setPileCode(pileCode);
                        result.setGunCode(stopEventVo.getGunCode());
                        if (StringUtil.isNotEmpty(stopEventVo.getFailReason())) {
                            result.setFailDetailReason(stopEventVo.getFailReason());
                        }
                        result.setEndTime(SunMaxUtil.timeStamp0Date(stopEventVo.getStopTime()));
                        result.setSerialNum(stopEventVo.getRecordId());

                        // 调用平台V2G协议提供的接口，推送电桩停止事件信息
                        // 这里需要实现调用外部接口的逻辑，将stopEventVo中的信息推送出去
                        log.info("内网MQTT推送平台V2G协议停止事件:{}", result);
                        PlatformDataDto platformData = ProtocolRunner.getPlatformData(pileCode);
                        if (platformData != null) {
                            PlatformRequestVo requestVo = new PlatformRequestVo();
                            requestVo.setPlatformId(platformData.getPlatformId());
                            requestVo.setData(PlatformUtil.encryptData(platformData, JSON.toJSONString(result)));
                            log.info("内网MQTT推送平台V2G协议停止事件响应结果:{}", PlatformUtil.post(platformData.getUrl(), "pushStopEvent", requestVo));
                        } else {
                            log.error("内网MQTT推送平台V2G协议停止事件失败，未找到对应平台ID");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 处理异常，记录推送失败的错误日志
            log.error("内网MQTT推送平台V2G协议电桩停止事件数据失败: ", e);
        }
    }


    /**
     * 向平台V2G协议平台推送电桩充放电记录信息。
     *
     * @param pileRecordVo 充电桩实时信息模型，包含充电桩的详细状态和操作信息。
     *                     此方法用于将充电桩的充放电记录信息推送至平台V2G协议平台。具体流程如下：
     *                     1. 从传入的Msg4011Model对象中提取充电桩编码和枪编码。
     *                     2. 从Redis中获取对应充电桩的状态信息。
     *                     3. 如果存在该充电桩的状态信息，并且该枪编码在状态信息中存在，且相关启动信息符合特定条件，则根据传入的信息创建一个电桩记录Vo对象。
     *                     4. 准备将此记录信息推送给平台V2G协议平台。
     *                     5. 如果在处理过程中发生异常，将记录错误日志。
     */
    public static void pushPileRecord(PileRecordReportVo pileRecordVo) {
        try {
            // 提取充电桩编码和枪编码
            String pileCode = pileRecordVo.getPilesCode();
            String gunCode = String.valueOf(pileRecordVo.getGunCode());

            // 从Redis获取充电桩状态信息
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            // 检查枪编码是否存在于状态信息中，以及启动信息是否符合特定条件
            if (pileRealModel != null && pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                if (gunRealModel != null && StringUtil.isNotEmpty(gunRealModel.getStarter()) && Objects.equals(gunRealModel.getStarter(), PlatformUtil.starter)) {

                    // 准备电桩停止事件的视图对象
                    PlatformStopEventVo stopEvent = new PlatformStopEventVo();
                    stopEvent.setPileCode(pileCode);
                    stopEvent.setGunCode(pileRecordVo.getGunCode());
                    stopEvent.setFailDetailReason(pileRecordVo.getStopReason());
                    stopEvent.setEndTime(SunMaxUtil.timeStamp0Date(pileRecordVo.getEndTime()));
                    stopEvent.setSerialNum(pileRecordVo.getRecordId());

                    // 调用平台V2G协议提供的接口，推送电桩停止事件信息
                    // 这里需要实现调用外部接口的逻辑，将stopEventVo中的信息推送出去
                    log.info("内网MQTT推送平台V2G协议电桩补偿停止事件:{}", stopEvent);
                    PlatformDataDto platformData = ProtocolRunner.getPlatformData(pileCode);
                    if (platformData != null) {
                        PlatformRequestVo requestVo = new PlatformRequestVo();
                        requestVo.setPlatformId(platformData.getPlatformId());
                        requestVo.setData(PlatformUtil.encryptData(platformData, JSON.toJSONString(stopEvent)));
                        log.info("内网MQTT推送平台V2G协议电桩补偿停止事件响应结果:{}", PlatformUtil.post(platformData.getUrl(), "pushStopEvent", requestVo));
                    } else {
                        log.error("内网MQTT推送平台V2G协议电桩补偿停止事件失败，未找到对应平台ID");
                    }

                    // 创建电桩记录Vo对象，并填充相关信息
                    PlatformPileRecordVo result = new PlatformPileRecordVo();
                    result.setPileCode(pileCode);
                    result.setGunCode(pileRecordVo.getGunCode());
                    result.setDealType(pileRecordVo.getReportType());
                    result.setItfRunPattern(pileRecordVo.getRunMode());
                    result.setStartTime(SunMaxUtil.timeStamp0Date(pileRecordVo.getStartTime()));
                    result.setEndTime(SunMaxUtil.timeStamp0Date(pileRecordVo.getEndTime()));
                    result.setStartMode(pileRecordVo.getStarter());
                    result.setSerialNum(pileRecordVo.getRecordId());
                    result.setStopDetailReason(pileRecordVo.getStopReason());
                    result.setRateTemplateId(pileRecordVo.getRateTemplateId());
                    result.setBusVin(pileRecordVo.getBusVin());
                    result.setTimeFrameNum(pileRecordVo.getTimeFrameNum());
                    result.setTimeFrameQt(pileRecordVo.getTimeFrameQ());
                    result.setTotalCurQt(DoubleUtil.getToDouble(pileRecordVo.getTotalQ() * 0.001, 4));
                    result.setTotalCost(DoubleUtil.getToBigDecimal(pileRecordVo.getTotalCost().multiply(new BigDecimal("0.001")), 4));
                    result.setStartSoc(pileRecordVo.getStartSoc());
                    result.setEndSoc(pileRecordVo.getEndSoc());
                    if (StringUtil.isEmpty(pileRecordVo.getStartDCMeters())) {
                        result.setStartDirMeterNum(0.0);
                    } else {
                        result.setStartDirMeterNum(DoubleUtil.getToDouble(pileRecordVo.getStartDCMeters() * 0.001, 4));
                    }
                    if (StringUtil.isEmpty(pileRecordVo.getEndDCMeters())) {
                        result.setEndDirMeterNum(0.0);
                    } else {
                        result.setEndDirMeterNum(DoubleUtil.getToDouble(pileRecordVo.getEndDCMeters() * 0.001, 4));
                    }
                    if (StringUtil.isEmpty(pileRecordVo.getStartACMeters())) {
                        result.setStartAlterMeterNum(0.0);
                    } else {
                        result.setStartAlterMeterNum(DoubleUtil.getToDouble(pileRecordVo.getStartACMeters() * 0.001, 4));
                    }
                    if (StringUtil.isEmpty(pileRecordVo.getEndACMeters())) {
                        result.setEndAlterMeterNum(0.0);
                    } else {
                        result.setEndAlterMeterNum(DoubleUtil.getToDouble(pileRecordVo.getEndACMeters() * 0.001, 4));
                    }

                    log.info("内网MQTT推送平台V2G协议推送电桩记录:{}", result);
                    if (platformData != null) {
                        PlatformRequestVo requestVo = new PlatformRequestVo();
                        requestVo.setPlatformId(platformData.getPlatformId());
                        requestVo.setData(PlatformUtil.encryptData(platformData, JSON.toJSONString(result)));
                        log.info("内网MQTT推送平台V2G协议电桩记录响应结果:{}", PlatformUtil.post(platformData.getUrl(), "pushPileRecord", requestVo));
                    } else {
                        log.error("内网MQTT推送平台V2G协议电桩记录失败，未找到对应平台ID");
                    }
                }
            }
        } catch (Exception e) {
            // 记录推送失败的错误日志
            log.error("内网MQTT推送平台V2G协议电桩记录数据失败: ", e);
        }
    }


}
