package com.sunmax.together.dto.operation.deviceManage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AlarmListDto", description = "告警列表返回实体类")
public class AlarmListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 运营商名称
     */
    @ApiModelProperty(value = "运营商名称")
    private String operateUnitName;

    /**
     * 运营商id
     */
    @ApiModelProperty(value = "运营商id")
    private String operateUnitId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

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
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
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

    /**
     * 故障时长
     */
    @ApiModelProperty(value = "故障时长")
    private String alarmDuration;
}
