package com.sunmax.device.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备接入详情实体类
 */
@Data
@Schema(description = "设备接入详情实体类")
public class DeviceAccessDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @Schema(description = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 设备描述
     */
    @Schema(description = "设备描述")
    private String deviceDesc;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 通信状态 0-未注册 1-在线 2-故障 88-离线
     */
    @Schema(description = "通信状态 0-未注册 1-在线 2-故障 88-离线")
    private Integer txStatus;

    /**
     * 直连设备通道信息
     */
    @Schema(description = "直连设备通道信息")
    private DasChannel dasChannel;

    /**
     * 网关设备通道信息
     */
    @Schema(description = "网关设备通道信息")
    private List<GatewayChannel> gatewayChannelList;

    @Data
    public static class DasChannel {

        /**
         * 协议类型
         */
        @Schema(description = "协议类型")
        private String protocolType;

        /**
         * 接入协议
         */
        @Schema(description = "接入协议")
        private String accessProtocol;
    }

    @Data
    public static class GatewayChannel {

        /**
         * 协议类型
         */
        @Schema(description = "协议类型")
        private String protocolType;

        /**
         * 接入协议
         */
        @Schema(description = "接入协议")
        private String accessProtocol;

        /**
         * IP地址/端口
         */
        @Schema(description = "IP地址/端口")
        private String ipPort;

        /**
         * 通讯状态 0-已连接 1-断开
         */
        @Schema(description = "通讯状态 0-已连接 1-断开")
        private Integer txStatus;

        /**
         * 点表数据
         */
        @Schema(description = "点表数据")
        private Object pointList;

    }

}
