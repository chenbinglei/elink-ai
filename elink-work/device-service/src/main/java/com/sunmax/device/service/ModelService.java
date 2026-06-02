package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.common.dto.device.ModelDetailDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.model.*;
import com.sunmax.device.vo.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ModelService {

    /**
     * 新增编辑模型数据
     * @param modelChangeVo 模型编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveModel(ModelChangeVo modelChangeVo, MultipartFile logoFile);

    /**
     * 查询模型列表
     * @param modelQueryVo 模型查询条件
     * @return 模型数据列表
     */
    ResponseResult<PageDto<ModelListDto>> queryModelList(ModelQueryVo modelQueryVo);

    /**
     * 模型发布(更新模型状态)
     * @param id 模型id
     * @param userId 用户id
     * @return 状态码
     */
    ResponseResult<Void> updateModelStatus(String id, String userId);

    /**
     * 根据模型id删除模型数据
     * @param id 模型id
     * @return 状态码
     */
    ResponseResult<Void> deleteModelById(String id);

    /**
     * 根据模型id查询模型详情数据
     * @param id 模型id
     * @return 模型详情数据
     */
    ResponseResult<ModelDetailDto> findModelDetailById(String id);

    /**
     * 绑定模型设备功能显示设置
     * @param modelId 模型id
     * @param functionIds 多个标准功能id 如['1111','2222']
     * @return 状态码
     */
    ResponseResult<Void> bindModelFunctionData(String modelId, String functionIds);

    /**
     * 根据模型id查询模型下面设备列表
     * @param modelId 模型id
     * @param keyword 关键词
     * @param page 当前页
     * @param size 当前页条数
     * @return 模型下面的设备数据
     */
    ResponseResult<ModelDeviceDto> findModelDeviceListByModelId(String modelId, String keyword, Integer page, Integer size);

    /**
     * 根据模型id查询模型关联标准功能数据
     * @param modelId 模型id
     * @param page 当前页
     * @param size 当前页条数
     * @return 模型关联标准功能数据
     */
    ResponseResult<PageDto<ModelFunctionListDto>> findModelFunctionListByModelId(String modelId, String functionName, Integer page, Integer size);

    /**
     * 根据模型id查询模型绑定标准功能列表
     * @param modelId 模型id
     * @return 模型绑定标准功能数据
     */
    ResponseResult<ModelBindFunctionDto> findModelBindFunctionByModelId(String modelId);

    /**
     * 模型绑定标准功能数据
     * @param modelId 模型id
     * @param functionMap 多个标准功能id和序列号 例如{'功能点id1':序列号1,'功能点id2':序列号2}
     * @param type 类型 1-添加编辑 2-删除
     * @return 状态码
     */
    ResponseResult<Void> modelBindFunctionData(String modelId, Map<String, Integer> functionMap, Integer type);

    /**
     * 根据模型id查询模型关联扩展属性数据
     * @param modelId 模型id
     * @param reaName 扩展属性名称
     * @param page 当前页
     * @param size 当前页条数
     * @return 状态码
     */
    ResponseResult<PageDto<ModelReaListDto>> findModelReaListByModelId(String modelId, String reaName, Integer page, Integer size);

    /**
     * 根据模型id查询模型绑定扩展属性列表
     * @param modelId 模型id
     * @return 模型绑定扩展属性列表
     */
    ResponseResult<ModelBindReaDto> findModelBindReaByModelId(String modelId);

    /**
     * 模型绑定扩展属性数据
     * @param modelId 模型id
     * @param reaIds 多个扩展属性id 例如["1","2"]
     * @param type 类型 1-添加编辑 2-删除
     * @return 状态码
     */
    ResponseResult<Void> modelBindReaData(String modelId, List<String> reaIds, Integer type);

    /**
     * 添加模型拓扑节点
     * @param id 主键id
     * @param modelId 模型id
     * @param nodeName 节点名称
     * @return 状态码
     */
    ResponseResult<Void> saveModelTopology(String id, String modelId, String nodeName);

    /**
     * 根据模型id查询模型拓扑节点列表
     * @param modelId 模型id
     * @return 模型拓扑节点列表
     */
    ResponseResult<List<ModelTopologyDto>> findModelTopologyListByModelId(String modelId);

    /**
     * 根据拓扑节点id删除模型拓扑节点数据
     * @param id 拓扑节点id
     * @return 状态码
     */
    ResponseResult<Void> deleteModelTopologyById(String id);

    /**
     * 获取模型事件相关可用功能点数据列表
     * @param modelId 模型id
     * @param calculateType 计算类型 1-值运算 2-位运算
     * @return 模型事件功能点数据列表
     */
    ResponseResult<List<ModelEventFunctionDto>> getModelEventFunctionList(String modelId, Integer calculateType);

    /**
     * 添加模型事件
     * @param eventChangeVo 模型事件编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveModelEvent(ModelEventChangeVo eventChangeVo);

    /**
     * 根据查询条件查询模型事件列表
     * @param eventQueryVo 模型事件查询参数
     * @return 模型事件列表数据
     */
    ResponseResult<PageDto<ModelEventListDto>> findModelEventList(ModelEventQueryVo eventQueryVo);

    /**
     * 根据模型事件id查询模型事件详情
     * @param id 模型事件id
     * @return 模型事件详情数据
     */
    ResponseResult<ModelEventDetailDto> findModelEventById(String id);

    /**
     * 批量删除模型事件数据
     * @param ids 多个模型事件id 例如['111','222']
     * @return 状态码
     */
    ResponseResult<Void> batchDeleteModelEventByIds(List<String> ids);


    /**
     * 导入电桩故障数据
     * @param modelId 模型id
     * @param file 文件
     * @return 导入结果
     */
    ResponseResult<ImportResultDto> importPileFaultList(String modelId, MultipartFile file);

    /**
     * 根据模型id查询电桩告警故障定义列表
     * @param modelId 模型id
     * @return 电桩告警故障定义列表
     */
    ResponseResult<List<PileFaultListDto>> findPileFaultListByModelId(String modelId);

    /**
     * 获取资产分类列表
     * @param ids 多个资产id
     * @return 设备类型列表
     */
    ResponseResult<List<AssetTypeDto>> getAssetTypeList(String ids);

    /**
     * 新增或编辑电桩故障定义数据
     * @param pileFaultVo 电桩故障定义数据
     * @return 状态码
     */
    ResponseResult<Void> savePileFault(PileFaultChangeVo pileFaultVo);

    /**
     * 根据主键id删除电桩故障
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deletePileFaultById(String id);

    /**
     * 编辑模型扩展属性默认值
     * @param id 主键id
     * @param defaultValue 默认值
     * @return 状态码
     */
    ResponseResult<Void> updateModelReaValue(String id, String defaultValue);

    /**
     * 编辑模型标准功能默认值
     * @param modelFunctionVo 模型标准功能数据
     * @return 状态码
     */
    ResponseResult<Void> updateModelFunctionValue(ModelFunctionChangeVo modelFunctionVo);

}
