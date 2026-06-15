package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统事件列表返回实体类")
public class SystemEventListDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备事件主键id
     */
    @Schema(description = "设备事件主键id")
    private String id;

    /**
     * 事件名称
     */
    @Schema(description = "事件名称")
    private String eventName;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @Schema(description = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 多个功能点名称
     */
    @Schema(description = "多个功能点名称")
    private String functionNames;

    /**
     * 事件描述
     */
    @Schema(description = "事件描述")
    private String eventDesc;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @Schema(description = "事件状态 0-未恢复 1-已修复")
    private Integer eventStatus;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 编辑时间
     */
    @Schema(description = "编辑时间")
    private String updateTime;

}
