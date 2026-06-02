package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.firmware.FirmwareDto;
import com.sunmax.device.dto.firmware.FirmwareParseDto;
import com.sunmax.device.vo.firmware.FirmwareChangeVo;
import com.sunmax.device.vo.firmware.FirmwareQueryVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface FirmwareService {

    /**
     * 上传或编辑固件包数据
     * @param firmwareVo 固件包编辑参数实体类
     * @param file 固件包文件
     * @return 状态码
     */
    ResponseResult<Void> uploadOrEditFirmware(FirmwareChangeVo firmwareVo, MultipartFile file);

    /**
     * 获取设备型号列表
     * @param typeId 固件类型id
     * @return 设备型号列表
     */
    ResponseResult<Set<String>> getEquipmentModelList(String typeId);

    /**
     * 查询固件包列表
     * @param firmwareVo 参数
     * @return 固件包列表
     */
    ResponseResult<PageDto<FirmwareDto>> queryFirmwareList(FirmwareQueryVo firmwareVo);

    /**
     * 解析固件包数据
     * @param file 固件包文件
     * @return 固件包数据
     */
    ResponseResult<FirmwareParseDto> parseFirmwareData(MultipartFile file);

    /**
     * 删除固件包
     * @param id 固件包id
     * @return 状态码
     */
    ResponseResult<Void> deleteFirmwareById(String id);

}
