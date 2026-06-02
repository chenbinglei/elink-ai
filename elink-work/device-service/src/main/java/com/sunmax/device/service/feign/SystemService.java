package com.sunmax.device.service.feign;

import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.OrganStructureTreeDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service")
@RestController
@RequestMapping("/system/feign/device")
public interface SystemService {

    @PostMapping("findUserInfoByIds")
    @ApiOperation("根据用户id查询用户信息")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds);

    @PostMapping("deleteAllOrganEmpowerBySiteId")
    @ApiOperation("根据站点id删除所有关联的资产授权数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<String> deleteAllOrganEmpowerBySiteId(@RequestParam String siteId);

    @PostMapping("findAllOrganEmpowerByUserId")
    @ApiOperation("根据用户id查询所有关联的资产授权数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId);

    @PostMapping("addOrganEmpowerInfo")
    @ApiOperation("添加资产授权信息")
    @ApiOperationSupport(order = 4)
    ResponseResult<String> addOrganEmpowerInfo(@RequestParam String siteIds, @RequestParam Integer authority, @RequestParam String organId, @RequestParam String tenantId);

    @PostMapping("findOrganStructureByTenantId")
    @ApiOperation("根据租户id查询租户下组织架构信息")
    @ApiOperationSupport(order = 5)
    ResponseResult<List<OrganStructureTreeDto>> findOrganStructureByTenantId(@RequestParam String tenantId);

    @PostMapping("findTenantDetailsByIds")
    @ApiOperation("根据多个租户id查询租户信息")
    @ApiOperationSupport(order = 6)
    ResponseResult<List<TenantDetailsDto>> findTenantDetailsByIds(@RequestBody List<String> tenantIds);

    @PostMapping("findOrganEmpowerListByTenantId")
    @ApiOperation("根据租户id查询资产授权列表信息")
    @ApiOperationSupport(order = 7)
    ResponseResult<List<OrganEmpowerListDto>> findOrganEmpowerListByTenantId(@RequestParam String tenantId);

    @PostMapping("deleteOrganEmpowerByTenantId")
    @ApiOperation("根据租户id删除租户下所有指定站点资产授权信息")
    @ApiOperationSupport(order = 8)
    ResponseResult<String> deleteOrganEmpowerByTenantId(@RequestParam String tenantId, @RequestParam String siteId);

    @PostMapping("updateOrganEmpowerByTenantId")
    @ApiOperation("根据租户id修改租户下所有指定站点资产授权权限信息")
    @ApiOperationSupport(order = 9)
    ResponseResult<String> updateOrganEmpowerByTenantId(@RequestParam String tenantId, @RequestParam String siteId, @RequestParam Integer authority);
}
