package com.sunmax.device.service;

import com.sunmax.common.dto.device.DeviceFieldDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceEventChangeVo;
import com.sunmax.common.vo.device.DeviceFieldQueryVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataFeignService {

    /**
     * 查询设备事件数据
     * @param eventStatus 事件状态 0-未修复 1-已修复
     * @return 设备id -> 多个事件id
     */
    ResponseResult<Map<String, Set<String>>> findDeviceEventIds(Integer eventStatus);

    /**
     * 批量更新设备事件数据
     * @param deviceEventVos 多个设备事件编辑参数
     * @return 状态码
     */
    ResponseResult<Void> batchUpdateDeviceEvent(List<DeviceEventChangeVo> deviceEventVos);

    /**
     * 查询所有设备的模型id
     * @param deviceId 设备id
     * @return 设备id -> 模型id
     */
    ResponseResult<Map<String, String>> findDeviceModelIds(String deviceId);

    /**
     * 查询设备数据及标识字段名
     * @param deviceFieldQueryVo 设备字段查询参数
     * @return 设备id -> 设备字段数据
     */
    ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldList(DeviceFieldQueryVo deviceFieldQueryVo);

    /**
     * 根据设备id和功能点标识查询设备功能点数据
     * @param deviceFieldQueryMap 设备id -> 多个功能点标识
     * @return 设备id -> 设备字段数据
     */
    ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldListByMap(Map<String, Set<String>> deviceFieldQueryMap);

}
