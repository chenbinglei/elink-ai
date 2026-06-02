package com.sunmax.devops.controller;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.dto.*;
import com.sunmax.devops.service.MonitorService;
import com.sunmax.devops.vo.DeviceQueryVo;
import com.sunmax.devops.vo.OverviewQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("monitor")
@Api(tags = "监控App管理")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @PostMapping("getSiteMapBySiteId")
    @ApiOperation("根据站点id查询站点地图展示信息")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<SiteMapDto> getSiteMapBySiteId(String siteId) {
        return monitorService.getSiteMapBySiteId(siteId);
    }

    @PostMapping("querySiteList")
    @ApiOperation("查询电站列表")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams( {
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", required = true),
            @ApiImplicitParam(name = "siteName", value = "站点名称", dataType = "String")
    })
    public ResponseResult<PageDto<SiteListDto>> querySiteList(String userId, Integer page, Integer size, String siteName) {
        return monitorService.querySiteList(userId, page, size, siteName);
    }

    @PostMapping("queryDeviceList")
    @ApiOperation("查询设备列表")
    @ApiOperationSupport(order = 3)
    public ResponseResult<DeviceListDto> queryDeviceList(DeviceQueryVo deviceQueryVo) {
        return monitorService.queryDeviceList(deviceQueryVo);
    }

    @PostMapping("findSiteOverview")
    @ApiOperation("查询电站概况")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<SiteOverviewDto> findSiteOverview(String siteId) {
        return monitorService.findSiteOverview(siteId);
    }

    @PostMapping("findSiteGwDeviceList")
    @ApiOperation("查询电站关口设备列表")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<List<SiteGwDeviceDto>> findSiteGwDeviceList(String siteId) {
        return monitorService.findSiteGwDeviceList(siteId);
    }

    @PostMapping("findSiteGwStaticData")
    @ApiOperation("查询电站关口总览静态数据")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<SiteGwStaticDataDto> findSiteGwStaticData(String deviceId) {
        return monitorService.findSiteGwStaticData(deviceId);
    }

    @PostMapping("findSiteGwCurveData")
    @ApiOperation("查询电站关口总览曲线数据")
    @ApiOperationSupport(order = 7)
    public ResponseResult<SiteGwCurveDataDto> findSiteGwCurveData(OverviewQueryVo overviewQueryVo) {
        return monitorService.findSiteGwCurveData(overviewQueryVo);
    }

    @PostMapping("findSitePvStaticData")
    @ApiOperation("查询电站光伏概览静态数据")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<SitePvStaticDataDto> findSitePvStaticData(String siteId) {
        return monitorService.findSitePvStaticData(siteId);
    }

    @PostMapping("findSitePvCurveData")
    @ApiOperation("查询电站光伏概览曲线数据")
    @ApiOperationSupport(order = 9)
    public ResponseResult<SitePvCurveDataDto> findSitePvCurveData(OverviewQueryVo overviewQueryVo) {
        return monitorService.findSitePvCurveData(overviewQueryVo);
    }

    @PostMapping("findSiteSeStaticData")
    @ApiOperation("查询电站储能概览静态数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<SiteSeStaticDataDto> findSiteSeStaticData(String siteId) {
        return monitorService.findSiteSeStaticData(siteId);
    }

    @PostMapping("findSiteSeCurveData")
    @ApiOperation("查询电站储能概览曲线数据")
    @ApiOperationSupport(order = 11)
    public ResponseResult<SiteSeCurveDataDto> findSiteSeCurveData(OverviewQueryVo overviewQueryVo) {
        return monitorService.findSiteSeCurveData(overviewQueryVo);
    }

    @PostMapping("findSitePileStaticData")
    @ApiOperation("查询电站电桩概览静态数据")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<SitePileStaticDataDto> findSitePileStaticData(String siteId) {
        return monitorService.findSitePileStaticData(siteId);
    }

    @PostMapping("findSitePileCurveData")
    @ApiOperation("查询电站电桩概览曲线数据")
    @ApiOperationSupport(order = 13)
    public ResponseResult<SitePileCurveDataDto> findSitePileCurveData(OverviewQueryVo overviewQueryVo) {
        return monitorService.findSitePileCurveData(overviewQueryVo);
    }

}
