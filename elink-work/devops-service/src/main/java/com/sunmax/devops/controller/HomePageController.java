package com.sunmax.devops.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.dto.*;
import com.sunmax.devops.service.HomePageService;
import com.sunmax.devops.vo.EnergyQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("homepage")
@Api(tags = "首页管理控制层")
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @PostMapping("getTenantSiteList")
    @ApiOperation("根据用户id查询租户站点列表")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true)
    public ResponseResult<List<TenantSiteDto>> getTenantSiteList(String userId) {
        return homePageService.getTenantSiteList(userId);
    }

    @PostMapping("getDeviceCap")
    @ApiOperation("根据多个场站id查询设备容量")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "siteIds", value = "多个站点id 例如['siteId1','siteId2']", dataType = "String", required = true)
    public ResponseResult<DeviceCapDto> getDeviceCap(String siteIds) {
        return homePageService.getDeviceCap(siteIds);
    }

    @PostMapping("findAllEnergyPile")
    @ApiOperation("查询电能趋势-电桩数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<EnergyPileDto> findAllEnergyPile(EnergyQueryVo energyQueryVo) {
        return homePageService.findAllEnergyPile(energyQueryVo);
    }

    @PostMapping("findAllEnergyStorage")
    @ApiOperation("查询电能趋势-储能数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<EnergyStorageDto> findAllEnergyStorage(EnergyQueryVo energyQueryVo) {
        return homePageService.findAllEnergyStorage(energyQueryVo);
    }

    @PostMapping("findAllEnergyPv")
    @ApiOperation("查询电能趋势-光伏数据")
    @ApiOperationSupport(order = 5)
    public ResponseResult<EnergyPvDto> findAllEnergyPv(EnergyQueryVo energyQueryVo) {
        return homePageService.findAllEnergyPv(energyQueryVo);
    }

}
