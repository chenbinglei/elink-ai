package com.sunmax.crontab.config.mqtt;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.system.MqttClientVo;
import com.sunmax.crontab.dao.MqttClientLogDao;
import com.sunmax.crontab.entity.MqttClientLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
public class MqttClientManager {

    public static final Map<String, MqttClient> clients = new ConcurrentHashMap<>();

    private static MqttClientLogDao mqttClientLogDao;

    public static void addClient(MqttClientVo mqttClient) throws MqttException {

        if (mqttClientLogDao == null) {
            mqttClientLogDao = SpringBeanUtil.getBean(MqttClientLogDao.class);
        }

        if (clients.containsKey(mqttClient.getClientId())) {
            return;
        }
        MqttClient client = new MqttClient(mqttClient.getAddress(), mqttClient.getClientId(), new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        if (mqttClient.getUsername() != null && mqttClient.getPassword() != null) {
            connOpts.setUserName(mqttClient.getUsername());
            connOpts.setPassword(mqttClient.getPassword().toCharArray());
        }
        // 保留会话
        connOpts.setCleanSession(true);
        // 设置超时时间，单位秒
        connOpts.setConnectionTimeout(10);
        // 设置心跳时间，单位秒，表示服务器每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
        connOpts.setKeepAliveInterval(60);
        // 设置回调
        client.setCallback(new MessageCallback(mqttClient, mqttClientLogDao));
        //自动重连
        connOpts.setAutomaticReconnect(true);
        client.connect(connOpts);
        clients.put(mqttClient.getClientId(), client);
        //订阅Mqtt主题
        List<String> topics = Lists.newArrayList();
        String topicPrefix = FileUtil.SLASH + mqttClient.getVendor() + FileUtil.SLASH + mqttClient.getGwSn() + "/service/";
        topics.add(topicPrefix + "set");
        topics.add(topicPrefix + "timing");
        MqttClientManager.subscribe(mqttClient.getClientId(), topics);
        log.info("客户端 " + mqttClient.getClientId() + " 连接到 " + mqttClient.getAddress());
    }

    public static void subscribe(String clientId, List<String> topics) throws MqttException {
        MqttClient client = clients.get(clientId);
        if (client != null && client.isConnected()) {
            int[] qos = new int[topics.size()];
            Arrays.fill(qos, 0);
            client.subscribe(topics.toArray(new String[]{}), qos);
            log.info("客户端 " + clientId + " 订阅到 " + topics + " 且 qos " + Arrays.toString(qos));
        } /*else {
            //断开连接
            disconnect(clientId);
        }*/
    }

    public static void publish(String clientId, String topic, Object payload) {
        try {
            MqttClient client = clients.get(clientId);
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage(JSON.toJSONString(payload).getBytes());
                message.setQos(0);
                client.publish(topic, message);
//                log.info("客户端 " + clientId + " 发布到 " + topic + ": " + payload);
            }/* else {
            //断开连接
            disconnect(clientId);
            }*/
        } catch (MqttException me) {
            log.error("客户端 " + clientId + " 发布到 " + topic + " 失败: " + payload, me);
        }
    }

    public static void publish(String clientId, String topic, Object payload, MqttClientLogEntity mqttClientLog) {
        try {
            MqttClient client = clients.get(clientId);
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage(JSON.toJSONString(payload).getBytes());
                message.setQos(0);
                client.publish(topic, message);
//                log.info("客户端 " + clientId + " 发布到 " + topic + ": " + payload);
                mqttClientLogDao.save(mqttClientLog);
            }/* else {
            //断开连接
            disconnect(clientId);
            }*/
        } catch (MqttException me) {
            log.error("客户端 " + clientId + " 发布到 " + topic + " 失败: " + payload, me);
        }
    }

    public static void disconnect(String clientId) throws MqttException {
        MqttClient client = clients.remove(clientId);
        if (client != null && client.isConnected()) {
            client.disconnect();
            log.info("客户端 " + clientId + " 关闭连接");
        }
    }

}