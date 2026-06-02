package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.25 CMD_DevUpdateReport,//设备升级-升级结果上报  25
 * 发送方向：前置服务--->平台服务
 */
@Data
public class DevUpdateReportVo {

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
     * 固件主版本号
     */
    @ApiModelProperty(value = "固件主版本号", required = true)
    private Integer majorNo;

    /**
     * 固件次版本号
     */
    @ApiModelProperty(value = "固件次版本号", required = true)
    private Integer childNo;

    /**
     * 固件内测版本号
     */
    @ApiModelProperty(value = "固件内测版本号", required = true)
    private Integer betaNo;

    /**
     * 失败原因 0-成功 1-数据校验失败 2-应答超时 3-flash擦除失败 4-flash写入失败 5-同版本不升级 255-其他原因。
     */
    @ApiModelProperty(value = "失败原因", required = true)
    private Integer failReason;

}
