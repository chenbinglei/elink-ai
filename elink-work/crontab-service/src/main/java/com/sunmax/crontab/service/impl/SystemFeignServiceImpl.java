package com.sunmax.crontab.service.impl;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.system.MqttClientVo;
import com.sunmax.crontab.config.mqtt.MqttClientManager;
import com.sunmax.crontab.service.SystemFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SystemFeignServiceImpl implements SystemFeignService {

    @Override
    public ResponseResult<Void> createMqttClient(MqttClientVo mqttClientVo) {
        try {
            //创建mqtt客户端连接
            MqttClientManager.addClient(mqttClientVo);
            return ResponseResult.ok();
        } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
            log.error("创建客户端连接失败", e);
            return ResponseResult.paramError("创建客户端连接失败");
        } catch (RuntimeException e) {
            log.error("创建客户端连接失败", e);
            return ResponseResult.paramError("创建客户端连接失败");
        }
    }

    @Override
    public ResponseResult<Void> deleteMqttClient(String clientId) {
        try {
            if (MqttClientManager.clients.containsKey(clientId)) {
                MqttClientManager.disconnect(clientId);
            }
            return ResponseResult.ok();
        } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
            log.error("删除客户端连接失败", e);
            return ResponseResult.paramError("删除客户端连接失败");
        } catch (RuntimeException e) {
            log.error("删除客户端连接失败");
            return ResponseResult.paramError("删除客户端连接失败");
        }
    }
}
