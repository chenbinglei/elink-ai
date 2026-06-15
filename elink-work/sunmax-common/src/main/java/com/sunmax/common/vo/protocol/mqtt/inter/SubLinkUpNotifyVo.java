package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.3 CMD_SUBLINKUP_NOTIFY,//上线通知  CMD=3
 * 发送方向：前置服务--->平台服务
 */
@Data
public class SubLinkUpNotifyVo {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 子设备状态
     * ONLINE：设备在线
     * OFFLINE：设备离线
     */
    @Schema(description = "子设备状态")
    private String status;

    /**
     * 子设备上线原因 1-复位上线 2-离网上线 3-离网断开
     */
    @Schema(description = "子设备上线原因")
    private Integer reason;

    /**
     * 特征码
     */
    @Schema(description = "特征码")
    private Long featureCode;

}
