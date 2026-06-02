package com.sunmax.together.controller.feign;

import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.operation.AppletUserService;
import io.swagger.annotations.*;
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
    private AppletUserService appletUserService;

    @PostMapping("queryAppletUserInfoByPhoneNum")
    @ApiOperation("根据小程序用户手机号查询用户信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(@RequestParam String phoneNum) {
        return appletUserService.queryAppletUserInfoByPhoneNum(phoneNum);
    }

    @PostMapping("updateOrSaveAppletUser")
    @ApiOperation("根据小程序用户手机号修改小程序编码信息(如果根据手机号查不到小程序用户，则创建小程序用户信息)")
    @ApiOperationSupport(order = 2)
    public ResponseResult<String> updateOrSaveAppletUser(@RequestParam String phoneNum, @RequestParam String openid, @RequestParam String appletId) {
        return appletUserService.updateOrSaveAppletUser(phoneNum, openid, appletId);
    }

    @PostMapping("updateAppletUserState")
    @ApiOperation("修改小程序用户状态")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "小程序用户唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "userState", value = "用户状态 1-正常 2-冻结 3-注销", paramType = "query")
    })
    public ResponseResult<String> updateAppletUserState(@RequestParam String id, @RequestParam Integer userState) {
        return appletUserService.updateAppletUserState(id, userState);
    }
}
