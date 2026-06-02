package com.sunmax.together.service.monitor;

import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.assetOverview.PileFunCurveDto;
import com.sunmax.together.dto.monitor.assetOverview.AssetSiteListDto;
import com.sunmax.together.dto.monitor.assetOverview.ChargeAssetCountDto;
import com.sunmax.together.dto.monitor.assetOverview.PvAssetCountDto;
import com.sunmax.together.dto.monitor.assetOverview.StorageAssetCountDto;

import java.util.List;
import java.util.Map;

public interface AssetOverviewService {

    /**
     * 查询资产站点列表
     *
     * @param userId       当前登录用户id
     * @param scenarioType 场景类型 1-光伏 2-储能 3-充电桩（不传默认全部）
     * @param areaType     区域类型 1-省级 2-市级（不传默认全部）
     * @param areaName     区域名称
     * @param siteName     站点名称(用于模糊查询)
     * @return
     */
    ResponseResult<List<AssetSiteListDto>> findAssetSiteList(String userId, Integer scenarioType, Integer areaType, String areaName, String siteName);

    /**
     * 查询光伏资产统计数据
     * @param siteIds 多个站点id
     * @return
     */
    ResponseResult<PvAssetCountDto> findPvAssetCountData(String siteIds);

    /**
     * 查询光伏发电量分析曲线数据
     * @param userId 当前登录用户id
     * @param siteId 站点id（不传默认查询全部光伏站点）
     * @return
     */
    ResponseResult<Map<String, Object>> findPvGenerationCurveData(String userId, String siteId);

    /**
     * 查询储能资产统计数据
     * @param siteIds 多个站点id
     * @return
     */
    ResponseResult<StorageAssetCountDto> findStorageAssetCountData(String siteIds);

    /**
     * 查询储能充放电量分析曲线数据
     * @param userId 当前登录用户id
     * @param siteId 站点id（不传默认查询全部储能站点）
     * @return
     */
    ResponseResult<Map<String, Object>> findStorageQtCurveData(String userId, String siteId);

    /**
     * 查询充放电资产统计数据
     * @param siteIds 多个站点id
     * @return
     */
    ResponseResult<ChargeAssetCountDto> findChargeAssetCountData(String siteIds);

    /**
     * 查询充放电功率曲线数据
     * @param userId 当前登录用户id
     * @param siteId 站点id（不传默认查询全部充放电站点）
     * @return
     */
    ResponseResult<Map<String, Object>> findChargePowerCurveData(String userId, String siteId);

    /**
     * 查询站点数量
     * @param userId 当前登录用户id
     * @param scenarioType 场景类型 1-光伏 2-储能 3-充电桩（不传默认全部）
     * @param areaType 区域类型 1-省级 2-市级（不传默认全部）
     * @param areaName 区域名称
     * @return
     */
    ResponseResult<List<Map<String, Object>>> querySiteNum(String userId, Integer scenarioType, Integer areaType, String areaName);

    /**
     * 查询系统设备列表
     * @param siteId
     * @return
     */
    ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId);

    /**
     * 查询储能、光伏、配电设备功能点实时数据
     * @param deviceId 设备id
     * @param functionLogos 多个功能点标识(以逗号分割)
     * @return 功能点标识 -> 功能点数据
     */
    ResponseResult<Map<String, RealDataModel>> queryDeviceFunRealData(String deviceId, String functionLogos);

    /**
     * 查询储能、光伏、配电设备功能点曲线数据数据
     * @param deviceId 设备id
     * @param functionLogo 功能点标识
     * @return "dataList" -> 数据列表
     *         "xAXisList" -> 时间轴
     */
    ResponseResult<Map<String, Object>> queryDeviceFunCurveData(String deviceId, String functionLogo);

    /**
     * 查询电桩设备功能点曲线数据数据
     * @param deviceId 设备id
     * @param functionLogo 功能点标识
     * @return "dataList" -> 数据列表
     *         "xAXisList" -> 时间轴
     */
    ResponseResult<PileFunCurveDto> queryPileFunCurveData(String deviceId, String functionLogo);

    /**
     * 查询储能资产总览站点功率曲线数据
     * @param siteIds   多个站点id
     * @param startTime 开始时间(yyyy-MM-dd HH:mm:ss)
     * @param endTime   结束时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    ResponseResult<PileFunCurveDto> findStorageOverviewPowerCurve(String siteIds, String startTime, String endTime);
}
