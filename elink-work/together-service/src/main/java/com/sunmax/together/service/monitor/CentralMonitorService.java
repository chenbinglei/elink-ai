package com.sunmax.together.service.monitor;


import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.WeatherDayDto;
import com.sunmax.common.dto.device.SiteTopDataDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.centralMonitor.*;

import java.util.List;
import java.util.Map;

public interface CentralMonitorService {

    /**
     * 场站状态总计
     *
     * @param userId 用户id
     * @return 场站状态总计
     */
    ResponseResult<Map<String, Integer>> statusTotal(String userId);

    /**
     * 场站统计
     *
     * @param word   关键字
     * @param area   区域名称
     * @param userId 用户id
     * @param page   当前页
     * @param size   当前页条数
     * @return 场站数据
     */
    ResponseResult<PageDto<SiteStatisticsDto>> statistics(String word, String area, String userId, Integer page, Integer size);

    /**
     * 光伏场站分页
     *
     * @param word   关键字
     * @param area   区域名称
     * @param userId 用户id
     * @param page   当前页
     * @param size   当前页条数
     * @return 场站数据
     */
    ResponseResult<PageDto<PhotovoltaicDto>> photovoltaicPage(String word, String area, String userId, Integer page, Integer size);

    /**
     * 储能场站分页
     *
     * @param word   关键字
     * @param area   区域名称
     * @param userId 用户id
     * @param page   当前页
     * @param size   当前页条数
     * @return 储能系统数据
     */
    ResponseResult<PageDto<EnergyStorageDto>> energyStoragePage(String word, String area, String userId, Integer page, Integer size);

    /**
     * 充电系统分页
     *
     * @param word   关键字
     * @param area   区域
     * @param userId 用户id
     * @param page   当前页
     * @param size   当前页条数
     * @return 充电系统分页数据
     */
    ResponseResult<PageDto<BatterySupplyDto>> batterySupplyPage(String word, String area, String userId, Integer page, Integer size);

    /**
     * 充电系统分页
     *
     * @param word   关键字
     * @param area   区域
     * @param userId 用户id
     * @param page   当前页
     * @param size   当前页条数
     * @return 充电系统分页数据
     */
    ResponseResult<PageDto<BatteryChangeDto>> batteryChangePage(String word, String area, String userId, Integer page, Integer size);

    /**
     * 设备概览
     *
     * @param siteId 场站id
     * @param type   类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
     * @return 设备概览数据
     */
    ResponseResult<List<DeviceOverviewDto>> getDeviceList(String siteId, Integer type);

    /**
     * 根据站点id查询拓扑节点数据
     *
     * @param siteId 场站id
     * @return 拓扑节点数据
     */
    ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(String siteId);

    /**
     * 根据站点id查询关口表数据
     *
     * @param siteId 场站id
     * @return 关口表数据
     */
    ResponseResult<List<SiteGateTopDto>> findSiteGateTopBySiteId(String siteId);

    /**
     * 根据站点id和节点id查询拓扑曲线数据
     * @param siteId 站点id
     * @param nodeId 节点id
     * @param queryDate 查询日期(yyyy-MM-dd)
     * @return 拓扑曲线数据
     */
    ResponseResult<SiteTopCurveDto> findSiteTopCurveList(String siteId, String nodeId, String queryDate);

    /**
     * 根据站点id查询天气数据
     * @param siteId 场站id
     * @return 天气数据
     */
    ResponseResult<List<WeatherDayDto>> getWeatherDayListBySiteId(String siteId);

}
