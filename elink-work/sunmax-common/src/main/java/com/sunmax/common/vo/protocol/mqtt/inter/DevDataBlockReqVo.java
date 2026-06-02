package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.23 CMD_DevDataBlockRequest,//设备升级-数据块请求 23
 * 发送方向：前置服务--->平台服务
 */
@Data
public class DevDataBlockReqVo {

    /**
     * 固件类型
     * 1 V2G_1.0 控制板
     * 2 V2G_2.0 控制板
     * 3 V2G_3.0 控制板
     * 4 V2G_4.0 控制板
     * 5 V2G_5.0 控制板
     * 6 V2G_6.0 控制板
     * 7 V2G_7.0 控制板
     * 8 V2G_8.0 控制板
     */
    @ApiModelProperty(value = "固件类型", required = true)
    private Integer deviceType;

    /**
     * 新固件主版本号
     */
    @ApiModelProperty(value = "固件主版本号", required = true)
    private Integer majorNo;

    /**
     * 新固件次版本号
     */
    @ApiModelProperty(value = "固件次版本号", required = true)
    private Integer childNo;

    /**
     * 新固件内测版本号
     */
    @ApiModelProperty(value = "固件内测版本号", required = true)
    private Integer betaNo;

    /**
     * 数据块标号
     */
    @ApiModelProperty(value = "数据块标号", required = true)
    private Integer dataFlag;

    /**
     * 固件数据块大小
     */
    @ApiModelProperty(value = "固件数据块大小", required = true)
    private Integer dataLen;

}
