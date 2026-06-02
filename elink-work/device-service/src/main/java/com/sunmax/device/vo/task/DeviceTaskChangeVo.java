package com.sunmax.device.vo.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceTaskChangeVo", description = "设备编辑查询参数实体类")
public class DeviceTaskChangeVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", required = true)
    private String taskName;

    /**
     * 任务描述
     */
    @ApiModelProperty(value = "任务描述")
    private String taskDesc;

    /**
     * 类型id
     */
    @ApiModelProperty(value = "类型id", required = true)
    private String typeId;

    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号", required = true)
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
    @ApiModelProperty(value = "固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_7.0 CCU控制板 9-V2G_9.0 TCP_BOOT控制板 10-V2G_10.0 TPU_BOOT控制板 11-V2G_11.0 CCU_BOOT控制板", required = true)
    private Integer firmwareType;

    /**
     * 固件包id
     */
    @ApiModelProperty(value = "固件包id", required = true)
    private String firmwareId;

    /**
     * 多个设备id 例如 ['deviceId1','deviceId2']
     */
    @ApiModelProperty(value = "多个电桩编号 例如 ['deviceId1','deviceId2']", required = true)
    private String deviceIds;

}
