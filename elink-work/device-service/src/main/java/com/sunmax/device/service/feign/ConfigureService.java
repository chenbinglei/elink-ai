package com.sunmax.device.service.feign;

import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 接收配置服务提供的接口
 */
@FeignClient(value = "configure-service")
@RestController
@RequestMapping("/configure/feign/device")
public interface ConfigureService {

    @PostMapping("notificationStationInfo")
    @ApiOperation("充电站信息变化推送")
    @ApiOperationSupport(order = 1)
    ResponseResult<Void> notificationStationInfo(@RequestBody Set<String> siteIds);

}
