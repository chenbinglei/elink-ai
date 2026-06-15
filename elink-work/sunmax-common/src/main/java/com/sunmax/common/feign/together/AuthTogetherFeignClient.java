package com.sunmax.common.feign.together;

import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收聚合服务提供的接口
 */
@FeignClient(value = "together-service", path = "/together/feign/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
public interface AuthTogetherFeignClient {

    @PostMapping("queryAppletUserInfoByPhoneNum")
    @Operation(summary = "根据小程序用户手机号查询用户信息")
    
    ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(@RequestParam String phoneNum);

    @PostMapping("updateOrSaveAppletUser")
    @Operation(summary = "根据小程序用户手机号修改小程序编码信息(如果根据手机号查不到小程序用户，则创建小程序用户信息)")
    
    ResponseResult<String> updateOrSaveAppletUser(@RequestParam String phoneNum, @RequestParam String openid, @RequestParam String appletId);

    @PostMapping("updateAppletUserState")
    @Operation(summary = "修改小程序用户状态")
    
    @Parameters({
            @Parameter(name = "id", description = "小程序用户唯一id"),
            @Parameter(name = "userState", description = "用户状态 1-正常 2-冻结 3-注销")
    })
    ResponseResult<String> updateAppletUserState(@RequestParam String id, @RequestParam Integer userState);
}
