package com.sunmax.device.service;

import com.sunmax.common.dto.AreaDto;
import com.sunmax.common.dto.CityDto;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.ProvinceDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.device.SiteBasicInfoDto;
import com.sunmax.device.dto.AreaAddressDto;
import com.sunmax.common.dto.device.SiteEnergyInfoDto;
import com.sunmax.device.dto.SiteListDto;
import com.sunmax.device.vo.SiteChangeVo;
import com.sunmax.device.vo.SiteEnergyInfoVo;
import com.sunmax.device.vo.SiteQueryVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SiteService {

    /**
     * 新增或编辑站点数据
     * @param siteChangeVo
     * @param imageFiles
     * @return
     */
    ResponseResult<Void> saveOrUpdateSite(SiteChangeVo siteChangeVo, MultipartFile[] imageFiles);

    /**
     * 根据站点id查询基本详情数据
     * @param id
     * @return
     */
    ResponseResult<SiteBasicInfoDto> findSiteBasicInfoById(String id);

    /**
     * 根据站点id删除数据
     * @param id
     * @return
     */
    ResponseResult<String> deleteSiteInfoById(String id);

    /**
     * 分页查询站点列表
     *
     * @param siteQueryVo
     * @param userId
     * @return
     */
    ResponseResult<PageDto<SiteListDto>> querySiteListByPage(SiteQueryVo siteQueryVo, String userId);

    /**
     * 新增或编辑站点能源信息
     * @param siteEnergyInfoVo
     * @return
     */
    ResponseResult<Void> saveOrUpdeteSiteEnergyInfo(SiteEnergyInfoVo siteEnergyInfoVo);

    /**
     * 根据站点id查询能源信息数据
     * @param siteId
     * @return
     */
    ResponseResult<SiteEnergyInfoDto> findSiteEnergyInfoById(String siteId);

    /**
     * 根据用户id查询站点列表信息
     * @param userId
     * @return
     */
    ResponseResult<List<SiteBasicInfoDto>> findSiteInfoListByUserId(String userId);

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
     * 根据多个站点id查询能源信息数据
     * @param siteIdList
     * @return
     */
    ResponseResult<Map<String, SiteEnergyInfoDto>> findSiteEnergyInfoBySiteIds(List<String> siteIdList);
}
