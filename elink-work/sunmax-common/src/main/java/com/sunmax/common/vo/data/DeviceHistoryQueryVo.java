package com.sunmax.common.vo.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "设备历史数据查询实体类")
public class DeviceHistoryQueryVo {

    /**
     * 多个设备id
     */
    @Schema(description = "多个设备id")
    private Set<String> deviceIds;

    /**
     * 多个功能点标识
     */
    @Schema(description = "多个功能点标识")
    private Set<String> functionLogos;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 查询条数
     */
    @Schema(description = "查询条数")
    private Integer limitSize;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年
     */
    @Schema(description = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年")
    private String timeInterval;

}
