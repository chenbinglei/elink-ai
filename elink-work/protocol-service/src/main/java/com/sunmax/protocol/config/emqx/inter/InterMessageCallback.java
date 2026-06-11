package com.sunmax.protocol.config.emqx.inter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.constant.CmdConstant;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.protocol.mqtt.inter.*;
import com.sunmax.protocol.util.platform.SmInterControlHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
public class InterMessageCallback implements MqttCallback {

    // 创建线程池
    private final ExecutorService executorService = new ThreadPoolExecutor(36, 48, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void connectionLost(Throwable cause) {
        log.info("内网MQTT连接断开，可以做重连");
        if (InterMqttConfig.client != null && InterMqttConfig.client.isConnected()) {
            InterMqttConfig.disconnect();
        }
        SpringBeanUtil.getBean(InterMqttConfig.class).interMqttClient();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // 在新的线程中处理消息
        executorService.submit(() -> {
//            log.info("内网MQTT接收到的主题" + topic);
            try {
                String content = new String(message.getPayload(), StandardCharsets.UTF_8);
                CMDTopicVo cmdTopicVo = JSONObject.parseObject(content, CMDTopicVo.class);
                protocolRecMain(cmdTopicVo);
            } catch (RuntimeException e) {
                log.error("内网MQTT处理数据失败", e);
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 处理消息发送完成的情况
//        log.info("处理消息完成" + token.isComplete());
    }

    private static void protocolRecMain(CMDTopicVo cmdTopicVo) {
        CMDTopicVo.Body body = cmdTopicVo.getBody();
        if (body != null && StringUtil.isNotEmpty(body.getDevId()) && StringUtil.isNotEmpty(body.getCmd())) {
            String deviceId = body.getDevId();
            //校验设备状态 平台未注册时不允许接收消息
            if (checkDeviceStatus(deviceId)) {
                return;
            }
            JSONObject data = JSONObject.parseObject(JSON.toJSONString(body.getBody()));
            switch (body.getCmd()) {
                case CmdConstant.CMD_RATE_REQ://费率请求
                    InterMqttDataHandler.rateReq(deviceId, data.toJavaObject(RateReqVo.class));
                    break;
//                case CmdConstant.CMD_RATE_RES://费率响应
//                    break;
                case CmdConstant.CMD_SUBLINKUP_NOTIFY://子设备上线通知
                    InterMqttDataHandler.subLinkUpNotify(deviceId, data.toJavaObject(SubLinkUpNotifyVo.class));
                    break;
                case CmdConstant.CMD_CBINFO_NOTIFY://硬件控制板软硬件信息通知
                    InterMqttDataHandler.cbInfoNotify(deviceId, data.toJavaObject(CbInfoNotifyVo.class));
                    break;
//                case CmdConstant.CMD_START://启动命令
//                    break;
                case CmdConstant.CMD_START_RES://启动响应
                    StartResVo startResVo = data.toJavaObject(StartResVo.class);
                    InterMqttDataHandler.startRes(deviceId, startResVo);
                    SmInterControlHandler.startRes(deviceId, startResVo);
                    break;
                case CmdConstant.CMD_START_EVENT://启动事件
                    StartEventVo startEventVo = data.toJavaObject(StartEventVo.class);
                    InterMqttDataHandler.startEvent(deviceId, startEventVo);
                    SmInterControlHandler.pushStartEvent(startEventVo);
                    break;
//                case CmdConstant.CMD_STOP://停止命令
//                    break;
                case CmdConstant.CMD_STOP_RES://停止响应
                    StopResVo stopResVo = data.toJavaObject(StopResVo.class);
                    InterMqttDataHandler.stopRes(deviceId, stopResVo);
                    SmInterControlHandler.stopRes(deviceId, stopResVo);
                    break;
                case CmdConstant.CMD_STOP_EVENT://停止事件
                    StopEventVo stopEventVo = data.toJavaObject(StopEventVo.class);
                    SmInterControlHandler.pushStopEvent(stopEventVo);
                    InterMqttDataHandler.stopEvent(deviceId, stopEventVo);
                    break;
//                case CmdConstant.CMD_POWERCONTROL://功率控制
//                    break;
                case CmdConstant.CMD_POWERCONTROL_RES://功率控制响应
                    PowerControlResVo powerControlResVo = data.toJavaObject(PowerControlResVo.class);
                    InterMqttDataHandler.powerControlRes(deviceId, powerControlResVo);
                    SmInterControlHandler.powerControlRes(deviceId, powerControlResVo);
                    break;
                case CmdConstant.CMD_PILE_DATA_REPORT://电桩数据上报
                    InterMqttDataHandler.pileDataReport(deviceId, data.toJavaObject(PileDataReportVo.class));
                    break;
                case CmdConstant.CMD_PILE_FAULT_REPORT://电桩故障上报
                    InterMqttDataHandler.pileFaultReport(deviceId, data.toJavaObject(PileFaultReportVo.class));
                    break;
                case CmdConstant.CMD_PILE_STATUS_REPORT://电桩状态上报
                    PileStatusReportVo pileStatusReportVo = data.toJavaObject(PileStatusReportVo.class);
                    InterMqttDataHandler.pileStatusReport(deviceId, pileStatusReportVo);
                    SmInterControlHandler.pushPileStatus(deviceId, pileStatusReportVo);
                    break;
                case CmdConstant.CMD_PILE_RECORD_REPORT://充电记录上报
                    PileRecordReportVo pileRecordVo = data.toJavaObject(PileRecordReportVo.class);
                    SmInterControlHandler.pushPileRecord(pileRecordVo);
                    InterMqttDataHandler.pileRecordReport(deviceId, pileRecordVo);
                    break;
//                case CmdConstant.CMD_PILE_RECORD_CONFIRM://充电记录上报确认
//                    break;
                case CmdConstant.CMD_AuthenticationRequest://鉴权请求
                    InterMqttDataHandler.authenticationReq(deviceId, data.toJavaObject(AuthenticationReqVo.class));
                    break;
//                case CmdConstant.CMD_AuthenticationResponse://鉴权响应
//                    break;
                case CmdConstant.CMD_StrategySettingRequest://策略设置
                    InterMqttDataHandler.strategySettingReq(deviceId, data.toJavaObject(StrategySettingReqVo.class));
                    break;
//                case CmdConstant.CMD_StrategySettingResponse://策略设置响应
//                    break;
//                case CmdConstant.CMD_DevUpdate://设备升级响应，平台-网关-设备
//                    break;
                case CmdConstant.CMD_DevDataBlockRequest://设备升级-数据块请求
                    InterMqttDataHandler.devDataBlockReq(deviceId, data.toJavaObject(DevDataBlockReqVo.class));
                    break;
//                case CmdConstant.CMD_DevDataBlockResponse://设备升级-数据块响应
//                    break;
                case CmdConstant.CMD_DevUpdateReport://设备升级-升级结果上报
                    InterMqttDataHandler.devUpdateReport(deviceId, data.toJavaObject(DevUpdateReportVo.class));
                    break;
//                case CmdConstant.CMD_RATE_SET://费率下发
//                    break;
                case CmdConstant.CMD_RATE_SET_RES://费率下发响应
                    InterMqttDataHandler.rateSetRes(deviceId, data.toJavaObject(RateSetResVo.class));
                    break;
//                case CmdConstant.CMD_PARAM_SET://网关通用参数设置
//                    break;
//                case CmdConstant.CMD_PARAM_SET_RES://网关通用参数设置响应
//                    break;
//                case CmdConstant.CMD_PileDataRequest://电桩运行数据请求
//                    break;
                case CmdConstant.CMD_PileDataResponse://电桩运行数据请求响应
                    InterMqttDataHandler.pileDataRes(deviceId, data.toJavaObject(PileDataResVo.class));
                    break;
//                case CmdConstant.CMD_VehicleInfoRequest://车辆信息请求
//                    break;
                case CmdConstant.CMD_VehicleInfoResponse://车辆信息请求响应
                    InterMqttDataHandler.vehicleInfoRes(deviceId, data.toJavaObject(VehicleInfoResVo.class));
                    break;
//                case CmdConstant.CMD_PileRecordReportRequest://电桩记录查询
//                    break;
                case CmdConstant.CMD_PileRecordReportResponse://电桩记录查询命令响应
                    InterMqttDataHandler.pileRecordReportRes(deviceId, data.toJavaObject(PileRecordReportResVo.class));
                    break;
                case CmdConstant.CMD_PileRecordReportInfoResponse://电桩记录查询结果响应
                    InterMqttDataHandler.pileRecordReportInfoRes(deviceId, data.toJavaObject(PileRecordReportInfoResVo.class));
                    break;
//                case CmdConstant.CMD_ModbusSetSingleCoil://Modbus协议写单个线圈
//                    break;
//                case CmdConstant.CMD_ModbusSetSingleReg://Modbus协议写单个寄存器
//                    break;
//                case CmdConstant.CMD_CreatePileShadow://电桩上线创建电桩影子线程
//                    break;
//                case CmdConstant.CMD_DeletePileShadow://电桩离线 删除电桩影子线程
//                    break;
//                case CmdConstant.CMD_PileToShadow://电桩转发数据到影子电桩
//                    break;
//                case CmdConstant.CMD_ShadowToPile://影子电桩收到的平台报文发送给电桩
//                    break;
                case CmdConstant.CMD_PILE_BMSINFO_REPORT://充电bms信息
                    InterMqttDataHandler.pileBmsInfoReport(deviceId, data.toJavaObject(PileBmsInfoReportVo.class));
                    break;
                case CmdConstant.CMD_MODBUS_INFO_REPORT://modbus数据
                    InterMqttDataHandler.modbusInfoReport(deviceId, data.toJavaObject(ModbusInfoReportVo.class));
                    break;
                case CmdConstant.CMD_Dev_HeartBeat://设备心跳
                    InterMqttDataHandler.pileHeartBeat(data.toJavaObject(DevHeartBeatVo.class));
                    break;
                case CmdConstant.CMD_PILE_LOGREPORT://日志数据上报
                    InterMqttDataHandler.pileLogReport(deviceId, data.toJavaObject(PileLogReportVo.class));
                    break;
                case CmdConstant.CMD_PILE_RESET_RESULT://电桩复位响应
                    InterMqttDataHandler.pileResetResult(deviceId, data.toJavaObject(PileResetResultVo.class));
                case CmdConstant.CMD_PILE_SETQR_RES://设置二维码前缀响应
                    InterMqttDataHandler.pileSetQrRes(data.toJavaObject(PileSetQrResVo.class));
                    break;
            }
        }
    }

    //校验设备状态
    private static boolean checkDeviceStatus(String deviceId) {
        DeviceModel deviceModel = RedisDeviceUtil.getDevice(deviceId);
        if (deviceModel == null || StringUtil.isEmpty(deviceModel.getTxStatus()) || deviceModel.getTxStatus() == 0) {
            RedisGeneralUtil.executePile(deviceId, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(deviceId);
                List<Integer> workStatus = Arrays.asList(-1, 0);
                if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && !workStatus.contains(pileRealModel.getWorkStatus())) {
                    pileRealModel.setWorkStatus(0);
                    pileRealModel.setOriginalStatus(null);
                    if (MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                        pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> {
                            gunRealModel.setGunStatus(-1);
                            gunRealModel.setGunOriginalStatus(null);
                            pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        });
                        pileRealModel.setGunRealModelMap(pileRealModel.getGunRealModelMap());
                    }
                    RedisGeneralUtil.setPileRealModel(deviceId, pileRealModel);
                }
            });
            return true;
        }
        return false;
    }

}
