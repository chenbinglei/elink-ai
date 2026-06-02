package com.sunmax.device.service;

import com.sunmax.common.dto.crontab.ConfigurSiteListDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.SiteListQueryVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CrontabFeignService {

    /**
     * 根据多个设备id查询设备基本信息
     * @param deviceIdList
     * @return
     */
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(List<String> deviceIdList);

    /**
     * 根据多个站点id查询设备列表数据
     *
     * @param siteIdList
     * @param deviceType
     * @return
     */
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(List<String> siteIdList, Integer deviceType);

    /**
     * 根据多个模型id查询模型基本信息
     * @param modelIdList
     * @return
     */
    ResponseResult<Map<String, ModelDetailDto>> findModelDetailByIds(List<String> modelIdList);

    /**
     * 根据多个功能点id查询功能点基本信息
     * @param functionIdList
     * @return
     */
    ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByIds(List<String> functionIdList);

    /**
     * 根据多个模型id查询模型功能点列表
     * @param modelIdList
     * @return
     */
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(List<String> modelIdList);

    /**
     * 根据设备id查询设备通讯状态
     * @param deleveIdMap 设备id -> 设备标识
     * @return
     */
    ResponseResult<Map<String, Integer>> findDeviceTxStatusById(Map<String, String> deleveIdMap);

    /**
     * 根据用户id查询站点列表数据
     * @param siteListQueryVo
     * @return
     */
    ResponseResult<List<ConfigurSiteListDto>> findSiteListByUserId(SiteListQueryVo siteListQueryVo);

    /**
     * 获取站点设备树数据
     * @param siteIds 多个站点id
     * @return 站点设备树数据
     */
    ResponseResult<List<SiteDeviceDto>> getSiteDeviceList(Set<String> siteIds);

    /**
     * 根据多个功能点标识查询功能点基本信息
     * @param functionLogos 多个功能点标识
     * @return 功能点基本信息
     */
    ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByLogos(Set<String> functionLogos);

    /**
     * 根据多个父节点id查询下级设备列表数据
     * @param parentIdList
     * @return
     */
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(List<String> parentIdList);
}
