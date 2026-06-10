package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.AppletService;
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

@RestController
@CrossOrigin
@RequestMapping("/feign/swebapp")
@Tag(name = "提供给webapp服务调用的远程接口")
@Hidden()
public class WebAppFeignController {

    @Autowired
    private AppletService appletService;

    @Autowired
    private TenantManageService tenantManageService;

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("findAppletById")
    @Operation(summary = "根据小程序主键id查询小程序数据")
    
    public ResponseResult<AppletDto> findAppletById(@RequestParam String id) {
        return appletService.findAppletById(id);
    }

    @PostMapping("findAppletByAppletCode")
    @Operation(summary = "根据小程序编码查询小程序数据")
    
    public ResponseResult<AppletDto> findAppletByAppletCode(@RequestParam String appletCode) {
        return appletService.findAppletByAppletCode(appletCode);
    }

    @PostMapping("findAccountListByAccountIds")
    @Operation(summary = "根据多个账户id查询租户账户信息")
    
    public ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(@RequestBody Set<String> accountIds) {
        return tenantManageService.findAccountListByAccountIds(accountIds);
    }

    @PostMapping("findAllOrganEmpowerByUserId")
    @Operation(summary = "根据用户id查询所有关联的资产授权数据")
    
    public ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId) {
        return deviceFeignService.findAllOrganEmpowerByUserId(userId);
    }

    @PostMapping("findAccountByRsaSerialNo")
    @Operation(summary = "根据RSA商户证书号查询租户账户信息")
    
    public ResponseResult<AccountDto> findAccountByRsaSerialNo(@RequestParam String rsaSerialNo) {
        return tenantManageService.findAccountByRsaSerialNo(rsaSerialNo);
    }

}
