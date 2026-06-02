package com.sunmax.auth.service.feign;

import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收聚合服务提供的接口
 */
@FeignClient(value = "together-service")
@RestController
@RequestMapping("/together/feign/sauth")
public interface TogetherService {

    @PostMapping("queryAppletUserInfoByPhoneNum")
    @ApiOperation("根据小程序用户手机号查询用户信息")
    @ApiOperationSupport(order = 1)
    ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(@RequestParam String phoneNum);

    @PostMapping("updateOrSaveAppletUser")
    @ApiOperation("根据小程序用户手机号修改小程序编码信息(如果根据手机号查不到小程序用户，则创建小程序用户信息)")
    @ApiOperationSupport(order = 2)
    ResponseResult<String> updateOrSaveAppletUser(@RequestParam String phoneNum, @RequestParam String openid, @RequestParam String appletId);

    @PostMapping("updateAppletUserState")
    @ApiOperation("修改小程序用户状态")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "小程序用户唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "userState", value = "用户状态 1-正常 2-冻结 3-注销", paramType = "query")
    })
    ResponseResult<String> updateAppletUserState(@RequestParam String id, @RequestParam Integer userState);
}
