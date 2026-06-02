package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "PileBatchUpdateVo", description = "电桩批量升级参数实体类")
public class PileBatchUpdateVo {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", required = true)
    private String taskId;

    /**
     * 充电桩编码
     */
    @ApiModelProperty(value = "多个充电桩编码", required = true)
    private Set<String> pileCodes;

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
    @ApiModelProperty(value = "固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_7.0 CCU控制板 9-V2G_9.0 TCP_BOOT控制板 10-V2G_10.0 TPU_BOOT控制板 11-V2G_11.0 CCU_BOOT控制板", required = true)
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
