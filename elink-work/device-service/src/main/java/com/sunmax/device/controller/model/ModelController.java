package com.sunmax.device.controller.model;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.common.dto.device.ModelDetailDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.model.*;
import com.sunmax.device.service.ModelService;
import com.sunmax.device.vo.model.*;

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
@Tag(name = "模型管理控制层")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @PostMapping("saveModel")
    @Operation(summary = "新增编辑模型数据")
    
    public ResponseResult<Void> saveModel(ModelChangeVo modelChangeVo, MultipartFile logoFile) {
        return modelService.saveModel(modelChangeVo, logoFile);
    }

    @PostMapping("queryModelList")
    @Operation(summary = "查询模型列表")
    
    public ResponseResult<PageDto<ModelListDto>> queryModelList(ModelQueryVo modelQueryVo) {
        return modelService.queryModelList(modelQueryVo);
    }

    @PostMapping("updateModelStatus")
    @Operation(summary = "模型发布(更新模型状态)")
    
    @Parameters({
            @Parameter(name = "id", description = "模型id"),
            @Parameter(name = "userId", description = "用户id")
    })
    public ResponseResult<Void> updateModelStatus(String id, String userId) {
        return modelService.updateModelStatus(id, userId);
    }

    @PostMapping("deleteModelById")
    @Operation(summary = "删除模型数据")
    
    @Parameter(name = "id", description = "模型id")
    public ResponseResult<Void> deleteModelById(String id) {
        return modelService.deleteModelById(id);
    }

    @PostMapping("findModelDetailById")
    @Operation(summary = "根据模型id查询模型详情数据")
    
    @Parameter(name = "id", description = "模型id")
    public ResponseResult<ModelDetailDto> findModelDetailById(String id) {
        return modelService.findModelDetailById(id);
    }

    @PostMapping("bindModelFunctionData")
    @Operation(summary = "绑定模型设备功能显示设置")
    
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "functionIds", description = "多个标准功能id 如['1111','2222']")
    })
    public ResponseResult<Void> bindModelFunctionData(String modelId, String functionIds) {
        return modelService.bindModelFunctionData(modelId, functionIds);
    }

    @PostMapping("findModelDeviceListByModelId")
    @Operation(summary = "根据模型id查询模型下面设备列表")
    
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "keyword", description = "关键词 设备ID或设备名称"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<ModelDeviceDto> findModelDeviceListByModelId(String modelId, String keyword, Integer page, Integer size) {
        return modelService.findModelDeviceListByModelId(modelId, keyword, page, size);
    }

    @PostMapping("findModelFunctionListByModelId")
    @Operation(summary = "根据模型id查询模型关联标准功能数据")
    
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "functionName", description = "标准功能名称"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<ModelFunctionListDto>> findModelFunctionListByModelId(String modelId, String functionName, Integer page, Integer size) {
        return modelService.findModelFunctionListByModelId(modelId, functionName, page, size);
    }

    @PostMapping("findModelBindFunctionByModelId")
    @Operation(summary = "根据模型id查询模型绑定标准功能列表")
    
    @Parameter(name = "modelId", description = "模型id")
    public ResponseResult<ModelBindFunctionDto> findModelBindFunctionByModelId(String modelId) {
        return modelService.findModelBindFunctionByModelId(modelId);
    }

    @PostMapping("modelBindFunctionData")
    @Operation(summary = "模型绑定标准功能数据")
    
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "functionMap", description = "多个标准功能id和序列号 例如{'功能点id1':序列号1,'功能点id2':序列号2}"),
            @Parameter(name = "type", description = "类型 1-添加编辑 2-删除")
    })
    public ResponseResult<Void> modelBindFunctionData(String modelId, String functionMap, Integer type) {
        return modelService.modelBindFunctionData(modelId, JSON.parseObject(functionMap, new TypeReference<Map<String, Integer>>(){}), type);
    }

    @PostMapping("findModelReaListByModelId")
    @Operation(summary = "根据模型id查询模型关联扩展属性数据")
    
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "reaName", description = "扩展属性名称"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<ModelReaListDto>> findModelReaListByModelId(String modelId, String reaName, Integer page, Integer size) {
        return modelService.findModelReaListByModelId(modelId, reaName, page, size);
    }

    @PostMapping("findModelBindReaByModelId")
    @Operation(summary = "根据模型id查询模型绑定扩展属性列表")
    
    @Parameter(name = "modelId", description = "模型id")
    public ResponseResult<ModelBindReaDto> findModelBindReaByModelId(String modelId) {
        return modelService.findModelBindReaByModelId(modelId);
    }

    @PostMapping("modelBindReaData")
    @Operation(summary = "模型绑定扩展属性数据")
    
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "reaIds", description = "多个扩展属性id 例如['1','2']"),
            @Parameter(name = "type", description = "类型 1-添加编辑 2-删除")
    })
    public ResponseResult<Void> modelBindReaData(String modelId, String reaIds, Integer type) {
        return modelService.modelBindReaData(modelId, JSON.parseArray(reaIds, String.class), type);
    }

    @PostMapping("saveModelTopology")
    @Operation(summary = "新增编辑模型拓扑节点")
    
    @Parameters({
            @Parameter(name = "modelId", description = "主键id"),
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "nodeName", description = "节点名称")
    })
    public ResponseResult<Void> saveModelTopology(String id, String modelId, String nodeName) {
        return modelService.saveModelTopology(id, modelId, nodeName);
    }

    @PostMapping("findModelTopologyListByModelId")
    @Operation(summary = "根据模型id查询模型拓扑节点列表")
    
    @Parameter(name = "modelId", description = "模型id")
    public ResponseResult<List<ModelTopologyDto>> findModelTopologyListByModelId(String modelId) {
        return modelService.findModelTopologyListByModelId(modelId);
    }

    @PostMapping("deleteModelTopologyById")
    @Operation(summary = "根据拓扑节点id删除模型拓扑节点数据")
    
    @Parameter(name = "id", description = "拓扑节点id")
    public ResponseResult<Void> deleteModelTopologyById(String id) {
        return modelService.deleteModelTopologyById(id);
    }

    @PostMapping("getModelEventFunctionList")
    @Operation(summary = "获取模型事件相关可用功能点数据列表")
    
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "calculateType", description = "计算类型 1-值运算 2-位运算")
    })
    public ResponseResult<List<ModelEventFunctionDto>> getModelEventFunctionList(String modelId, Integer calculateType) {
        return modelService.getModelEventFunctionList(modelId, calculateType);
    }

    @PostMapping("saveModelEvent")
    @Operation(summary = "添加模型事件")
    
    public ResponseResult<Void> saveModelEvent(ModelEventChangeVo eventChangeVo) {
        return modelService.saveModelEvent(eventChangeVo);
    }

    @PostMapping("findModelEventList")
    @Operation(summary = "根据查询条件查询模型事件列表")
    
    public ResponseResult<PageDto<ModelEventListDto>> findModelEventList(ModelEventQueryVo eventQueryVo) {
        return modelService.findModelEventList(eventQueryVo);
    }

    @PostMapping("findModelEventListByModelId")
    @Operation(summary = "根据模型事件id查询模型事件详情")
    
    @Parameter(name = "id", description = "模型事件id")
    public ResponseResult<ModelEventDetailDto> findModelEventById(String id) {
        return modelService.findModelEventById(id);
    }

    @PostMapping("batchDeleteModelEventByIds")
    @Operation(summary = "批量删除模型事件数据")
    
    @Parameter(name = "ids", description = "多个模型事件id 例如['111','222']")
    public ResponseResult<Void> batchDeleteModelEventByIds(String ids) {
        return modelService.batchDeleteModelEventByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("importPileFaultList")
    @Operation(summary = "导入电桩故障数据")
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "file", description = "文件")
    })
    
    public ResponseResult<ImportResultDto> importPileFaultList(String modelId, MultipartFile file) {
        return modelService.importPileFaultList(modelId, file);
    }

    @PostMapping("findPileFaultListByModelId")
    @Operation(summary = "根据模型id查询电桩故障定义列表")
    @Parameter(name = "modelId", description = "模型id")
    
    public ResponseResult<List<PileFaultListDto>> findPileFaultListByModelId(String modelId) {
        return modelService.findPileFaultListByModelId(modelId);
    }

    @PostMapping("getAssetTypeList")
    @Operation(summary = "获取资产分类列表")
    
    public ResponseResult<List<AssetTypeDto>> getAssetTypeList(String ids) {
        return modelService.getAssetTypeList(ids);
    }

    @PostMapping("savePileFault")
    @Operation(summary = "新增或编辑电桩故障定义数据")
    
    public ResponseResult<Void> savePileFault(PileFaultChangeVo pileFaultVo) {
        return modelService.savePileFault(pileFaultVo);
    }

    @PostMapping("deletePileFaultById")
    @Operation(summary = "根据主键id删除电桩故障")
    
    public ResponseResult<Void> deletePileFaultById(String id) {
        return modelService.deletePileFaultById(id);
    }

    @PostMapping("updateModelReaValue")
    @Operation(summary = "编辑模型扩展属性默认值")
    
    public ResponseResult<Void> updateModelReaValue(String id, String defaultValue) {
        return modelService.updateModelReaValue(id, defaultValue);
    }

    @PostMapping("updateModelFunctionValue")
    @Operation(summary = "更新模型功能点数据定义")
    
    public ResponseResult<Void> updateModelFunctionValue(ModelFunctionChangeVo modelFunctionVo) {
        return modelService.updateModelFunctionValue(modelFunctionVo);
    }

}
