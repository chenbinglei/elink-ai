package com.sunmax.configure.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.service.DeviceFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Set;

/**
 * 提供设备服务需要的接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/device")
@Tag(name = "提供设备服务需要的接口")
@Hidden()
public class DeviceFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("notificationStationInfo")
    @Operation(summary = "充电站信息变化推送")
    
    public ResponseResult<Void> notificationStationInfo(@RequestBody Set<String> siteIds) {
        return deviceFeignService.notificationStationInfo(siteIds);
    }

}
