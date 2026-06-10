/*
package com.sunmax.device.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.AreaDto;
import com.sunmax.common.dto.CityDto;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.ProvinceDto;
import com.sunmax.common.util.NauticalUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.device.SiteBasicInfoDto;
import com.sunmax.device.dto.AreaAddressDto;
import com.sunmax.common.dto.device.SiteEnergyInfoDto;
import com.sunmax.device.dto.SiteListDto;
import com.sunmax.device.service.SiteService;
import com.sunmax.device.vo.SiteChangeVo;
import com.sunmax.device.vo.SiteEnergyInfoVo;
import com.sunmax.device.vo.SiteQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("site")
@Tag(name = "站点管理-旧版")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @PostMapping("saveOrUpdateSite")
    @Operation(summary = "新增或编辑站点数据")
    
    public ResponseResult<Void> saveOrUpdateSite(SiteChangeVo siteChangeVo, MultipartFile[] imageFiles) {
        return siteService.saveOrUpdateSite(siteChangeVo, imageFiles);
    }

    @PostMapping("findSiteBasicInfoById")
    @Operation(summary = "根据站点id查询基本详情数据")
    
    @Parameters({
            @Parameter(name = "id", description = "站点id")
    })
    public ResponseResult<SiteBasicInfoDto> findSiteBasicInfoById(String id) {
        return siteService.findSiteBasicInfoById(id);
    }

    @PostMapping("deleteSiteInfoById")
    @Operation(summary = "根据站点id删除数据")
    
    @Parameters({
            @Parameter(name = "id", description = "站点id")
    })
    public ResponseResult<String> deleteSiteInfoById(String id) {
        return siteService.deleteSiteInfoById(id);
    }

    @PostMapping("querySiteListByPage")
    @Operation(summary = "分页查询站点列表")
    
    public ResponseResult<PageDto<SiteListDto>> querySiteListByPage(SiteQueryVo siteQueryVo, String userId) {
        return siteService.querySiteListByPage(siteQueryVo, userId);
    }

    @PostMapping("saveOrUpdeteSiteEnergyInfo")
    @Operation(summary = "新增或编辑站点能源信息")
    
    public ResponseResult<Void> saveOrUpdeteSiteEnergyInfo(SiteEnergyInfoVo siteEnergyInfoVo) {
        return siteService.saveOrUpdeteSiteEnergyInfo(siteEnergyInfoVo);
    }

    @PostMapping("findSiteEnergyInfoById")
    @Operation(summary = "根据站点id查询能源信息数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<SiteEnergyInfoDto> findSiteEnergyInfoById(String siteId) {
        return siteService.findSiteEnergyInfoById(siteId);
    }

    @PostMapping("findSiteInfoListByUserId")
    @Operation(summary = "根据用户id查询站点列表信息")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id")
    })
    public ResponseResult<List<SiteBasicInfoDto>> findSiteInfoListByUserId(String userId) {
        return siteService.findSiteInfoListByUserId(userId);
    }

    @PostMapping("queryProvinceData")
    @Operation(summary = "查询全国省份列表")
    
    public ResponseResult<List<ProvinceDto>> queryProvinceData() {
        return siteService.queryProvinceData();
    }

    @PostMapping("queryCityDataByProvinceId")
    @Operation(summary = "根据全国省编码id查询下面市级数据")
    
    @Parameters({
            @Parameter(name = "provinceId", description = "省份id")
    })
    public ResponseResult<List<CityDto>> queryCityDataByProvinceId(String provinceId) {
        return siteService.queryCityDataByProvinceId(provinceId);
    }

    @PostMapping("queryAreaDataByCityId")
    @Operation(summary = "根据全国市编码id查询下面区县级数据")
    
    @Parameters({
            @Parameter(name = "cityId", description = "市级id")
    })
    public ResponseResult<List<AreaDto>> queryAreaDataByCityId(String cityId) {
        return siteService.queryAreaDataByCityId(cityId);
    }

    @PostMapping("getAreaAddressByCoordinates")
    @Operation(summary = "根据坐标获取区域地址")
    @Parameter(name = "coordinates", description = "坐标")
    
    public ResponseResult<AreaAddressDto> getAreaAddressByCoordinates(String coordinates) {
        return siteService.getAreaAddressByCoordinates(coordinates);
    }

    @PostMapping("getLonAndLatByAddress")
    @Operation(summary = "根据地址获取经纬度")
    
    @Parameters({
            @Parameter(name = "address", description = "地址")
    })
    public ResponseResult<List<Map<String,String>>> getLonAndLatByAddress(String address) {
        return ResponseResult.ok(NauticalUtil.getLonAndLatByAddress(address));
    }
}
*/
