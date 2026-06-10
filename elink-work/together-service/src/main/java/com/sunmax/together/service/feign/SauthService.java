package com.sunmax.together.service.feign;

import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 获取接入服务提供的接口
 */
@FeignClient(value = "sauth-service", path = "/sauth/feign/permission")
//@FeignClient(value = "sauth-service-cbl",url = "http://121.41.109.130:60001")
public interface SauthService {

    @PostMapping("findPermissionByUserAccount")
    @Operation(summary = "根据用户账号查询权限数据")
    
    ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(@RequestParam String userAccount, @RequestParam String clientId);

}
