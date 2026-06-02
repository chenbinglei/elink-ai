package com.sunmax.common.dto.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SystemDeviceListDto", description = "系统设备列表返回实体类")
public class SystemDeviceListDto {

    /**
     * 储能系统设备列表
     */
    @ApiModelProperty(value = "储能系统设备列表")
    private List<SystemDeviceListDto.DeviceData> storageDeviceList;

    /**
     * 光伏系统设备列表
     */
    @ApiModelProperty(value = "光伏系统设备列表")
    private List<SystemDeviceListDto.DeviceData> pvDeviceList;

    /**
     * 电桩系统设备列表
     */
    @ApiModelProperty(value = "电桩系统设备列表")
    private List<SystemDeviceListDto.DeviceData> pileDeviceList;

    /**
     * 配电系统设备列表
     */
    @ApiModelProperty(value = "配电系统设备列表")
    private List<SystemDeviceListDto.DeviceData> powerDeviceList;

    /**
     * 设备信息
     */
    @Data
    public static class DeviceData {

        /**
         * 设备唯一id
         */
        @ApiModelProperty(value = "设备唯一id")
        private String id;

        /**
         * 设备名称
         */
        @ApiModelProperty(value = "设备名称")
        private String deviceName;

        /**
         * 资产分类id
         */
        @ApiModelProperty(value = "资产分类id")
        private String typeId;
    }
}
