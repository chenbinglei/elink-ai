package com.sunmax.data.service;

import com.sunmax.data.model.TableDataModel;
import com.sunmax.data.model.TableDifDataModel;
import com.sunmax.data.vo.DeviceDataVo;

import java.util.List;
import java.util.Map;

public interface AccessDataService {

    /**
     * 批量插入功能点数据
     *
     * @param tableName     表名
     * @param deviceDataVos 设备数据参数
     */
    void batchInsertTableData(String tableName, Long currentTime, List<DeviceDataVo> deviceDataVos);

    /**
     * 根据表名和字段名和查询时间查询通道下点表数据
     *
     * @param pointTableQueryMap 点表查询参数
     * @param startTime          开始时间
     * @param endTime            结束时间
     * @param timeInterval       时间间隔
     * @param limitSize          条数
     * @return 表名 -> 数据[{字段名称 -> 数据值}]
     */
    Map<String, Map<String, List<TableDataModel>>> queryTableDataList(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval, Integer limitSize);

    /**
     * 根据表名和字段名和查询时间查询通道下点表数据（可查询指定单个时间（单个值、临近值）或指定单个时间或时间段的原始数据(没加last或first函数)、数据,计算节点专用）
     *
     * @param pointTableQueryMap 点表查询参数
     * @param startTime          开始时间
     * @param endTime            结束时间（可为空，为空就只查开始时间单个值数据）
     * @param timeInterval       时间间隔（可为空）
     * @param isNear             是否查临近值 1-是
     * @return 表名 -> 数据[{字段名称 -> 数据值}]
     */
    Map<String, Map<String, List<TableDataModel>>> queryDifferenceTableDataList(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval, Integer isNear);

    /**
     * 根据表名和字段名和查询时间查询通道下统计值点表数据（查询统计函数）
     * @param pointTableQueryMap 点表查询参数
     * @param startTime          开始时间
     * @param endTime            结束时间
     * @param timeInterval       时间间隔（可为空）
     * @param cuntFun            统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG
     * @return 表名 -> 数据[{字段名称 -> 数据值}]
     */
    Map<String, Map<String, List<TableDataModel>>> queryCountTableDataList(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval, String cuntFun);

    /**
     * 查询设备功能点指定时间段内的last和first历史数据
     * @param pointTableQueryMap 点表查询参数
     * @param startTime          开始时间
     * @param endTime            结束时间
     * @param timeInterval       时间间隔
     * @return
     */
    Map<String, Map<String, List<TableDifDataModel>>> findNodeDifHistoryListFeign(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval);

    /**
     *根据表名和字段名和查询时间查询通道下点表数据(会返回功能点数据为空的数据)
     *
     * @param pointTableQueryMap 点表查询参数
     * @param startTime          开始时间
     * @param endTime            结束时间
     * @param timeInterval       时间间隔
     * @param limitSize          条数
     * @return 表名 -> 数据[{字段名称 -> 数据值}]
     * @return
     */
    Map<String, Map<String, List<TableDataModel>>> queryFunctionPointTableDataList(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval, Integer limitSize);
}
