package com.sunmax.webapp.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.webapp.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("sauth")
@Api(tags = "登录管理")
public class SauthController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("appletLogin")
    @ApiOperation("微信小程序登录")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String,Object>> appletLogin(@RequestParam Map<String, String> parameters){
        return userInfoService.postAccessToken(parameters);
    }
}
