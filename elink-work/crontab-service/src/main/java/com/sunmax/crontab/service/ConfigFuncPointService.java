package com.sunmax.crontab.service;

import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.dto.crontab.SystemVarInfoDto;
import com.sunmax.common.dto.operate.ConfigurationResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.DeviceVarChartQueryVo;
import com.sunmax.common.vo.crontab.GunSystemVarChartQueryVo;
import com.sunmax.common.vo.crontab.SiteListQueryVo;
import com.sunmax.common.vo.crontab.SystemVarNewValueVo;
import com.sunmax.crontab.dto.SiteDeviceDataDto;
import com.sunmax.crontab.entity.SystemVariableEntity;
import com.sunmax.crontab.vo.SiteDeviceQueryVo;

import java.util.List;
import java.util.Map;

public interface ConfigFuncPointService {

    /**
     * 查询电枪系统变量图表数据
     *
     * @return
     */
    ResponseResult<ConfigurationResultDto> findGunSystemVarChartData(GunSystemVarChartQueryVo chartQueryVo);

    /**
     * 查询设备系统变量图表数据
     * @param deviceChartQueryVo
     * @return
     */
    ResponseResult<ConfigurationResultDto> findDeviceSystemVarChartData(DeviceVarChartQueryVo deviceChartQueryVo);

    /**
     * 查询系统变量最新值数据
     * @param systemVarNewValueVo
     * @return
     */
    ResponseResult<ConfigurationResultDto> findSystemVarNewValue(SystemVarNewValueVo systemVarNewValueVo);

    /**
     * 推送组态功能点数据
     */
    void sendConfigFuncPointData();

    /**
     * 查询站点列表
     * @param siteListQueryVo
     * @return
     */
    ResponseResult<ConfigurationResultDto> findSiteListByUserId(SiteListQueryVo siteListQueryVo);

    /**
     * 根据站点/设备id查询系统变量列表
     *
     * @param deviceId 站点/设备id
     * @param queryType 查询类型 1-站点 2-设备
     * @return
     */
    ResponseResult<List<SystemVarInfoDto>> findSystemVarListByDeviceId(String deviceId, Integer queryType);

    /**
     * 查询计算节点最新值
     * @param nodeVariables
     * @param siteIdList 可以是站点id/设备id
     * @param varCodeList
     * @return
     */
    Map<String, List<ConfigurationResultDto.FieldData>> siteProcessNodeNewValue(List<SystemVariableEntity> nodeVariables, List<String> siteIdList, List<String> varCodeList);

    /**
     * 查询站点设备数据
     * @param siteDeviceQueryVo 站点设备数据查询条件
     * @return 站点设备数据
     */
    ResponseResult<SiteDeviceDataDto> findSiteDeviceDataList(SiteDeviceQueryVo siteDeviceQueryVo);

    /**
     * 根据站点/设备id查询计算节点列表
     * @param deviceId 站点/设备id
     * @return 计算节点列表
     */
    ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(String deviceId);
}
