package com.sunmax.common.model.general;

import com.google.common.collect.Maps;
import com.sunmax.common.dto.protocol.mqtt.UpdateInfoDto;
import com.sunmax.common.model.PointTableModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@ApiModel(value = "GatewayRealModel", description = "网关实时数据")
public class GatewayRealModel {

    @ApiModelProperty(value = "网关编号")
    private String terminalCode;

    @ApiModelProperty(value = "设备状态 -1-未知 0-未注册 1-在线 2-故障 88-离线")
    private Integer deviceStatus;

    @ApiModelProperty(value = "上次心跳时间")
    private String lastBeatTime;

    @ApiModelProperty(value = "上次发送报文的时间")
    private String lastSendTime;

    @ApiModelProperty(value = "子设备状态")
    private Map<String, Integer> subDeviceRealMap = Maps.newConcurrentMap();

    @ApiModelProperty(value = "固件升级电桩数据 固件类型 -> 固件包升级信息")
    private Map<Integer, UpdateInfoDto> updateInfoMap = Maps.newHashMap();

    @ApiModelProperty(value = "通过网关升级的桩编码 固件类型 -> 多个桩编码")
    private Map<Integer, Set<String>> updatePileMap = Maps.newHashMap();

    @ApiModelProperty(value = "通道数据")
    private Map<String, ChannelRealModel> channelRealMap = Maps.newConcurrentMap();

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
