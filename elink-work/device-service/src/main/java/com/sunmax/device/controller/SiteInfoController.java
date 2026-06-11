package com.sunmax.device.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

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
@Tag(name = "站点管理-新版")
public class SiteInfoController {

    @Autowired
    private SiteInfoService siteInfoService;

    @Autowired
    private CrontabFeignService crontabFeignService;

    @PostMapping("saveOrUpdateSiteInfo")
    @Operation(summary = "新增或编辑站点数据")
    
    public ResponseResult<Void> saveOrUpdateSiteInfo(SiteInfoChangeVo siteChangeVo) {
        return siteInfoService.saveOrUpdateSiteInfo(siteChangeVo);
    }

    @PostMapping("querySiteListByPage")
    @Operation(summary = "分页查询站点列表")
    
    public ResponseResult<PageDto<SitePageDto>> querySiteListByPage(SiteQueryVo siteQueryVo, String userId) {
        return siteInfoService.querySiteListByPage(siteQueryVo, userId);
    }

    @PostMapping("findSiteInfoById")
    @Operation(summary = "根据站点id查询基本详情数据")
    
    @Parameters({
            @Parameter(name = "id", description = "站点id")
    })
    public ResponseResult<SiteInfoDto> findSiteInfoById(String id) {
        return siteInfoService.findSiteInfoById(id);
    }

    @PostMapping("deleteSiteInfoById")
    @Operation(summary = "根据站点id删除站点相关信息")
    
    @Parameters({
            @Parameter(name = "id", description = "站点id")
    })
    public ResponseResult<String> deleteSiteInfoById(String id) {
        return siteInfoService.deleteSiteInfoById(id);
    }

    @PostMapping("saveOrUpdateSiteScenarioType")
    @Operation(summary = "新增或编辑站点能源场景信息数据")
    
    public ResponseResult<Void> saveOrUpdateSiteScenarioType(ScenarioTypeChangeVo scenarioTypeChangeVo) {
        return siteInfoService.saveOrUpdateSiteScenarioType(scenarioTypeChangeVo);
    }

    @PostMapping("deleteSiteScenarioTypeById")
    @Operation(summary = "根据能源场景id删除指定能源信息")
    
    @Parameters({
            @Parameter(name = "id", description = "能源场景id")
    })
    public ResponseResult<String> deleteSiteScenarioTypeById(String id) {
        return siteInfoService.deleteSiteScenarioTypeById(id);
    }

    @PostMapping("saveOrUpdateAffiliatesInfo")
    @Operation(summary = "新增或编辑关联方信息")
    
    public ResponseResult<String> saveOrUpdateAffiliatesInfo(AffiliatesChangeVo affiliatesChangeVo) {
        return siteInfoService.saveOrUpdateAffiliatesInfo(affiliatesChangeVo);
    }

