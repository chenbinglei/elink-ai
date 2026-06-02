package com.sunmax.together.vo.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceAlarmQueryVo", description = "设备告警查询条件参数")
public class DeviceAlarmQueryVo {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true)
    private String deviceId;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期(yyyy-MM-dd)", required = true)
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期(yyyy-MM-dd)", required = true)
    private String endDate;

}
