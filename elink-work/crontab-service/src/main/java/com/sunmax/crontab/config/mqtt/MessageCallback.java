package com.sunmax.crontab.config.mqtt;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.vo.system.MqttClientVo;
import com.sunmax.crontab.dao.MqttClientLogDao;
import com.sunmax.crontab.entity.MqttClientLogEntity;
import com.sunmax.crontab.vo.mqtt.HDSetVo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MessageCallback implements MqttCallback {

    private final MqttClientVo mqttClientVo;

    private MqttClientLogDao mqttClientLogDao;

    // 创建线程池
    private final ExecutorService executorService = new ThreadPoolExecutor(36, 48, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    public MessageCallback(MqttClientVo mqttClient, MqttClientLogDao mqttClientDao) {
        mqttClientVo = mqttClient;
        mqttClientLogDao = mqttClientDao;
    }

    // MqttCallback methods
    @Override
    public void connectionLost(Throwable cause) {
        log.error("mqtt连接失败, 错误信息: " + cause.getMessage());
        try {
            if (MqttClientManager.clients.containsKey(mqttClientVo.getClientId())) {
                MqttClientManager.disconnect(mqttClientVo.getClientId());
            }
            MqttClientManager.addClient(mqttClientVo);
        } catch (Exception e) {
            log.error("mqtt连接断开, 释放资源失败, 错误信息: " + e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        executorService.submit(() -> {
            String data = new String(message.getPayload());
            log.info("订阅到主题: " + topic + " 的消息: " + data);
            if (mqttClientLogDao == null) {
                mqttClientLogDao = SpringBeanUtil.getBean(MqttClientLogDao.class);
            }
            mqttClientLogDao.save(MqttClientLogEntity.builder().clientId(mqttClientVo.getClientId()).topic(topic).message(data).type(2).build());
            //华电协议标识
            if (Objects.equals(ProtocolEnum.HD.getCode(), mqttClientVo.getProtocolCode())) {
                //即时指令下发处理
                if (topic.contains("/service/set")) {
                    HDSetVo hdTopicVo = JSON.parseObject(data, HDSetVo.class);
                    if (hdTopicVo != null) {
                        log.info("收到华电计划指令下发的数据:{}", hdTopicVo);
                        //华电协议指令接收响应
                        MqttClientDataHandler.hdSetReply(mqttClientVo, hdTopicVo);
                        //华电协议指令返回响应
                        MqttClientDataHandler.hdReply(mqttClientVo, hdTopicVo);
                    }
                }
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Called when a message has been delivered to the server.
        // You can add your own logic here if needed.
//        log.info("处理消息完成" + token.isComplete());
    }

}
