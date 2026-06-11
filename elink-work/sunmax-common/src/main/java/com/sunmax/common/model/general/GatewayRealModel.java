package com.sunmax.common.model.general;

import com.google.common.collect.Maps;
import com.sunmax.common.dto.protocol.mqtt.UpdateInfoDto;
import com.sunmax.common.model.PointTableModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Schema(description = "网关实时数据")
public class GatewayRealModel {

    @Schema(description = "网关编号")
    private String terminalCode;

    @Schema(description = "设备状态 -1-未知 0-未注册 1-在线 2-故障 88-离线")
    private Integer deviceStatus;

    @Schema(description = "上次心跳时间")
    private String lastBeatTime;

    @Schema(description = "上次发送报文的时间")
    private String lastSendTime;

    @Schema(description = "子设备状态")
    private Map<String, Integer> subDeviceRealMap = Maps.newConcurrentMap();

    @Schema(description = "固件升级电桩数据 固件类型 -> 固件包升级信息")
    private Map<Integer, UpdateInfoDto> updateInfoMap = Maps.newHashMap();

    @Schema(description = "通过网关升级的桩编码 固件类型 -> 多个桩编码")
    private Map<Integer, Set<String>> updatePileMap = Maps.newHashMap();

    @Schema(description = "通道数据")
    private Map<String, ChannelRealModel> channelRealMap = Maps.newConcurrentMap();

    @Data
    @Schema(description = "通道数据")
    public static class ChannelRealModel {

        /**
         * 协议类型 MQTT,HTTP,TCP
         */
        @Schema(description = "协议类型 MQTT,HTTP,TCP")
        private String protocolType;

        /**
         * 接入协议
         */
        @Schema(description = "接入协议")
        private String accessProtocol;

        /**
         * IP地址
         */
        @Schema(description = "IP地址")
        private String ip;

        /**
         * 端口号
         */
        @Schema(description = "端口号")
        private Integer port;

        /**
         * 通讯状态 0-连接 1-未连接
         */
        @Schema(description = "通讯状态 0-连接 1-未连接")
        private Integer txStatus;

        /**
         * 点表数据
         */
        @Schema(description = "点表数据")
        private Map<String, PointTableModel> pointTableMap = Maps.newConcurrentMap();

    }

}
