package com.sunmax.data.service;

import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexListQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeviceFeignService {

    /**
     * 批量删除数据存储表
     * @param tableNames 多个表名
     * @return 状态码
     */
    ResponseResult<Void> deleteAllDataStoreTable(Set<String> tableNames);

    /**
     * 根据多个设备id和多个功能点标识和时间获取设备功能点历史列表
     * @param deviceQueryVo 设备历史数据查询条件
     * @return 设备id -> (功能点标识 -> 功能点历史数据)
     */
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(DeviceHistoryQueryVo deviceQueryVo);

    /**
     * 查询设备功能点指定单个时间或时间段内的数据或临近值历史数据
     * @param deviceQueryVo
     * @param isNear 是否查临近值 1-是
     * @return 设备id -> (功能点标识 -> 功能点历史数据)
     */
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceDifferenceListFeign(DeviceHistoryQueryVo deviceQueryVo, Integer isNear);

    /**
     * 查询指定设备指定功能点和时间段内设备统计值函数历史数据
     * @param deviceCountQueryVo
     * @return 设备id -> (功能点标识 -> 功能点历史数据)
     */
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceCountFunListFeign(DeviceCountQueryVo deviceCountQueryVo);

    /**
     * 查询设备功能点指定时间段内的last和first历史数据
     * @param deviceQueryVo
     * @return 设备id -> (功能点标识 -> 功能点历史数据)
     */
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(DeviceHistoryQueryVo deviceQueryVo);

    /**
     * 根据多个设备id和多个功能点标识和时间获取设备功能点历史列表
     * @param deviceQueryVo 设备历史数据查询条件
     * @return 设备id -> (功能点标识 -> 功能点历史数据)
     */
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryIndexValueList(DeviceIndexQueryVo deviceQueryVo);

    /**
     * 查询设备功能点指定单个时间或时间段内的数据或临近值历史数据(查询索引枪数据)
     * @param deviceQueryVo 设备历史数据查询条件
     * @return 设备id -> (功能点标识 -> 功能点历史数据)
     */
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceDiffIndexValueListFeign(DeviceIndexQueryVo deviceQueryVo);

    /**
     * 根据多个设备id和多个功能点标识以及多个索引查询历史数据列表
     * @param deviceQueryVo 设备历史数据查询条件
     * @return 设备id -> (功能点标识 -> (电枪编码 -> 电枪历史数据))
     */
    ResponseResult<Map<String, Map<String, Map<String, List<DeviceHistoryDto>>>>> findIndexListHistorValueFeign(DeviceIndexListQueryVo deviceQueryVo);

    /**
     * 查询设备功能点指定时间段内的last和first历史数据(查询索引数据)
     * @param deviceQueryVo
     * @return
     */
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryIndexListFeign(DeviceIndexQueryVo deviceQueryVo);
}
