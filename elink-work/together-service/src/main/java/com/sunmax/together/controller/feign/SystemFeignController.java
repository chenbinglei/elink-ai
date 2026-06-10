package com.sunmax.together.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.SystemFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * @Author: yqz
 * @注释: 提供给系统管理服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/system")
@Tag(name = "提供给系统管理服务调用的远程接口")
@Hidden()
public class SystemFeignController {

    @Autowired
    private SystemFeignService systemFeignService;

    @PostMapping("deleteAndGatewayPlatformSet")
    @Operation(summary = "根据删除平台id删除网关关联关系并重新下发")
    
    public ResponseResult<String> deleteAndGatewayPlatformSet(@RequestParam String PlatformId) {
        return systemFeignService.deleteAndGatewayPlatformSet(PlatformId);
    }
}
