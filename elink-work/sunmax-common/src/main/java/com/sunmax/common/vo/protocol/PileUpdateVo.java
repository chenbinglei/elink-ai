package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 固件下发参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "PileUpdateVo", description = "电桩升级参数实体类")
public class PileUpdateVo {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", required = true)
    private String taskId;

    /**
     * 充电桩编码
     */
    @ApiModelProperty(value = "充电桩编码", required = true)
    private String pileCode;

    /**
     * 强制升级类型 0-不强制 1-强制
     */
    @ApiModelProperty(value = "强制升级类型 0-不强制 1-强制", required = true)
    private Integer upgradeType;

    /**
     * 固件包路径
     */
    @ApiModelProperty("固件包路径")
    private String firmwarePath;

    /**
     * 硬件主版本号
     */
    @ApiModelProperty("硬件主版本号")
    private Integer hardwareMajorVersion;

    /**
     * 硬件次版本号
     */
    @ApiModelProperty("硬件次版本号")
    private Integer hardwareMinorVersion;

    /**
     * 固件类型
     */
    @ApiModelProperty("固件类型")
    private Integer firmwareType;

    /**
     * 固件主版本号
     */
    @ApiModelProperty("固件主版本号")
    private Integer firmwareMajorVersion;

    /**
     * 固件次版本号
     */
    @ApiModelProperty("固件次版本号")
    private Integer firmwareMinorVersion;

    /**
     * 固件内测版本号
     */
    @ApiModelProperty("固件内测版本号")
    private Integer firmwareInternalVersion;

    /**
     * 固件编译时间
     */
    @ApiModelProperty("固件编译时间")
    private LocalDateTime firmwareCompileTime;

    /**
     * 固件数据大小
     */
    @ApiModelProperty("固件数据大小")
    private Integer firmwareSize;

    /**
     * 固件数据检验码
     */
    @ApiModelProperty("固件数据检验码")
    private Long crc32;

}
