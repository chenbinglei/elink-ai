package com.sunmax.protocol.config.emqx.inter;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Sets;
import com.sunmax.common.constant.InterTopicConstant;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.PlatformLogoVo;
import com.sunmax.common.vo.protocol.mqtt.inter.CMDTopicVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Configuration
@Slf4j
public class InterMqttConfig {

    private static InterMqttProperties interMqttProperties;

    public static MqttClient client;

    @Bean
    public MqttClient interMqttClient() {
        try {
            interMqttProperties = SpringBeanUtil.getBean(InterMqttProperties.class);
            client = new MqttClient(interMqttProperties.getHostUrl(), interMqttProperties.getClientId() + FileUtil.BAR + UUID.randomUUID(), new MemoryPersistence());
            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(interMqttProperties.getUsername());
            connOpts.setPassword(interMqttProperties.getPassword().toCharArray());
            // 保留会话
            connOpts.setCleanSession(true);
            // 设置超时时间，单位秒
            connOpts.setConnectionTimeout(interMqttProperties.getTimeout());
            // 设置心跳时间，单位秒，表示服务器每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
            connOpts.setKeepAliveInterval(interMqttProperties.getKeepAlive());
            // 设置回调
            client.setCallback(new InterMessageCallback());
            //自动重连
            connOpts.setAutomaticReconnect(true);
            // 建立连接
            client.connect(connOpts);
            log.info("内网MQTT连接状态: {}", client.isConnected());
            this.addAllTopic(Sets.newHashSet(Collections.singletonList(InterTopicConstant.PLATFORM_TOPIC)));
            return client;
        } catch (MqttException me) {
            log.error("客户端连接内网MQTT报错", me);
            return null;
        }
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
                client = interMqttClient();
            }
            client.subscribe(topic, interMqttProperties.getQos());
        } catch (MqttException me) {
            log.error("内网MQTT订阅主题失败: ", me);
        }
    }

    /**
     * 订阅
     * @param topics 主题
     */
    public void addAllTopic(Set<String> topics) {
        try {
            if (client == null || !client.isConnected()) {
                //重新订阅
                client = interMqttClient();
            }
            int[] qos = new int[topics.size()];
            Arrays.fill(qos, 0);
            client.subscribe(topics.toArray(new String[]{}), qos);
        } catch (MqttException me) {
            log.error("内网MQTT批量订阅主题失败", me);
        }
    }

    /**
     * 发送消息到MQTT
     * @param deviceCode 设备编号
     * @param cmd 命令
     * @param data 数据
     */
    public static void sendToMqtt(String deviceCode, String cmd, Object data) {
        CMDTopicVo cmdTopicVo = new CMDTopicVo();
//        cmdTopicVo.setToken();
        cmdTopicVo.setTimestamp(DateUtil.localDateTimeToStr(LocalDateTime.now()));
        CMDTopicVo.Body body = CMDTopicVo.Body.builder()
                .devId(deviceCode)
                .point(null)
                .cmd(cmd)
                .platformId(PlatformLogoVo.SUNMAX_LOGO)
//               .type()
                .body(data).build();
        cmdTopicVo.setBody(body);
        InterMqttConfig.sendToMqtt(deviceCode, cmdTopicVo);
    }

    /**
     * 消息发布
     * @param deviceCode 设备编号
     * @param data  消息
     */
    private static void sendToMqtt(String deviceCode, Object data) {
        try {
            MqttMessage message = new MqttMessage(JSON.toJSONString(data).getBytes());
            message.setQos(interMqttProperties.getQos()); // 消息服务质量等级
//            message.setRetained(true); // 保留消息
            if (client == null || !client.isConnected()) {
                //断开连接
                disconnect();
                //重新连接
                SpringBeanUtil.getBean(InterMqttConfig.class).interMqttClient();
            }
            client.publish(InterTopicConstant.PREFIX_TOPIC, message);
            //保存最后发送数据
//            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(deviceCode);
//            if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getPileCode())) {
//                pileRealModel.setLastSendTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
//                RedisGeneralUtil.setPileRealModel(deviceCode, pileRealModel);
//            }
            //保存消息记录数据
//            webMqttRecordDao.save(WebMqttRecordEntity.builder().topic(topic).deviceCode(terminalCode).content(content).type(0).build());
        } catch (MqttException me) {
            log.error("内网MQTT消息发布失败", me);
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
            log.error("内网MQTT批量取消订阅失败", e);
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
            log.error("内网MQTT批量取消订阅失败", e);
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
            log.error("内网MQTT端口连接失败: ", me);
        }
    }

}
