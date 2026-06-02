package com.sunmax.together.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.SystemFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author: yqz
 * @注释: 提供给系统管理服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/system")
@Api(tags = "提供给系统管理服务调用的远程接口")
@ApiIgnore()
public class SystemFeignController {

    @Autowired
    private SystemFeignService systemFeignService;

    @PostMapping("deleteAndGatewayPlatformSet")
    @ApiOperation("根据删除平台id删除网关关联关系并重新下发")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> deleteAndGatewayPlatformSet(@RequestParam String PlatformId) {
        return systemFeignService.deleteAndGatewayPlatformSet(PlatformId);
    }
}
