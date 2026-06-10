package com.sunmax.protocol.config.emqx.web;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.protocol.config.emqx.TopicConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Configuration
@Slf4j
public class WebMqttConfig {

    @Resource
    private TopicConfig topicConfig;

    public static MqttClient client;

    public static WebMqttProperties webMqttProperties;

    public void init() {
        try {
            webMqttProperties = SpringBeanUtil.getBean(WebMqttProperties.class);
            client = new MqttClient(webMqttProperties.getHostUrl(), webMqttProperties.getClientId() + FileUtil.BAR + UUID.randomUUID(), new MemoryPersistence());
            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(webMqttProperties.getUsername());
            connOpts.setPassword(webMqttProperties.getPassword().toCharArray());
            // 保留会话
            connOpts.setCleanSession(true);
            // 设置超时时间，单位秒
            connOpts.setConnectionTimeout(webMqttProperties.getTimeout());
            // 设置心跳时间，单位秒，表示服务器每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
            connOpts.setKeepAliveInterval(webMqttProperties.getKeepAlive());
            // 设置回调
            client.setCallback(new WebMessageCallback());
            //自动重连
            connOpts.setAutomaticReconnect(true);
            // 建立连接
            client.connect(connOpts);
            this.addAllTopic(topicConfig.getWebTopicList());
            log.info("外网MQTT连接状态: {}", client.isConnected());
        } catch (MqttException me) {
            log.error("客户端连接外网MQTT报错", me);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void webMqttClient() {
        init();
    }

    /**
     * 订阅
     *
     * @param topic 主题
     */
    public void addTopic(String topic) {
        try {
            if (client == null || !client.isConnected()) {
                //重新订阅
                this.addAllTopic(topicConfig.getWebTopicList());
            }
            client.subscribe(topic, webMqttProperties.getQos());
        } catch (MqttException me) {
            log.error("外网MQTT订阅主题失败: ", me);
        }
    }

    /**
     * 订阅
     * @param topics 主题
     */
    public void addAllTopic(Set<String> topics) {
        try {
            int[] qos = new int[topics.size()];
            Arrays.fill(qos, webMqttProperties.getQos());
            client.subscribe(topics.toArray(new String[]{}), qos);
        } catch (MqttException me) {
            log.error("外网MQTT批量订阅主题失败", me);
        }
    }

    /**
     * 消息发布
     * @param terminalCode 终端编号
     * @param topic 主题
     * @param data  消息
     */
    public static void sendToMqtt(String terminalCode, String topic, Object data) {
        try {
            MqttMessage message = new MqttMessage(JSON.toJSONString(data).getBytes());
            message.setQos(webMqttProperties.getQos()); // 消息服务质量等级
//            message.setRetained(true); // 保留消息
            if (client == null || !client.isConnected()) {
                //断开连接
                disconnect();
                //重新连接
                SpringBeanUtil.getBean(WebMqttConfig.class).webMqttClient();
            }
            client.publish(topic, message);
            //保存最后发送数据
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
            if (gatewayRealModel != null && StringUtil.isNotEmpty(gatewayRealModel.getTerminalCode())) {
                gatewayRealModel.setLastSendTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
            }
            //保存消息记录数据
//            webMqttRecordDao.save(WebMqttRecordEntity.builder().topic(topic).deviceCode(terminalCode).content(content).type(0).build());
        } catch (MqttException me) {
            log.error("外网MQTT消息发布失败", me);
        }
    }

    /**
     * 删除主题
     * @param topic 主题路径
     */
    public void deleteTopic(String topic) {
        try {
            if (StringUtil.isNotEmpty(client) && StringUtil.isNotEmpty(topic)) { //不为空，删除主题
                client.unsubscribe(topic);
            }
        } catch (MqttException e) {
            log.error("外网MQTT批量取消订阅失败", e);
        }
    }

    /**
     * 批量删除主题
     * @param topicList 主题路径
     */
    public void deleteAllTopic(Set<String> topicList) {
        try {
            if (StringUtil.isNotEmpty(client) && CollectionUtils.isNotEmpty(topicList)) { //不为空，删除主题
                client.unsubscribe(topicList.toArray(new String[0]));
            }
        } catch (MqttException e) {
            log.error("外网MQTT批量取消订阅失败", e);
        }
    }

    /**
     * 断开连接
     */
    public static void disconnect() {
        try {
            client.disconnect();
            client.close();
        } catch (MqttException me) {
            log.error("外网MQTT端口连接失败: ", me);
        }
    }



}
