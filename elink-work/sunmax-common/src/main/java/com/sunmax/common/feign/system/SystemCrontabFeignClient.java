package com.sunmax.common.feign.system;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.system.MqttClientVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "crontab-service", path = "/crontab/feign/system", fallbackFactory = GenericFeignFallbackFactory.class)
public interface SystemCrontabFeignClient {

    @PostMapping("createMqttClient")
    @Operation(summary = "创建mqtt客户端数据")
    
    ResponseResult<Void> createMqttClient(@RequestBody MqttClientVo mqttClientVo);

    @PostMapping("deleteMqttClient")
    @Operation(summary = "删除mqtt客户端数据")
    
    ResponseResult<Void> deleteMqttClient(@RequestParam String clientId);

}
