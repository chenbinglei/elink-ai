package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.OrganStructureTreeDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.DeviceFeignService;
import com.sunmax.system.service.TenantManageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: yqz
 * @注释: 提供给设备管理服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/device")
@Api(tags = "提供给设备管理服务调用的远程接口")
@ApiIgnore()
public class DeviceFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;
    @Autowired
    private TenantManageService tenantManageService;

    @PostMapping("findUserInfoByIds")
    @ApiOperation("根据用户id查询用户信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds) {
        return deviceFeignService.findUserInfoByIdsFeign(userIds);
    }

    @PostMapping("deleteAllOrganEmpowerBySiteId")
    @ApiOperation("根据站点id删除所有关联的资产授权数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<String> deleteAllOrganEmpowerBySiteId(@RequestParam String siteId) {
        return deviceFeignService.deleteAllOrganEmpowerBySiteId(siteId);
    }

    @PostMapping("findAllOrganEmpowerByUserId")
    @ApiOperation("根据用户id查询所有关联的资产授权数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(@RequestParam String userId) {
        return deviceFeignService.findAllOrganEmpowerByUserId(userId);
    }

    @PostMapping("addOrganEmpowerInfo")
    @ApiOperation("添加资产授权信息")
    @ApiOperationSupport(order = 4)
    public ResponseResult<String> addOrganEmpowerInfo(@RequestParam String siteIds, @RequestParam Integer authority, @RequestParam String organId, @RequestParam String tenantId) {
        return tenantManageService.addOrganEmpowerInfo(Arrays.stream(siteIds.split(",")).map(String::trim).collect(Collectors.toList()), authority, organId, tenantId);
    }

    @PostMapping("findOrganStructureByTenantId")
    @ApiOperation("根据租户id查询租户下组织架构信息")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<OrganStructureTreeDto>> findOrganStructureByTenantId(@RequestParam String tenantId) {
        return tenantManageService.findOrganStructureByTenantId(tenantId);
    }

    @PostMapping("findTenantDetailsByIds")
    @ApiOperation("根据多个租户id查询租户信息")
    @ApiOperationSupport(order = 6)
    public ResponseResult<List<TenantDetailsDto>> findTenantDetailsByIds(@RequestBody List<String> tenantIds) {
        return tenantManageService.findAllTenantInfoByIds(tenantIds);
    }

    @PostMapping("findOrganEmpowerListByTenantId")
    @ApiOperation("根据租户id查询资产授权列表信息")
    @ApiOperationSupport(order = 7)
    public ResponseResult<List<OrganEmpowerListDto>> findOrganEmpowerListByTenantId(@RequestParam String tenantId) {
        return deviceFeignService.findOrganEmpowerListByTenantId(tenantId);
    }

    @PostMapping("deleteOrganEmpowerByTenantId")
    @ApiOperation("根据租户id删除租户下所有指定站点资产授权信息")
    @ApiOperationSupport(order = 8)
    public ResponseResult<String> deleteOrganEmpowerByTenantId(@RequestParam String tenantId, @RequestParam String siteId) {
        return deviceFeignService.deleteOrganEmpowerByTenantId(tenantId, siteId);
    }

    @PostMapping("updateOrganEmpowerByTenantId")
    @ApiOperation("根据租户id修改租户下所有指定站点资产授权权限信息")
    @ApiOperationSupport(order = 9)
    public ResponseResult<String> updateOrganEmpowerByTenantId(@RequestParam String tenantId, @RequestParam String siteId, @RequestParam Integer authority) {
        return deviceFeignService.updateOrganEmpowerByTenantId(tenantId, siteId, authority);
    }
}
