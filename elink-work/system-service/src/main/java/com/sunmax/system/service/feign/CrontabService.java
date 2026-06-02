package com.sunmax.system.service.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.system.MqttClientVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "scrontab-service")
@RestController
@RequestMapping("/scrontab/feign/system")
public interface CrontabService {

    @PostMapping("createMqttClient")
    @ApiOperation("创建mqtt客户端数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<Void> createMqttClient(@RequestBody MqttClientVo mqttClientVo);

    @PostMapping("deleteMqttClient")
    @ApiOperation("删除mqtt客户端数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Void> deleteMqttClient(@RequestParam String clientId);

}
