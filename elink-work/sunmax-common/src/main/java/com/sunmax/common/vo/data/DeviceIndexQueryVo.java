package com.sunmax.common.vo.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Schema(description = "设备历史下标查询参数实体类")
public class DeviceIndexQueryVo {

    /**
     * 多个功能点标识 设备id -> 功能点标识 + index + 0
     */
    @Schema(description = "多个功能点标识")
    private Map<String, Set<String>> deviceFuctionMap;

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
