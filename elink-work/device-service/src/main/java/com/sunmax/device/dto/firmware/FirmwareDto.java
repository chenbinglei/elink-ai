package com.sunmax.device.dto.firmware;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 固件包列表实体类
 */
@Data
@Schema(description = "固件包列表返回实体类")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FirmwareDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备类型id
     */
    @Schema(description = "设备类型id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @Schema(description = "设备类型名称")
    private String typeName;

    /**
     * 设备型号(多选,例如'型号1','型号2')
     */
    @Schema(description = "设备型号(多选,例如'型号1','型号2')")
    private String equipmentModels;

    /**
     * 固件包名称
     */
    @Schema(description = "固件包名称")
    private String firmwareName;

    /**
     * 固件包路径
     */
    @Schema(description = "固件包路径")
    private String firmwarePath;

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
     * 固件版本号
     */
    @Schema(description = "固件版本号")
    private String firmwareVersion;

    /**
     * 固件文件大小
     */
    @Schema(description = "固件文件大小")
    private Long firmwareSize;

    /**
     * 固件文件大小展示
     */
    @Schema(description = "固件文件大小展示")
    private String firmwareSizeShow;

    /**
     * 固件包描述
     */
    @Schema(description = "固件包描述")
    private String firmwareDesc;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称")
    private String createName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称")
    private String updateName;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
