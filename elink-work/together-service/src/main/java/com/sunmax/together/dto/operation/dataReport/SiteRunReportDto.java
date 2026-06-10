package com.sunmax.together.dto.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点运行报表返回实体类")
public class SiteRunReportDto {

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
     * 所在省份
     */
    @Schema(description = "所在省份")
    private String province;

    /**
     * 所在市
     */
    @Schema(description = "所在市")
    private String city;

    /**
     * 所在区县
     */
    @Schema(description = "所在区县")
    private String county;

    /**
     * 商户id
     */
    @Schema(description = "商户id")
    private String accountId;

    /**
     * 商户名称
     */
    @Schema(description = "商户名称")
    private String accountName;

    /**
     * 时间利用率
     */
    @Schema(description = "时间利用率")
    private Double timeUtilize = 0.0;

    /**
     * 功率利用率
     */
    @Schema(description = "功率利用率")
    private Double powerUtilize = 0.0;

    /**
     * 枪均充电量
     */
    @Schema(description = "枪均充电量")
    private Double gunAvgQt = 0.0;

    /**
     * 桩均在线时长
     */
    @Schema(description = "桩均在线时长")
    private Double pileAvgDuration;

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

    /**
     * 订单总时长
     */
    @Schema(description = "订单总时长")
    private Double totalDuration;

    /**
     * 电枪总时间
     */
    @Schema(description = "电枪总时间")
    private Double gunTotalDuration;

    /**
     * 站点下电桩在线总时长
     */
    @Schema(description = "站点下电桩在线总时长")
    private Double pileOnlineDuration;

    /**
     * 站点累计电量(充电 + 放电)
     */
    @Schema(description = "站点累计电量(充电 + 放电)")
    private Double siteSumQt;

    /**
     * 电桩累计额定功率之和 * 24
     */
    @Schema(description = "电桩累计额定功率之和 * 24")
    private Double sumPwr;

}
