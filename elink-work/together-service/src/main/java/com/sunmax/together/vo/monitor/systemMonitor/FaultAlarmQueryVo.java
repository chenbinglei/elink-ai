package com.sunmax.together.vo.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FaultAlarmQueryVo", description = "故障告警查询条件参数实体类")
public class FaultAlarmQueryVo {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 设备类型id
     */
    @ApiModelProperty(value = "设备类型id")
    private String typeId;

    /**
     * 事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 开始时间(yyyy-MM-dd)
     */
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd)")
    private String startDate;

    /**
     * 结束时间(yyyy-MM-dd)
     */
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd)")
    private String endDate;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @ApiModelProperty(value = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

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

}
