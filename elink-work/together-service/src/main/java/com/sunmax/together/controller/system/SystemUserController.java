package com.sunmax.together.controller.system;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.feign.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@CrossOrigin
@RequestMapping("systemUser")
@Api(tags = "系统用户管理")
@ApiIgnore
public class SystemUserController {

    @Autowired
    private SystemService systemService;

    @PostMapping("getPasswordByAccount")
    @ApiOperation("根据用户账号获取密码")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> getPasswordByAccount(@RequestParam String userAccount) {
        return systemService.getPasswordByAccount(userAccount);
    }

}
