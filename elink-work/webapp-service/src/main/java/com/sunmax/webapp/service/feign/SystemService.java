package com.sunmax.webapp.service.feign;

import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = "system-service")
@RestController
@RequestMapping("/system/feign/swebapp")
public interface SystemService {

    @PostMapping("findAppletById")
    @ApiOperation("根据小程序主键id查询小程序数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<AppletDto> findAppletById(@RequestParam String id);

    @PostMapping("findAppletByAppletCode")
    @ApiOperation("根据小程序编码查询小程序数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<AppletDto> findAppletByAppletCode(@RequestParam String appletCode);

    @PostMapping("findAccountListByAccountIds")
    @ApiOperation("根据多个账户id查询租户账户信息")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(@RequestBody Set<String> accountIds);

    @PostMapping("findAllOrganEmpowerByUserId")
    @ApiOperation("根据用户id查询所有关联的资产授权数据")
    @ApiOperationSupport(order = 4)
    ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId);

    @PostMapping("findAccountByRsaSerialNo")
    @ApiOperation("根据RSA商户证书号查询租户账户信息")
    @ApiOperationSupport(order = 5)
    ResponseResult<AccountDto> findAccountByRsaSerialNo(@RequestParam String rsaSerialNo);

}
