package com.sunmax.auth.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.auth.service.UserService;
import com.sunmax.common.util.ResponseResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/oauth/user")
@Tag(name = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("findUserIsExitByPhone")
    @Operation(summary = "根据手机号查询该用户是否存在")
    
    public ResponseResult<String> findUserIsExitByPhone(String userAccount, String phone) {
        return userService.findUserIsExitByPhone(userAccount, phone);
    }

    @PostMapping("updateUserPassword")
    @Operation(summary = "修改用户密码")
    
    @Parameters({
            @Parameter(name = "userAccount", description = "用户账号"),
            @Parameter(name = "phone", description = "用户手机号"),
            @Parameter(name = "password", description = "用户密码")
    })
    public ResponseResult<String> updateUserPassword(String userAccount, String phone, String password) {
        return userService.updateUserPassword(userAccount, phone, password);
    }

}
