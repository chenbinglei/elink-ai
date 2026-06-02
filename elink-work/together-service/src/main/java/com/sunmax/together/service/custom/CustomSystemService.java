package com.sunmax.together.service.custom;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.custom.*;
import com.sunmax.together.vo.operation.storageCount.StorageCountVo;

import java.util.List;

/**
 * 定制系统业务逻辑接口
 */
public interface CustomSystemService {

    /**
     * 获取电站概览
     *
     * @param siteId 站点ID
     * @return 电站概览数据
     */
    ResponseResult<SiteOverviewDto> getSiteOverview(String siteId);

    /**
     * 获取电站VS交流系统
     *
     * @param siteId 站点ID
     * @param type   类型 (1-平均, 2-累计)
     * @return 电站交流系统数据
     */
    ResponseResult<SiteAcSystemDto> getSiteAcSystem(String siteId, Integer type);

    /**
     * 电站电量统计
     *
     * @param siteId 站点ID
     * @param type   类型 1-关口 2-直流母线 3-光伏 4-储能 5-负载
     * @param dateType 日期类型 1-日 2-月 3-年
     * @return 电量统计数据
     */
    ResponseResult<SiteQtCurveDto> getSiteQtCurve(String siteId, Integer type, Integer dateType);

    /**
     * 电站告警信息列表
     *
     * @param siteId 站点id
     * @return 告警信息列表
     */
    ResponseResult<List<SiteAlarmDto>> getSiteAlarmList(String siteId);

    /**
     * 获取光伏DC/DC设备数据
     *
     * @param deviceId 设备ID
     * @return 光伏DC/DC设备数据
     */
    ResponseResult<PvDcDcDeviceDto> getPvDcDcDeviceData(String deviceId);

    /**
     * 获取储能DC/DC设备数据
     *
     * @param seDcIds 多个储能DC/DC的id
     * @param batteryIds 多个电池蔟id
     * @return 储能DC/DC设备数据
     */
    ResponseResult<SeCabinetDeviceDto> getSeCabinetDeviceData(String seDcIds, String batteryIds);

    /**
     * 获取交流配电柜设备数据
     *
     * @param deviceId 设备ID
     * @return 交流配电柜设备数据
     */
    ResponseResult<AcGGDDeviceDto> getAcGGDDeviceData(String deviceId);

    /**
     * 获取直流配电柜设备数据
     *
     * @param deviceId 设备ID
     * @return 直流配电柜设备数据
     */
    ResponseResult<DCADDeviceDto> getDCADDeviceData(String deviceId);

    /**
     * 获取直流母线柜设备数据
     *
     * @param deviceId 设备ID
     * @return 直流母线柜设备数据
     */
    ResponseResult<DCBusDeviceDto> getDCBusDeviceData(String deviceId);

    /**
     * 获取直流注塑机设备数据
     *
     * @param deviceId 设备ID
     * @return 直流注塑机设备数据
     */
    ResponseResult<DCInjectorDeviceDto> getDCInjectorDeviceData(String deviceId);

    /**
     * 获取站点综合分析
     *
     * @param storageCountVo 存储计数VO (包含siteId等信息)
     * @return 站点综合分析数据
     */
    ResponseResult<SiteSystemNearbyDto> getSiteSystemNearby(StorageCountVo storageCountVo);
}
