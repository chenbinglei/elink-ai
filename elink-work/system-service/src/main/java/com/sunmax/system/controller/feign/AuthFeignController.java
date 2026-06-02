package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.AppletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@CrossOrigin
@RequestMapping("/feign/sauth")
@Api(tags = "提供给登录服务调用的远程接口")
@ApiIgnore()
public class AuthFeignController {

    @Autowired
    private AppletService appletService;

    @PostMapping("findByAppletCodeAndAppletType")
    @ApiOperation("根据小程序编码和类型查询小程序信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<AppletDto> findByAppletCodeAndAppletType(@RequestParam String appletCode, @RequestParam Integer appletType) {
        return appletService.findByAppletCodeAndAppletType(appletCode, appletType);
    }
}
