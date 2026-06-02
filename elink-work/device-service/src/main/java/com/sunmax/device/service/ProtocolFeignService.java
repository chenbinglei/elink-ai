package com.sunmax.device.service;

import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.DevicePointDto;
import com.sunmax.common.dto.device.PileFaultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PileSetQrVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProtocolFeignService {

    /**
     * 获取设备序列号列表数据
     * @param accessType 接入类型 1-直连设备 2-网关设备 3-网关子设备
     * @return 设备序列号列表数据
     */
    ResponseResult<Set<String>> getDeviceNumberList(Integer accessType);

    /**
     * 获取设备故障列表数据
     * @param deviceCode 设备序列号
     * @param faultCodes 故障码列表
     * @return 故障码 -> 设备故障列表数据
     */
    ResponseResult<Map<Integer, PileFaultDto>> findPileFaultList(String deviceCode, Set<Integer> faultCodes);

    /**
     * 根据多个模型id查询设备点号数据
     * @param modelIds 多个模型id
     * @return 设备点号数据 点号->设备数据
     */
    ResponseResult<Map<String, DevicePointDto>> findAllDevicePointByModelIds(Set<String> modelIds);

    /**
     * 根据多个设备类型id查询设备基础信息
     * @param typeIds 多个设备类型id
     * @return 设备基础信息
     */
    ResponseResult<Map<String, DeviceBasicInfoDto>> findAllDeviceInfoByTypeIds(Set<String> typeIds);

    /**
     * 更新设备二维码地址
     * @param pileSetQrVo 电桩设置二维码地址参数
     * @return 状态码
     */
    ResponseResult<Void> updateDeviceQrStr(PileSetQrVo pileSetQrVo);

    /**
     * 根据多个设备编码查询设备电枪数据
     * @param deviceCodes 多个设备编码
     * @return 设备编号 -> 设备电枪信息列表
     */
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceCodes(List<String> deviceCodes);

}
