package com.sunmax.auth.controller.feign;

import com.sunmax.auth.service.UserLoginService;
import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

/**
 * 提供接入服务调用的接口
 */
@CrossOrigin
@RestController
@RequestMapping("/feign/permission")
@Tag(name = "提供权限相关的接口")
@Hidden()
public class PermissionFeignController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("findPermissionByUserAccount")
    @Operation(summary = "根据用户账号查询权限数据")
    
    public ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(@RequestParam String userAccount, @RequestParam String clientId) {
        return userLoginService.findPermissionByUserAccount(userAccount, clientId);
    }

}
