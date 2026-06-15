package com.sunmax.common.vo.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "设备统计值历史数据查询实体类")
public class DeviceCountQueryVo {

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

    /**
     * 统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG;最后一次值-LAST
     */
    @Schema(description = "统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG;最后一次值-LAST")
    private String cuntFun;
}
