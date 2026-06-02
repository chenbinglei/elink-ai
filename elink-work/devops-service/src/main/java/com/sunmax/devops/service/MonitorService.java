package com.sunmax.devops.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.dto.*;
import com.sunmax.devops.vo.DeviceQueryVo;
import com.sunmax.devops.vo.OverviewQueryVo;

import java.util.List;

public interface MonitorService {

    /**
     * 根据站点id查询站点地图展示信息
     * @param siteId 站点id
     * @return 站点地图数据
     */
    ResponseResult<SiteMapDto> getSiteMapBySiteId(String siteId);

    /**
     * 根据站点id查询站点列表展示信息
     * @param userId 用户id
     * @param page 当前页
     * @param size 当前页条数
     * @param siteName 站点名称
     * @return 站点列表数据
     */
    ResponseResult<PageDto<SiteListDto>> querySiteList(String userId, Integer page, Integer size, String siteName);

    /**
     * 根据查询条件查询设备列表
     * @param deviceQueryVo 设备查询参数
     * @return 设备列表数据
     */
    ResponseResult<DeviceListDto> queryDeviceList(DeviceQueryVo deviceQueryVo);

    /**
     * 根据站点id查询站点概览信息
     * @param siteId 站点id
     * @return 站点概览数据
     */
    ResponseResult<SiteOverviewDto> findSiteOverview(String siteId);

    /**
     * 根据站点id查询站点关口设备列表
     * @param siteId 站点id
     * @return 关口设备列表数据
     */
    ResponseResult<List<SiteGwDeviceDto>> findSiteGwDeviceList(String siteId);

    /**
     * 根据设备id查询站点关口总览静态数据
     * @param deviceId 设备id
     * @return 关口总览静态数据
     */
    ResponseResult<SiteGwStaticDataDto> findSiteGwStaticData(String deviceId);

    /**
     * 根据设备id查询站点关口总览曲线数据
     * @param overviewQueryVo 关口总览曲线查询参数
     * @return 关口总览曲线数据
     */
    ResponseResult<SiteGwCurveDataDto> findSiteGwCurveData(OverviewQueryVo overviewQueryVo);

    /**
     * 根据站点id查询站点光伏概览静态数据
     * @param siteId 站点id
     * @return 光伏概览静态数据
     */
    ResponseResult<SitePvStaticDataDto> findSitePvStaticData(String siteId);

    /**
     * 根据站点id查询站点光伏概览曲线数据
     * @param overviewQueryVo 光伏概览曲线查询参数
     * @return 光伏概览曲线数据
     */
    ResponseResult<SitePvCurveDataDto> findSitePvCurveData(OverviewQueryVo overviewQueryVo);

    /**
     * 根据站点id查询站点储能概览静态数据
     * @param siteId 站点id
     * @return 储能概览静态数据
     */
    ResponseResult<SiteSeStaticDataDto> findSiteSeStaticData(String siteId);

    /**
     * 根据站点id查询站点储能概览曲线数据
     * @param overviewQueryVo 储能概览曲线查询参数
     * @return 储能概览曲线数据
     */
    ResponseResult<SiteSeCurveDataDto> findSiteSeCurveData(OverviewQueryVo overviewQueryVo);

    /**
     * 根据站点id查询站点电桩概览静态数据
     * @param siteId 站点id
     * @return 电桩概览静态数据
     */
    ResponseResult<SitePileStaticDataDto> findSitePileStaticData(String siteId);

    /**
     * 根据站点id查询站点电桩概览曲线数据
     * @param overviewQueryVo 电桩概览曲线查询参数
     * @return 电桩概览曲线数据
     */
    ResponseResult<SitePileCurveDataDto> findSitePileCurveData(OverviewQueryVo overviewQueryVo);

}
