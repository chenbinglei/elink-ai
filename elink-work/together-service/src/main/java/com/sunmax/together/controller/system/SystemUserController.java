package com.sunmax.together.controller.system;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.feign.SystemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@CrossOrigin
@RequestMapping("systemUser")
@Tag(name = "系统用户管理")
@Hidden
public class SystemUserController {

    @Autowired
    private SystemService systemService;

    @PostMapping("getPasswordByAccount")
    @Operation(summary = "根据用户账号获取密码")
    
    public ResponseResult<String> getPasswordByAccount(@RequestParam String userAccount) {
        return systemService.getPasswordByAccount(userAccount);
    }

}
