package com.sunmax.devops.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.service.UserInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("sauth")
@Tag(name = "登录管理")
public class SauthController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("appletLogin")
    @Operation(summary = "微信小程序登录")
    
    public ResponseResult<Map<String,Object>> appletLogin(@RequestParam Map<String, String> parameters){
        return userInfoService.postAccessToken(parameters);
    }
}
