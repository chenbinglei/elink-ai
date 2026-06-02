package com.sunmax.common.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceEventQueryVo", description = "设备事件查询条件参数")
public class DeviceAlarmEventQueryVo {

    /**
     * 多个设备id
     */
    @ApiModelProperty(value = "多个设备id", required = true)
    private String deviceIds;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @ApiModelProperty(value = "事件状态 0-未恢复 1-已修复")
    private Integer eventStatus;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private String endDate;

    /**
     * 忽略状态 0-未忽略 1-已忽略
     */
    @ApiModelProperty(value = "忽略状态 0-未忽略 1-已忽略")
    private Integer ignoreStatus;

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
