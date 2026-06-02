package com.sunmax.crontab.controller;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.dto.SystemVariableDto;
import com.sunmax.crontab.service.SystemVariableService;
import com.sunmax.crontab.vo.ComputeNodeListVo;
import com.sunmax.crontab.vo.SystemVariableChangeVo;
import com.sunmax.crontab.vo.SystemVariableQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("systemVariable")
@Api(tags = "系统变量管理")
public class SystemVariableController {

    @Autowired
    private SystemVariableService systemVariableService;

    @PostMapping("saveOrUpdateSystemVariable")
    @ApiOperation("添加或编辑系统变量数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> saveOrUpdateSystemVariable(SystemVariableChangeVo systemVariableChangeVo) {
        return systemVariableService.saveOrUpdateSystemVariable(systemVariableChangeVo);
    }

    @PostMapping("findSystemVariableListByPage")
    @ApiOperation("分页查询系统变量数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<SystemVariableDto>> findSystemVariableListByPage(SystemVariableQueryVo systemVariableQueryVo) {
        return systemVariableService.findSystemVariableListByPage(systemVariableQueryVo);
    }

    @PostMapping("deleteSystemVariableById")
    @ApiOperation("根据变量id删除系统变量数据")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "变量唯一id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteSystemVariableById(String id) {
        return systemVariableService.deleteSystemVariableById(id);
    }

    @PostMapping("deleteVariableNodeById")
    @ApiOperation("根据关联实例id删除关联实例数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "实例唯一id", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteVariableNodeById(String id) {
        return systemVariableService.deleteVariableNodeById(id);
    }

    @PostMapping("addVariableNode")
    @ApiOperation("添加实例")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "varId", value = "变量id", dataType = "String", required = true),
            @ApiImplicitParam(name = "deviceId", value = "所选设备/站点/模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "nodeId", value = "节点id(跟随映射类型变化)", dataType = "String"),
            @ApiImplicitParam(name = "storageId", value = "存储id(跟随映射类型变化)", dataType = "Long"),
            @ApiImplicitParam(name = "functionId", value = "功能点id(跟随映射类型变化)", dataType = "String")
    })
    public ResponseResult<String> addVariableNode(String varId, String deviceId, String nodeId, Long storageId, String functionId) {
        return systemVariableService.addVariableNode(varId, deviceId, nodeId, storageId, functionId);
    }

    @PostMapping("findNotComputeNodeByPage")
    @ApiOperation("分页查询未被关联的计算节点数据")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "varId", value = "变量id", dataType = "String", required = true)
    })
    public ResponseResult<PageDto<ComputeNodeListDto>> findNotComputeNodeByPage(ComputeNodeListVo computeNodeListVo, String varId) {
        return systemVariableService.findNotComputeNodeByPage(computeNodeListVo, varId);
    }

    @PostMapping("findModelFunctionListByPage")
    @ApiOperation("分页查询未被关联的模型功能点")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "模型id", dataType = "String", required = true),
            @ApiImplicitParam(name = "varId", value = "变量id", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionName", value = "标准功能名称", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    public ResponseResult<PageDto<ModelFunctionListDto>> findModelFunctionListByPage(String modelId, String varId, String functionName, Integer page, Integer size) {
        return systemVariableService.findModelFunctionListByPage(modelId, varId, functionName, page, size);
    }
}
