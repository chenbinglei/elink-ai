package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.22 CMD_DevUpdate,//设备升级响应，平台-网关-设备 22
 * 发送方向：前置服务<---平台服务
 */
@Data
public class DevUpdateCmdDto {

    /**
     * 固件获取发起者 0-电桩获取 1-平台下发
     */
    @Schema(description = "固件获取发起者 0-电桩获取 1-平台下发")
    private Integer sponsor;

    /**
     * 强制升级 0-不强制 1-强制
     */
    @Schema(description = "强制升级 0-不强制 1-强制")
    private Integer forced_update;

    /**
     * 要求硬件主版本号
     */
    @Schema(description = "要求硬件主版本号")
    private Integer request_majorNo;

    /**
     * 要求硬件子版本号
     */
    @Schema(description = "要求硬件子版本号")
    private Integer request_childNo;

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
     * 新固件主版本号
     */
    @Schema(description = "新固件主版本号")
    private Integer majorNo;

    /**
     * 新固件次版本号
     */
    @Schema(description = "新固件次版本号")
    private Integer childNo;

    /**
     * 新固件内测版本号
     */
    @Schema(description = "新固件内测版本号")
    private Integer betaNo;

    /**
     * 新固件编译时间
     */
    @Schema(description = "新固件编译时间")
    private Integer compiletime;

    /**
     * 新固件数据大小
     */
    @Schema(description = "新固件数据大小")
    private Integer dataLen;

    /**
     * 新固件数据校验码
     */
    @Schema(description = "新固件数据校验码")
    private Long deviceCRC;

}
