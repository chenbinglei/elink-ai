package com.sunmax.crontab.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.dto.SystemVariableDto;
import com.sunmax.crontab.service.SystemVariableService;
import com.sunmax.crontab.vo.ComputeNodeListVo;
import com.sunmax.crontab.vo.SystemVariableChangeVo;
import com.sunmax.crontab.vo.SystemVariableQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("systemVariable")
@Tag(name = "系统变量管理")
public class SystemVariableController {

    @Autowired
    private SystemVariableService systemVariableService;

    @PostMapping("saveOrUpdateSystemVariable")
    @Operation(summary = "添加或编辑系统变量数据")
    
    public ResponseResult<String> saveOrUpdateSystemVariable(SystemVariableChangeVo systemVariableChangeVo) {
        return systemVariableService.saveOrUpdateSystemVariable(systemVariableChangeVo);
    }

    @PostMapping("findSystemVariableListByPage")
    @Operation(summary = "分页查询系统变量数据")
    
    public ResponseResult<PageDto<SystemVariableDto>> findSystemVariableListByPage(SystemVariableQueryVo systemVariableQueryVo) {
        return systemVariableService.findSystemVariableListByPage(systemVariableQueryVo);
    }

    @PostMapping("deleteSystemVariableById")
    @Operation(summary = "根据变量id删除系统变量数据")
    
    @Parameters({
            @Parameter(name = "id", description = "变量唯一id")
    })
    public ResponseResult<String> deleteSystemVariableById(String id) {
        return systemVariableService.deleteSystemVariableById(id);
    }

    @PostMapping("deleteVariableNodeById")
    @Operation(summary = "根据关联实例id删除关联实例数据")
    
    @Parameters({
            @Parameter(name = "id", description = "实例唯一id")
    })
    public ResponseResult<String> deleteVariableNodeById(String id) {
        return systemVariableService.deleteVariableNodeById(id);
    }

    @PostMapping("addVariableNode")
    @Operation(summary = "添加实例")
    
    @Parameters({
            @Parameter(name = "varId", description = "变量id"),
            @Parameter(name = "deviceId", description = "所选设备/站点/模型id"),
            @Parameter(name = "nodeId", description = "节点id(跟随映射类型变化)"),
            @Parameter(name = "storageId", description = "存储id(跟随映射类型变化)"),
            @Parameter(name = "functionId", description = "功能点id(跟随映射类型变化)")
    })
    public ResponseResult<String> addVariableNode(String varId, String deviceId, String nodeId, Long storageId, String functionId) {
        return systemVariableService.addVariableNode(varId, deviceId, nodeId, storageId, functionId);
    }

    @PostMapping("findNotComputeNodeByPage")
    @Operation(summary = "分页查询未被关联的计算节点数据")
    
    @Parameters({
            @Parameter(name = "varId", description = "变量id")
    })
    public ResponseResult<PageDto<ComputeNodeListDto>> findNotComputeNodeByPage(ComputeNodeListVo computeNodeListVo, String varId) {
        return systemVariableService.findNotComputeNodeByPage(computeNodeListVo, varId);
    }

    @PostMapping("findModelFunctionListByPage")
    @Operation(summary = "分页查询未被关联的模型功能点")
    
    @Parameters({
            @Parameter(name = "modelId", description = "模型id"),
            @Parameter(name = "varId", description = "变量id"),
            @Parameter(name = "functionName", description = "标准功能名称"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<ModelFunctionListDto>> findModelFunctionListByPage(String modelId, String varId, String functionName, Integer page, Integer size) {
        return systemVariableService.findModelFunctionListByPage(modelId, varId, functionName, page, size);
    }
}
