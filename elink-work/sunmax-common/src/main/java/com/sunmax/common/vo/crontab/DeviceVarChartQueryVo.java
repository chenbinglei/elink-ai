package com.sunmax.common.vo.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "组态电枪系统变量图表查询实体类")
public class DeviceVarChartQueryVo {

    /**
     * 多个设备id(以逗号分割)
     */
    @Schema(description = "多个设备id(以逗号分割)")
    private String deviceIds;

    /**
     * 站点唯一id(不可传多个，和设备编码同时只能传一个)
     */
    @Schema(description = "站点唯一id(不可传多个，和设备编码同时只能传一个)")
    private String siteId;

    /**
     * 多个系统变量标识(以逗号分割)
     */
    @Schema(description = "多个系统变量标识(以逗号分割)")
    private String varCodes;

    /**
     * 查询时间类型 0-当日 1-昨日 2-近七天 3-近30天 4-当月 5-上月 6-本年
     */
    @Schema(description = "查询时间类型 0-当日 1-昨日 2-近七天 3-近30天 4-当月 5-上月 6-本年")
    private Integer dateType;

    /**
     * 时间间隔 m-分钟;h-小时;d-天;n-月;y-年
     */
    @Schema(description = "时间间隔 m-分钟;h-小时;d-天;n-月;y-年")
    private String timeInterval;

    /**
     * 查询条数
     */
    @Schema(description = "查询条数")
    private Integer limitSize;
}
