package com.sunmax.configure.service;

import com.sunmax.common.dto.system.SiteOperateDto;

import java.util.List;
import java.util.Map;

/**
 * @Author: yqz
 * @Date: 2023/9/2616:36
 * @version: 1.0
 * @注释:
 */
public interface CityDataAccessService {

    /**
     * 查询充电站信息
     *
     * @param lastQueryTime 上次查询时间
     * @param pageNo        查询页码
     * @param pageSize      每页数量
     * @param siteOperateList    数据转发配置中站点和运营商id列表
     * @return
     */
    Map<String, Object> queryStationsInfo(String lastQueryTime, Integer pageNo, Integer pageSize, List<SiteOperateDto> siteOperateList);

    /**
     * 查询充电站接口状态
     * @param stationIdList
     * @return
     */
    Map<String, Object> queryStationStatus(List<String> stationIdList);

    /**
     * 查询充电状态
     *
     * @param startChargeSeq 充电订单号
     * @param siteIdList  数据转发配置站点id列表
     * @return
     */
    Map<String, Object> queryEquipChargeStatus(String startChargeSeq, List<String> siteIdList);

    /**
     * 查询统计信息
     * @param stationId
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String, Object> queryStationStats(String stationId, String startTime, String endTime);
}
