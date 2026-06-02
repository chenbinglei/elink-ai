package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FaultAlarmListDto", description = "故障告警列表返回实体类")
public class FaultAlarmListDto {

    /**
     * 设备主键id
     */
    @ApiModelProperty(value = "设备主键id")
    private String id;

    /**
     * 设备类型id
     */
    @ApiModelProperty(value = "设备类型id")
    private String typeId;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 事件id
     */
    @ApiModelProperty(value = "事件id")
    private String eventId;

    /**
     * 事件名称
     */
    @ApiModelProperty(value = "事件名称")
    private String eventName;

    /**
     * 事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @ApiModelProperty(value = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 告警时长
     */
    @ApiModelProperty(value = "故障时长")
    private String alarmDuration;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private String updateTime;

}
