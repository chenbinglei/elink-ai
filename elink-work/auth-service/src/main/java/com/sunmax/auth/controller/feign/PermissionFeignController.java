package com.sunmax.auth.controller.feign;

import com.sunmax.auth.service.UserLoginService;
import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 提供接入服务调用的接口
 */
@CrossOrigin
@RestController
@RequestMapping("/feign/permission")
@Api(tags = "提供权限相关的接口")
@ApiIgnore()
public class PermissionFeignController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("findPermissionByUserAccount")
    @ApiOperation("根据用户账号查询权限数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(@RequestParam String userAccount, @RequestParam String clientId) {
        return userLoginService.findPermissionByUserAccount(userAccount, clientId);
    }

}
