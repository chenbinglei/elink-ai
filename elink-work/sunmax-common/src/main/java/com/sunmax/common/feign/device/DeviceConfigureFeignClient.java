package com.sunmax.common.feign.device;

import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收配置服务提供的接口
 */
@FeignClient(value = "configure-service", path = "/configure/feign/device", fallbackFactory = GenericFeignFallbackFactory.class)
public interface DeviceConfigureFeignClient {

    @PostMapping("notificationStationInfo")
    @Operation(summary = "充电站信息变化推送")
    
    ResponseResult<Void> notificationStationInfo(@RequestBody Set<String> siteIds);

}
