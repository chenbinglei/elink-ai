package com.sunmax.system.service.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.system.MqttClientVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "crontab-service", path = "/crontab/feign/system")
public interface CrontabService {

    @PostMapping("createMqttClient")
    @Operation(summary = "创建mqtt客户端数据")
    
    ResponseResult<Void> createMqttClient(@RequestBody MqttClientVo mqttClientVo);

    @PostMapping("deleteMqttClient")
    @Operation(summary = "删除mqtt客户端数据")
    
    ResponseResult<Void> deleteMqttClient(@RequestParam String clientId);

}
