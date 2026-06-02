package com.sunmax.device.controller;

import com.sunmax.common.dto.AreaDto;
import com.sunmax.common.dto.CityDto;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.ProvinceDto;
import com.sunmax.common.dto.crontab.ConfigurSiteListDto;
import com.sunmax.common.dto.device.AffiliatesInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.util.NauticalUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.SiteListQueryVo;
import com.sunmax.common.vo.device.SiteInfoChangeVo;
import com.sunmax.device.dto.*;
import com.sunmax.device.service.CrontabFeignService;
import com.sunmax.device.service.SiteInfoService;
import com.sunmax.device.service.impl.TopDeviceDto;
import com.sunmax.device.util.TopNodeDataUtil;
import com.sunmax.device.vo.*;
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
@RequestMapping("siteInfo")
@Api(tags = "站点管理-新版")
public class SiteInfoController {

    @Autowired
    private SiteInfoService siteInfoService;

    @Autowired
    private CrontabFeignService crontabFeignService;

    @PostMapping("saveOrUpdateSiteInfo")
    @ApiOperation("新增或编辑站点数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveOrUpdateSiteInfo(SiteInfoChangeVo siteChangeVo) {
        return siteInfoService.saveOrUpdateSiteInfo(siteChangeVo);
    }

    @PostMapping("querySiteListByPage")
    @ApiOperation("分页查询站点列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<SitePageDto>> querySiteListByPage(SiteQueryVo siteQueryVo, String userId) {
        return siteInfoService.querySiteListByPage(siteQueryVo, userId);
    }

