package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "电桩升级参数实体类")
public class PileUpdateVo {

    /**
     * 任务id
     */
    @Schema(description = "任务id")
    private String taskId;

    /**
     * 充电桩编码
     */
    @Schema(description = "充电桩编码")
    private String pileCode;

    /**
     * 强制升级类型 0-不强制 1-强制
     */
    @Schema(description = "强制升级类型 0-不强制 1-强制")
    private Integer upgradeType;

    /**
     * 固件包路径
     */
    @Schema(description = "固件包路径")
    private String firmwarePath;

    /**
     * 硬件主版本号
     */
    @Schema(description = "硬件主版本号")
    private Integer hardwareMajorVersion;

    /**
     * 硬件次版本号
     */
    @Schema(description = "硬件次版本号")
    private Integer hardwareMinorVersion;

    /**
     * 固件类型
     */
    @Schema(description = "固件类型")
    private Integer firmwareType;

    /**
     * 固件主版本号
     */
    @Schema(description = "固件主版本号")
    private Integer firmwareMajorVersion;

    /**
     * 固件次版本号
     */
    @Schema(description = "固件次版本号")
    private Integer firmwareMinorVersion;

    /**
     * 固件内测版本号
     */
    @Schema(description = "固件内测版本号")
    private Integer firmwareInternalVersion;

    /**
     * 固件编译时间
     */
    @Schema(description = "固件编译时间")
    private LocalDateTime firmwareCompileTime;

    /**
     * 固件数据大小
     */
    @Schema(description = "固件数据大小")
    private Integer firmwareSize;

    /**
     * 固件数据检验码
     */
    @Schema(description = "固件数据检验码")
    private Long crc32;

}
