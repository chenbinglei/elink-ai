package com.sunmax.devops.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.dto.*;
import com.sunmax.devops.service.HomePageService;
import com.sunmax.devops.vo.EnergyQueryVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("homepage")
@Tag(name = "首页管理控制层")
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @PostMapping("getTenantSiteList")
    @Operation(summary = "根据用户id查询租户站点列表")
    
    @Parameter(name = "userId", description = "用户id")
    public ResponseResult<List<TenantSiteDto>> getTenantSiteList(String userId) {
        return homePageService.getTenantSiteList(userId);
    }

    @PostMapping("getDeviceCap")
    @Operation(summary = "根据多个场站id查询设备容量")
    
    @Parameter(name = "siteIds", description = "多个站点id 例如['siteId1','siteId2']")
    public ResponseResult<DeviceCapDto> getDeviceCap(String siteIds) {
        return homePageService.getDeviceCap(siteIds);
    }

    @PostMapping("findAllEnergyPile")
    @Operation(summary = "查询电能趋势-电桩数据")
    
    public ResponseResult<EnergyPileDto> findAllEnergyPile(EnergyQueryVo energyQueryVo) {
        return homePageService.findAllEnergyPile(energyQueryVo);
    }

    @PostMapping("findAllEnergyStorage")
    @Operation(summary = "查询电能趋势-储能数据")
    
    public ResponseResult<EnergyStorageDto> findAllEnergyStorage(EnergyQueryVo energyQueryVo) {
        return homePageService.findAllEnergyStorage(energyQueryVo);
    }

    @PostMapping("findAllEnergyPv")
    @Operation(summary = "查询电能趋势-光伏数据")
    
    public ResponseResult<EnergyPvDto> findAllEnergyPv(EnergyQueryVo energyQueryVo) {
        return homePageService.findAllEnergyPv(energyQueryVo);
    }

}
