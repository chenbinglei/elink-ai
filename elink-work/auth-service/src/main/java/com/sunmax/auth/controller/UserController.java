package com.sunmax.auth.controller;

import com.sunmax.auth.service.UserService;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/oauth/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("findUserIsExitByPhone")
    @ApiOperation("根据手机号查询该用户是否存在")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> findUserIsExitByPhone(String userAccount, String phone) {
        return userService.findUserIsExitByPhone(userAccount, phone);
    }

    @PostMapping("updateUserPassword")
    @ApiOperation("修改用户密码")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userAccount", value = "用户账号", dataType = "String", required = true),
            @ApiImplicitParam(name = "phone", value = "用户手机号", dataType = "String", required = true),
            @ApiImplicitParam(name = "password", value = "用户密码", dataType = "String", required = true)
    })
    public ResponseResult<String> updateUserPassword(String userAccount, String phone, String password) {
        return userService.updateUserPassword(userAccount, phone, password);
    }

}
