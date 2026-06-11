package com.sunmax.together.vo.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备告警查询条件参数")
public class DeviceAlarmQueryVo {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期(yyyy-MM-dd)")
    private String startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期(yyyy-MM-dd)")
    private String endDate;

}
