package com.sunmax.configure.controller.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.service.SystemFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 提供系统服务需要的接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/system")
@Api(tags = "提供系统服务需要的接口")
@ApiIgnore()
public class SystemFeignController {

    @Autowired
    private SystemFeignService systemFeignService;

    @PostMapping("updateHttpSiteForward")
    @ApiOperation("更新站点转发平台运营商配置")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Boolean> updateHttpSiteForward(@RequestBody PlatformDataForwardDto platformDataForward) {
        return systemFeignService.updateHttpSiteForward(platformDataForward);
    }
}
