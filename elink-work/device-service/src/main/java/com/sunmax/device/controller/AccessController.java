package com.sunmax.device.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ChannelInfoDto;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.SubDeviceFunctionDto;
import com.sunmax.device.dto.device.DeviceAccessDto;
import com.sunmax.device.dto.PointTableDto;
import com.sunmax.device.service.AccessService;
import com.sunmax.device.vo.ChannelChangeVo;
import com.sunmax.device.vo.PointTableChangeVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 设备接入管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("access")
@Tag(name = "设备接入管理控制层")
public class AccessController {

    @Autowired
    private AccessService accessService;

    @PostMapping("findAccessDetailByDeviceId")
    @Operation(summary = "根据设备id查询设备接入详情")
    @Parameter(name = "deviceId", description = "设备id")
    
    public ResponseResult<DeviceAccessDto> findAccessDetailByDeviceId(String deviceId) {
        return accessService.findAccessDetailByDeviceId(deviceId);
    }

    @PostMapping("saveChannel")
    @Operation(summary = "新增或编辑通道数据")
    
    public ResponseResult<Void> saveChannel(ChannelChangeVo channelChangeVo) {
        return accessService.saveChannel(channelChangeVo);
    }

    @PostMapping("deleteChannelById")
    @Operation(summary = "根据通道id删除通道数据")
    
    public ResponseResult<Void> deleteChannelById(String deviceId, String channelId) {
        return accessService.deleteChannelById(deviceId, channelId);
    }

    @PostMapping("findChannelInfoListByDeviceId")
    @Operation(summary = "根据设备id查询设备通道信息列表")
    @Parameter(name = "deviceId", description = "设备id")
    
    public ResponseResult<List<ChannelInfoDto>> findChannelInfoListByDeviceId(String deviceId) {
        return accessService.findChannelInfoListByDeviceId(deviceId);
    }

    @PostMapping("updateDeviceStatus")
    @Operation(summary = "设备注册注销")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "updateType", description = "更新类型 1-注册 2-注销")
    })
    public ResponseResult<Void> updateDeviceStatus(String deviceId, Integer updateType) {
        return accessService.updateDeviceStatus(deviceId, updateType);
    }

    @PostMapping("findPointTableListByChannelId")
    @Operation(summary = "根据通道id查询点表数据列表")
    @Parameter(name = "channelId", description = "通道id")
    
    public ResponseResult<List<PointTableDto>> findPointTableListByChannelId(String channelId) {
        return accessService.findPointTableListByChannelId(channelId);
    }

    @PostMapping("savePointTable")
    @Operation(summary = "新增或编辑或删除点表数据")
    @Parameter(name = "pointTableVos", description = "多个点表数据[{点表数据1},{点表数据2}]")
    
    public ResponseResult<Void> savePointTable(String pointTableVos) {
        return accessService.savePointTable(JSON.parseArray(pointTableVos, PointTableChangeVo.class));
    }

    @PostMapping("findSubDeviceFunctionListByDeviceId")
    @Operation(summary = "根据设备id查询网关子设备功能点列表")
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id 标明:类型为1和2时传网关设备id,为3传查询设备id"),
            @Parameter(name = "type", description = "类型 1-设备及功能点数据 2-设备数据 3-功能点数据")
    })
    
    public ResponseResult<List<SubDeviceFunctionDto>> findSubDeviceFunctionListByDeviceId(String deviceId, Integer type) {
        return accessService.findSubDeviceFunctionListByDeviceId(deviceId, type);
    }

    @PostMapping("importPointTableData")
    @Operation(summary = "导入点表数据")
    @Parameters({
            @Parameter(name = "channelId", description = "通道id"),
            @Parameter(name = "pointTableFile", description = "点表数据文件")
    })
    
    public ResponseResult<ImportResultDto> importPointTableData(String channelId, MultipartFile pointTableFile) {
        return accessService.importPointTableData(channelId, pointTableFile);
    }

}
