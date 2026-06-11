package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.AppletService;
import com.sunmax.system.service.ConfigureCenterService;
import com.sunmax.system.service.DeviceFeignService;
import com.sunmax.system.service.TenantManageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.together.TogetherSystemFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Tag(name = "提供给能源聚合服务调用的远程接口")
@Hidden()
public class TogetherFeignEndpoint implements TogetherSystemFeignClient {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @Autowired
    private TenantManageService tenantManageService;

    @Autowired
    private ConfigureCenterService configureCenterService;

    @Autowired
    private AppletService appletService;

    @PostMapping("findAllOrganEmpowerByUserId")
    @Operation(summary = "根据用户id查询所有关联的资产授权数据")
    
    public ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId) {
        return deviceFeignService.findAllOrganEmpowerByUserId(userId);
    }

    @PostMapping("findAllTenantInfoByIds")
    @Operation(summary = "根据多个租户id查询租户基本信息")
    
    public ResponseResult<List<TenantDetailsDto>> findAllTenantInfoByIds(@RequestBody List<String> tenantIdList) {
        return tenantManageService.findAllTenantInfoByIds(tenantIdList);
    }

    @PostMapping("findChargePlatformInfoByLogos")
    @Operation(summary = "根据多个平台标识查询平台信息")
    
    public ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByLogos(@RequestBody List<String> platformLogoList) {
        return configureCenterService.findChargePlatformInfoByLogos(platformLogoList);
    }

    @PostMapping("findChargePlatformInfoByIds")
    @Operation(summary = "根据多个平台id查询平台信息")
    
    public ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByIds(@RequestBody List<String> platformIdList) {
        return configureCenterService.findChargePlatformInfoByIds(platformIdList);
    }

    @PostMapping("findAccountListByTenantId")
    @Operation(summary = "根据租户id和平台类型查询租户账户信息")
    
    public ResponseResult<List<AccountDto>> findAccountListByTenantId(@RequestParam String tenantId, @RequestParam(required = false) Integer platformType) {
        return tenantManageService.findAccountListByTenantId(tenantId, platformType);
    }

    @PostMapping("findAccountListByAccountIds")
    @Operation(summary = "根据多个账户id查询租户账户信息")
    
    public ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(@RequestBody Set<String> accountIds) {
        return tenantManageService.findAccountListByAccountIds(accountIds);
    }

    @PostMapping("findTenantListByUserId")
    @Operation(summary = "根据用户id查询所有租户列表数据")
    
    public ResponseResult<List<TenantDetailsDto>> findTenantListByUserId(@RequestParam String userId) {
        return tenantManageService.findTenantListByUserId(userId);
    }

    @PostMapping("findUserInfoByIds")
    @Operation(summary = "根据用户id查询用户信息")
    
    public ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds) {
        return deviceFeignService.findUserInfoByIdsFeign(userIds);
    }

    @PostMapping("findAppletListByIds")
    @Operation(summary = "根据多个小程序主键id查询小程序数据")
    
    public ResponseResult<Map<String, AppletDto>> findAppletListByIds(@RequestBody Set<String> appletIds) {
        return appletService.findAppletListByIds(appletIds);
    }

    @PostMapping("getPasswordByAccount")
    @Operation(summary = "根据用户账号获取密码")
    
    @Override
    public ResponseResult<String> getPasswordByAccount(@RequestParam String userAccount) {
        return deviceFeignService.getPasswordByAccount(userAccount);
    }

    @PostMapping("findUserNameByUserAccounts")
    @Operation(summary = "根据多个用户账号查询用户名称")
    
    public ResponseResult<Map<String, String>> findUserNameByUserAccounts(@RequestBody List<String> userAccounts) {
        return deviceFeignService.findUserNameByUserAccounts(userAccounts);
    }

    @PostMapping("findUserListByUserId")
    @Operation(summary = "根据用户id查询租户下所有的用户")
    
    public ResponseResult<List<UserDto>> findUserListByUserId(@RequestParam String userId) {
        return deviceFeignService.findUserListByUserId(userId);
    }

}
