package com.sunmax.device.controller.model;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.device.FunctionDetailDto;
import com.sunmax.device.dto.model.FunctionListDto;
import com.sunmax.device.dto.model.PileRealFieldDto;
import com.sunmax.device.service.FunctionService;
import com.sunmax.device.vo.model.FunctionChangeVo;
import com.sunmax.device.vo.model.FunctionQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模型标准功能管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("function")
@Api(tags = "模型标准功能管理控制层")
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @PostMapping("saveFunction")
    @ApiOperation("新增编辑模型标准功能")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveFunction(FunctionChangeVo functionChangeVo) {
        return functionService.saveFunction(functionChangeVo);
    }

    @PostMapping("queryFunctionList")
    @ApiOperation("查询模型标准功能列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<FunctionListDto>> queryFunctionList(FunctionQueryVo functionQueryVo) {
        return functionService.queryFunctionList(functionQueryVo);
    }

    @PostMapping("deleteFunctionById")
    @ApiOperation("删除模型标准功能属性")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型标准功能id", dataType = "String", required = true),
            @ApiImplicitParam(name = "deleteLogo", value = "删除标识 true-删除 false-不删除", dataType = "Boolean", required = true)
    })
    public ResponseResult<Void> deleteFunctionById(String id, Boolean deleteLogo) {
        return functionService.deleteFunctionById(id, deleteLogo);
    }

    @PostMapping("findFunctionDetailById")
    @ApiOperation("根据主键id查询标准功能详情")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "id", value = "模型标准功能id", dataType = "String", required = true)
    public ResponseResult<FunctionDetailDto> findFunctionDetailById(String id) {
        return functionService.findFunctionDetailById(id);
    }

    @PostMapping("getPileRealFieldList")
    @ApiOperation("获取电桩协议字段列表")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "dataType", value = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)", dataType = "int", required = true)
    public ResponseResult<List<PileRealFieldDto>> getPileRealFieldList(Integer dataType) {
        return functionService.getPileRealFieldList(dataType);
    }

}
