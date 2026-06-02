package com.sunmax.common.dto.device;

import com.google.common.collect.Maps;
import com.sunmax.common.model.PointTableModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value = "CloudGatewayDeviceDto",  description = "云网关设备数据返回实体类")
public class CloudGatewayDeviceDto {

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
     * 通道数据
     */
    @ApiModelProperty(value = "通道数据")
    private Map<String, CloudGatewayDeviceDto.ChannelRealModel> channelRealMap = Maps.newConcurrentMap();

    @Data
    @ApiModel(value = "通道数据")
    public static class ChannelRealModel {

        /**
         * 协议类型 MQTT,HTTP,TCP
         */
        @ApiModelProperty(value = "协议类型 MQTT,HTTP,TCP")
        private String protocolType;

        /**
         * 接入协议
         */
        @ApiModelProperty(value = "接入协议")
        private String accessProtocol;

        /**
         * IP地址
         */
        @ApiModelProperty(value = "IP地址")
        private String ip;

        /**
         * 端口号
         */
        @ApiModelProperty(value = "端口号")
        private Integer port;

        /**
         * 通讯状态 0-连接 1-未连接
         */
        @ApiModelProperty(value = "通讯状态 0-连接 1-未连接")
        private Integer txStatus;

        /**
         * 点表数据
         */
        @ApiModelProperty(value = "点表数据")
        private Map<String, PointTableModel> pointTableMap = Maps.newConcurrentMap();

    }

}
