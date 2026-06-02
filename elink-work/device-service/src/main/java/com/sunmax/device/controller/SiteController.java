/*
package com.sunmax.device.controller;

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
import io.swagger.annotations.*;
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
@Api(tags = "站点管理-旧版")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @PostMapping("saveOrUpdateSite")
    @ApiOperation("新增或编辑站点数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveOrUpdateSite(SiteChangeVo siteChangeVo, MultipartFile[] imageFiles) {
        return siteService.saveOrUpdateSite(siteChangeVo, imageFiles);
    }

    @PostMapping("findSiteBasicInfoById")
    @ApiOperation("根据站点id查询基本详情数据")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "站点id", dataType = "String", required = true)
    })
    public ResponseResult<SiteBasicInfoDto> findSiteBasicInfoById(String id) {
        return siteService.findSiteBasicInfoById(id);
    }

    @PostMapping("deleteSiteInfoById")
    @ApiOperation("根据站点id删除数据")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "站点id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteSiteInfoById(String id) {
        return siteService.deleteSiteInfoById(id);
    }

    @PostMapping("querySiteListByPage")
    @ApiOperation("分页查询站点列表")
    @ApiOperationSupport(order = 4)
    public ResponseResult<PageDto<SiteListDto>> querySiteListByPage(SiteQueryVo siteQueryVo, String userId) {
        return siteService.querySiteListByPage(siteQueryVo, userId);
    }

    @PostMapping("saveOrUpdeteSiteEnergyInfo")
    @ApiOperation("新增或编辑站点能源信息")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Void> saveOrUpdeteSiteEnergyInfo(SiteEnergyInfoVo siteEnergyInfoVo) {
        return siteService.saveOrUpdeteSiteEnergyInfo(siteEnergyInfoVo);
    }

    @PostMapping("findSiteEnergyInfoById")
    @ApiOperation("根据站点id查询能源信息数据")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    })
    public ResponseResult<SiteEnergyInfoDto> findSiteEnergyInfoById(String siteId) {
        return siteService.findSiteEnergyInfoById(siteId);
    }

    @PostMapping("findSiteInfoListByUserId")
    @ApiOperation("根据用户id查询站点列表信息")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true)
    })
    public ResponseResult<List<SiteBasicInfoDto>> findSiteInfoListByUserId(String userId) {
        return siteService.findSiteInfoListByUserId(userId);
    }

    @PostMapping("queryProvinceData")
    @ApiOperation("查询全国省份列表")
    @ApiOperationSupport(order = 8)
    public ResponseResult<List<ProvinceDto>> queryProvinceData() {
        return siteService.queryProvinceData();
    }

    @PostMapping("queryCityDataByProvinceId")
    @ApiOperation("根据全国省编码id查询下面市级数据")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "provinceId", value = "省份id", paramType = "query", required = true)
    })
    public ResponseResult<List<CityDto>> queryCityDataByProvinceId(String provinceId) {
        return siteService.queryCityDataByProvinceId(provinceId);
    }

    @PostMapping("queryAreaDataByCityId")
    @ApiOperation("根据全国市编码id查询下面区县级数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cityId", value = "市级id", paramType = "query", required = true)
    })
    public ResponseResult<List<AreaDto>> queryAreaDataByCityId(String cityId) {
        return siteService.queryAreaDataByCityId(cityId);
    }

    @PostMapping("getAreaAddressByCoordinates")
    @ApiOperation("根据坐标获取区域地址")
    @ApiImplicitParam(name = "coordinates", value = "坐标", dataType = "String", required = true)
    @ApiOperationSupport(order = 11)
    public ResponseResult<AreaAddressDto> getAreaAddressByCoordinates(String coordinates) {
        return siteService.getAreaAddressByCoordinates(coordinates);
    }

    @PostMapping("getLonAndLatByAddress")
    @ApiOperation("根据地址获取经纬度")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "地址", paramType = "query", required = true)
    })
    public ResponseResult<List<Map<String,String>>> getLonAndLatByAddress(String address) {
        return ResponseResult.ok(NauticalUtil.getLonAndLatByAddress(address));
    }
}
*/
