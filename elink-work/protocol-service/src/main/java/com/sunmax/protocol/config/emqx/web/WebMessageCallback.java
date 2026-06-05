package com.sunmax.protocol.config.emqx.web;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.constant.IEGConstant;
import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.protocol.mqtt.web.command.CmdToPoFwResSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.command.CmdToPoUpdateSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.DataSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.IEGTopicVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.ResponseSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.data.*;
import com.sunmax.common.vo.protocol.mqtt.web.from.*;
import com.sunmax.common.vo.protocol.mqtt.web.request.*;
import com.sunmax.common.vo.protocol.mqtt.web.response.*;
import com.sunmax.protocol.util.platform.SmWebControlHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class WebMessageCallback implements MqttCallback {

    // 创建线程池
    private final ExecutorService executorService = new ThreadPoolExecutor(36, 48, 30L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void connectionLost(Throwable cause) {
        log.info("外网MQTT连接断开，可以做重连");
        if (WebMqttConfig.client != null && WebMqttConfig.client.isConnected()) {
            WebMqttConfig.disconnect();
        }
        // 处理连接丢失的情况
        SpringBeanUtil.getBean(WebMqttConfig.class).init();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // 在新的线程中处理消息
        executorService.submit(() -> {
            // 处理接收到的消息
//            log.info("外网MQTT接收到的主题" + topic);
            try {
                String content = new String(message.getPayload());
                if (this.checkOldMsg(topic)) {
                    String deviceCode = this.getDevSn(topic);
                    //校验设备状态 平台未注册时不允许接收消息
                    if (checkDeviceStatus(deviceCode)) {
                        return;
                    }
                    IEGTopicVo iegTopicVo = JSONObject.parseObject(content, IEGTopicVo.class);
                    //解析旧主题
                    protocolRecOldMain(deviceCode, iegTopicVo);
                }
                if (this.checkNewMsg(topic)) {
                    String deviceCode = this.getNewDevSn(topic);
                    //校验设备状态 平台未注册时不允许接收消息
                    if (checkDeviceStatus(deviceCode)) {
                        return;
                    }
                    content = JSON.parseObject(content).getString("body");
                    //解析新主题
                    protocolRecNewMain(topic, deviceCode, content);
                }
            } catch (Exception e) {
                log.error("外网协议服务解析MQTT数据报错", e);
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 处理消息发送完成的情况
//        log.info("处理消息完成" + token.isComplete());
    }

    private static void protocolRecOldMain(String terminalCode, IEGTopicVo iegTopicVo) {
        try {
//            log.info("外网MQTT接收到终端编号{},数据上报消息{}", terminalCode, iegTopicVo.getMid());
            if (StringUtil.isNotEmpty(iegTopicVo.getType())) {
                switch (iegTopicVo.getType()) {
                    case IEGConstant.Type.CMD_REPORT_DATA: //数据上报
                        //转换data数据
                        List<DataSubscribeVo.DevicesVo> devices = JSONObject.parseObject(JSON.toJSONString(iegTopicVo.getParam()),
                                DataSubscribeVo.class).getDevices();
                        if (devices != null && !devices.isEmpty()) {
                            Map<String, String> serviceIds = new HashMap<>();//包含的服务id
                            Map<String, PileStateSubscribeVo> pileStateMap = new HashMap<>();//电桩状态列表 电桩id->电桩状态数据
                            Map<String, PileFaultSubscribeVo> pileFaultMap = new HashMap<>();//电桩故障列表 电桩id->电桩故障数据
                            Map<String, PileDataSubscribeVo> pileDataMap = new HashMap<>();//电桩数据列表 电桩id->电桩数据
                            Map<String, PileLogSubscribeVo> pileLogMap = new HashMap<>();//电桩数据列表 电桩id->电桩数据
                            AtomicReference<ZFPointsSubscribeVo> iegMqtt = new AtomicReference<>(new ZFPointsSubscribeVo());//转发遥信
                            AtomicReference<StationInfoSubscribeVo> stationInfo = new AtomicReference<>(new StationInfoSubscribeVo());//征战信息
                            devices.forEach(d -> {
                                List<DataSubscribeVo.ServiceVo> serviceList = d.getServices();
                                if (serviceList != null && !serviceList.isEmpty()) {
                                    //获取所包含的serviceId
                                    serviceList.forEach(serviceVo -> serviceIds.put(serviceVo.getServiceId(), serviceVo.getServiceId()));

                                    //获取电桩状态
                                    Optional<DataSubscribeVo.ServiceVo> pileStateOptional = serviceList.stream().filter(s ->
                                            s.getServiceId().equals(IEGConstant.Param.PILE_STATE)).findFirst();
                                    pileStateOptional.ifPresent(serviceVo ->
                                            pileStateMap.put(d.getDeviceId(), JSON.parseObject(JSON.toJSONString(serviceVo.getData()), PileStateSubscribeVo.class)));

                                    //获取电桩故障
                                    Optional<DataSubscribeVo.ServiceVo> pileFaultOptional = serviceList.stream().filter(s ->
                                            s.getServiceId().equals(IEGConstant.Param.PILE_FAULT)).findFirst();
                                    pileFaultOptional.ifPresent(serviceVo -> pileFaultMap.put(d.getDeviceId(), JSON.parseObject(JSON.toJSONString(serviceVo.getData()), PileFaultSubscribeVo.class)));

                                    //获取电桩数据
                                    Optional<DataSubscribeVo.ServiceVo> pileDataOptional = serviceList.stream().filter(s ->
                                            s.getServiceId().equals(IEGConstant.Param.PILE_DATA)).findFirst();
                                    pileDataOptional.ifPresent(serviceVo -> pileDataMap.put(d.getDeviceId(), JSON.parseObject(JSON.toJSONString(serviceVo.getData()), PileDataSubscribeVo.class)));

                                    //获取电桩日志上报数据
                                    Optional<DataSubscribeVo.ServiceVo> pileLogOption = serviceList.stream().filter(s ->
                                            Objects.equals(s.getServiceId(), IEGConstant.Param.PILE_LOG)).findFirst();
                                    pileLogOption.ifPresent(serviceVo -> pileLogMap.put(d.getDeviceId(), JSON.parseObject(JSON.toJSONString(serviceVo.getData()), PileLogSubscribeVo.class)));

                                    //获取转发数据
                                    Optional<DataSubscribeVo.ServiceVo> iegMqttOption = serviceList.stream().filter(s ->
                                            Objects.equals(s.getServiceId(), IEGConstant.Param.IEG_MQTT_ZF)).findFirst();
                                    iegMqttOption.ifPresent(serviceVo -> iegMqtt.set(JSON.parseObject(JSON.toJSONString(serviceVo.getData()), ZFPointsSubscribeVo.class)));

                                    //获取整站信息
                                    Optional<DataSubscribeVo.ServiceVo> stationInfoOption = serviceList.stream().filter(s ->
                                            Objects.equals(s.getServiceId(), IEGConstant.Param.GATEWAY_STATION_INFO)).findFirst();
                                    stationInfoOption.ifPresent(serviceVo -> stationInfo.set(JSON.parseObject(JSON.toJSONString(serviceVo.getData()), StationInfoSubscribeVo.class)));

                                }
                            });
                            if (!pileStateMap.isEmpty()) { //更新电桩状态
                                WebMqttDataHandler.updateStatus(terminalCode, pileStateMap);
                                SmWebControlHandler.pushPileStatus(pileStateMap);
                                return;
                            }

                            if (!pileFaultMap.isEmpty()) { //电桩故障(2001)
                                WebMqttDataHandler.parseFault(pileFaultMap);
                                return;
                            }

                            if (!pileDataMap.isEmpty()) { //电桩数据(2002)
                                WebMqttDataHandler.parseData(pileDataMap);
                                return;
                            }
                            if (!pileLogMap.isEmpty()) { //电桩日志上报(6006)
                                WebMqttDataHandler.parsePileLog(pileLogMap);
                                return;
                            }
                            if (serviceIds.containsKey(IEGConstant.Param.IEG_MQTT_ZF)) {
                                WebMqttDataHandler.parseIegYcYx(terminalCode, iegMqtt.get());
                            }
                            if (serviceIds.containsKey(IEGConstant.Param.GATEWAY_STATION_INFO)) {
                                WebMqttDataHandler.gatewayStationInfo(terminalCode, stationInfo.get());
                                return;
                            }
                        }
                        break;
                    case IEGConstant.Type.CMD_SERVICE: //电桩服务
                        //转换response数据
                        ResponseSubscribeVo responseData = JSONObject.parseObject(JSON.toJSONString(iegTopicVo.getParam()),
                                ResponseSubscribeVo.class);
                        if (responseData != null) {
                            String params = JSON.toJSONString(responseData.getParas());
                            switch (responseData.getCmd()) {
                                case IEGConstant.Param.PILE_RATE_SET_RESPONSE: //费率下发响应(1004)
                                    WebMqttDataHandler.rateSetRes(JSON.parseObject(params, PileRateSetResponseSubscribeVo.class));
                                    break;
                                case IEGConstant.Param.PILE_START_RESPONSE: //启动响应(4001)
                                    PileStartResponseSubscribeVo pileStartResponseSubscribeVo = JSON.parseObject(params, PileStartResponseSubscribeVo.class);
                                    WebMqttDataHandler.startCmdRes(pileStartResponseSubscribeVo);
                                    SmWebControlHandler.startCmdRes(pileStartResponseSubscribeVo);
                                    break;
                                case IEGConstant.Param.PILE_START_EVENT: //启动事件(4002)
                                    PileStartEventSubscribeVo pileStartEventSubscribeVo = JSON.parseObject(params, PileStartEventSubscribeVo.class);
                                    WebMqttDataHandler.startEvent(pileStartEventSubscribeVo);
                                    SmWebControlHandler.pushStartEvent(pileStartEventSubscribeVo);
                                    break;
                                case IEGConstant.Param.PILE_STOP_RESPONSE: //停止响应(4004)
                                    PileStopResponseSubscribeVo pileStopResponseSubscribeVo = JSON.parseObject(params, PileStopResponseSubscribeVo.class);
                                    WebMqttDataHandler.stopCmdRes(pileStopResponseSubscribeVo);
                                    SmWebControlHandler.stopCmdRes(pileStopResponseSubscribeVo);
                                    break;
                                case IEGConstant.Param.PILE_STOP_EVENT: //停止事件(4005)
                                    PileStopEventSubscribeVo pileStopEventSubscribeVo = JSON.parseObject(params, PileStopEventSubscribeVo.class);
                                    WebMqttDataHandler.stopEvent(pileStopEventSubscribeVo);
                                    SmWebControlHandler.pushStopEvent(pileStopEventSubscribeVo);
                                    break;
                                case IEGConstant.Param.PILE_BMS_INFO_RESPONSE: //业务命令响应-bms信息(4008)
                                    WebMqttDataHandler.saveBmsInfo(JSON.parseObject(params, PileBmsInfoSubscribeVo.class));
                                    break;
                                case IEGConstant.Param.PILE_CTRL_RESPONSE: //业务命令响应-功率控制(4010)
                                    PileCtrlResponseSubscribeVo pileCtrlResponseSubscribeVo = JSON.parseObject(params, PileCtrlResponseSubscribeVo.class);
                                    WebMqttDataHandler.powerCtrlRes(pileCtrlResponseSubscribeVo);
                                    SmWebControlHandler.powerCtrlRes(pileCtrlResponseSubscribeVo);
                                    break;
                                case IEGConstant.Param.PILE_RECORD_REQUEST://记录上报(4011)
                                    PileRecordSubscribeVo pileRecordSubscribeVo = JSON.parseObject(params, PileRecordSubscribeVo.class);
                                    SmWebControlHandler.pushPileRecord(pileRecordSubscribeVo);
                                    WebMqttDataHandler.recordRes(terminalCode, pileRecordSubscribeVo);
                                    break;
                                case IEGConstant.Param.LOAD_PARAMSET_RESPONSE://网关参数设置响应
                                    WebMqttDataHandler.gwLoadParamSetResponse(terminalCode, JSON.parseObject(params, GwLoadParamSetResponseSubscribeVo.class));
                                    break;
                                case IEGConstant.Param.GENERAL_PARAM_RESPONSE://网关通用参数下发响应
                                    WebMqttDataHandler.gwGeneralParamResponse(terminalCode, JSON.parseObject(params, GwGeneralParamResponseSubscribeVo.class));
                                    break;
                                case IEGConstant.Param.GATEWAY_PEAK_VALLEY_CONTROL_SET_RESPONSE://调控需求下发响应
                                    WebMqttDataHandler.gatewayPeakValleyControlSetResponse(terminalCode, JSON.parseObject(params, GwPVControlSetResponseSubscribeVo.class));
                                    break;
                                case IEGConstant.Param.GATEWAY_PEAK_VALLEY_CONTROL_STOP_RESPONSE://调控需求终止下发响应
                                    WebMqttDataHandler.gatewayPeakValleyControlStopResponse(terminalCode, JSON.parseObject(params, GwPVControlStopResponseSubscribeVo.class));
                                    break;
                                case IEGConstant.Param.PILE_RESET_RESPONSE://电桩复位响应(6011)
                                    WebMqttDataHandler.pileResetResponse(JSON.parseObject(params, PileResetResponseSubscribeVo.class));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case IEGConstant.Type.EVENT_LINK_UP: //上线请求(1900) 设备信息
                        WebMqttDataHandler.iegLinkUp(JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), EventLinkUpSubscribeVo.class));
                        break;
                    case IEGConstant.Type.EVENT_LINK_DOWN: //下线请求()
                        WebMqttDataHandler.iegLinkDown(JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), EventLinkDownSubscribeVo.class));
                        break;
                    case IEGConstant.Type.EVENT_RATE_REQ://费率请求(1000)
                        WebMqttDataHandler.rateReq(terminalCode, JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), EventRateReqSubscribeVo.class));
                        break;
                    case IEGConstant.Type.CMD_TO_PO_UPDATE: //更新子设备(1100)
                        WebMqttDataHandler.pilesLinkUp(terminalCode, JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), CmdToPoUpdateSubscribeVo.class));
                        break;
                    case IEGConstant.Type.CMD_TO_PO_CALL_STATE: //子设备状态响应(120000)
//                        smMqttMsgDto.setMsgObj(JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), CmdToPoUpdateSubscribeVo.class));
                        break;
                    case IEGConstant.Type.CMD_REQUEST_FW_DATA: //固件数据块请求（8002）
                        WebMqttDataHandler.firmwareDataRes(terminalCode, JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), FwReqSubscribeVo.class));
                        break;
                    case IEGConstant.Type.EVENT_DEVICE_UPDATE: //网关接收完成上报（8005）
                        WebMqttDataHandler.firmwareIegReceiveOver(terminalCode, JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), FwGwOverSubscribeVo.class));
                        break;
                    case IEGConstant.Type.EVENT_PLIES_UPDATE: //电桩接收完成上报（8004）
                        WebMqttDataHandler.firmwarePileReceiveOver(JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), FwPileOverSubscribeVo.class));
                        break;
                    case IEGConstant.Type.EVENT_AUTHENTICATION_REQUEST: //鉴权请求(3000)
                        WebMqttDataHandler.authenticationRes(terminalCode, JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), EventAuthRequestSubscribeVo.class));
                        break;
                    case IEGConstant.Type.EVENT_STRATEGY_SETTING: //策略设置(3002)
                        WebMqttDataHandler.strategyRes(terminalCode, JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), EventStrategySettingSubscribeVo.class));
                        break;
                    case IEGConstant.Type.CMD_TOPO_FW_RES: //控制板信息响应
                        WebMqttDataHandler.ctrlBoardInfoRes(JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), CmdToPoFwResSubscribeVo.class));
                        break;
                    case IEGConstant.Type.EVENT_GATEWAY_LOG: //网关日志上报
                        //转换response数据
                        ResponseSubscribeVo logData = JSONObject.parseObject(JSON.toJSONString(iegTopicVo.getParam()), ResponseSubscribeVo.class);
                        if (logData != null) {
                            if (logData.getCmd().equals(IEGConstant.Param.POWER_CTRL)) { //功率控制日志
                                WebMqttDataHandler.updateGateWayCtrlLog(terminalCode, JSON.parseObject(JSON.toJSONString(logData.getParas()), GatewayLogSubscribeVo.class));
                            }
                        }
                        break;
                    case IEGConstant.Type.EVENT_OFFLINE_RECORD: //离线记录上报
                        WebMqttDataHandler.offLineRecordRes(terminalCode, JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), PileOffLineRecordSubscribeVo.class));
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            log.error("外网MQTT老主题数据处理报错", e);
        }
    }

    public void protocolRecNewMain(String topic, String terminalCode, String content) {
        try {
            topic = topic.replace(WebTopicConstant.GATEWAY_RESPONSE_VERSION + FileUtil.SLASH + terminalCode, FileUtil.separator);
            switch (topic) {
                case WebTopicConstant.GATEWAY_STATUS_RESPONSE: //网关状态查询响应
                    WebMqttDataHandler.gatewayStatus(terminalCode, JSON.parseObject(content, GatewayStatusSubscribeVo.class));
                    break;
                case WebTopicConstant.SERVICE_LIST_RESPONSE: //服务列表查询响应
                    WebMqttDataHandler.serviceList(terminalCode, JSON.parseArray(content, ServiceListSubscribeVo.class));
                    break;
                case WebTopicConstant.SYNC_CLOCK_RESPONSE: //同步时钟响应
                    WebMqttDataHandler.syncClock(terminalCode, JSON.parseObject(content, SyncClockSubscribeVo.class));
                    break;
                case WebTopicConstant.REBOOT_RESPONSE: //网关重启响应
                    WebMqttDataHandler.reboot(terminalCode, JSON.parseObject(content, RebootSubscribeVo.class));
                    break;
                case WebTopicConstant.LICENSE_STATUS_RESPONSE: //许可状态查询响应
                    WebMqttDataHandler.licenseStatus(terminalCode, JSON.parseObject(content, LicenseSubscribeVo.class));
                    break;
                case WebTopicConstant.LICENSE_KET_RESPONSE: //获取设备key响应
                    WebMqttDataHandler.licenseKey(terminalCode, JSON.parseObject(content, LicenseSubscribeVo.class));
                    break;
                case WebTopicConstant.LICENSE_RESPONSE: //下发许可响应
                    WebMqttDataHandler.license(terminalCode, JSON.parseObject(content, LicenseSubscribeVo.class));
                    break;
            }
        } catch (Exception e) {
            log.error("外网MQTT新主题数据处理报错", e);
        }
    }

    /**
     * 旧主题校验
     *
     * @param topic 主题名
     * @return 状态码
     */
    public boolean checkOldMsg(String topic) {
        String[] strings = topic.split(FileUtil.SLASH);
        if (WebTopicConstant.TOPIC_VERSION.equals(strings[1])) {
            String terminalCode = strings[2];//获取设备编码
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
            if (gatewayRealModel != null) {
                gatewayRealModel.setLastBeatTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
                return true;
            }
        }
        return false;
    }

    /**
     * 新主题校验
     *
     * @param topic 主题名
     * @return 状态码
     */
    private boolean checkNewMsg(String topic) {
        String[] strings = topic.split(FileUtil.SLASH);
        if (WebTopicConstant.GATEWAY_RESPONSE_VERSION.equals(strings[0])) {
            String terminalCode = strings[2];//获取设备编码
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
            if (gatewayRealModel != null) {
                gatewayRealModel.setLastBeatTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
                return true;
            }
        }
        return false;
    }

    //根据主题获取桩编码
    private String getDevSn(String topic) {
        String[] strings = topic.split(FileUtil.SLASH);
        return strings[2];//获取设备编码
    }

    //根据主题获取桩编码
    private String getNewDevSn(String topic) {
        String[] strings = topic.split(FileUtil.SLASH);
        return strings[1];//获取设备编码
    }

    //校验设备状态
    private static boolean checkDeviceStatus(String deviceId) {
        DeviceModel deviceModel = RedisDeviceUtil.getDevice(deviceId);
        if (deviceModel == null || StringUtil.isEmpty(deviceModel.getTxStatus()) || deviceModel.getTxStatus() == 0) {
            RedisGeneralUtil.executePile(deviceId, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(deviceId);
                List<Integer> deviceStatus = Arrays.asList(-1, 0);
                if (gatewayRealModel != null && StringUtil.isNotEmpty(gatewayRealModel.getDeviceStatus()) && !deviceStatus.contains(gatewayRealModel.getDeviceStatus())) {
                    gatewayRealModel.setDeviceStatus(0);
                    gatewayRealModel.getSubDeviceRealMap().forEach((pileCode, workStatus) -> {
                        gatewayRealModel.getSubDeviceRealMap().put(pileCode, 0);
                        checkSubDeviceStatus(pileCode);
                    });
                    RedisGeneralUtil.setGatewayRealModel(deviceId, gatewayRealModel);
                }
            });
            return true;
        }
        return false;
    }

    //校验设备状态
    private static void checkSubDeviceStatus(String deviceId) {
        DeviceModel deviceModel = RedisDeviceUtil.getDevice(deviceId);
        if (deviceModel == null || StringUtil.isEmpty(deviceModel.getTxStatus()) || deviceModel.getTxStatus() == 0) {
            RedisGeneralUtil.executePile(deviceId, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(deviceId);
                List<Integer> workStatus = Arrays.asList(-1, 0);
                if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && !workStatus.contains(pileRealModel.getWorkStatus())) {
                    pileRealModel.setWorkStatus(0); //默认给未注册
                    pileRealModel.setOriginalStatus(null);
                    if (MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                        pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> {
                            gunRealModel.setGunStatus(-1); //默认给未知
                            gunRealModel.setGunOriginalStatus(null);
                            pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        });
                        pileRealModel.setGunRealModelMap(pileRealModel.getGunRealModelMap());
                    }
                    RedisGeneralUtil.setPileRealModel(deviceId, pileRealModel);
                }
            });
        }
    }

}
