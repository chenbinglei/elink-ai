package com.sunmax.crontab.controller.feign;

import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.crontab.service.ConfigFuncPointService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import com.sunmax.common.feign.configure.ConfigureCrontabFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/configure")
@Tag(name = "提供给配置服务调用的远程接口")
@Hidden()
public class ConfigureFeignEndpoint implements ConfigureCrontabFeignClient {

    @Autowired
    private ConfigFuncPointService configFuncPointService;

    @PostMapping("findComputeNodeListByDeviceId")
    @Operation(summary = "根据站点/设备id查询计算节点列表")
    
    @Parameter(name = "deviceId", description = "站点/设备唯一id")
    public ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(@RequestParam String deviceId) {
        return configFuncPointService.findComputeNodeListByDeviceId(deviceId);
    }
}
