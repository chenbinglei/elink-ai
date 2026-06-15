package com.sunmax.common.feign.webapp;

import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

@FeignClient(value = "system-service", path = "/system/feign/swebapp", fallbackFactory = GenericFeignFallbackFactory.class)
public interface WebAppSystemServiceFeignClient {

    @PostMapping("findAppletById")
    @Operation(summary = "根据小程序主键id查询小程序数据")
    
    ResponseResult<AppletDto> findAppletById(@RequestParam String id);

    @PostMapping("findAppletByAppletCode")
    @Operation(summary = "根据小程序编码查询小程序数据")
    
    ResponseResult<AppletDto> findAppletByAppletCode(@RequestParam String appletCode);

    @PostMapping("findAccountListByAccountIds")
    @Operation(summary = "根据多个账户id查询租户账户信息")
    
    ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(@RequestBody Set<String> accountIds);

    @PostMapping("findAllOrganEmpowerByUserId")
    @Operation(summary = "根据用户id查询所有关联的资产授权数据")
    
    ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId);

    @PostMapping("findAccountByRsaSerialNo")
    @Operation(summary = "根据RSA商户证书号查询租户账户信息")
    
    ResponseResult<AccountDto> findAccountByRsaSerialNo(@RequestParam String rsaSerialNo);

}
