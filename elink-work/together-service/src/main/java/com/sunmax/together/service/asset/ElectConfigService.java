package com.sunmax.together.service.asset;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.asset.electConfig.ElectConfigDetailDto;
import com.sunmax.together.dto.asset.electConfig.ElectConfigListDto;
import com.sunmax.together.vo.operation.electConfig.ElectConfigChangeVo;

import java.util.List;
import java.util.Map;

public interface ElectConfigService {

    /**
     * 新增或编辑电价策略配置数据
     * @param electConfigVo 电价策略配置入参参数实体类
     * @return 状态码
     */
    ResponseResult<Void> saveElectConfig(ElectConfigChangeVo electConfigVo);

    /**
     * 批量删除电价策略配置数据
     * @param ids 多个电价策略配置id
     * @return 状态码
     */
    ResponseResult<Void> deleteAllElectConfigByIds(List<String> ids);

    /**
     * 查询电价策略配置数据
     * @param siteId 站点id
     * @return 电价模块类型 -> 电价策略配置数据
     */
    ResponseResult<Map<Integer, List<ElectConfigListDto>>> queryElectConfigList(String siteId);

    /**
     * 查询电价策略配置数据详情
     * @param id 电价策略配置id
     * @return 电价策略配置数据详情
     */
    ResponseResult<ElectConfigDetailDto> findElectConfigById(String id);

    /**
     * 批量应用电价策略配置数据到其他站点
     * @param electConfigIds 多个电价策略配置id
     * @param siteIds 多个站点id
     * @return 状态码
     */
    ResponseResult<List<String>> applyElectConfigToOtherSite(List<String> electConfigIds, List<String> siteIds);

}
