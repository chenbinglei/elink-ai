package com.sunmax.configure.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.service.DeviceFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Set;

/**
 * 提供设备服务需要的接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/device")
@Api(tags = "提供设备服务需要的接口")
@ApiIgnore()
public class DeviceFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("notificationStationInfo")
    @ApiOperation("充电站信息变化推送")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> notificationStationInfo(@RequestBody Set<String> siteIds) {
        return deviceFeignService.notificationStationInfo(siteIds);
    }

}
