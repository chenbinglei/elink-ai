package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.AppletService;
import com.sunmax.system.service.ConfigureCenterService;
import com.sunmax.system.service.DeviceFeignService;
import com.sunmax.system.service.TenantManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Api(tags = "提供给能源聚合服务调用的远程接口")
@ApiIgnore()
public class TogetherFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @Autowired
    private TenantManageService tenantManageService;

    @Autowired
    private ConfigureCenterService configureCenterService;

    @Autowired
    private AppletService appletService;

    @PostMapping("findAllOrganEmpowerByUserId")
    @ApiOperation("根据用户id查询所有关联的资产授权数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId) {
        return deviceFeignService.findAllOrganEmpowerByUserId(userId);
    }

    @PostMapping("findAllTenantInfoByIds")
    @ApiOperation("根据多个租户id查询租户基本信息")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<TenantDetailsDto>> findAllTenantInfoByIds(@RequestBody List<String> tenantIdList) {
        return tenantManageService.findAllTenantInfoByIds(tenantIdList);
    }

    @PostMapping("findChargePlatformInfoByLogos")
    @ApiOperation("根据多个平台标识查询平台信息")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByLogos(@RequestBody List<String> platformLogoList) {
        return configureCenterService.findChargePlatformInfoByLogos(platformLogoList);
    }

    @PostMapping("findChargePlatformInfoByIds")
    @ApiOperation("根据多个平台id查询平台信息")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByIds(@RequestBody List<String> platformIdList) {
        return configureCenterService.findChargePlatformInfoByIds(platformIdList);
    }

    @PostMapping("findAccountListByTenantId")
    @ApiOperation("根据租户id和平台类型查询租户账户信息")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<AccountDto>> findAccountListByTenantId(@RequestParam String tenantId, @RequestParam(required = false) Integer platformType) {
        return tenantManageService.findAccountListByTenantId(tenantId, platformType);
    }

    @PostMapping("findAccountListByAccountIds")
    @ApiOperation("根据多个账户id查询租户账户信息")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(@RequestBody Set<String> accountIds) {
        return tenantManageService.findAccountListByAccountIds(accountIds);
    }

    @PostMapping("findTenantListByUserId")
    @ApiOperation("根据用户id查询所有租户列表数据")
    @ApiOperationSupport(order = 7)
    public ResponseResult<List<TenantDetailsDto>> findTenantListByUserId(@RequestParam String userId) {
        return tenantManageService.findTenantListByUserId(userId);
    }

    @PostMapping("findUserInfoByIds")
    @ApiOperation("根据用户id查询用户信息")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds) {
        return deviceFeignService.findUserInfoByIdsFeign(userIds);
    }

    @PostMapping("findAppletListByIds")
    @ApiOperation("根据多个小程序主键id查询小程序数据")
    @ApiOperationSupport(order = 9)
    public ResponseResult<Map<String, AppletDto>> findAppletListByIds(@RequestBody Set<String> appletIds) {
        return appletService.findAppletListByIds(appletIds);
    }

    @PostMapping("getPasswordByAccount")
    @ApiOperation("根据用户账号获取密码")
    @ApiOperationSupport(order = 10)
    public ResponseResult<String> getPasswordByAccount(@RequestParam String userAccount) {
        return deviceFeignService.getPasswordByAccount(userAccount);
    }

    @PostMapping("findUserNameByUserAccounts")
    @ApiOperation("根据多个用户账号查询用户名称")
    @ApiOperationSupport(order = 11)
    public ResponseResult<Map<String, String>> findUserNameByUserAccounts(@RequestBody List<String> userAccounts) {
        return deviceFeignService.findUserNameByUserAccounts(userAccounts);
    }

    @PostMapping("findUserListByUserId")
    @ApiOperation("根据用户id查询租户下所有的用户")
    @ApiOperationSupport(order = 12)
    public ResponseResult<List<UserDto>> findUserListByUserId(@RequestParam String userId) {
        return deviceFeignService.findUserListByUserId(userId);
    }

}
