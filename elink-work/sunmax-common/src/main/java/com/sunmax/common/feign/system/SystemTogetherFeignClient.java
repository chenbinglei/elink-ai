package com.sunmax.common.feign.system;

import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收聚合服务提供的接口
 */
@FeignClient(value = "together-service", path = "/together/feign/system", fallbackFactory = GenericFeignFallbackFactory.class)
public interface SystemTogetherFeignClient {

    @PostMapping("deleteAndGatewayPlatformSet")
    @Operation(summary = "根据删除平台id删除网关关联关系并重新下发")
    
    ResponseResult<String> deleteAndGatewayPlatformSet(@RequestParam String PlatformId);
}
