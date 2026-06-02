package com.sunmax.device.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 设备接入详情实体类
 */
@Data
@ApiModel(value = "DeviceAccessDto", description = "设备接入详情实体类")
public class DeviceAccessDto {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @ApiModelProperty(value = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 设备描述
     */
    @ApiModelProperty(value = "设备描述")
    private String deviceDesc;

    /**
     * 模型名称
     */
    @ApiModelProperty(value = "模型名称")
    private String modelName;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 通信状态 0-未注册 1-在线 2-故障 88-离线
     */
    @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-故障 88-离线")
    private Integer txStatus;

    /**
     * 直连设备通道信息
     */
    @ApiModelProperty(value = "直连设备通道信息")
    private DasChannel dasChannel;

    /**
     * 网关设备通道信息
     */
    @ApiModelProperty(value = "网关设备通道信息")
    private List<GatewayChannel> gatewayChannelList;

    @Data
    public static class DasChannel {

        /**
         * 协议类型
         */
        @ApiModelProperty(value = "协议类型")
        private String protocolType;

        /**
         * 接入协议
         */
        @ApiModelProperty(value = "接入协议")
        private String accessProtocol;
    }

    @Data
    public static class GatewayChannel {

        /**
         * 协议类型
         */
        @ApiModelProperty(value = "协议类型")
        private String protocolType;

        /**
         * 接入协议
         */
        @ApiModelProperty(value = "接入协议")
        private String accessProtocol;

        /**
         * IP地址/端口
         */
        @ApiModelProperty(value = "IP地址/端口")
        private String ipPort;

        /**
         * 通讯状态 0-已连接 1-断开
         */
        @ApiModelProperty(value = "通讯状态 0-已连接 1-断开")
        private Integer txStatus;

        /**
         * 点表数据
         */
        @ApiModelProperty(value = "点表数据")
        private Object pointList;

    }

}
