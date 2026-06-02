package com.sunmax.crontab.service.feign;

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

/**
 * 获取接入服务提供的接口
 */
@FeignClient(value = "sauth-service")
@RestController
@RequestMapping("/sauth/feign/permission")
public interface SauthService {

    @PostMapping("findPermissionByUserAccount")
    @ApiOperation("根据用户账号查询权限数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(@RequestParam String userAccount, @RequestParam String clientId);

}
