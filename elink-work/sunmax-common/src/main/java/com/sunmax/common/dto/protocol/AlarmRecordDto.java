package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AlarmRecordDto", description = "告警记录返回实体类")
public class AlarmRecordDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "枪编号")
    private String gunCode;

    /**
     * 故障码
     */
    @ApiModelProperty(value = "故障码")
    private Integer faultCode;

    /**
     * 特征码
     */
    @ApiModelProperty(value = "特征码")
    private Long featureCode;

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
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @ApiModelProperty(value = "事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警")
    private Integer eventLevel;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @ApiModelProperty(value = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 告警类型 1-通道类告警 2-电桩类告警
     */
    @ApiModelProperty(value = "告警类型 1-通道类告警 2-电桩类告警")
    private Integer alarmType;

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
