package com.sunmax.protocol.config.emqx.web;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;


/**
 * 配置文件的操作
 * */
@Getter
@Configuration
@IntegrationComponentScan
public class WebMqttProperties {

    @Value("${mqtt.web.username}")
    private String username;

    @Value("${mqtt.web.password}")
    private String password;

    @Value("${mqtt.web.host-url}")
    private String hostUrl;

    @Value("${mqtt.web.client-id}")
    private String clientId;

    @Value("${mqtt.web.client-topic}")
    private String clientTopic;

    @Value("${mqtt.timeout}")
    private Integer timeout;

    @Value("${mqtt.keep-alive}")
    private Integer keepAlive;

    @Value("${mqtt.qos}")
    private Integer qos;

    @Value("${mqtt.expire}")
    private Long expire;

}
