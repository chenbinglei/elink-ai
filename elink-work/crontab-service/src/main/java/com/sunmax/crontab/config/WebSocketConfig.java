package com.sunmax.crontab.config;


import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dao.SystemVariableDao;
import com.sunmax.crontab.dao.VariableNodeDao;
import com.sunmax.crontab.service.ComputeNodeService;
import com.sunmax.crontab.service.ConfigFuncPointService;
import com.sunmax.crontab.service.feign.ConfigService;
import com.sunmax.crontab.service.feign.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig /*implements ServletContextInitializer*/ {

    public static DeviceService deviceService;

    public static ConfigService configService;

    public static SystemVariableDao systemVariableDao;

    public static ConfigFuncPointService configFuncPointService;

    public static VariableNodeDao variableNodeDao;

    public static ComputeNodeService computeNodeService;

    public static ComputeNodeDao computeNodeDao;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 在此处修改WebSocket传输的限制，如不修改，无法传输长字符串
     * @param servletContext
     */
//    @Override
//    public void onStartup(ServletContext servletContext) {
//        servletContext.addListener(WebAppRootListener.class);
//        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize", "53687091200");
//        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","53687091200");
//    }

    /**
     * 因 SpringBoot WebSocket 对每个客户端连接都会创建一个 WebSocketServer（@ServerEndpoint 注解对应的） 对象，Bean 注入操作会被直接略过，因而手动注入一个全局变量
     *
     */
    @Autowired
    public void set(DeviceService deviceService, ConfigService configService, SystemVariableDao systemVariableDao, ConfigFuncPointService configFuncPointService,
                    VariableNodeDao variableNodeDao, ComputeNodeService computeNodeService, ComputeNodeDao computeNodeDao){
        WebSocketConfig.deviceService = deviceService;
        WebSocketConfig.configService = configService;
        WebSocketConfig.systemVariableDao = systemVariableDao;
        WebSocketConfig.configFuncPointService = configFuncPointService;
        WebSocketConfig.variableNodeDao = variableNodeDao;
        WebSocketConfig.computeNodeService = computeNodeService;
        WebSocketConfig.computeNodeDao = computeNodeDao;
    }

}

