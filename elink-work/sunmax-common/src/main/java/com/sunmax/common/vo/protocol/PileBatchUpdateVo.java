package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 电桩批量升级参数实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "电桩批量升级参数实体类")
public class PileBatchUpdateVo {

    /**
     * 任务id
     */
    @Schema(description = "任务id")
    private String taskId;

    /**
     * 充电桩编码
     */
    @Schema(description = "多个充电桩编码")
    private Set<String> pileCodes;

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
     * 1-V2G_1.0 TCP控制板
     * 2-V2G_2.0 TCP控制板
     * 3-V2G_3.0 TCP控制板
     * 4-V2G_4.0 TPU控制板
     * 5-V2G_4.0 CCU控制板
     * 6-V2G_6.0 TCP控制板
     * 7-V2G_7.0 TPU控制板
     * 8-V2G_7.0 CCU控制板
     * 9-V2G_9.0 TCP_BOOT控制板
     * 10-V2G_10.0 TPU_BOOT控制板
     * 11-V2G_11.0 CCU_BOOT控制板
     */
    @Schema(description = "固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_7.0 CCU控制板 9-V2G_9.0 TCP_BOOT控制板 10-V2G_10.0 TPU_BOOT控制板 11-V2G_11.0 CCU_BOOT控制板")
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
