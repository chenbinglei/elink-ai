package com.sunmax.together.controller.monitor;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.assetOverview.PileFunCurveDto;
import com.sunmax.together.dto.monitor.assetOverview.AssetSiteListDto;
import com.sunmax.together.dto.monitor.assetOverview.ChargeAssetCountDto;
import com.sunmax.together.dto.monitor.assetOverview.PvAssetCountDto;
import com.sunmax.together.dto.monitor.assetOverview.StorageAssetCountDto;
import com.sunmax.together.service.monitor.AssetOverviewService;

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
@Tag(name = "资产总览管理")
public class AssetOverviewController {

    @Autowired
    private AssetOverviewService assetOverviewService;

    @PostMapping("findAssetSiteList")
    @Operation(summary = "查询资产站点列表-(停止使用)")
    
    @Parameters({
            @Parameter(name = "userId", description = "当前登录用户id"),
            @Parameter(name = "scenarioType", description = "场景类型 1-光伏 2-储能 3-充电桩（不传默认全部）"),
            @Parameter(name = "areaType", description = "区域类型 1-省级 2-市级（不传默认全部）"),
            @Parameter(name = "areaName", description = "区域名称"),
            @Parameter(name = "siteName", description = "站点名称(用于模糊查询)")
    })
    public ResponseResult<List<AssetSiteListDto>> findAssetSiteList(String userId, Integer scenarioType, Integer areaType, String areaName, String siteName) {
        return assetOverviewService.findAssetSiteList(userId, scenarioType, areaType, areaName, siteName);
    }

    @PostMapping("findPvAssetCountData")
    @Operation(summary = "查询光伏资产统计数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id")
    })
    public ResponseResult<PvAssetCountDto> findPvAssetCountData(String siteIds) {
        return assetOverviewService.findPvAssetCountData(siteIds);
    }

    @PostMapping("findPvGenerationCurveData")
    @Operation(summary = "查询光伏发电量分析曲线数据-(停止使用)")
    
    @Parameters({
            @Parameter(name = "userId", description = "当前登录用户id"),
            @Parameter(name = "siteId", description = "站点id（不传默认查询全部光伏站点）")
    })
    public ResponseResult<Map<String, Object>> findPvGenerationCurveData(String userId, String siteId) {
        return assetOverviewService.findPvGenerationCurveData(userId, siteId);
    }

    @PostMapping("findStorageAssetCountData")
    @Operation(summary = "查询储能资产统计数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id")
    })
    public ResponseResult<StorageAssetCountDto> findStorageAssetCountData(String siteIds) {
        return assetOverviewService.findStorageAssetCountData(siteIds);
    }

    @PostMapping("findStorageQtCurveData")
    @Operation(summary = "查询储能充放电量分析曲线数据-(停止使用)")
    
    @Parameters({
            @Parameter(name = "userId", description = "当前登录用户id"),
            @Parameter(name = "siteId", description = "站点id（不传默认查询全部储能站点）")
    })
    public ResponseResult<Map<String, Object>> findStorageQtCurveData(String userId, String siteId) {
        return assetOverviewService.findStorageQtCurveData(userId, siteId);
    }

    @PostMapping("findChargeAssetCountData")
    @Operation(summary = "查询充放电资产统计数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id")
    })
    public ResponseResult<ChargeAssetCountDto> findChargeAssetCountData(String siteIds) {
        return assetOverviewService.findChargeAssetCountData(siteIds);
    }

    @PostMapping("findChargePowerCurveData")
    @Operation(summary = "查询充放电功率曲线数据-(停止使用)")
    
    @Parameters({
            @Parameter(name = "userId", description = "当前登录用户id"),
            @Parameter(name = "siteId", description = "站点id（不传默认查询全部充放电站点）")
    })
    public ResponseResult<Map<String, Object>> findChargePowerCurveData(String userId, String siteId) {
        return assetOverviewService.findChargePowerCurveData(userId, siteId);
    }

    @PostMapping("querySiteNum")
    @Operation(summary = "查询站点数量")
    
    @Parameters({
            @Parameter(name = "userId", description = "当前登录用户id"),
            @Parameter(name = "scenarioType", description = "场景类型 1-光伏 2-储能 3-充电桩（不传默认全部）"),
            @Parameter(name = "areaType", description = "区域类型 1-省级 2-市级（不传默认全部）"),
            @Parameter(name = "areaName", description = "区域名称")
    })
    public ResponseResult<List<Map<String, Object>>> querySiteNum(String userId, Integer scenarioType, Integer areaType, String areaName) {
        return assetOverviewService.querySiteNum(userId, scenarioType, areaType, areaName);
    }

    @PostMapping("querySystemDeviceList")
    @Operation(summary = "查询系统设备列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId) {
        return assetOverviewService.querySystemDeviceList(siteId);
    }

    @PostMapping("queryDeviceFunRealData")
    @Operation(summary = "查询设备功能点实时数据")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "functionLogos", description = "多个功能点标识(以逗号分割)")
    })
    public ResponseResult<Map<String, RealDataModel>> queryDeviceFunRealData(String deviceId, String functionLogos) {
        return assetOverviewService.queryDeviceFunRealData(deviceId, functionLogos);
    }

    @PostMapping("queryDeviceFunCurveData")
    @Operation(summary = "查询储能、光伏、配电设备功能点曲线数据数据")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "functionLogo", description = "功能点标识")
    })
    public ResponseResult<Map<String, Object>> queryDeviceFunCurveData(String deviceId, String functionLogo) {
        return assetOverviewService.queryDeviceFunCurveData(deviceId, functionLogo);
    }

    @PostMapping("queryPileFunCurveData")
    @Operation(summary = "查询电桩设备功能点曲线数据数据")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "functionLogo", description = "功能点标识")
    })
    public ResponseResult<PileFunCurveDto> queryPileFunCurveData(String deviceId, String functionLogo) {
        return assetOverviewService.queryPileFunCurveData(deviceId, functionLogo);
    }

    @PostMapping("findStorageOverviewPowerCurve")
    @Operation(summary = "查询储能站点充放电功率曲线数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "startTime", description = "开始时间(yyyy-MM-dd HH:mm:ss)"),
            @Parameter(name = "endTime", description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    })
    public ResponseResult<PileFunCurveDto> findStorageOverviewPowerCurve(String siteIds, String startTime, String endTime) {
        return assetOverviewService.findStorageOverviewPowerCurve(siteIds, startTime, endTime);
    }
}
