package com.sunmax.system.controller.feign;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.StorageDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.ConfigureFeignService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Set;
import com.sunmax.common.feign.configure.ConfigureSystemFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/configure")
@Tag(name = "提供给定时任务服务调用的远程接口")
@Hidden()
public class ConfigureFeignEndpoint implements ConfigureSystemFeignClient {

    @Autowired
    private ConfigureFeignService configureFeignService;

    @PostMapping("getPlatformDataForward")
    @Operation(summary = "根据平台id和协议标识查询平台数据转发")
    @Parameters({
            @Parameter(name = "platformId", description = "平台运营商id"),
            @Parameter(name = "protocolCode", description = "协议标识")
    })
    
    @Override
    public ResponseResult<PlatformDataForwardDto> getPlatformDataForward(@RequestParam String platformId, @RequestParam String protocolCode) {
        return configureFeignService.getPlatformDataForward(platformId, protocolCode);
    }

    @PostMapping("getPlatformDataForwardList")
    @Operation(summary = "根据多个协议标识查询平台数据转发")
    @Parameter(name = "protocolCodes", description = "多个协议编号")
    
    public ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(@RequestBody Set<String> protocolCodes) {
        return configureFeignService.getPlatformDataForwardList(protocolCodes);
    }

    @PostMapping("getStorageDataForwardList")
    @Operation(summary = "根据多个协议标识查询储能平台数据转发")
    @Parameter(name = "protocolCodes", description = "多个协议标识")
    
    public ResponseResult<List<StorageDataForwardDto>> getStorageDataForwardList(@RequestBody Set<String> protocolCodes) {
        return configureFeignService.getStorageDataForwardList(protocolCodes);
    }

}
