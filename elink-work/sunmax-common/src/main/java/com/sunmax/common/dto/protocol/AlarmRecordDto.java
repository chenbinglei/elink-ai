package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "告警记录返回实体类")
public class AlarmRecordDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private String gunCode;

    /**
     * 故障码
     */
    @Schema(description = "故障码")
    private Integer faultCode;

    /**
     * 特征码
     */
    @Schema(description = "特征码")
    private Long featureCode;

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
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @Schema(description = "事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警")
    private Integer eventLevel;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @Schema(description = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 告警类型 1-通道类告警 2-电桩类告警
     */
    @Schema(description = "告警类型 1-通道类告警 2-电桩类告警")
    private Integer alarmType;

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
