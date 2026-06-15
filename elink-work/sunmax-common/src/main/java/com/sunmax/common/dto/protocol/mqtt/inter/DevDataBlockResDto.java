package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.24 CMD_DevDataBlockResponse,//设备升级-数据块响应 24
 * 发送方向：前置服务<---平台服务
 */
@Data
public class DevDataBlockResDto {

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
    @Schema(description = "固件类型")
    private Integer deviceType;

    /**
     * 固件主版本号
     */
    @Schema(description = "固件主版本号")
    private Integer majorNo;

    /**
     * 固件次版本号
     */
    @Schema(description = "固件次版本号")
    private Integer childNo;

    /**
     * 固件内测版本号
     */
    @Schema(description = "固件内测版本号")
    private Integer betaNo;

    /**
     * 数据块标号
     */
    @Schema(description = "数据块标号")
    private Integer dataFlag;

    /**
     * 固件数据块大小
     */
    @Schema(description = "固件数据块大小")
    private Integer dataLen;

    /**
     * 数据块
     */
    @Schema(description = "数据块")
    private String dataBlock;

}