    @PostMapping("findSiteInfoById")
    @ApiOperation("根据站点id查询基本详情数据")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "站点id", dataType = "String", required = true)
    })
    public ResponseResult<SiteInfoDto> findSiteInfoById(String id) {
        return siteInfoService.findSiteInfoById(id);
    }

    @PostMapping("deleteSiteInfoById")
    @ApiOperation("根据站点id删除站点相关信息")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "站点id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteSiteInfoById(String id) {
        return siteInfoService.deleteSiteInfoById(id);
    }

    @PostMapping("saveOrUpdateSiteScenarioType")
    @ApiOperation("新增或编辑站点能源场景信息数据")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Void> saveOrUpdateSiteScenarioType(ScenarioTypeChangeVo scenarioTypeChangeVo) {
        return siteInfoService.saveOrUpdateSiteScenarioType(scenarioTypeChangeVo);
    }

    @PostMapping("deleteSiteScenarioTypeById")
    @ApiOperation("根据能源场景id删除指定能源信息")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "能源场景id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteSiteScenarioTypeById(String id) {
        return siteInfoService.deleteSiteScenarioTypeById(id);
    }

    @PostMapping("saveOrUpdateAffiliatesInfo")
    @ApiOperation("新增或编辑关联方信息")
    @ApiOperationSupport(order = 7)
    public ResponseResult<String> saveOrUpdateAffiliatesInfo(AffiliatesChangeVo affiliatesChangeVo) {
        return siteInfoService.saveOrUpdateAffiliatesInfo(affiliatesChangeVo);
    }

    @PostMapping("findAffiliatesListBySiteId")
    @ApiOperation("根据站点id查询关联方列表信息")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "keywordType", value = "关键字类型 1-企业名称", dataType = "Integer"),
            @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "String")
    })
    public ResponseResult<List<AffiliatesInfoDto>> findAffiliatesListBySiteId(String siteId, Integer keywordType, String keyword) {
        return siteInfoService.findAffiliatesListBySiteId(siteId, keywordType, keyword);
    }

    @PostMapping("deleteAffiliatesInfoById")
    @ApiOperation("根据关联方id删除指定关联方信息")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "关联方id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteAffiliatesInfoById(String id) {
        return siteInfoService.deleteAffiliatesInfoById(id);
    }

    @PostMapping("findSiteInfoListByUserId")
    @ApiOperation("根据用户id查询站点列表信息")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true)
    })
    public ResponseResult<List<SiteInfoDto>> findSiteInfoListByUserId(String userId) {
        return siteInfoService.findSiteInfoListByUserId(userId);
    }

    @PostMapping("queryProvinceData")
    @ApiOperation("查询全国省份列表")
    @ApiOperationSupport(order = 8)
    public ResponseResult<List<ProvinceDto>> queryProvinceData() {
        return siteInfoService.queryProvinceData();
    }

    @PostMapping("queryCityDataByProvinceId")
    @ApiOperation("根据全国省编码id查询下面市级数据")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "provinceId", value = "省份id", paramType = "query", required = true)
    })
    public ResponseResult<List<CityDto>> queryCityDataByProvinceId(String provinceId) {
        return siteInfoService.queryCityDataByProvinceId(provinceId);
    }

    @PostMapping("queryAreaDataByCityId")
    @ApiOperation("根据全国市编码id查询下面区县级数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cityId", value = "市级id", paramType = "query", required = true)
    })
    public ResponseResult<List<AreaDto>> queryAreaDataByCityId(String cityId) {
        return siteInfoService.queryAreaDataByCityId(cityId);
    }

    @PostMapping("getAreaAddressByCoordinates")
    @ApiOperation("根据坐标获取区域地址")
    @ApiImplicitParam(name = "coordinates", value = "坐标", dataType = "String", required = true)
    @ApiOperationSupport(order = 11)
    public ResponseResult<AreaAddressDto> getAreaAddressByCoordinates(String coordinates) {
        return siteInfoService.getAreaAddressByCoordinates(coordinates);
    }

    @PostMapping("getLonAndLatByAddress")
    @ApiOperation("根据地址获取经纬度")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "地址", paramType = "query", required = true)
    })
    public ResponseResult<List<Map<String, String>>> getLonAndLatByAddress(String address) {
        return ResponseResult.ok(NauticalUtil.getLonAndLatByAddress(address));
    }

    @PostMapping("updateSiteImageById")
    @ApiOperation("根据站点id操作站点图片")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "deleteImagePaths", value = "删除图片路径(多个以逗号分割)", paramType = "query")
    })
    public ResponseResult<String> updateSiteImageById(String id, String deleteImagePaths, MultipartFile[] imageFiles) {
        return siteInfoService.updateSiteImageById(id, deleteImagePaths, imageFiles);
    }

    @PostMapping("findSiteListByUserId")
    @ApiOperation("查询站点列表(智慧能源综合管理平台单独提供)")
    @ApiOperationSupport(order = 14)
    public ResponseResult<List<ConfigurSiteListDto>> findSiteListByUserId(SiteListQueryVo siteListQueryVo) {
        return crontabFeignService.findSiteListByUserId(siteListQueryVo);
    }

    @PostMapping("updateSiteSetUp")
    @ApiOperation("编辑站点设置")
    @ApiOperationSupport(order = 15)
    public ResponseResult<String> updateSiteSetUp(SiteSetUpChangeVo siteSetUpChangeVo) {
        return siteInfoService.updateSiteSetUp(siteSetUpChangeVo);
    }

    @PostMapping("findSiteSetUpBySiteId")
    @ApiOperation("根据站点id查询站点设置")
    @ApiOperationSupport(order = 16)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<SiteSetUpDto> findSiteSetUpBySiteId(String siteId) {
        return siteInfoService.findSiteSetUpBySiteId(siteId);
    }

    @PostMapping("saveSiteTopNode")
    @ApiOperation("新增或编辑拓扑节点信息")
    @ApiOperationSupport(order = 17)
    public ResponseResult<Void> saveSiteTopNode(SiteTopNodeChangeVo siteTopNodeVo) {
        return siteInfoService.saveSiteTopNode(siteTopNodeVo);
    }

    @PostMapping("deleteTopNodeInfoById")
    @ApiOperation("根据拓扑节点id删除拓扑信息")
    @ApiOperationSupport(order = 18)
    @ApiImplicitParam(name = "topNodeId", value = "拓扑节点id", paramType = "query", required = true)
    public ResponseResult<Void> deleteTopNodeInfoById(String topNodeId) {
        return siteInfoService.deleteTopNodeInfoById(topNodeId);
    }

    @PostMapping("findTopNodeListBySiteId")
    @ApiOperation("根据站点id查询拓扑节点列表")
    @ApiOperationSupport(order = 19)
    @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    public ResponseResult<List<SiteTopNodeListDto>> findTopNodeListBySiteId(String siteId) {
        return siteInfoService.findTopNodeListBySiteId(siteId);
    }

    @PostMapping("findTopNodeInfoById")
    @ApiOperation("根据拓扑节点id查询拓扑信息")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParam(name = "topNodeId", value = "拓扑节点id", paramType = "query", required = true)
    public ResponseResult<TopNodeInfoDto> findTopNodeInfoById(String topNodeId) {
        return siteInfoService.findTopNodeInfoById(topNodeId);
    }

    @PostMapping("findDeviceListBySiteId")
    @ApiOperation("根据站点id查询设备列表")
    @ApiOperationSupport(order = 21)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "deviceType", value = "设备类型 1-关口表 2-电能表 3-逆变器 4-PCS 5-电池簇 6-充电桩 7-智能断路器 8-换电仓", paramType = "query", required = true)
    })
    public ResponseResult<List<TopDeviceDto>> findDeviceListBySiteId(String siteId, Integer deviceType) {
        return siteInfoService.findDeviceListBySiteId(siteId, deviceType);
    }

    @PostMapping("getTopItemList")
    @ApiOperation("根据节点类型查询拓扑节点默认项数据")
    @ApiOperationSupport(order = 22)
    @ApiImplicitParam(name = "nodeType", value = "节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站", paramType = "query", required = true)
    public ResponseResult<List<TopItemDto>> getTopItemList(Integer nodeType) {
        return ResponseResult.ok(TopNodeDataUtil.getTopItemList(nodeType));
    }


}
