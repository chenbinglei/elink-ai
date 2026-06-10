package com.sunmax.device.controller;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.firmware.FirmwareDto;
import com.sunmax.device.dto.firmware.FirmwareParseDto;
import com.sunmax.device.service.FirmwareService;
import com.sunmax.device.vo.firmware.FirmwareChangeVo;
import com.sunmax.device.vo.firmware.FirmwareQueryVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "固件包管理控制层")
public class FirmwareController {

    @Autowired
    private FirmwareService firmwareService;

    @PostMapping("uploadOrEditFirmware")
    @Operation(summary = "上传或编辑固件包数据")
    
    public ResponseResult<Void> uploadOrEditFirmware(FirmwareChangeVo firmwareVo, MultipartFile file) {
        return firmwareService.uploadOrEditFirmware(firmwareVo, file);
    }

    @PostMapping("getEquipmentModelList")
    @Operation(summary = "获取设备型号列表")
    
    @Parameter(name = "typeId", description = "设备类型id")
    public ResponseResult<Set<String>> getEquipmentModelList(String typeId) {
        return firmwareService.getEquipmentModelList(typeId);
    }

    @PostMapping("queryFirmwareList")
    @Operation(summary = "查询固件包列表")
    
    public ResponseResult<PageDto<FirmwareDto>> queryFirmwareList(FirmwareQueryVo firmwareQueryVo) {
        return firmwareService.queryFirmwareList(firmwareQueryVo);
    }

    @PostMapping("parseFirmwareData")
    @Operation(summary = "解析固件包数据")
    
    public ResponseResult<FirmwareParseDto> parseFirmwareData(MultipartFile file) {
        return firmwareService.parseFirmwareData(file);
    }

    @PostMapping("deleteFirmwareById")
    @Operation(summary = "删除固件包数据")
    
    public ResponseResult<Void> deleteFirmwareById(String id) {
        return firmwareService.deleteFirmwareById(id);
    }

}
