package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "系统设备列表返回实体类")
public class SystemDeviceListDto {

    /**
     * 储能系统设备列表
     */
    @Schema(description = "储能系统设备列表")
    private List<SystemDeviceListDto.DeviceData> storageDeviceList;

    /**
     * 光伏系统设备列表
     */
    @Schema(description = "光伏系统设备列表")
    private List<SystemDeviceListDto.DeviceData> pvDeviceList;

    /**
     * 电桩系统设备列表
     */
    @Schema(description = "电桩系统设备列表")
    private List<SystemDeviceListDto.DeviceData> pileDeviceList;

    /**
     * 配电系统设备列表
     */
    @Schema(description = "配电系统设备列表")
    private List<SystemDeviceListDto.DeviceData> powerDeviceList;

    /**
     * 设备信息
     */
    @Data
    public static class DeviceData {

        /**
         * 设备唯一id
         */
        @Schema(description = "设备唯一id")
        private String id;

        /**
         * 设备名称
         */
        @Schema(description = "设备名称")
        private String deviceName;

        /**
         * 资产分类id
         */
        @Schema(description = "资产分类id")
        private String typeId;
    }
}
