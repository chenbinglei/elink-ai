package com.sunmax.common.feign.configure;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.StorageDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service", path = "/system/feign/configure", fallbackFactory = GenericFeignFallbackFactory.class)
public interface ConfigureSystemFeignClient {

    @PostMapping("getPlatformDataForward")
    @Operation(summary = "根据平台id和协议标识查询平台数据转发")
    @Parameters({
            @Parameter(name = "platformId", description = "平台运营商id"),
            @Parameter(name = "protocolCode", description = "协议标识")
    })
    
    ResponseResult<PlatformDataForwardDto> getPlatformDataForward(@RequestParam String platformId, @RequestParam String protocolCode);

    @PostMapping("getPlatformDataForwardList")
    @Operation(summary = "根据多个协议标识查询平台数据转发")
    @Parameter(name = "protocolCodes", description = "多个协议标识")
    
    ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(@RequestBody Set<String> protocolCodes);

    @PostMapping("getStorageDataForwardList")
    @Operation(summary = "根据多个协议标识查询储能平台数据转发")
    @Parameter(name = "protocolCodes", description = "多个协议标识")
    
    ResponseResult<List<StorageDataForwardDto>> getStorageDataForwardList(@RequestBody Set<String> protocolCodes);

}
