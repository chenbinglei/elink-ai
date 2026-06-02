package com.sunmax.crontab.controller.feign;

import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.crontab.service.ConfigFuncPointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/feign/configure")
@Api(tags = "提供给配置服务调用的远程接口")
@ApiIgnore()
public class ConfigureFeignController {

    @Autowired
    private ConfigFuncPointService configFuncPointService;

    @PostMapping("findComputeNodeListByDeviceId")
    @ApiOperation("根据站点/设备id查询计算节点列表")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "deviceId", value = "站点/设备唯一id", dataType = "String", required = true)
    public ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(@RequestParam String deviceId) {
        return configFuncPointService.findComputeNodeListByDeviceId(deviceId);
    }
}
