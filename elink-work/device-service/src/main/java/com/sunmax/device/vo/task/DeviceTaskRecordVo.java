package com.sunmax.device.vo.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备任务记录查询参数实体类
 */
@Data
@ApiModel(value = "DeviceTaskRecordVo", description = "设备任务记录查询参数实体类")
public class DeviceTaskRecordVo {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", required = true)
    private String taskId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

}
