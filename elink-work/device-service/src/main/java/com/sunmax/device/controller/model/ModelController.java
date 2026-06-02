package com.sunmax.device.controller.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.common.dto.device.ModelDetailDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.model.*;
import com.sunmax.device.service.ModelService;
import com.sunmax.device.vo.model.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 模型管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("model")
@Api(tags = "模型管理控制层")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @PostMapping("saveModel")
    @ApiOperation("新增编辑模型数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveModel(ModelChangeVo modelChangeVo, MultipartFile logoFile) {
        return modelService.saveModel(modelChangeVo, logoFile);
    }

    @PostMapping("queryModelList")
    @ApiOperation("查询模型列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<ModelListDto>> queryModelList(ModelQueryVo modelQueryVo) {
        return modelService.queryModelList(modelQueryVo);
    }

    @PostMapping("updateModelStatus")
    @ApiOperation("模型发布(更新模型状态)")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true)
    })
    public ResponseResult<Void> updateModelStatus(String id, String userId) {
        return modelService.updateModelStatus(id, userId);
    }

    @PostMapping("deleteModelById")
    @ApiOperation("删除模型数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "id", value = "模型id", dataType = "String", required = true)
    public ResponseResult<Void> deleteModelById(String id) {
        return modelService.deleteModelById(id);
    }

    @PostMapping("findModelDetailById")
    @ApiOperation("根据模型id查询模型详情数据")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "id", value = "模型id", dataType = "String", required = true)
    public ResponseResult<ModelDetailDto> findModelDetailById(String id) {
        return modelService.findModelDetailById(id);
    }

    @PostMapping("bindModelFunctionData")
    @ApiOperation("绑定模型设备功能显示设置")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionIds", value = "多个标准功能id 如['1111','2222']", dataType = "String", required = true)
    })
    public ResponseResult<Void> bindModelFunctionData(String modelId, String functionIds) {
        return modelService.bindModelFunctionData(modelId, functionIds);
    }

    @PostMapping("findModelDeviceListByModelId")
    @ApiOperation("根据模型id查询模型下面设备列表")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键词 设备ID或设备名称", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    public ResponseResult<ModelDeviceDto> findModelDeviceListByModelId(String modelId, String keyword, Integer page, Integer size) {
        return modelService.findModelDeviceListByModelId(modelId, keyword, page, size);
    }

    @PostMapping("findModelFunctionListByModelId")
    @ApiOperation("根据模型id查询模型关联标准功能数据")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionName", value = "标准功能名称", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    public ResponseResult<PageDto<ModelFunctionListDto>> findModelFunctionListByModelId(String modelId, String functionName, Integer page, Integer size) {
        return modelService.findModelFunctionListByModelId(modelId, functionName, page, size);
    }

    @PostMapping("findModelBindFunctionByModelId")
    @ApiOperation("根据模型id查询模型绑定标准功能列表")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true)
    public ResponseResult<ModelBindFunctionDto> findModelBindFunctionByModelId(String modelId) {
        return modelService.findModelBindFunctionByModelId(modelId);
    }

    @PostMapping("modelBindFunctionData")
    @ApiOperation("模型绑定标准功能数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionMap", value = "多个标准功能id和序列号 例如{'功能点id1':序列号1,'功能点id2':序列号2}", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-添加编辑 2-删除", dataType = "int", required = true)
    })
    public ResponseResult<Void> modelBindFunctionData(String modelId, String functionMap, Integer type) {
        return modelService.modelBindFunctionData(modelId, JSON.parseObject(functionMap, new TypeReference<Map<String, Integer>>(){}), type);
    }

    @PostMapping("findModelReaListByModelId")
    @ApiOperation("根据模型id查询模型关联扩展属性数据")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "reaName", value = "扩展属性名称", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    public ResponseResult<PageDto<ModelReaListDto>> findModelReaListByModelId(String modelId, String reaName, Integer page, Integer size) {
        return modelService.findModelReaListByModelId(modelId, reaName, page, size);
    }

    @PostMapping("findModelBindReaByModelId")
    @ApiOperation("根据模型id查询模型绑定扩展属性列表")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true)
    public ResponseResult<ModelBindReaDto> findModelBindReaByModelId(String modelId) {
        return modelService.findModelBindReaByModelId(modelId);
    }

    @PostMapping("modelBindReaData")
    @ApiOperation("模型绑定扩展属性数据")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "reaIds", value = "多个扩展属性id 例如['1','2']", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-添加编辑 2-删除", dataType = "int", required = true)
    })
    public ResponseResult<Void> modelBindReaData(String modelId, String reaIds, Integer type) {
        return modelService.modelBindReaData(modelId, JSON.parseArray(reaIds, String.class), type);
    }

    @PostMapping("saveModelTopology")
    @ApiOperation("新增编辑模型拓扑节点")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "主键id", dataType = "String"),
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "nodeName", value = "节点名称", dataType = "String", required = true)
    })
    public ResponseResult<Void> saveModelTopology(String id, String modelId, String nodeName) {
        return modelService.saveModelTopology(id, modelId, nodeName);
    }

    @PostMapping("findModelTopologyListByModelId")
    @ApiOperation("根据模型id查询模型拓扑节点列表")
    @ApiOperationSupport(order = 15)
    @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true)
    public ResponseResult<List<ModelTopologyDto>> findModelTopologyListByModelId(String modelId) {
        return modelService.findModelTopologyListByModelId(modelId);
    }

    @PostMapping("deleteModelTopologyById")
    @ApiOperation("根据拓扑节点id删除模型拓扑节点数据")
    @ApiOperationSupport(order = 16)
    @ApiImplicitParam(name = "id", value = "拓扑节点id", dataType = "String", required = true)
    public ResponseResult<Void> deleteModelTopologyById(String id) {
        return modelService.deleteModelTopologyById(id);
    }

    @PostMapping("getModelEventFunctionList")
    @ApiOperation("获取模型事件相关可用功能点数据列表")
    @ApiOperationSupport(order = 17)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "calculateType", value = "计算类型 1-值运算 2-位运算", dataType = "int", required = true)
    })
    public ResponseResult<List<ModelEventFunctionDto>> getModelEventFunctionList(String modelId, Integer calculateType) {
        return modelService.getModelEventFunctionList(modelId, calculateType);
    }

    @PostMapping("saveModelEvent")
    @ApiOperation("添加模型事件")
    @ApiOperationSupport(order = 18)
    public ResponseResult<Void> saveModelEvent(ModelEventChangeVo eventChangeVo) {
        return modelService.saveModelEvent(eventChangeVo);
    }

    @PostMapping("findModelEventList")
    @ApiOperation("根据查询条件查询模型事件列表")
    @ApiOperationSupport(order = 19)
    public ResponseResult<PageDto<ModelEventListDto>> findModelEventList(ModelEventQueryVo eventQueryVo) {
        return modelService.findModelEventList(eventQueryVo);
    }

    @PostMapping("findModelEventListByModelId")
    @ApiOperation("根据模型事件id查询模型事件详情")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParam(name = "id", value = "模型事件id", dataType = "String", required = true)
    public ResponseResult<ModelEventDetailDto> findModelEventById(String id) {
        return modelService.findModelEventById(id);
    }

    @PostMapping("batchDeleteModelEventByIds")
    @ApiOperation("批量删除模型事件数据")
    @ApiOperationSupport(order = 21)
    @ApiImplicitParam(name = "ids", value = "多个模型事件id 例如['111','222']", dataType = "String", required = true)
    public ResponseResult<Void> batchDeleteModelEventByIds(String ids) {
        return modelService.batchDeleteModelEventByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("importPileFaultList")
    @ApiOperation("导入电桩故障数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "file", value = "文件", dataType = "File", required = true)
    })
    @ApiOperationSupport(order = 22)
    public ResponseResult<ImportResultDto> importPileFaultList(String modelId, MultipartFile file) {
        return modelService.importPileFaultList(modelId, file);
    }

    @PostMapping("findPileFaultListByModelId")
    @ApiOperation("根据模型id查询电桩故障定义列表")
    @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true)
    @ApiOperationSupport(order = 23)
    public ResponseResult<List<PileFaultListDto>> findPileFaultListByModelId(String modelId) {
        return modelService.findPileFaultListByModelId(modelId);
    }

    @PostMapping("getAssetTypeList")
    @ApiOperation("获取资产分类列表")
    @ApiOperationSupport(order = 24)
    public ResponseResult<List<AssetTypeDto>> getAssetTypeList(String ids) {
        return modelService.getAssetTypeList(ids);
    }

    @PostMapping("savePileFault")
    @ApiOperation("新增或编辑电桩故障定义数据")
    @ApiOperationSupport(order = 25)
    public ResponseResult<Void> savePileFault(PileFaultChangeVo pileFaultVo) {
        return modelService.savePileFault(pileFaultVo);
    }

    @PostMapping("deletePileFaultById")
    @ApiOperation("根据主键id删除电桩故障")
    @ApiOperationSupport(order = 26)
    public ResponseResult<Void> deletePileFaultById(String id) {
        return modelService.deletePileFaultById(id);
    }

    @PostMapping("updateModelReaValue")
    @ApiOperation("编辑模型扩展属性默认值")
    @ApiOperationSupport(order = 27)
    public ResponseResult<Void> updateModelReaValue(String id, String defaultValue) {
        return modelService.updateModelReaValue(id, defaultValue);
    }

    @PostMapping("updateModelFunctionValue")
    @ApiOperation("更新模型功能点数据定义")
    @ApiOperationSupport(order = 28)
    public ResponseResult<Void> updateModelFunctionValue(ModelFunctionChangeVo modelFunctionVo) {
        return modelService.updateModelFunctionValue(modelFunctionVo);
    }

}
