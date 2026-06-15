package com.sunmax.device.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备功能点查询条件")
public class DeviceFunctionQueryVo {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 功能点id
     */
    @Schema(description = "功能点id")
    private String functionId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年
     */
    @Schema(description = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年")
    private String timeInterval;

}
