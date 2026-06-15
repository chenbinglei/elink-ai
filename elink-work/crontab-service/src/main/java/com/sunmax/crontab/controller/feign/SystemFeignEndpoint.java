package com.sunmax.crontab.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.system.MqttClientVo;
import com.sunmax.crontab.service.SystemFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;
import com.sunmax.common.feign.system.SystemCrontabFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/system")
@Tag(name = "提供给运营服务调用的远程接口")
@Hidden()
public class SystemFeignEndpoint implements SystemCrontabFeignClient {

    @Autowired
    private SystemFeignService systemFeignService;

    @PostMapping("createMqttClient")
    @Operation(summary = "创建mqtt客户端数据")
    
    @Override
    public ResponseResult<Void> createMqttClient(@RequestBody MqttClientVo mqttClientVo) {
        return systemFeignService.createMqttClient(mqttClientVo);
    }

    @PostMapping("deleteMqttClient")
    @Operation(summary = "删除mqtt客户端数据")
    
    @Override
    public ResponseResult<Void> deleteMqttClient(@RequestParam String clientId) {
        return systemFeignService.deleteMqttClient(clientId);
    }
}
