package com.sunmax.common.feign.system;

import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 系统服务 - 提供给认证服务调用的接口
 * 消费者: auth-service
 */
@FeignClient(value = "system-service", path = "/system/feign/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
public interface SystemSauthFeignClient {

    @PostMapping("findByAppletCodeAndAppletType")
    @Operation(summary = "根据小程序编码和类型查询小程序信息")
    ResponseResult<AppletDto> findByAppletCodeAndAppletType(@RequestParam String appletCode, @RequestParam Integer appletType);
}
