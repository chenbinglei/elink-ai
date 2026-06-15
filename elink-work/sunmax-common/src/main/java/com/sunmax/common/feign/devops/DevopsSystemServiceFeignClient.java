package com.sunmax.common.feign.devops;

import com.sunmax.common.dto.system.*;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service", path = "/system/feign/together", fallbackFactory = GenericFeignFallbackFactory.class)
public interface DevopsSystemServiceFeignClient {

    @PostMapping("findAllOrganEmpowerByUserId")
    @Operation(summary = "根据租户id查询所有关联的资产授权数据")
    
    ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId);

    @PostMapping("findAllTenantInfoByIds")
    @Operation(summary = "根据多个租户id查询租户基本信息")
    
    ResponseResult<List<TenantDetailsDto>> findAllTenantInfoByIds(@RequestBody List<String> tenantIdList);

    @PostMapping("findChargePlatformInfoByLogos")
    @Operation(summary = "根据多个平台标识查询平台信息")
    
    ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByLogos(@RequestBody List<String> platformLogoList);

    @PostMapping("findChargePlatformInfoByIds")
    @Operation(summary = "根据多个平台id查询平台信息")
    
    ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByIds(@RequestBody List<String> platformIdList);

    @PostMapping("findAccountListByTenantId")
    @Operation(summary = "根据租户id和平台类型查询租户账户信息")
    
    ResponseResult<List<AccountDto>> findAccountListByTenantId(@RequestParam String tenantId, @RequestParam(required = false) Integer platformType);

    @PostMapping("findAccountListByAccountIds")
    @Operation(summary = "根据多个账户id查询租户账户信息")
    
    ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(@RequestBody Set<String> accountIds);

    @PostMapping("findTenantListByUserId")
    @Operation(summary = "根据用户id查询所有租户列表数据")
    
    ResponseResult<List<TenantDetailsDto>> findTenantListByUserId(@RequestParam String userId);

    @PostMapping("findUserInfoByIds")
    @Operation(summary = "根据多个用户id查询用户信息")
    
    ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds);

    @PostMapping("findAppletListByIds")
    @Operation(summary = "根据多个小程序主键id查询小程序数据")
    
    ResponseResult<Map<String, AppletDto>> findAppletListByIds(@RequestBody Set<String> appletIds);

    @PostMapping("getPasswordByAccount")
    @Operation(summary = "根据用户账号获取密码")
    
    ResponseResult<String> getPasswordByAccount(@RequestParam String userAccount);
}
