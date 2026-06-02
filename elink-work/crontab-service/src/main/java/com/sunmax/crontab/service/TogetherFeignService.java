package com.sunmax.crontab.service;

import com.sunmax.common.dto.crontab.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.DeviceNodeValueVo;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.common.vo.crontab.VarNodeCuntFunQueryVo;
import com.sunmax.common.vo.crontab.VarNodeValueVo;

import java.util.List;
import java.util.Map;

public interface TogetherFeignService {

    /**
     * 根据多个设备id和系统变量查询关联节点数据
     * @param varNodeValueVo
     * @return 设备id -> 变量标识 -> 节点历史数据
     */
    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceVarNodeValueByIds(VarNodeValueVo varNodeValueVo);

    /**
     * 根据站点id和系统变量查询关联节点数据
     * @param varNodeValueVo
     * @return 变量标识 -> 站点id -> 节点历史数据
     */
    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findSiteVarNodeValueByIds(VarNodeValueVo varNodeValueVo);

    /**
     * 根据多个变量编码和设备/站点id查询计算节点实时缓存数据
     * @param varCodeList 多个系统变量编码
     * @param queryIdList 多个设备/站点id
     * @return 系统变量编码 -> 设备/站点id -> 计算节点实时缓存数据
     */
    ResponseResult<Map<String, Map<String, LocalCacheDto>>> findNodeCacheByVarCodes(List<String> varCodeList, List<String> queryIdList);

    /**
     * 根据多个系统变量编码和多个设备/站点id查询计算节点统计值数据
     * @param varNodeCuntFunQueryVo
     * @return 系统变量标识 -> 设备/站点id -> 统计值数据
     */
    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findVarNodeDataByCountFun(VarNodeCuntFunQueryVo varNodeCuntFunQueryVo);

    /**
     * 批量修改自动策略任务
     *
     * @param strategyTaskVos 自动策略任务参数
     * @return 状态码
     */
    ResponseResult<Void> batchUpdateStrategyTask(List<StrategyTaskVo> strategyTaskVos);

    /**
     * 批量删除自动策略任务
     *
     * @param strategyTaskIds 多个策略任务id
     * @return 状态码
     */
    ResponseResult<Void> batchDeleteStrategyTask(List<String> strategyTaskIds);

    /**
     * 根据站点/设备id查询所关联所有系统变量数据列表
     * @param varCodes 多个系统变量编码(以逗号分割，不传则查询所关联全部系统变量)
     * @param queryId  设备/站点id
     * @return
     */
    ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(String varCodes, String queryId);

    /**
     * 查询计算节点指定时间段内的last和first历史数据
     * @param varNodeValueVo
     * @return 站点/设备id -> 变量标识 -> last和first历史数据
     */
    ResponseResult<Map<String, Map<String, List<NodeDifDataDto>>>> findNodeDifDataListFeign(VarNodeValueVo varNodeValueVo);

    /**
     * 根据多个设备id查询计算节点列表
     * @param deviceIds 多个设备id
     * @return 计算节点列表
     */
    ResponseResult<Map<String, List<ComputeNodeListDto>>> findComputeNodeListByDeviceIds(List<String> deviceIds);

    /**
     * 查询设备节点历史数据
     * @param deviceNodeValueVo 设备节点查询条件
     * @return 设备节点历史数据
     */
    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceNodeValueList(DeviceNodeValueVo deviceNodeValueVo);

}
