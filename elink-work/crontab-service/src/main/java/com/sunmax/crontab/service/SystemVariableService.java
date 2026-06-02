package com.sunmax.crontab.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.dto.SystemVariableDto;
import com.sunmax.crontab.vo.ComputeNodeListVo;
import com.sunmax.crontab.vo.SystemVariableChangeVo;
import com.sunmax.crontab.vo.SystemVariableQueryVo;

public interface SystemVariableService {

    /**
     * 添加或编辑系统变量数据
     * @param systemVariableChangeVo
     * @return
     */
    ResponseResult<String> saveOrUpdateSystemVariable(SystemVariableChangeVo systemVariableChangeVo);

    /**
     * 分页查询系统变量数据
     * @param systemVariableQueryVo
     * @return
     */
    ResponseResult<PageDto<SystemVariableDto>> findSystemVariableListByPage(SystemVariableQueryVo systemVariableQueryVo);

    /**
     * 根据变量id删除系统变量数据
     * @param id
     * @return
     */
    ResponseResult<String> deleteSystemVariableById(String id);

    /**
     * 根据关联实例id删除关联实例数据
     * @param id
     * @return
     */
    ResponseResult<String> deleteVariableNodeById(String id);

    /**
     * 添加实例
     *
     * @param varId
     * @param deviceId
     * @param nodeId
     * @param storageId
     * @param functionId
     * @return
     */
    ResponseResult<String> addVariableNode(String varId, String deviceId, String nodeId, Long storageId, String functionId);

    /**
     * 分页查询未被关联的计算节点数据
     * @param computeNodeListVo
     * @param varId
     * @return
     */
    ResponseResult<PageDto<ComputeNodeListDto>> findNotComputeNodeByPage(ComputeNodeListVo computeNodeListVo, String varId);

    /**
     * 分页查询未被关联的模型功能点
     * @param modelId
     * @param varId
     * @param functionName
     * @param page
     * @param size
     * @return
     */
    ResponseResult<PageDto<ModelFunctionListDto>> findModelFunctionListByPage(String modelId, String varId, String functionName, Integer page, Integer size);
}
