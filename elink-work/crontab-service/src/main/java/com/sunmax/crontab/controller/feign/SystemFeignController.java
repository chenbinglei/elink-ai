package com.sunmax.crontab.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.system.MqttClientVo;
import com.sunmax.crontab.service.SystemFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@CrossOrigin
@RequestMapping("/feign/system")
@Api(tags = "提供给运营服务调用的远程接口")
@ApiIgnore()
public class SystemFeignController {

    @Autowired
    private SystemFeignService systemFeignService;

    @PostMapping("createMqttClient")
    @ApiOperation("创建mqtt客户端数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> createMqttClient(@RequestBody MqttClientVo mqttClientVo) {
        return systemFeignService.createMqttClient(mqttClientVo);
    }

    @PostMapping("deleteMqttClient")
    @ApiOperation("删除mqtt客户端数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> deleteMqttClient(@RequestParam String clientId) {
        return systemFeignService.deleteMqttClient(clientId);
    }
}
