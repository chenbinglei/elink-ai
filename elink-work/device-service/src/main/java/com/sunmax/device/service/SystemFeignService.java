package com.sunmax.device.service;

import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.util.ResponseResult;

import java.util.List;
import java.util.Map;

public interface SystemFeignService {

    /**
     * 查询全部站点详情列表
     * @param siteNameLike
     * @return
     */
    ResponseResult<List<SiteInfoDto>> findAllSiteBasicInfoList(String siteNameLike);

    /**
     * 根据多个站点id查询站站点详情数据
     * @param siteIdList
     * @return
     */
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(List<String> siteIdList);

    /**
     * 根据多个站点id查询站站点设置数据
     * @param siteIdList
     * @return
     */
    ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(List<String> siteIdList);

    /**
     * 根据多个站点id查询站站点详情数据(只查询站点本体表结构数据，关于其他关联和设计别的表字段信息不查询返回,使用时看清楚)
     * @param siteIdList
     * @return
     */
    ResponseResult<List<SiteInfoDto>> findSiteInfoListByIds(List<String> siteIdList);
}
