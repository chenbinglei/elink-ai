package com.sunmax.together.controller.monitor;

import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.assetOverview.PileFunCurveDto;
import com.sunmax.together.dto.monitor.assetOverview.AssetSiteListDto;
import com.sunmax.together.dto.monitor.assetOverview.ChargeAssetCountDto;
import com.sunmax.together.dto.monitor.assetOverview.PvAssetCountDto;
import com.sunmax.together.dto.monitor.assetOverview.StorageAssetCountDto;
import com.sunmax.together.service.monitor.AssetOverviewService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("assetOverview")
@Api(tags = "资产总览管理")
public class AssetOverviewController {

    @Autowired
    private AssetOverviewService assetOverviewService;

    @PostMapping("findAssetSiteList")
    @ApiOperation("查询资产站点列表-(停止使用)")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前登录用户id", paramType = "query", required = true),
            @ApiImplicitParam(name = "scenarioType", value = "场景类型 1-光伏 2-储能 3-充电桩（不传默认全部）", paramType = "query"),
            @ApiImplicitParam(name = "areaType", value = "区域类型 1-省级 2-市级（不传默认全部）", paramType = "query"),
            @ApiImplicitParam(name = "areaName", value = "区域名称", paramType = "query"),
            @ApiImplicitParam(name = "siteName", value = "站点名称(用于模糊查询)", paramType = "query")
    })
    public ResponseResult<List<AssetSiteListDto>> findAssetSiteList(String userId, Integer scenarioType, Integer areaType, String areaName, String siteName) {
        return assetOverviewService.findAssetSiteList(userId, scenarioType, areaType, areaName, siteName);
    }

    @PostMapping("findPvAssetCountData")
    @ApiOperation("查询光伏资产统计数据")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true)
    })
    public ResponseResult<PvAssetCountDto> findPvAssetCountData(String siteIds) {
        return assetOverviewService.findPvAssetCountData(siteIds);
    }

    @PostMapping("findPvGenerationCurveData")
    @ApiOperation("查询光伏发电量分析曲线数据-(停止使用)")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前登录用户id", paramType = "query", required = true),
            @ApiImplicitParam(name = "siteId", value = "站点id（不传默认查询全部光伏站点）", paramType = "query")
    })
    public ResponseResult<Map<String, Object>> findPvGenerationCurveData(String userId, String siteId) {
        return assetOverviewService.findPvGenerationCurveData(userId, siteId);
    }

    @PostMapping("findStorageAssetCountData")
    @ApiOperation("查询储能资产统计数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true)
    })
    public ResponseResult<StorageAssetCountDto> findStorageAssetCountData(String siteIds) {
        return assetOverviewService.findStorageAssetCountData(siteIds);
    }

    @PostMapping("findStorageQtCurveData")
    @ApiOperation("查询储能充放电量分析曲线数据-(停止使用)")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前登录用户id", paramType = "query", required = true),
            @ApiImplicitParam(name = "siteId", value = "站点id（不传默认查询全部储能站点）", paramType = "query")
    })
    public ResponseResult<Map<String, Object>> findStorageQtCurveData(String userId, String siteId) {
        return assetOverviewService.findStorageQtCurveData(userId, siteId);
    }

    @PostMapping("findChargeAssetCountData")
    @ApiOperation("查询充放电资产统计数据")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true)
    })
    public ResponseResult<ChargeAssetCountDto> findChargeAssetCountData(String siteIds) {
        return assetOverviewService.findChargeAssetCountData(siteIds);
    }

    @PostMapping("findChargePowerCurveData")
    @ApiOperation("查询充放电功率曲线数据-(停止使用)")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前登录用户id", paramType = "query", required = true),
            @ApiImplicitParam(name = "siteId", value = "站点id（不传默认查询全部充放电站点）", paramType = "query")
    })
    public ResponseResult<Map<String, Object>> findChargePowerCurveData(String userId, String siteId) {
        return assetOverviewService.findChargePowerCurveData(userId, siteId);
    }

    @PostMapping("querySiteNum")
    @ApiOperation("查询站点数量")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前登录用户id", paramType = "query", required = true),
            @ApiImplicitParam(name = "scenarioType", value = "场景类型 1-光伏 2-储能 3-充电桩（不传默认全部）", paramType = "query"),
            @ApiImplicitParam(name = "areaType", value = "区域类型 1-省级 2-市级（不传默认全部）", paramType = "query"),
            @ApiImplicitParam(name = "areaName", value = "区域名称", paramType = "query")
    })
    public ResponseResult<List<Map<String, Object>>> querySiteNum(String userId, Integer scenarioType, Integer areaType, String areaName) {
        return assetOverviewService.querySiteNum(userId, scenarioType, areaType, areaName);
    }

    @PostMapping("querySystemDeviceList")
    @ApiOperation("查询系统设备列表")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId) {
        return assetOverviewService.querySystemDeviceList(siteId);
    }

    @PostMapping("queryDeviceFunRealData")
    @ApiOperation("查询设备功能点实时数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", paramType = "query", required = true),
            @ApiImplicitParam(name = "functionLogos", value = "多个功能点标识(以逗号分割)", paramType = "query", required = true)
    })
    public ResponseResult<Map<String, RealDataModel>> queryDeviceFunRealData(String deviceId, String functionLogos) {
        return assetOverviewService.queryDeviceFunRealData(deviceId, functionLogos);
    }

    @PostMapping("queryDeviceFunCurveData")
    @ApiOperation("查询储能、光伏、配电设备功能点曲线数据数据")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", paramType = "query", required = true),
            @ApiImplicitParam(name = "functionLogo", value = "功能点标识", paramType = "query", required = true)
    })
    public ResponseResult<Map<String, Object>> queryDeviceFunCurveData(String deviceId, String functionLogo) {
        return assetOverviewService.queryDeviceFunCurveData(deviceId, functionLogo);
    }

    @PostMapping("queryPileFunCurveData")
    @ApiOperation("查询电桩设备功能点曲线数据数据")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", paramType = "query", required = true),
            @ApiImplicitParam(name = "functionLogo", value = "功能点标识", paramType = "query", required = true)
    })
    public ResponseResult<PileFunCurveDto> queryPileFunCurveData(String deviceId, String functionLogo) {
        return assetOverviewService.queryPileFunCurveData(deviceId, functionLogo);
    }

    @PostMapping("findStorageOverviewPowerCurve")
    @ApiOperation("查询储能站点充放电功率曲线数据")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true)
    })
    public ResponseResult<PileFunCurveDto> findStorageOverviewPowerCurve(String siteIds, String startTime, String endTime) {
        return assetOverviewService.findStorageOverviewPowerCurve(siteIds, startTime, endTime);
    }
}
