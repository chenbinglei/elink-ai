package com.sunmax.together.dto.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileRunReportDto", description = "电桩运行报表返回实体类")
public class PileRunReportDto {

    /**
     * 站点id
     */
    @ApiModelProperty("站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty("站点名称")
    private String siteName;

    /**
     * 电桩编码
     */
    @ApiModelProperty(value = "电桩编码")
    private String pileCode;

    /**
     * 电枪编码
     */
    @ApiModelProperty(value = "电枪编码")
    private Integer gunCode;

    /**
     * 电桩类型 28-交流 29-直流 30-V2G
     */
    @ApiModelProperty(value = "电桩类型 28-交流 29-直流 30-V2G")
    private String typeTd;

    /**
     * 电桩额定功率
     */
    @ApiModelProperty(value = "电桩额定功率")
    private Double ratedPower = 0.0;

    /**
     * 充电时长
     */
    @ApiModelProperty(value = "充电时长")
    private Double chargeDuration = 0.0;

    /**
     * 放电时长
     */
    @ApiModelProperty(value = "放电时长")
    private Double dischargeDuration = 0.0;

    /**
     * 时间利用率
     */
    @ApiModelProperty(value = "时间利用率")
    private Double timeUtilize;

    /**
     * 日均充电量
     */
    @ApiModelProperty(value = "日均充电量")
    private Double dayAvgQt = 0.0;

    /**
     * 在线时长
     */
    @ApiModelProperty(value = "在线时长")
    private Double onlineDuration = 0.0;

    /**
     * 设备在线率
     */
    @ApiModelProperty(value = "设备在线率")
    private Double onlineRate;

    /**
     * 告警次数
     */
    @ApiModelProperty(value = "告警次数")
    private Integer alarmNum = 0;

    /**
     * 启动失败次数
     */
    @ApiModelProperty(value = "启动失败次数")
    private Integer startFailNum = 0;

    /**
     * 异常订单数量
     */
    @ApiModelProperty(value = "异常订单数量")
    private Integer abnormalOrderNum = 0;

    /**
     * 订单异常率
     */
    @ApiModelProperty(value = "订单异常率")
    private Double abnormalOrderRate;

    /**
     * 订单数量
     */
    @ApiModelProperty(value = "订单数量")
    private Integer orderCount = 0;
}
