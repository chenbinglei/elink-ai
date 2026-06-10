package com.sunmax.device.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "设备任务列表返回实体类")
public class DeviceTaskListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;

    /**
     * 任务状态 1-待执行 2-执行中 3-执行关闭
     */
    @Schema(description = "任务状态 1-待执行 2-执行中 3-执行关闭")
    private Integer taskStatus;

    /**
     * 任务描述
     */
    @Schema(description = "任务描述")
    private String taskDesc;

    /**
     * 类型id
     */
    @Schema(description = "类型id")
    private String typeId;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称")
    private String typeName;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String equipmentModel;

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
     * 目标版本号
     */
    @Schema(description = "目标版本号")
    private String targetVersion;

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

}
