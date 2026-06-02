package com.sunmax.crontab.mapper.tdengine;

import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ComputeNodeMapper {

    //根据表名称查询表是否存在
    Map<String,Object> findTableIfExists(String tableName);

    //创建超级表
    void createSuperTable(@Param("stableName") String stableName);

    //根据表名删除表
    void dropTable(String tableName);

    //根据表名称查询超级表是否存在
    Map<String,Object> findSTableIfExists(String stableName);

    /**
     * 创建节点表
     * @param tableName
     * @param stableName
     * @param siteId
     * @param storageId
     */
    void createTaosTable(@Param("tableName") String tableName,@Param("stableName") String stableName,@Param("siteId") String siteId,@Param("storageId") Long storageId);

    /**
     * 根据时间间隔查询节点last和first数据
     * @param tableName
     * @param startTime
     * @param endTime
     * @param timeInterval
     * @return
     */
    List<NodeHistoryDataDto> findNodeDataListByTimeInterval(@Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("timeInterval") String timeInterval);

    /**
     * 查询最新一条节点数据
     * @param tableName
     * @return
     */
    NodeHistoryDataDto findNodeLastDataByTableName(@Param("tableName") String tableName);

    /**
     * 根据时间段查询节点数据
     * @param tableName
     * @param startTime
     * @param endTime
     * @return
     */
    List<NodeHistoryDataDto> findNodeDataByTime(@Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 查询最新一条节点临近值数据
     * @param tableName
     * @return
     */
    NodeHistoryDataDto findNodeFirstDataByTableName(@Param("tableName") String tableName);

    /**
     * 获取周期内的第一个临近值
     * @param tableName
     * @param startTime
     * @param endTime
     * @return
     */
    List<NodeHistoryDataDto> findNodeFirstDataByTime(@Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 存储数值
     * @param tableName
     * @param stableName
     * @param storageId
     * @param taosNodeValue
     * @param siteId
     * @param ts
     */
    void insertTableData(@Param("tableName") String tableName, @Param("stableName") String stableName, @Param("storageId") Long storageId, @Param("taosNodeValue") Double taosNodeValue, @Param("siteId") String siteId, @Param("ts") String ts);

    /**
     * 查询节点统计值数据
     * @param tableName
     * @param startTime
     * @param endTime
     * @param timeInterval
     * @param countFun
     * @return
     */
    List<NodeHistoryDataDto> findNodeCountDataListByTimeInterval(@Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("timeInterval") String timeInterval, @Param("countFun") String countFun);

    /**
     * 查询指定时间的计算节点值
     * @param tableName
     * @param startTime
     * @return
     */
    NodeHistoryDataDto findNodeAppointTimeDataByTableName(@Param("tableName") String tableName, @Param("startTime") String startTime);

    /**
     * 查询节点历史数据
     * @param tableName
     * @param startTime
     * @param endTime
     * @param timeInterval
     * @param limitSize
     * @return
     */
    List<NodeHistoryDataDto> findNodeTaosDataList(@Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("timeInterval") String timeInterval, @Param("limitSize") Integer limitSize);

    /**
     * 删除指定表指定时间段内节点数据
     * @param tableName
     * @param startTime
     * @param endTime
     */
    void deleteDataByTimeBetween(@Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 批量插入节点数据
     * @param tableName
     * @param stableName
     * @param storageId
     * @param siteId
     * @param nodeHistoryDataDtoList
     */
    Integer batchInsertTableData(@Param("tableName") String tableName, @Param("stableName") String stableName, @Param("storageId") Long storageId, @Param("siteId") String siteId, @Param("nodeHistoryDataDtoList") List<NodeHistoryDataDto> nodeHistoryDataDtoList);

    List<NodeHistoryDataDto> findNodeTaosCountFunDataByIds(@Param("tableName") String tableName, @Param("startTime") String startMillTime, @Param("endTime") String endMillTime, @Param("countFun") String countFun, @Param("timeInterval") String timeInterval);
}
