package com.sunmax.system.service.feign;

import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收聚合服务提供的接口
 */
@FeignClient(value = "together-service")
@RestController
@RequestMapping("/together/feign/system")
public interface TogetherService {

    @PostMapping("deleteAndGatewayPlatformSet")
    @ApiOperation("根据删除平台id删除网关关联关系并重新下发")
    @ApiOperationSupport(order = 1)
    ResponseResult<String> deleteAndGatewayPlatformSet(@RequestParam String PlatformId);
}
