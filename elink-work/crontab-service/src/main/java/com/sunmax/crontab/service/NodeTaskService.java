package com.sunmax.crontab.service;

import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.NodeHistoryDataVo;
import com.sunmax.crontab.vo.ComputeNodeTaskVo;

import java.util.List;
import java.util.Map;

public interface NodeTaskService {

    /**
     * 添加所有节点至定时任务中
     * @param computeNodeTaskVoList
     */
    void addAllComputeNodeTask(List<ComputeNodeTaskVo> computeNodeTaskVoList);

    /**
     * 删除该节点定时任务以及节点表
     * @param storageId
     */
    ResponseResult<Boolean> deleteComputeNodeTaskAndTaosData(Long storageId);

    /**
     * 批量修改模型节点定时任务
     * @param computeNodeTaskVoList
     */
    void updateComputeNodeListTask(List<ComputeNodeTaskVo> computeNodeTaskVoList);

    /**
     * 根据多个节点存储id查询节点数据
     * @return
     */
    Map<Long, List<NodeHistoryDataDto>> findNodeTaosDataByIds(NodeHistoryDataVo nodeHistoryDataVo);

    /**
     * 根据多个节点存储id查询节点数据
     * @return
     */
    Map<Long, List<NodeHistoryDataDto>> findNodeTaosDiffDataByIds(NodeHistoryDataVo nodeHistoryDataVo);

    /**
     * 根据多个节点存储id和统计函数查询节点统计值数据
     * @param storageIdList 多个节点存储id
     * @param startTime 开始时间-可为空
     * @param endTime 结束时间-可为空
     * @param cuntFun 统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG
     * @param timeInterval 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年(可为空)
     * @return 存储id -> 统计值列表
     */
    Map<Long, List<NodeHistoryDataDto>> findNodeTaosCountFunDataByIds(List<Long> storageIdList, String startTime, String endTime, String cuntFun, String timeInterval);
}
