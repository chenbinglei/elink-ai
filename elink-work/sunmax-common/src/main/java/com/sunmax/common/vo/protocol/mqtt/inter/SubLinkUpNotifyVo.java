package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "设备id", required = true)
    private String deviceId;

    /**
     * 子设备状态
     * ONLINE：设备在线
     * OFFLINE：设备离线
     */
    @ApiModelProperty(value = "子设备状态", required = true)
    private String status;

    /**
     * 子设备上线原因 1-复位上线 2-离网上线 3-离网断开
     */
    @ApiModelProperty(value = "子设备上线原因", required = true)
    private Integer reason;

    /**
     * 特征码
     */
    @ApiModelProperty(value = "特征码", required = true)
    private Long featureCode;

}
