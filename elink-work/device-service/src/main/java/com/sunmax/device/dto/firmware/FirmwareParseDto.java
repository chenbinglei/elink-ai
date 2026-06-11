package com.sunmax.device.dto.firmware;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 固件文件解析实体类
 */
@Data
@Schema(description = "固件文件解析实体类")
public class FirmwareParseDto {

    /**
     * 固件类型
     */
    @Schema(description = "固件类型")
    private Integer firmwareType;

    /**
     * 硬件版本号
     */
    @Schema(description = "硬件版本号")
    private String hardwareVersion;

    /**
     * 固件版本号
     */
    @Schema(description = "固件版本号")
    private String firmwareVersion;

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

    /**
     * 保留(12位字节数)
     */
    @Schema(description = "保留(12位字节数)")
    private String reserve;

}
