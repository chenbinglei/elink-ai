package com.sunmax.configure.controller.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.service.SystemFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;
import com.sunmax.common.feign.system.SystemConfigureFeignClient;

/**
 * 提供系统服务需要的接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/system")
@Tag(name = "提供系统服务需要的接口")
@Hidden()
public class SystemFeignEndpoint implements SystemConfigureFeignClient {

    @Autowired
    private SystemFeignService systemFeignService;

    @PostMapping("updateHttpSiteForward")
    @Operation(summary = "更新站点转发平台运营商配置")
    
    @Override
    public ResponseResult<Boolean> updateHttpSiteForward(@RequestBody PlatformDataForwardDto platformDataForward) {
        return systemFeignService.updateHttpSiteForward(platformDataForward);
    }
}
