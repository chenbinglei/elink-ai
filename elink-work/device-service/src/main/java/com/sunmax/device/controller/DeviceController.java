package com.sunmax.device.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceReaDto;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.GatewaySubDeviceDto;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.common.dto.device.SiteDeviceTreeDto;
import com.sunmax.device.dto.device.*;
import com.sunmax.device.dto.model.ModelFieldUpdateDto;
import com.sunmax.device.dto.device.DeviceGunListDto;
import com.sunmax.device.dto.model.ModelNameDto;
import com.sunmax.device.service.DeviceService;
import com.sunmax.device.vo.device.*;
import com.sunmax.device.vo.device.DeviceGunChangeVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 设备管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("device")
@Tag(name = "设备管理控制层")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("saveDevice")
    @Operation(summary = "新增编辑设备数据")
    
    @Parameter(name = "imageFiles", description = "多个设备图片文件")
    public ResponseResult<Void> saveDevice(DeviceChangeVo deviceChangeVo, MultipartFile[] imageFiles) {
        return deviceService.saveDevice(deviceChangeVo, imageFiles);
    }

    @PostMapping("batchInsertDevice")
    @Operation(summary = "批量添加设备数据")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "typeId", description = "设备类型id"),
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "parentId", description = "父节点id"),
            @Parameter(name = "dataFile", description = "设备数据文件")
    })
    public ResponseResult<ImportResultDto> batchInsertDevice(String userId, String typeId, String modelId, String siteId, String parentId, MultipartFile dataFile) {
        return deviceService.batchInsertDevice(userId, typeId, modelId, siteId, parentId, dataFile);
    }

    @PostMapping("getModelNameListByTypeId")
    @Operation(summary = "根据资产分类id获取模型名称列表")
    
    @Parameter(name = "typeId", description = "资产分类id")
    public ResponseResult<List<ModelNameDto>> getModelNameListByTypeId(String typeId) {
        return deviceService.getModelNameListByTypeId(typeId);
    }

    @PostMapping("getModelFieldUpdateListByModelId")
    @Operation(summary = "根据模型id获取模型编辑字段列表")
    
    @Parameter(name = "modelId", description = "模型id")
    public ResponseResult<List<ModelFieldUpdateDto>> getModelFieldUpdateListByModelId(String modelId) {
        return deviceService.getModelFieldUpdateListByModelId(modelId);
    }

    @PostMapping("queryDeviceList")
    @Operation(summary = "查询设备数据列表")
    
    public ResponseResult<PageDto<DeviceListDto>> queryDeviceList(DeviceQueryVo deviceQueryVo) {
        return deviceService.queryDeviceList(deviceQueryVo);
    }

    @PostMapping("deleteDeviceById")
    @Operation(summary = "删除设备数据")
    
    @Parameters({
            @Parameter(name = "id", description = "设备id"),
            @Parameter(name = "deleteLogo", description = "删除标识 true-删除 false-不删除")
    })
    public ResponseResult<Void> deleteDeviceById(String id, Boolean deleteLogo) {
        return deviceService.deleteDeviceById(id, deleteLogo);
    }

    @PostMapping("findDeviceBasicInfoById")
    @Operation(summary = "根据设备id查询设备基本信息数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<DeviceBasicInfoDto> findDeviceBasicInfoById(String deviceId) {
        return deviceService.findDeviceBasicInfoById(deviceId);
    }

    @PostMapping("findDeviceFunctionListById")
    @Operation(summary = "根据设备id查询设备功能属性列表数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<List<DeviceFunctionDto>> findDeviceFunctionListById(String deviceId) {
        return deviceService.findDeviceFunctionListById(deviceId);
    }

    @PostMapping("queryDeviceFunctionValueList")
    @Operation(summary = "根据查询条件查询设备功能属性列表数据")
    
    public ResponseResult<DeviceFunctionValueDto> queryDeviceFunctionValueList(DeviceFunctionQueryVo functionQueryVo) {
        return deviceService.queryDeviceFunctionValueList(functionQueryVo);
    }

    @PostMapping("findDeviceReaListById")
    @Operation(summary = "根据设备id查询设备扩展属性列表数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<List<DeviceReaDto>> findDeviceReaListById(String deviceId) {
        return deviceService.findDeviceReaListById(deviceId);
    }

    @PostMapping("findDeviceNodeListById")
    @Operation(summary = "根据设备id查询设备节点列表")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<List<DeviceNodeListDto>> findDeviceNodeListById(String deviceId) {
        return deviceService.findDeviceNodeListById(deviceId);
    }

    @PostMapping("findDeviceNodeUpdateList")
    @Operation(summary = "查询设备拓扑图编辑列表")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "nodeId", description = "节点id")
    })
    public ResponseResult<DeviceNodeUpdateDto> findDeviceNodeUpdateList(String deviceId, String nodeId) {
        return deviceService.findDeviceNodeUpdateList(deviceId, nodeId);
    }

    @PostMapping("batchBindDeviceTopology")
    @Operation(summary = "批量绑定设备拓扑图数据")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "nodeId", description = "节点id"),
            @Parameter(name = "bindData", description = "绑定数据 例如[\"设备id,节点id1\",\"设备id,节点id2\"]")
    })
    public ResponseResult<Void> batchBindDeviceTopology(String deviceId, String nodeId, String bindData) {
        return deviceService.batchBindDeviceTopology(deviceId, nodeId, JSON.parseArray(bindData, String.class));
    }

    @PostMapping("deleteDeviceTopologyById")
    @Operation(summary = "根据关联主键id删除设备节点数据")
    
    @Parameter(name = "id", description = "关联主键id")
    public ResponseResult<Void> deleteDeviceTopologyById(String id) {
        return deviceService.deleteDeviceTopologyById(id);
    }

    @PostMapping("findDeviceEventList")
    @Operation(summary = "根据查询条件查询设备事件列表")
    
    public ResponseResult<PageDto<DeviceEventListDto>> findDeviceEventList(DeviceEventQueryVo eventQueryVo) {
        return deviceService.findDeviceEventList(eventQueryVo);
    }

    @PostMapping("updateDeviceEventStatusById")
    @Operation(summary = "根据设备事件id消除告警")
    
    @Parameter(name = "id", description = "设备事件id")
    public ResponseResult<Void> updateDeviceEventStatusById(String id) {
        return deviceService.updateDeviceEventStatusById(id);
    }

    @PostMapping("getSiteDeviceTreeList")
    @Operation(summary = "获取站点设备树形结构")
    @Parameters({
            @Parameter(name = "userId", description = "当前用户id"),
            @Parameter(name = "type", description = "类型 0-所有数据 1-站点数据 2-设备数据 3-子设备数据 4-站点设备数据 5-站点设备子设备数据 6-站点子系统数据")
    })
    
    public ResponseResult<List<SiteDeviceTreeDto>> getSiteDeviceTreeList(String userId, Integer type) {
        return deviceService.getSiteDeviceTreeList(userId, type);
    }

    @PostMapping("getSiteAssetsTreeList")
    @Operation(summary = "获取站点设备资产树形结构")
    @Parameter(name = "userId", description = "当前用户id")
    
    public ResponseResult<List<SiteDeviceTreeDto>> getSiteAssetsTreeList(String userId) {
        return deviceService.getSiteAssetsTreeList(userId);
    }

    @PostMapping("getRedisDeviceData")
    @Operation(summary = "获取redis里面的设备数据")
    
    public ResponseResult<DeviceModel> getRedisDeviceData(String deviceNumber) {
        DeviceModel device = RedisDeviceUtil.getDevice(deviceNumber);
        return ResponseResult.ok(device);
    }

    @PostMapping("findGatewaySubDeviceList")
    @Operation(summary = "根据网关id查询网关子设备列表")
    @Parameter(name = "gatewayId", description = "当前网关id")
    
    public ResponseResult<List<GatewaySubDeviceDto>> findGatewaySubDeviceList(String gatewayId) {
        return deviceService.findGatewaySubDeviceList(gatewayId);
    }

    @PostMapping("findSiteSubDeviceList")
    @Operation(summary = "根据网关id查询网关下站点子设备列表")
    @Parameter(name = "gatewayId", description = "当前网关id")
    
    public ResponseResult<List<GatewaySubDeviceDto>> findSiteSubDeviceList(String gatewayId) {
        return deviceService.findSiteSubDeviceList(gatewayId);
    }

    @PostMapping("batchUpdateGatewaySubDevice")
    @Operation(summary = "批量修改网关子设备数据")
    @Parameters({
            @Parameter(name = "gatewayId", description = "当前网关id"),
            @Parameter(name = "subDeviceIds", description = "多个子设备id 例如['1','2','3']"),
            @Parameter(name = "type", description = "类型 1-批量添加 2-批量删除")
    })
    
    public ResponseResult<Void> batchUpdateGatewaySubDevice(String gatewayId, String subDeviceIds, Integer type) {
        return deviceService.batchUpdateGatewaySubDevice(gatewayId, JSON.parseArray(subDeviceIds, String.class), type);
    }

    @PostMapping("findDeviceAssetList")
    @Operation(summary = "根据站点id查询设备资产父节点数据")
    @Parameter(name = "siteId", description = "站点id")
    
    public ResponseResult<List<DeviceAssetDto>> getDeviceAssetList(String siteId) {
        return deviceService.getDeviceAssetList(siteId);
    }

    @PostMapping("saveDeviceGun")
    @Operation(summary = "添加设备枪数据")
    
    public ResponseResult<Void> saveDeviceGun(DeviceGunChangeVo deviceGunChangeVo) {
        return deviceService.saveDeviceGun(deviceGunChangeVo);
    }

    @PostMapping("findDeviceGunListByDeviceId")
    @Operation(summary = "根据设备id查询设备枪列表数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<List<DeviceGunListDto>> findDeviceGunListByDeviceId(String deviceId) {
        return deviceService.findDeviceGunListByDeviceId(deviceId);
    }

    @PostMapping("saveAllDeviceGun")
    @Operation(summary = "批量添加设备枪数据")
    
    @Parameters({
            @Parameter(name = "deviceGunIds", description = "多个设备枪id 例如['1','2','3']"),
            @Parameter(name = "deviceIds", description = "多个设备id 例如['1','2','3']")
    })
    public ResponseResult<Void> saveAllDeviceGun(String deviceGunIds, String deviceIds) {
        return deviceService.saveAllDeviceGun(JSON.parseArray(deviceGunIds, String.class), JSON.parseArray(deviceIds, String.class));
    }

    @PostMapping("deleteAllDeviceGun")
    @Operation(summary = "批量删除设备枪数据")
    
    @Parameter(name = "deviceGunIds", description = "多个设备枪id 例如['1','2','3']")
    public ResponseResult<Void> deleteAllDeviceGun(String deviceGunIds) {
        return deviceService.deleteAllDeviceGun(JSON.parseArray(deviceGunIds, String.class));
    }

    @PostMapping("saveDeviceFunctionField")
    @Operation(summary = "添加设备功能点字段数据")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "functionFields", description = "多个功能点字段id 例如['1','2','3']")
    })
    public ResponseResult<Void> saveDeviceFunctionField(String deviceId, String functionFields) {
        return deviceService.saveDeviceFunctionField(deviceId, functionFields);
    }

}
