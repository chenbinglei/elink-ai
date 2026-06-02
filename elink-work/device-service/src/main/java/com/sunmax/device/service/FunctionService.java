package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.device.FunctionDetailDto;
import com.sunmax.device.dto.model.FunctionListDto;
import com.sunmax.device.dto.model.PileRealFieldDto;
import com.sunmax.device.vo.model.FunctionChangeVo;
import com.sunmax.device.vo.model.FunctionQueryVo;

import java.util.List;

public interface FunctionService {

    /**
     * 新增编辑模型标准功能
     * @param functionChangeVo 模型标准功能编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveFunction(FunctionChangeVo functionChangeVo);

    /**
     * 查询模型标准功能列表
     * @param functionQueryVo 模型标准功能查询参数
     * @return 模型标准功能列表
     */
    ResponseResult<PageDto<FunctionListDto>> queryFunctionList(FunctionQueryVo functionQueryVo);

    /**
     * 删除模型标准功能属性
     * @param id 模型标准功能id
     * @param deleteLogo 删除标识 true-删除 false-不删除
     * @return 状态码
     */
    ResponseResult<Void> deleteFunctionById(String id, Boolean deleteLogo);

    /**
     * 根据主键id查询标准功能详情
     * @param id 模型标准功能id
     * @return 标准功能详情数据
     */
    ResponseResult<FunctionDetailDto> findFunctionDetailById(String id);

    /**
     * 获取电桩协议字段列表
     * @param dataType 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     * @return 电桩协议相关字段
     */
    ResponseResult<List<PileRealFieldDto>> getPileRealFieldList(Integer dataType);

}
