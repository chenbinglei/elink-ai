package com.sunmax.device.controller;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ChannelInfoDto;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.SubDeviceFunctionDto;
import com.sunmax.device.dto.device.DeviceAccessDto;
import com.sunmax.device.dto.PointTableDto;
import com.sunmax.device.service.AccessService;
import com.sunmax.device.vo.ChannelChangeVo;
import com.sunmax.device.vo.PointTableChangeVo;
import io.swagger.annotations.*;
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
@Api(tags = "设备接入管理控制层")
public class AccessController {

    @Autowired
    private AccessService accessService;

    @PostMapping("findAccessDetailByDeviceId")
    @ApiOperation("根据设备id查询设备接入详情")
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    @ApiOperationSupport(order = 1)
    public ResponseResult<DeviceAccessDto> findAccessDetailByDeviceId(String deviceId) {
        return accessService.findAccessDetailByDeviceId(deviceId);
    }

    @PostMapping("saveChannel")
    @ApiOperation("新增或编辑通道数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> saveChannel(ChannelChangeVo channelChangeVo) {
        return accessService.saveChannel(channelChangeVo);
    }

    @PostMapping("deleteChannelById")
    @ApiOperation("根据通道id删除通道数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Void> deleteChannelById(String deviceId, String channelId) {
        return accessService.deleteChannelById(deviceId, channelId);
    }

    @PostMapping("findChannelInfoListByDeviceId")
    @ApiOperation("根据设备id查询设备通道信息列表")
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    @ApiOperationSupport(order = 4)
    public ResponseResult<List<ChannelInfoDto>> findChannelInfoListByDeviceId(String deviceId) {
        return accessService.findChannelInfoListByDeviceId(deviceId);
    }

    @PostMapping("updateDeviceStatus")
    @ApiOperation("设备注册注销")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "updateType", value = "更新类型 1-注册 2-注销", dataType = "int", required = true)
    })
    public ResponseResult<Void> updateDeviceStatus(String deviceId, Integer updateType) {
        return accessService.updateDeviceStatus(deviceId, updateType);
    }

    @PostMapping("findPointTableListByChannelId")
    @ApiOperation("根据通道id查询点表数据列表")
    @ApiImplicitParam(name = "channelId", value = "通道id", dataType = "String", required = true)
    @ApiOperationSupport(order = 6)
    public ResponseResult<List<PointTableDto>> findPointTableListByChannelId(String channelId) {
        return accessService.findPointTableListByChannelId(channelId);
    }

    @PostMapping("savePointTable")
    @ApiOperation("新增或编辑或删除点表数据")
    @ApiImplicitParam(name = "pointTableVos", value = "多个点表数据[{点表数据1},{点表数据2}]", dataType = "String", required = true)
    @ApiOperationSupport(order = 7)
    public ResponseResult<Void> savePointTable(String pointTableVos) {
        return accessService.savePointTable(JSON.parseArray(pointTableVos, PointTableChangeVo.class));
    }

    @PostMapping("findSubDeviceFunctionListByDeviceId")
    @ApiOperation("根据设备id查询网关子设备功能点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id 标明:类型为1和2时传网关设备id,为3传查询设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-设备及功能点数据 2-设备数据 3-功能点数据", dataType = "int", required = true)
    })
    @ApiOperationSupport(order = 8)
    public ResponseResult<List<SubDeviceFunctionDto>> findSubDeviceFunctionListByDeviceId(String deviceId, Integer type) {
        return accessService.findSubDeviceFunctionListByDeviceId(deviceId, type);
    }

    @PostMapping("importPointTableData")
    @ApiOperation("导入点表数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channelId", value = "通道id", dataType = "String", required = true),
            @ApiImplicitParam(name = "pointTableFile", value = "点表数据文件", dataType = "File", required = true)
    })
    @ApiOperationSupport(order = 9)
    public ResponseResult<ImportResultDto> importPointTableData(String channelId, MultipartFile pointTableFile) {
        return accessService.importPointTableData(channelId, pointTableFile);
    }

}
