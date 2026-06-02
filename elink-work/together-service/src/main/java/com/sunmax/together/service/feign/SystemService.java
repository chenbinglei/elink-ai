package com.sunmax.together.service.feign;

import com.sunmax.common.dto.system.*;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service")
//@FeignClient(value = "system-service-cbl",url = "http://121.41.109.130:60002")
@RestController
@RequestMapping("/system/feign/together")
public interface SystemService {

    @PostMapping("findAllOrganEmpowerByUserId")
    @ApiOperation("根据租户id查询所有关联的资产授权数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId);

    @PostMapping("findAllTenantInfoByIds")
    @ApiOperation("根据多个租户id查询租户基本信息")
    @ApiOperationSupport(order = 2)
    ResponseResult<List<TenantDetailsDto>> findAllTenantInfoByIds(@RequestBody List<String> tenantIdList);

    @PostMapping("findChargePlatformInfoByLogos")
    @ApiOperation("根据多个平台标识查询平台信息")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByLogos(@RequestBody List<String> platformLogoList);

    @PostMapping("findChargePlatformInfoByIds")
    @ApiOperation("根据多个平台id查询平台信息")
    @ApiOperationSupport(order = 4)
    ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByIds(@RequestBody List<String> platformIdList);

    @PostMapping("findAccountListByTenantId")
    @ApiOperation("根据租户id和平台类型查询租户账户信息")
    @ApiOperationSupport(order = 5)
    ResponseResult<List<AccountDto>> findAccountListByTenantId(@RequestParam String tenantId, @RequestParam(required = false) Integer platformType);

    @PostMapping("findAccountListByAccountIds")
    @ApiOperation("根据多个账户id查询租户账户信息")
    @ApiOperationSupport(order = 6)
    ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(@RequestBody Set<String> accountIds);

    @PostMapping("findTenantListByUserId")
    @ApiOperation("根据用户id查询所有租户列表数据")
    @ApiOperationSupport(order = 7)
    ResponseResult<List<TenantDetailsDto>> findTenantListByUserId(@RequestParam String userId);

    @PostMapping("findUserInfoByIds")
    @ApiOperation("根据多个用户id查询用户信息")
    @ApiOperationSupport(order = 8)
    ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds);

    @PostMapping("findAppletListByIds")
    @ApiOperation("根据多个小程序主键id查询小程序数据")
    @ApiOperationSupport(order = 9)
    ResponseResult<Map<String, AppletDto>> findAppletListByIds(@RequestBody Set<String> appletIds);

    @PostMapping("getPasswordByAccount")
    @ApiOperation("根据用户账号获取密码")
    @ApiOperationSupport(order = 10)
    ResponseResult<String> getPasswordByAccount(@RequestParam String userAccount);

    @PostMapping("findUserNameByUserAccounts")
    @ApiOperation("根据多个用户账号查询用户名称")
    @ApiOperationSupport(order = 11)
    ResponseResult<Map<String, String>> findUserNameByUserAccounts(@RequestBody List<String> userAccounts);

    @PostMapping("findUserListByUserId")
    @ApiOperation("根据用户id查询租户下所有的用户")
    @ApiOperationSupport(order = 12)
    ResponseResult<List<UserDto>> findUserListByUserId(@RequestParam String userId);
}
