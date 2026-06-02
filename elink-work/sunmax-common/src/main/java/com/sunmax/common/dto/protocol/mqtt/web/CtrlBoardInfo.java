package com.sunmax.common.dto.protocol.mqtt.web;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty("硬件名称")
    private String hardwareName;

    /**
     *控制板类型
     */
    @ApiModelProperty("控制板类型")
    private Integer ctrlBoardType;

    /**
     *序号
     */
    @ApiModelProperty("序号")
    private Integer seqNum;

    /**
     *硬件主版本号
     */
    @ApiModelProperty("硬件主版本号")
    private Integer hardMainVCode;

    /**
     *硬件次版本号
     */
    @ApiModelProperty("硬件次版本号")
    private Integer hardSecVCode;

    /**
     *固件名称
     */
    @ApiModelProperty("固件名称")
    private String firmwareName;

    /**
     *固件主版本号
     */
    @ApiModelProperty("固件主版本号")
    private Integer firmMainVCode;

    /**
     *固件次版本号
     */
    @ApiModelProperty("固件次版本号")
    private Integer firmSecVCode;

    /**
     *固件内测版本号
     */
    @ApiModelProperty("固件内测版本号")
    private Integer firmInnerVCode;
}
