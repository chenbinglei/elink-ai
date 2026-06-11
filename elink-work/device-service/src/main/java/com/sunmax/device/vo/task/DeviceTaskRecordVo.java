package com.sunmax.device.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备任务记录查询参数实体类
 */
@Data
@Schema(description = "设备任务记录查询参数实体类")
public class DeviceTaskRecordVo {

    /**
     * 任务id
     */
    @Schema(description = "任务id")
    private String taskId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

}
