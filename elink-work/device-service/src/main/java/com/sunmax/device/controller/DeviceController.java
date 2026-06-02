package com.sunmax.device.controller;

import com.alibaba.fastjson.JSON;
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
import io.swagger.annotations.*;
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
@Api(tags = "设备管理控制层")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("saveDevice")
    @ApiOperation("新增编辑设备数据")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "imageFiles", value = "多个设备图片文件", dataType = "file")
    public ResponseResult<Void> saveDevice(DeviceChangeVo deviceChangeVo, MultipartFile[] imageFiles) {
        return deviceService.saveDevice(deviceChangeVo, imageFiles);
    }

    @PostMapping("batchInsertDevice")
    @ApiOperation("批量添加设备数据")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "typeId", value = "设备类型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "parentId", value = "父节点id", dataType = "String"),
            @ApiImplicitParam(name = "dataFile", value = "设备数据文件", dataType = "file", required = true)
    })
    public ResponseResult<ImportResultDto> batchInsertDevice(String userId, String typeId, String modelId, String siteId, String parentId, MultipartFile dataFile) {
        return deviceService.batchInsertDevice(userId, typeId, modelId, siteId, parentId, dataFile);
    }

    @PostMapping("getModelNameListByTypeId")
    @ApiOperation("根据资产分类id获取模型名称列表")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "typeId", value = "资产分类id", dataType = "String", required = true)
    public ResponseResult<List<ModelNameDto>> getModelNameListByTypeId(String typeId) {
        return deviceService.getModelNameListByTypeId(typeId);
    }

    @PostMapping("getModelFieldUpdateListByModelId")
    @ApiOperation("根据模型id获取模型编辑字段列表")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true)
    public ResponseResult<List<ModelFieldUpdateDto>> getModelFieldUpdateListByModelId(String modelId) {
        return deviceService.getModelFieldUpdateListByModelId(modelId);
    }

    @PostMapping("queryDeviceList")
    @ApiOperation("查询设备数据列表")
    @ApiOperationSupport(order = 5)
    public ResponseResult<PageDto<DeviceListDto>> queryDeviceList(DeviceQueryVo deviceQueryVo) {
        return deviceService.queryDeviceList(deviceQueryVo);
    }

    @PostMapping("deleteDeviceById")
    @ApiOperation("删除设备数据")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "deleteLogo", value = "删除标识 true-删除 false-不删除", dataType = "Boolean", required = true)
    })
    public ResponseResult<Void> deleteDeviceById(String id, Boolean deleteLogo) {
        return deviceService.deleteDeviceById(id, deleteLogo);
    }

    @PostMapping("findDeviceBasicInfoById")
    @ApiOperation("根据设备id查询设备基本信息数据")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<DeviceBasicInfoDto> findDeviceBasicInfoById(String deviceId) {
        return deviceService.findDeviceBasicInfoById(deviceId);
    }

    @PostMapping("findDeviceFunctionListById")
    @ApiOperation("根据设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<List<DeviceFunctionDto>> findDeviceFunctionListById(String deviceId) {
        return deviceService.findDeviceFunctionListById(deviceId);
    }

    @PostMapping("queryDeviceFunctionValueList")
    @ApiOperation("根据查询条件查询设备功能属性列表数据")
    @ApiOperationSupport(order = 9)
    public ResponseResult<DeviceFunctionValueDto> queryDeviceFunctionValueList(DeviceFunctionQueryVo functionQueryVo) {
        return deviceService.queryDeviceFunctionValueList(functionQueryVo);
    }

    @PostMapping("findDeviceReaListById")
    @ApiOperation("根据设备id查询设备扩展属性列表数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<List<DeviceReaDto>> findDeviceReaListById(String deviceId) {
        return deviceService.findDeviceReaListById(deviceId);
    }

    @PostMapping("findDeviceNodeListById")
    @ApiOperation("根据设备id查询设备节点列表")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<List<DeviceNodeListDto>> findDeviceNodeListById(String deviceId) {
        return deviceService.findDeviceNodeListById(deviceId);
    }

    @PostMapping("findDeviceNodeUpdateList")
    @ApiOperation("查询设备拓扑图编辑列表")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataType = "String", required = true)
    })
    public ResponseResult<DeviceNodeUpdateDto> findDeviceNodeUpdateList(String deviceId, String nodeId) {
        return deviceService.findDeviceNodeUpdateList(deviceId, nodeId);
    }

    @PostMapping("batchBindDeviceTopology")
    @ApiOperation("批量绑定设备拓扑图数据")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "bindData", value = "绑定数据 例如[\"设备id,节点id1\",\"设备id,节点id2\"]", dataType = "String", required = true)
    })
    public ResponseResult<Void> batchBindDeviceTopology(String deviceId, String nodeId, String bindData) {
        return deviceService.batchBindDeviceTopology(deviceId, nodeId, JSON.parseArray(bindData, String.class));
    }

    @PostMapping("deleteDeviceTopologyById")
    @ApiOperation("根据关联主键id删除设备节点数据")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParam(name = "id", value = "关联主键id", dataType = "String", required = true)
    public ResponseResult<Void> deleteDeviceTopologyById(String id) {
        return deviceService.deleteDeviceTopologyById(id);
    }

    @PostMapping("findDeviceEventList")
    @ApiOperation("根据查询条件查询设备事件列表")
    @ApiOperationSupport(order = 15)
    public ResponseResult<PageDto<DeviceEventListDto>> findDeviceEventList(DeviceEventQueryVo eventQueryVo) {
        return deviceService.findDeviceEventList(eventQueryVo);
    }

    @PostMapping("updateDeviceEventStatusById")
    @ApiOperation("根据设备事件id消除告警")
    @ApiOperationSupport(order = 16)
    @ApiImplicitParam(name = "id", value = "设备事件id", dataType = "String", required = true)
    public ResponseResult<Void> updateDeviceEventStatusById(String id) {
        return deviceService.updateDeviceEventStatusById(id);
    }

    @PostMapping("getSiteDeviceTreeList")
    @ApiOperation("获取站点设备树形结构")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 0-所有数据 1-站点数据 2-设备数据 3-子设备数据 4-站点设备数据 5-站点设备子设备数据 6-站点子系统数据", dataType = "int", required = true)
    })
    @ApiOperationSupport(order = 17)
    public ResponseResult<List<SiteDeviceTreeDto>> getSiteDeviceTreeList(String userId, Integer type) {
        return deviceService.getSiteDeviceTreeList(userId, type);
    }

    @PostMapping("getSiteAssetsTreeList")
    @ApiOperation("获取站点设备资产树形结构")
    @ApiImplicitParam(name = "userId", value = "当前用户id", dataType = "String", required = true)
    @ApiOperationSupport(order = 18)
    public ResponseResult<List<SiteDeviceTreeDto>> getSiteAssetsTreeList(String userId) {
        return deviceService.getSiteAssetsTreeList(userId);
    }

    @PostMapping("getRedisDeviceData")
    @ApiOperation("获取redis里面的设备数据")
    @ApiOperationSupport(order = 19)
    public ResponseResult<DeviceModel> getRedisDeviceData(String deviceNumber) {
        DeviceModel device = RedisDeviceUtil.getDevice(deviceNumber);
        return ResponseResult.ok(device);
    }

    @PostMapping("findGatewaySubDeviceList")
    @ApiOperation("根据网关id查询网关子设备列表")
    @ApiImplicitParam(name = "gatewayId", value = "当前网关id", dataType = "String", required = true)
    @ApiOperationSupport(order = 20)
    public ResponseResult<List<GatewaySubDeviceDto>> findGatewaySubDeviceList(String gatewayId) {
        return deviceService.findGatewaySubDeviceList(gatewayId);
    }

    @PostMapping("findSiteSubDeviceList")
    @ApiOperation("根据网关id查询网关下站点子设备列表")
    @ApiImplicitParam(name = "gatewayId", value = "当前网关id", dataType = "String", required = true)
    @ApiOperationSupport(order = 21)
    public ResponseResult<List<GatewaySubDeviceDto>> findSiteSubDeviceList(String gatewayId) {
        return deviceService.findSiteSubDeviceList(gatewayId);
    }

    @PostMapping("batchUpdateGatewaySubDevice")
    @ApiOperation("批量修改网关子设备数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gatewayId", value = "当前网关id", dataType = "String", required = true),
            @ApiImplicitParam(name = "subDeviceIds", value = "多个子设备id 例如['1','2','3']", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-批量添加 2-批量删除", dataType = "int", required = true)
    })
    @ApiOperationSupport(order = 22)
    public ResponseResult<Void> batchUpdateGatewaySubDevice(String gatewayId, String subDeviceIds, Integer type) {
        return deviceService.batchUpdateGatewaySubDevice(gatewayId, JSON.parseArray(subDeviceIds, String.class), type);
    }

    @PostMapping("findDeviceAssetList")
    @ApiOperation("根据站点id查询设备资产父节点数据")
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    @ApiOperationSupport(order = 23)
    public ResponseResult<List<DeviceAssetDto>> getDeviceAssetList(String siteId) {
        return deviceService.getDeviceAssetList(siteId);
    }

    @PostMapping("saveDeviceGun")
    @ApiOperation("添加设备枪数据")
    @ApiOperationSupport(order = 24)
    public ResponseResult<Void> saveDeviceGun(DeviceGunChangeVo deviceGunChangeVo) {
        return deviceService.saveDeviceGun(deviceGunChangeVo);
    }

    @PostMapping("findDeviceGunListByDeviceId")
    @ApiOperation("根据设备id查询设备枪列表数据")
    @ApiOperationSupport(order = 25)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<List<DeviceGunListDto>> findDeviceGunListByDeviceId(String deviceId) {
        return deviceService.findDeviceGunListByDeviceId(deviceId);
    }

    @PostMapping("saveAllDeviceGun")
    @ApiOperation("批量添加设备枪数据")
    @ApiOperationSupport(order = 26)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceGunIds", value = "多个设备枪id 例如['1','2','3']", dataType = "String", required = true),
            @ApiImplicitParam(name = "deviceIds", value = "多个设备id 例如['1','2','3']", dataType = "String", required = true)
    })
    public ResponseResult<Void> saveAllDeviceGun(String deviceGunIds, String deviceIds) {
        return deviceService.saveAllDeviceGun(JSON.parseArray(deviceGunIds, String.class), JSON.parseArray(deviceIds, String.class));
    }

    @PostMapping("deleteAllDeviceGun")
    @ApiOperation("批量删除设备枪数据")
    @ApiOperationSupport(order = 27)
    @ApiImplicitParam(name = "deviceGunIds", value = "多个设备枪id 例如['1','2','3']", dataType = "String", required = true)
    public ResponseResult<Void> deleteAllDeviceGun(String deviceGunIds) {
        return deviceService.deleteAllDeviceGun(JSON.parseArray(deviceGunIds, String.class));
    }

    @PostMapping("saveDeviceFunctionField")
    @ApiOperation("添加设备功能点字段数据")
    @ApiOperationSupport(order = 28)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionFields", value = "多个功能点字段id 例如['1','2','3']", dataType = "String", required = true)
    })
    public ResponseResult<Void> saveDeviceFunctionField(String deviceId, String functionFields) {
        return deviceService.saveDeviceFunctionField(deviceId, functionFields);
    }

}
