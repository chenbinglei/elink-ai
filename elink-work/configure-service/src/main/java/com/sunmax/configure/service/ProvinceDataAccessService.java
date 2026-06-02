package com.sunmax.configure.service;

import com.sunmax.common.dto.system.OperatorInfoDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.configure.dto.province.StationInfoDto;

import java.util.List;
import java.util.Map;

/**
 * @Author: yqz
 * @Date: 2023/9/2111:40
 * @version: 1.0
 * @注释:
 */
public interface ProvinceDataAccessService {

    /**
     * 获取充电站信息
     * @param stationIds       充电站ID列表
     * @param siteOperateList 数据转发配置中的站点和运营商id列表
     * @return 站点数据
     */
    StationInfoDto getStationInfo(String stationIds, List<SiteOperateDto> siteOperateList);

    /**
     * 查询运营商信息
     *
     * @param pageNo           当前页
     * @param pageSize         当前页条数
     * @param operatorInfoList 数据转发配置中的运营商信息
     * @return 数据
     */
    Map<String, Object> queryOperatorInfo(Integer pageNo, Integer pageSize, List<OperatorInfoDto> operatorInfoList);

    /**
     * 根据时间查询站点及下面充电设备列表
     *
     * @param lastQueryTime   上次查询时间
     * @param pageNo          查询页码
     * @param pageSize        每页数量
     * @param stationIds      充电站ID列表
     * @param siteOperateList 数据转发配置中的站点和运营商id列表
     * @return
     */
    Map<String, Object> findStationInfoListByTime(String lastQueryTime, Integer pageNo, Integer pageSize, String stationIds, List<SiteOperateDto> siteOperateList);

    /**
     * 查询充电站接口状态
     * @param siteOperateList 数据转发配置站点和运营商id列表
     * @return
     */
    Map<String, Object> queryStationStatus(List<SiteOperateDto> siteOperateList);
}
