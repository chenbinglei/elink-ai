package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "故障告警列表返回实体类")
public class FaultAlarmListDto {

    /**
     * 设备主键id
     */
    @Schema(description = "设备主键id")
    private String id;

    /**
     * 设备类型id
     */
    @Schema(description = "设备类型id")
    private String typeId;

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceCode;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 事件id
     */
    @Schema(description = "事件id")
    private String eventId;

    /**
     * 事件名称
     */
    @Schema(description = "事件名称")
    private String eventName;

    /**
     * 事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警
     */
    @Schema(description = "事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @Schema(description = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 告警时长
     */
    @Schema(description = "故障时长")
    private String alarmDuration;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private String updateTime;

}
