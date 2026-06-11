package com.sunmax.together.controller.feign;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.operation.AppletUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@CrossOrigin
@RequestMapping("/feign/sauth")
@Tag(name = "提供给登录服务调用的远程接口")
@Hidden()
public class AuthFeignEndpoint {

    @Autowired
    private AppletUserService appletUserService;

    @PostMapping("queryAppletUserInfoByPhoneNum")
    @Operation(summary = "根据小程序用户手机号查询用户信息")
    
    public ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(@RequestParam String phoneNum) {
        return appletUserService.queryAppletUserInfoByPhoneNum(phoneNum);
    }

    @PostMapping("updateOrSaveAppletUser")
    @Operation(summary = "根据小程序用户手机号修改小程序编码信息(如果根据手机号查不到小程序用户，则创建小程序用户信息)")
    
    public ResponseResult<String> updateOrSaveAppletUser(@RequestParam String phoneNum, @RequestParam String openid, @RequestParam String appletId) {
        return appletUserService.updateOrSaveAppletUser(phoneNum, openid, appletId);
    }

    @PostMapping("updateAppletUserState")
    @Operation(summary = "修改小程序用户状态")
    
    @Parameters({
            @Parameter(name = "id", description = "小程序用户唯一id"),
            @Parameter(name = "userState", description = "用户状态 1-正常 2-冻结 3-注销")
    })
    public ResponseResult<String> updateAppletUserState(@RequestParam String id, @RequestParam Integer userState) {
        return appletUserService.updateAppletUserState(id, userState);
    }
}
