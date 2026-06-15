package com.sunmax.devops.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.dto.*;
import com.sunmax.devops.service.MonitorService;
import com.sunmax.devops.vo.DeviceQueryVo;
import com.sunmax.devops.vo.OverviewQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("monitor")
@Tag(name = "监控App管理")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @PostMapping("getSiteMapBySiteId")
    @Operation(summary = "根据站点id查询站点地图展示信息")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SiteMapDto> getSiteMapBySiteId(String siteId) {
        return monitorService.getSiteMapBySiteId(siteId);
    }

    @PostMapping("querySiteList")
    @Operation(summary = "查询电站列表")
    
    @Parameters( {
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "每页条数"),
            @Parameter(name = "siteName", description = "站点名称")
    })
    public ResponseResult<PageDto<SiteListDto>> querySiteList(String userId, Integer page, Integer size, String siteName) {
        return monitorService.querySiteList(userId, page, size, siteName);
    }

    @PostMapping("queryDeviceList")
    @Operation(summary = "查询设备列表")
    
    public ResponseResult<DeviceListDto> queryDeviceList(DeviceQueryVo deviceQueryVo) {
        return monitorService.queryDeviceList(deviceQueryVo);
    }

    @PostMapping("findSiteOverview")
    @Operation(summary = "查询电站概况")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SiteOverviewDto> findSiteOverview(String siteId) {
        return monitorService.findSiteOverview(siteId);
    }

    @PostMapping("findSiteGwDeviceList")
    @Operation(summary = "查询电站关口设备列表")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<SiteGwDeviceDto>> findSiteGwDeviceList(String siteId) {
        return monitorService.findSiteGwDeviceList(siteId);
    }

    @PostMapping("findSiteGwStaticData")
    @Operation(summary = "查询电站关口总览静态数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<SiteGwStaticDataDto> findSiteGwStaticData(String deviceId) {
        return monitorService.findSiteGwStaticData(deviceId);
    }

    @PostMapping("findSiteGwCurveData")
    @Operation(summary = "查询电站关口总览曲线数据")
    
    public ResponseResult<SiteGwCurveDataDto> findSiteGwCurveData(OverviewQueryVo overviewQueryVo) {
        return monitorService.findSiteGwCurveData(overviewQueryVo);
    }

    @PostMapping("findSitePvStaticData")
    @Operation(summary = "查询电站光伏概览静态数据")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SitePvStaticDataDto> findSitePvStaticData(String siteId) {
        return monitorService.findSitePvStaticData(siteId);
    }

    @PostMapping("findSitePvCurveData")
    @Operation(summary = "查询电站光伏概览曲线数据")
    
    public ResponseResult<SitePvCurveDataDto> findSitePvCurveData(OverviewQueryVo overviewQueryVo) {
        return monitorService.findSitePvCurveData(overviewQueryVo);
    }

    @PostMapping("findSiteSeStaticData")
    @Operation(summary = "查询电站储能概览静态数据")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SiteSeStaticDataDto> findSiteSeStaticData(String siteId) {
        return monitorService.findSiteSeStaticData(siteId);
    }

    @PostMapping("findSiteSeCurveData")
    @Operation(summary = "查询电站储能概览曲线数据")
    
    public ResponseResult<SiteSeCurveDataDto> findSiteSeCurveData(OverviewQueryVo overviewQueryVo) {
        return monitorService.findSiteSeCurveData(overviewQueryVo);
    }

    @PostMapping("findSitePileStaticData")
    @Operation(summary = "查询电站电桩概览静态数据")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SitePileStaticDataDto> findSitePileStaticData(String siteId) {
        return monitorService.findSitePileStaticData(siteId);
    }

    @PostMapping("findSitePileCurveData")
    @Operation(summary = "查询电站电桩概览曲线数据")
    
    public ResponseResult<SitePileCurveDataDto> findSitePileCurveData(OverviewQueryVo overviewQueryVo) {
        return monitorService.findSitePileCurveData(overviewQueryVo);
    }

}
