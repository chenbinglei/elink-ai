package com.sunmax.device.controller.model;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.device.FunctionDetailDto;
import com.sunmax.device.dto.model.FunctionListDto;
import com.sunmax.device.dto.model.PileRealFieldDto;
import com.sunmax.device.service.FunctionService;
import com.sunmax.device.vo.model.FunctionChangeVo;
import com.sunmax.device.vo.model.FunctionQueryVo;

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
@Tag(name = "模型标准功能管理控制层")
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @PostMapping("saveFunction")
    @Operation(summary = "新增编辑模型标准功能")
    
    public ResponseResult<Void> saveFunction(FunctionChangeVo functionChangeVo) {
        return functionService.saveFunction(functionChangeVo);
    }

    @PostMapping("queryFunctionList")
    @Operation(summary = "查询模型标准功能列表")
    
    public ResponseResult<PageDto<FunctionListDto>> queryFunctionList(FunctionQueryVo functionQueryVo) {
        return functionService.queryFunctionList(functionQueryVo);
    }

    @PostMapping("deleteFunctionById")
    @Operation(summary = "删除模型标准功能属性")
    
    @Parameters({
            @Parameter(name = "id", description = "模型标准功能id"),
            @Parameter(name = "deleteLogo", description = "删除标识 true-删除 false-不删除")
    })
    public ResponseResult<Void> deleteFunctionById(String id, Boolean deleteLogo) {
        return functionService.deleteFunctionById(id, deleteLogo);
    }

    @PostMapping("findFunctionDetailById")
    @Operation(summary = "根据主键id查询标准功能详情")
    
    @Parameter(name = "id", description = "模型标准功能id")
    public ResponseResult<FunctionDetailDto> findFunctionDetailById(String id) {
        return functionService.findFunctionDetailById(id);
    }

    @PostMapping("getPileRealFieldList")
    @Operation(summary = "获取电桩协议字段列表")
    
    @Parameter(name = "dataType", description = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    public ResponseResult<List<PileRealFieldDto>> getPileRealFieldList(Integer dataType) {
        return functionService.getPileRealFieldList(dataType);
    }

}
