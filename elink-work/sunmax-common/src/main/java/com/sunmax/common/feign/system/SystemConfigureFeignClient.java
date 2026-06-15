package com.sunmax.common.feign.system;
import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;
/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "configure-service", path = "/configure/feign/system", fallbackFactory = GenericFeignFallbackFactory.class)
public interface SystemConfigureFeignClient {
    @PostMapping("updateHttpSiteForward")
    @Operation(summary = "更新站点转发平台运营商配置")
    
    ResponseResult<Boolean> updateHttpSiteForward(@RequestBody PlatformDataForwardDto platformDataForward);
}
