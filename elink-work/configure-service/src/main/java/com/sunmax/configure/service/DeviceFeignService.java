package com.sunmax.configure.service;

import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.dto.device.SiteDeviceTreeDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.FunctionListDto;
import com.sunmax.configure.dto.SiteListDto;

import java.util.List;
import java.util.Set;

public interface DeviceFeignService {

    /**
     * 充电站信息变化推送
     * @param siteIds 多个站点id
     * @return 状态码
     */
    ResponseResult<Void> notificationStationInfo(Set<String> siteIds);

    /**
     * 查询系统设备列表
     * @param siteId
     * @return
     */
    ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId);

    /**
     * 根据站点/设备id查询计算节点列表
     * @param deviceId 站点/设备id
     * @return 计算节点列表
     */
    ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(String deviceId);

    /**
     * 根据用户id查询站点列表
     * @param userId 用户id
     * @return 站点列表
     */
    ResponseResult<List<SiteListDto>> findSiteListByUserId(String userId);

    /**
     * 根据设备id查询功能点列表
     * @param deviceId 设备id
     * @return 功能点列表
     */
    ResponseResult<List<FunctionListDto>> findFunctionListByDeviceId(String deviceId);

    /**
     * 根据站点id查询设备树
     * @param siteId 站点id
     * @return 设备树
     */
    ResponseResult<List<SiteDeviceTreeDto>> findSiteDeviceListBySiteId(String siteId);

}
