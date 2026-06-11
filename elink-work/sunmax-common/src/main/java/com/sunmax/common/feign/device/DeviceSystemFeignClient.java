package com.sunmax.common.feign.device;

import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.OrganStructureTreeDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service", path = "/system/feign/device", fallbackFactory = GenericFeignFallbackFactory.class)
public interface DeviceSystemFeignClient {

    @PostMapping("findUserInfoByIds")
    @Operation(summary = "根据用户id查询用户信息")
    
    ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds);

    @PostMapping("deleteAllOrganEmpowerBySiteId")
    @Operation(summary = "根据站点id删除所有关联的资产授权数据")
    
    ResponseResult<String> deleteAllOrganEmpowerBySiteId(@RequestParam String siteId);

    @PostMapping("findAllOrganEmpowerByUserId")
    @Operation(summary = "根据用户id查询所有关联的资产授权数据")
    
    ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId);

    @PostMapping("addOrganEmpowerInfo")
    @Operation(summary = "添加资产授权信息")
    
    ResponseResult<String> addOrganEmpowerInfo(@RequestParam String siteIds, @RequestParam Integer authority, @RequestParam String organId, @RequestParam String tenantId);

    @PostMapping("findOrganStructureByTenantId")
    @Operation(summary = "根据租户id查询租户下组织架构信息")
    
    ResponseResult<List<OrganStructureTreeDto>> findOrganStructureByTenantId(@RequestParam String tenantId);

    @PostMapping("findTenantDetailsByIds")
    @Operation(summary = "根据多个租户id查询租户信息")
    
    ResponseResult<List<TenantDetailsDto>> findTenantDetailsByIds(@RequestBody List<String> tenantIds);

    @PostMapping("findOrganEmpowerListByTenantId")
    @Operation(summary = "根据租户id查询资产授权列表信息")
    
    ResponseResult<List<OrganEmpowerListDto>> findOrganEmpowerListByTenantId(@RequestParam String tenantId);

    @PostMapping("deleteOrganEmpowerByTenantId")
    @Operation(summary = "根据租户id删除租户下所有指定站点资产授权信息")
    
    ResponseResult<String> deleteOrganEmpowerByTenantId(@RequestParam String tenantId, @RequestParam String siteId);

    @PostMapping("updateOrganEmpowerByTenantId")
    @Operation(summary = "根据租户id修改租户下所有指定站点资产授权权限信息")
    
    ResponseResult<String> updateOrganEmpowerByTenantId(@RequestParam String tenantId, @RequestParam String siteId, @RequestParam Integer authority);
}
