package com.sunmax.devops.service.feign;

import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 获取认证服务提供的接口
 */
@FeignClient(value = "sauth-service")
@RestController
@RequestMapping("/sauth")
public interface SauthService {

    @PostMapping("/feign/permission/findPermissionByUserAccount")
    @ApiOperation("根据用户账号查询权限数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(@RequestParam String userAccount, @RequestParam String clientId);

    @PostMapping("/oauth/token")
    @ApiOperation("用户登录Post请求方式")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String,Object>> postAccessToken(@RequestParam Map<String, String> parameters);

}
