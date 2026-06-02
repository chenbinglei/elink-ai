package com.sunmax.devops.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.dto.*;
import com.sunmax.devops.vo.EnergyQueryVo;

import java.util.List;

public interface HomePageService {

    /**
     * 根据用户id查询租户站点列表
     * @param userId 用户id
     * @return 租户站点列表
     */
    ResponseResult<List<TenantSiteDto>> getTenantSiteList(String userId);

    /**
     * 根据多个场站id查询设备容量
     * @param siteIds 多个站点id
     * @return 设备能力
     */
    ResponseResult<DeviceCapDto> getDeviceCap(String siteIds);

    /**
     * 查询电能趋势-电桩数据
     * @param energyQueryVo 能源电桩查询参数
     * @return 电能趋势-电桩数据列表
     */
    ResponseResult<EnergyPileDto> findAllEnergyPile(EnergyQueryVo energyQueryVo);

    /**
     * 查询电能趋势-储能数据
     * @param energyQueryVo 能源储能查询参数
     * @return 电能趋势-储能数据列表
     */
    ResponseResult<EnergyStorageDto> findAllEnergyStorage(EnergyQueryVo energyQueryVo);

    /**
     * 查询电能趋势-光伏数据
     * @param energyQueryVo 能源光伏查询参数
     * @return 电能趋势-光伏数据列表
     */
    ResponseResult<EnergyPvDto> findAllEnergyPv(EnergyQueryVo energyQueryVo);

}
