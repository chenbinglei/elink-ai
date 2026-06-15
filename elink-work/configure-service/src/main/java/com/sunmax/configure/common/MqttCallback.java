package com.sunmax.configure.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.constant.IEGConstant;
import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.protocol.mqtt.web.common.DataSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.IEGTopicVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.ResponseSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.data.PileStateSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.response.PileOffLineRecordSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.response.PileRecordSubscribeVo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MqttCallback implements org.eclipse.paho.client.mqttv3.MqttCallback {

    // 创建线程池
    private final ExecutorService executorService = new ThreadPoolExecutor(36,48,0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void connectionLost(Throwable cause) {
        log.info("外网MQTT连接断开，可以做重连");
        if (MqttConfig.client != null && MqttConfig.client.isConnected()) {
            MqttConfig.disconnect();
        }
        // 处理连接丢失的情况
        SpringBeanUtil.getBean(MqttConfig.class).init();
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
                    IEGTopicVo iegTopicVo = JSONObject.parseObject(content, IEGTopicVo.class);
                    //解析旧主题
                    protocolRecOldMain(iegTopicVo);
                }
            } catch (RuntimeException e) {
                log.error("外网协议服务解析MQTT数据报错", e);
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 处理消息发送完成的情况
//        log.info("处理消息完成" + token.isComplete());
    }

    private static void protocolRecOldMain(IEGTopicVo iegTopicVo) {
        try {
//            log.info("配置服务MQTT接收到的数据" + iegTopicVo.getMid());
            if (StringUtil.isNotEmpty(iegTopicVo.getType())) {
                switch (iegTopicVo.getType()) {
                    case IEGConstant.Type.CMD_REPORT_DATA: //数据上报
                        //转换data数据
                        List<DataSubscribeVo.DevicesVo> devices = JSONObject.parseObject(JSON.toJSONString(iegTopicVo.getParam()),
                                DataSubscribeVo.class).getDevices();
                        if (devices != null && !devices.isEmpty()) {
                            Map<String, PileStateSubscribeVo> pileStateMap = new HashMap<>();//电桩状态列表 电桩id->电桩状态数据
                            devices.forEach(d -> {
                                List<DataSubscribeVo.ServiceVo> serviceList = d.getServices();
                                if (serviceList != null && !serviceList.isEmpty()) {
                                    //获取电桩状态
                                    Optional<DataSubscribeVo.ServiceVo> pileStateOptional = serviceList.stream().filter(s ->
                                            s.getServiceId().equals(IEGConstant.Param.PILE_STATE)).findFirst();
                                    pileStateOptional.ifPresent(serviceVo ->
                                            pileStateMap.put(d.getDeviceId(), JSON.parseObject(JSON.toJSONString(serviceVo.getData()), PileStateSubscribeVo.class)));

                                }
                            });
                            if (!pileStateMap.isEmpty()) { //更新电桩状态
                                MqttDataHandler.updateStatus(pileStateMap);
                                return;
                            }
                        }
                        break;
                    case IEGConstant.Type.CMD_SERVICE: //电桩服务
                        //转换response数据
                        ResponseSubscribeVo responseData = JSONObject.parseObject(JSON.toJSONString(iegTopicVo.getParam()), ResponseSubscribeVo.class);
                        if (responseData != null) {
                            String params = JSON.toJSONString(responseData.getParas());
                            if (responseData.getCmd().equals(IEGConstant.Param.PILE_RECORD_REQUEST)) {//记录上报(4011)
                                MqttDataHandler.recordRes(JSON.parseObject(params, PileRecordSubscribeVo.class));
                            }
                        }
                        break;
                    case IEGConstant.Type.EVENT_OFFLINE_RECORD: //离线记录上报
                        MqttDataHandler.offLineRecordRes(JSON.parseObject(JSON.toJSONString(iegTopicVo.getParam()), PileOffLineRecordSubscribeVo.class));
                        break;
                    default:
                        break;
                }
            }

        } catch (RuntimeException e) {
            log.error("通信MQTT老主题数据处理报错", e);
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
            return gatewayRealModel != null;
        }
        return false;
    }

}
