package com.sunmax.device.controller;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.firmware.FirmwareDto;
import com.sunmax.device.dto.firmware.FirmwareParseDto;
import com.sunmax.device.service.FirmwareService;
import com.sunmax.device.vo.firmware.FirmwareChangeVo;
import com.sunmax.device.vo.firmware.FirmwareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 设备管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("firmware")
@Api(tags = "固件包管理控制层")
public class FirmwareController {

    @Autowired
    private FirmwareService firmwareService;

    @PostMapping("uploadOrEditFirmware")
    @ApiOperation("上传或编辑固件包数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> uploadOrEditFirmware(FirmwareChangeVo firmwareVo, MultipartFile file) {
        return firmwareService.uploadOrEditFirmware(firmwareVo, file);
    }

    @PostMapping("getEquipmentModelList")
    @ApiOperation("获取设备型号列表")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "typeId", value = "设备类型id", dataType = "String", required = true)
    public ResponseResult<Set<String>> getEquipmentModelList(String typeId) {
        return firmwareService.getEquipmentModelList(typeId);
    }

    @PostMapping("queryFirmwareList")
    @ApiOperation("查询固件包列表")
    @ApiOperationSupport(order = 3)
    public ResponseResult<PageDto<FirmwareDto>> queryFirmwareList(FirmwareQueryVo firmwareQueryVo) {
        return firmwareService.queryFirmwareList(firmwareQueryVo);
    }

    @PostMapping("parseFirmwareData")
    @ApiOperation("解析固件包数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<FirmwareParseDto> parseFirmwareData(MultipartFile file) {
        return firmwareService.parseFirmwareData(file);
    }

    @PostMapping("deleteFirmwareById")
    @ApiOperation("删除固件包数据")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Void> deleteFirmwareById(String id) {
        return firmwareService.deleteFirmwareById(id);
    }

}
