package com.sunmax.together.vo.operation.deviceManage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AlarmListQueryVo", description = "告警列表查询参数")
public class AlarmListQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id")
    private String siteId;

    /**
     * 电桩编码
     */
    @ApiModelProperty(value = "电桩编码")
    private String pileCode;

    /**
     * 故障码
     */
    @ApiModelProperty(value = "故障码")
    private String faultCode;

    /**
     * 告警开始时间
     */
    @ApiModelProperty(value = "告警开始时间")
    private String alarmStartDate;

    /**
     * 告警结束时间
     */
    @ApiModelProperty(value = "告警结束时间")
    private String alarmEndDate;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @ApiModelProperty(value = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;
}
