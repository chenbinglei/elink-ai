package com.sunmax.device.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.firmware.FirmwareDto;
import com.sunmax.device.dto.task.DeviceTaskListDto;
import com.sunmax.device.dto.task.DeviceTaskRecordListDto;
import com.sunmax.device.dto.task.DeviceUpdateDto;
import com.sunmax.device.service.DeviceTaskService;
import com.sunmax.device.vo.task.DeviceTaskChangeVo;
import com.sunmax.device.vo.task.DeviceTaskQueryVo;
import com.sunmax.device.vo.task.DeviceTaskRecordVo;
import com.sunmax.device.vo.task.DeviceUpdateQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 设备升级任务控制层
 */
@RestController
@CrossOrigin
@RequestMapping("deviceTask")
@Tag(name = "设备升级任务控制层")
public class DeviceTaskController {

    @Autowired
    private DeviceTaskService deviceTaskService;

    @PostMapping("getFirmwareListByTypeId")
    @Operation(summary = "根据设备类型id查询固件包数据")
    
    @Parameter(name = "typeId", description = "设备类型id")
    public ResponseResult<List<FirmwareDto>> getFirmwareListByTypeId(String typeId) {
        return deviceTaskService.getFirmwareListByTypeId(typeId);
    }

    @PostMapping("queryDeviceUpdateList")
    @Operation(summary = "查询设备升级任务编辑列表")
    
    public ResponseResult<PageDto<DeviceUpdateDto>> queryDeviceUpdateList(DeviceUpdateQueryVo deviceQueryVo) {
        return deviceTaskService.queryDeviceUpdateList(deviceQueryVo);
    }

    @PostMapping("getDeviceVersionList")
    @Operation(summary = "获取设备版本列表")
    
    @Parameters({
            @Parameter(name = "typeId", description = "设备类型id"),
            @Parameter(name = "equipmentModel", description = "设备型号"),
            @Parameter(name = "firmwareType", description = "固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_8.0 CCU控制板")
    })
    public ResponseResult<Set<String>> getDeviceVersionList(String typeId, String equipmentModel, Integer firmwareType) {
        return deviceTaskService.getDeviceVersionList(typeId, equipmentModel, firmwareType);
    }

    @PostMapping("createDeviceTask")
    @Operation(summary = "创建设备升级任务")
    
    public ResponseResult<Void> createDeviceTask(DeviceTaskChangeVo taskChangeVo) {
        return deviceTaskService.createDeviceTask(taskChangeVo);
    }

    @PostMapping("queryDeviceTaskList")
    @Operation(summary = "查询设备升级任务列表")
    
    public ResponseResult<PageDto<DeviceTaskListDto>> queryDeviceTaskList(DeviceTaskQueryVo taskQueryVo) {
        return deviceTaskService.queryDeviceTaskList(taskQueryVo);
    }

    @PostMapping("queryDeviceTaskRecordList")
    @Operation(summary = "查询设备任务记录列表")
    
    public ResponseResult<List<DeviceTaskRecordListDto>> queryDeviceTaskRecordList(DeviceTaskRecordVo taskRecordVo) {
        return deviceTaskService.queryDeviceTaskRecordList(taskRecordVo);
    }

    @PostMapping("deleteDeviceTaskById")
    @Operation(summary = "删除设备升级任务数据")
    
    @Parameters({
            @Parameter(name = "id", description = "主键id"),
            @Parameter(name = "type", description = "任务类型 1-设备升级任务 2-设备升级记录")
    })
    public ResponseResult<Void> deleteDeviceTaskById(String id, Integer type) {
        return deviceTaskService.deleteDeviceTaskById(id, type);
    }

}
