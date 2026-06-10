package com.sunmax.common.dto.protocol.mqtt.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 控制板信息数据
 */
@Data
public class CtrlBoardInfo implements Serializable {

    /**
     *硬件名称
     */
    @Schema(description = "硬件名称")
    private String hardwareName;

    /**
     *控制板类型
     */
    @Schema(description = "控制板类型")
    private Integer ctrlBoardType;

    /**
     *序号
     */
    @Schema(description = "序号")
    private Integer seqNum;

    /**
     *硬件主版本号
     */
    @Schema(description = "硬件主版本号")
    private Integer hardMainVCode;

    /**
     *硬件次版本号
     */
    @Schema(description = "硬件次版本号")
    private Integer hardSecVCode;

    /**
     *固件名称
     */
    @Schema(description = "固件名称")
    private String firmwareName;

    /**
     *固件主版本号
     */
    @Schema(description = "固件主版本号")
    private Integer firmMainVCode;

    /**
     *固件次版本号
     */
    @Schema(description = "固件次版本号")
    private Integer firmSecVCode;

    /**
     *固件内测版本号
     */
    @Schema(description = "固件内测版本号")
    private Integer firmInnerVCode;
}