    @PostMapping("findAffiliatesListBySiteId")
    @Operation(summary = "根据站点id查询关联方列表信息")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "keywordType", description = "关键字类型 1-企业名称"),
            @Parameter(name = "keyword", description = "关键字")
    })
    public ResponseResult<List<AffiliatesInfoDto>> findAffiliatesListBySiteId(String siteId, Integer keywordType, String keyword) {
        return siteInfoService.findAffiliatesListBySiteId(siteId, keywordType, keyword);
    }

    @PostMapping("deleteAffiliatesInfoById")
    @Operation(summary = "根据关联方id删除指定关联方信息")
    
    @Parameters({
            @Parameter(name = "id", description = "关联方id")
    })
    public ResponseResult<String> deleteAffiliatesInfoById(String id) {
        return siteInfoService.deleteAffiliatesInfoById(id);
    }

    @PostMapping("findSiteInfoListByUserId")
    @Operation(summary = "根据用户id查询站点列表信息")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id")
    })
    public ResponseResult<List<SiteInfoDto>> findSiteInfoListByUserId(String userId) {
        return siteInfoService.findSiteInfoListByUserId(userId);
    }

    @PostMapping("queryProvinceData")
    @Operation(summary = "查询全国省份列表")
    
    public ResponseResult<List<ProvinceDto>> queryProvinceData() {
        return siteInfoService.queryProvinceData();
    }

    @PostMapping("queryCityDataByProvinceId")
    @Operation(summary = "根据全国省编码id查询下面市级数据")
    
    @Parameters({
            @Parameter(name = "provinceId", description = "省份id")
    })
    public ResponseResult<List<CityDto>> queryCityDataByProvinceId(String provinceId) {
        return siteInfoService.queryCityDataByProvinceId(provinceId);
    }

    @PostMapping("queryAreaDataByCityId")
    @Operation(summary = "根据全国市编码id查询下面区县级数据")
    
    @Parameters({
            @Parameter(name = "cityId", description = "市级id")
    })
    public ResponseResult<List<AreaDto>> queryAreaDataByCityId(String cityId) {
        return siteInfoService.queryAreaDataByCityId(cityId);
    }

    @PostMapping("getAreaAddressByCoordinates")
    @Operation(summary = "根据坐标获取区域地址")
    @Parameter(name = "coordinates", description = "坐标")
    
    public ResponseResult<AreaAddressDto> getAreaAddressByCoordinates(String coordinates) {
        return siteInfoService.getAreaAddressByCoordinates(coordinates);
    }

    @PostMapping("getLonAndLatByAddress")
    @Operation(summary = "根据地址获取经纬度")
    
    @Parameters({
            @Parameter(name = "address", description = "地址")
    })
    public ResponseResult<List<Map<String, String>>> getLonAndLatByAddress(String address) {
        return ResponseResult.ok(NauticalUtil.getLonAndLatByAddress(address));
    }

    @PostMapping("updateSiteImageById")
    @Operation(summary = "根据站点id操作站点图片")
    
    @Parameters({
            @Parameter(name = "id", description = "站点id"),
            @Parameter(name = "deleteImagePaths", description = "删除图片路径(多个以逗号分割)")
    })
    public ResponseResult<String> updateSiteImageById(String id, String deleteImagePaths, MultipartFile[] imageFiles) {
        return siteInfoService.updateSiteImageById(id, deleteImagePaths, imageFiles);
    }

    @PostMapping("findSiteListByUserId")
    @Operation(summary = "查询站点列表(智慧能源综合管理平台单独提供)")
    
    public ResponseResult<List<ConfigurSiteListDto>> findSiteListByUserId(SiteListQueryVo siteListQueryVo) {
        return crontabFeignService.findSiteListByUserId(siteListQueryVo);
    }

    @PostMapping("updateSiteSetUp")
    @Operation(summary = "编辑站点设置")
    
    public ResponseResult<String> updateSiteSetUp(SiteSetUpChangeVo siteSetUpChangeVo) {
        return siteInfoService.updateSiteSetUp(siteSetUpChangeVo);
    }

    @PostMapping("findSiteSetUpBySiteId")
    @Operation(summary = "根据站点id查询站点设置")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<SiteSetUpDto> findSiteSetUpBySiteId(String siteId) {
        return siteInfoService.findSiteSetUpBySiteId(siteId);
    }

    @PostMapping("saveSiteTopNode")
    @Operation(summary = "新增或编辑拓扑节点信息")
    
    public ResponseResult<Void> saveSiteTopNode(SiteTopNodeChangeVo siteTopNodeVo) {
        return siteInfoService.saveSiteTopNode(siteTopNodeVo);
    }

    @PostMapping("deleteTopNodeInfoById")
    @Operation(summary = "根据拓扑节点id删除拓扑信息")
    
    @Parameter(name = "topNodeId", description = "拓扑节点id")
    public ResponseResult<Void> deleteTopNodeInfoById(String topNodeId) {
        return siteInfoService.deleteTopNodeInfoById(topNodeId);
    }

    @PostMapping("findTopNodeListBySiteId")
    @Operation(summary = "根据站点id查询拓扑节点列表")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<SiteTopNodeListDto>> findTopNodeListBySiteId(String siteId) {
        return siteInfoService.findTopNodeListBySiteId(siteId);
    }

    @PostMapping("findTopNodeInfoById")
    @Operation(summary = "根据拓扑节点id查询拓扑信息")
    
    @Parameter(name = "topNodeId", description = "拓扑节点id")
    public ResponseResult<TopNodeInfoDto> findTopNodeInfoById(String topNodeId) {
        return siteInfoService.findTopNodeInfoById(topNodeId);
    }

    @PostMapping("findDeviceListBySiteId")
    @Operation(summary = "根据站点id查询设备列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "deviceType", description = "设备类型 1-关口表 2-电能表 3-逆变器 4-PCS 5-电池簇 6-充电桩 7-智能断路器 8-换电仓")
    })
    public ResponseResult<List<TopDeviceDto>> findDeviceListBySiteId(String siteId, Integer deviceType) {
        return siteInfoService.findDeviceListBySiteId(siteId, deviceType);
    }

    @PostMapping("getTopItemList")
    @Operation(summary = "根据节点类型查询拓扑节点默认项数据")
    
    @Parameter(name = "nodeType", description = "节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站")
    public ResponseResult<List<TopItemDto>> getTopItemList(Integer nodeType) {
        return ResponseResult.ok(TopNodeDataUtil.getTopItemList(nodeType));
    }


}
