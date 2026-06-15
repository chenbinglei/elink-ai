package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.AppletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@CrossOrigin
@RequestMapping("/feign/sauth")
@Tag(name = "提供给登录服务调用的远程接口")
@Hidden()
public class AuthFeignController {

    @Autowired
    private AppletService appletService;

    @PostMapping("findByAppletCodeAndAppletType")
    @Operation(summary = "根据小程序编码和类型查询小程序信息")
    
    public ResponseResult<AppletDto> findByAppletCodeAndAppletType(@RequestParam String appletCode, @RequestParam Integer appletType) {
        return appletService.findByAppletCodeAndAppletType(appletCode, appletType);
    }
}
