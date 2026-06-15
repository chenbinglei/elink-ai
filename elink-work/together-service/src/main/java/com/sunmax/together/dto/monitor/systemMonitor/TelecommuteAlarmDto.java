package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TelecommuteAlarmDto {

    /**
     * 设备事件id
     */
    @Schema(description = "设备事件id")
    private String id;

    /**
     * 事件名称
     */
    @Schema(description = "事件名称")
    private String eventName;

    /**
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @Schema(description = "事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警")
    private Integer eventLevel;

    /**
     * 事件类型 1-模型事件 2-故障定义
     */
    @Schema(description = "事件类型 1-模型事件 2-故障定义")
    private Integer type;

    /**
     * 告警时间
     */
    @Schema(description = "告警时间")
    private String alarmTime;

}
