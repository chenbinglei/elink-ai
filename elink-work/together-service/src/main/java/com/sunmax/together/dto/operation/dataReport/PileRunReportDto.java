package com.sunmax.together.dto.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩运行报表返回实体类")
public class PileRunReportDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 电桩编码
     */
    @Schema(description = "电桩编码")
    private String pileCode;

    /**
     * 电枪编码
     */
    @Schema(description = "电枪编码")
    private Integer gunCode;

    /**
     * 电桩类型 28-交流 29-直流 30-V2G
     */
    @Schema(description = "电桩类型 28-交流 29-直流 30-V2G")
    private String typeTd;

    /**
     * 电桩额定功率
     */
    @Schema(description = "电桩额定功率")
    private Double ratedPower = 0.0;

    /**
     * 充电时长
     */
    @Schema(description = "充电时长")
    private Double chargeDuration = 0.0;

    /**
     * 放电时长
     */
    @Schema(description = "放电时长")
    private Double dischargeDuration = 0.0;

    /**
     * 时间利用率
     */
    @Schema(description = "时间利用率")
    private Double timeUtilize;

    /**
     * 日均充电量
     */
    @Schema(description = "日均充电量")
    private Double dayAvgQt = 0.0;

    /**
     * 在线时长
     */
    @Schema(description = "在线时长")
    private Double onlineDuration = 0.0;

    /**
     * 设备在线率
     */
    @Schema(description = "设备在线率")
    private Double onlineRate;

    /**
     * 告警次数
     */
    @Schema(description = "告警次数")
    private Integer alarmNum = 0;

    /**
     * 启动失败次数
     */
    @Schema(description = "启动失败次数")
    private Integer startFailNum = 0;

    /**
     * 异常订单数量
     */
    @Schema(description = "异常订单数量")
    private Integer abnormalOrderNum = 0;

    /**
     * 订单异常率
     */
    @Schema(description = "订单异常率")
    private Double abnormalOrderRate;

    /**
     * 订单数量
     */
    @Schema(description = "订单数量")
    private Integer orderCount = 0;
}
