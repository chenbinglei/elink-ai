package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceBatchUpdateVo;
import com.sunmax.device.dto.firmware.FirmwareDto;
import com.sunmax.device.dto.task.DeviceTaskListDto;
import com.sunmax.device.dto.task.DeviceTaskRecordListDto;
import com.sunmax.device.dto.task.DeviceUpdateDto;
import com.sunmax.device.vo.task.DeviceTaskChangeVo;
import com.sunmax.device.vo.task.DeviceTaskQueryVo;
import com.sunmax.device.vo.task.DeviceTaskRecordVo;
import com.sunmax.device.vo.task.DeviceUpdateQueryVo;

import java.util.List;
import java.util.Set;

public interface DeviceTaskService {

    /**
     * 根据设备类型id查询固件包数据
     * @param typeId 类型id
     * @return 固件包数据
     */
    ResponseResult<List<FirmwareDto>> getFirmwareListByTypeId(String typeId);

    /**
     * 根据设备类型id查询设备升级数据
     * @param deviceQueryVo 查询条件
     * @return 设备升级数据
     */
    ResponseResult<PageDto<DeviceUpdateDto>> queryDeviceUpdateList(DeviceUpdateQueryVo deviceQueryVo);

    /**
     * 获取设备版本列表
     * @param typeId 设备类型id
     * @param equipmentModel 设备型号
     * @param firmwareType 固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_8.0 CCU控制板
     * @return 设备版本号列表
     */
    ResponseResult<Set<String>> getDeviceVersionList(String typeId, String equipmentModel, Integer firmwareType);

    /**
     * 创建设备升级任务
     * @param taskChangeVo 设备升级任务数据
     * @return 状态码
     */
    ResponseResult<Void> createDeviceTask(DeviceTaskChangeVo taskChangeVo);

    /**
     * 查询设备升级任务列表
     * @param taskQueryVo 任务查询参数
     * @return 设备升级任务数据
     */
    ResponseResult<PageDto<DeviceTaskListDto>> queryDeviceTaskList(DeviceTaskQueryVo taskQueryVo);

    /**
     * 查询设备任务记录列表
     * @param taskRecordVo 任务记录查询参数
     * @return 设备任务记录列表数据
     */
    ResponseResult<List<DeviceTaskRecordListDto>> queryDeviceTaskRecordList(DeviceTaskRecordVo taskRecordVo);

    /**
     * 删除设备升级任务
     * @param id 任务id
     * @param type 任务类型 1-设备升级任务 2-设备升级记录
     * @return 状态码
     */
    ResponseResult<Void> deleteDeviceTaskById(String id, Integer type);

    /**
     * 批量修改设备升级任务
     * @param deviceUpdateVo 设备升级任务数据
     * @return 状态码
     */
    ResponseResult<Void> batchUpdateDeviceTask(DeviceBatchUpdateVo deviceUpdateVo);

}
