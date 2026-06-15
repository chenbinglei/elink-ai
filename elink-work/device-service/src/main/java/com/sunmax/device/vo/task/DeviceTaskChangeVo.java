package com.sunmax.device.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备编辑查询参数实体类")
public class DeviceTaskChangeVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;

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
     * 固件包id
     */
    @Schema(description = "固件包id")
    private String firmwareId;

    /**
     * 多个设备id 例如 ['deviceId1','deviceId2']
     */
    @Schema(description = "多个电桩编号 例如 ['deviceId1','deviceId2']")
    private String deviceIds;

}
