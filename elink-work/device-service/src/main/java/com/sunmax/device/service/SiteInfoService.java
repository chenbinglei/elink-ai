package com.sunmax.device.service;

import com.sunmax.common.dto.AreaDto;
import com.sunmax.common.dto.CityDto;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.ProvinceDto;
import com.sunmax.common.dto.device.AffiliatesInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.SiteInfoChangeVo;
import com.sunmax.device.dto.AreaAddressDto;
import com.sunmax.device.dto.SitePageDto;
import com.sunmax.device.dto.SiteTopNodeListDto;
import com.sunmax.device.dto.TopNodeInfoDto;
import com.sunmax.device.service.impl.TopDeviceDto;
import com.sunmax.device.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SiteInfoService {

    /**
     * 新增或编辑站点数据
     * @param siteChangeVo
     * @return
     */
    ResponseResult<Void> saveOrUpdateSiteInfo(SiteInfoChangeVo siteChangeVo);

    /**
     * 分页查询站点列表
     * @param siteQueryVo
     * @param userId
     * @return
     */
    ResponseResult<PageDto<SitePageDto>> querySiteListByPage(SiteQueryVo siteQueryVo, String userId);

    /**
     * 根据站点id查询基本详情数据
     * @param id
     * @return
     */
    ResponseResult<SiteInfoDto> findSiteInfoById(String id);

    /**
     * 根据站点id删除站点相关信息
     * @param id
     * @return
     */
    ResponseResult<String> deleteSiteInfoById(String id);

    /**
     * 新增或编辑站点能源场景信息数据
     * @param scenarioTypeChangeVo
     * @return
     */
    ResponseResult<Void> saveOrUpdateSiteScenarioType(ScenarioTypeChangeVo scenarioTypeChangeVo);

    /**
     * 根据能源场景id删除指定能源信息
     * @param id
     * @return
     */
    ResponseResult<String> deleteSiteScenarioTypeById(String id);

    /**
     * 新增或编辑关联方信息
     * @param affiliatesChangeVo
     * @return
     */
    ResponseResult<String> saveOrUpdateAffiliatesInfo(AffiliatesChangeVo affiliatesChangeVo);

    /**
     * 根据站点id查询关联方列表信息
     * @param siteId
     * @param keywordType
     * @param keyword
     * @return
     */
    ResponseResult<List<AffiliatesInfoDto>> findAffiliatesListBySiteId(String siteId, Integer keywordType, String keyword);

    /**
     * 根据关联方id删除指定关联方信息
     * @param id
     * @return
     */
    ResponseResult<String> deleteAffiliatesInfoById(String id);

    /**
     * 根据用户id查询站点列表信息
     * @param userId
     * @return
     */
    ResponseResult<List<SiteInfoDto>> findSiteInfoListByUserId(String userId);

    /**
     * 查询全国省份列表
     * @return
     */
    ResponseResult<List<ProvinceDto>> queryProvinceData();

    /**
     * 根据全国省编码id查询下面市级数据
     * @param provinceId
     * @return
     */
    ResponseResult<List<CityDto>> queryCityDataByProvinceId(String provinceId);

    /**
     * 根据全国市编码id查询下面区县级数据
     * @param cityId
     * @return
     */
    ResponseResult<List<AreaDto>> queryAreaDataByCityId(String cityId);

    /**
     * 根据坐标获取区域地址
     * @param coordinates 坐标
     * @return 区域地址
     */
    ResponseResult<AreaAddressDto> getAreaAddressByCoordinates(String coordinates);

    /**
     * 根据站点id操作站点图片
     * @param id
     * @param deleteImagePaths
     * @param imageFiles
     * @return
     */
    ResponseResult<String> updateSiteImageById(String id, String deleteImagePaths, MultipartFile[] imageFiles);

    /**
     * 批量新增或编辑互联互通站点数据
     * @param siteInfoChangeVos
     * @return
     */
    ResponseResult<Void> saveOrUpdateInterflowSite(List<SiteInfoChangeVo> siteInfoChangeVos);

    /**
     * 编辑站点设置
     * @param siteSetUpChangeVo
     * @return
     */
    ResponseResult<String> updateSiteSetUp(SiteSetUpChangeVo siteSetUpChangeVo);

    /**
     * 根据站点id查询站点设置
     * @param siteId
     * @return
     */
    ResponseResult<SiteSetUpDto> findSiteSetUpBySiteId(String siteId);

    /**
     * 新增或编辑拓扑节点信息
     * @param siteTopNodeVo 拓扑节点编辑实体类
     * @return 状态码
     */
    ResponseResult<Void> saveSiteTopNode(SiteTopNodeChangeVo siteTopNodeVo);

    /**
     * 根据拓扑节点id删除拓扑信息
     * @param topNodeId 拓扑点节点
     * @return 状态码
     */
    ResponseResult<Void> deleteTopNodeInfoById(String topNodeId);

    /**
     * 根据站点id查询拓扑节点列表
     * @param siteId 站点id
     * @return 拓扑节点列表
     */
    ResponseResult<List<SiteTopNodeListDto>> findTopNodeListBySiteId(String siteId);

    /**
     * 根据拓扑节点id查询拓扑信息
     * @param topNodeId 拓扑节点id
     * @return 拓扑信息
     */
    ResponseResult<TopNodeInfoDto> findTopNodeInfoById(String topNodeId);

    /**
     * 根据站点id查询设备列表
     * @param siteId 站点id
     * @param deviceType 设备类型 1-关口表 2-电能表 3-逆变器 4-PCS 5-电池簇 6-充电桩 7-智能断路器
     * @return 设备列表数据
     */
    ResponseResult<List<TopDeviceDto>> findDeviceListBySiteId(String siteId, Integer deviceType);
}
