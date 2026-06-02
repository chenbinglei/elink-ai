package com.sunmax.device.config;

import com.sunmax.device.dao.access.DeviceTaskDao;
import com.sunmax.device.dao.access.DeviceTaskRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    public static DeviceTaskDao deviceTaskDao;

    public static DeviceTaskRecordDao deviceTaskRecordDao;
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 因 SpringBoot WebSocket 对每个客户端连接都会创建一个 WebSocketServer（@ServerEndpoint 注解对应的） 对象，Bean 注入操作会被直接略过，因而手动注入一个全局变量
     *
     */
    @Autowired
    public void set(DeviceTaskDao deviceTaskDao, DeviceTaskRecordDao deviceTaskRecordDao){
        WebSocketConfig.deviceTaskDao = deviceTaskDao;
        WebSocketConfig.deviceTaskRecordDao = deviceTaskRecordDao;
    }

}

