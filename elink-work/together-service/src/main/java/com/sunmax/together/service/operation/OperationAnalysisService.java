package com.sunmax.together.service.operation;

import com.sunmax.common.dto.together.PileMonitorDataDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.operationAnalysis.OperationCurveDto;
import com.sunmax.together.dto.operation.operationAnalysis.OperationOverviewDto;
import com.sunmax.together.dto.operation.operationAnalysis.PvOperationAnalysisDto;
import com.sunmax.common.vo.together.OperationCurveVo;
import com.sunmax.common.vo.together.OperationOverviewVo;
import com.sunmax.together.vo.operation.operationAnalysis.PvOperationAnalysisVo;

import java.util.List;
import java.util.Map;

public interface OperationAnalysisService {

    /**
     * 统计运行总览数据
     * @param operationOverviewVo 运行总览查询条件
     * @return 运行总览数据
     */
    ResponseResult<OperationOverviewDto> countOperationOverview(OperationOverviewVo operationOverviewVo);

    /**
     * 统计运营总览曲线数据
     * @param operationCurveVo 运行曲线查询条件
     * @return 运行曲线数据
     */
    ResponseResult<OperationCurveDto> countOperationCurve(OperationCurveVo operationCurveVo);

    /**
     * 添加站点运行统计记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 添加结果
     */
    ResponseResult<Void> addAllSiteCountRecord(String startDate, String endDate);

    /**
     * 统计PV运营分析数据
     * @param pvOperationAnalysisVo PV运营分析查询条件
     * @return PV运营分析数据
     */
    ResponseResult<PvOperationAnalysisDto> countPvOperationAnalysis(PvOperationAnalysisVo pvOperationAnalysisVo);

    /**
     * 根据站点id统计运行总览数据
     * @param operationOverviewVo 运行总览查询条件
     * @return 运行总览数据
     */
    ResponseResult<Map<String, OperationOverviewDto>> countOperationOverviewBySiteId(OperationOverviewVo operationOverviewVo);

    /**
     * 统计电桩监控数据
     * @param dataIds 多个数据id
     * @param type 类型 1-站点级 2-设备级
     * @return 运行曲线数据
     */
    ResponseResult<Map<String, PileMonitorDataDto>> countPileMonitorData(List<String> dataIds, Integer type);

}
