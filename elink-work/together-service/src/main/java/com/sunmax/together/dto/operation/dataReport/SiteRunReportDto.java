package com.sunmax.together.dto.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteRunReportDto", description = "站点运行报表返回实体类")
public class SiteRunReportDto {

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
     * 所在省份
     */
    @ApiModelProperty(value = "所在省份")
    private String province;

    /**
     * 所在市
     */
    @ApiModelProperty(value = "所在市")
    private String city;

    /**
     * 所在区县
     */
    @ApiModelProperty(value = "所在区县")
    private String county;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private String accountId;

    /**
     * 商户名称
     */
    @ApiModelProperty(value = "商户名称")
    private String accountName;

    /**
     * 时间利用率
     */
    @ApiModelProperty(value = "时间利用率")
    private Double timeUtilize = 0.0;

    /**
     * 功率利用率
     */
    @ApiModelProperty(value = "功率利用率")
    private Double powerUtilize = 0.0;

    /**
     * 枪均充电量
     */
    @ApiModelProperty(value = "枪均充电量")
    private Double gunAvgQt = 0.0;

    /**
     * 桩均在线时长
     */
    @ApiModelProperty(value = "桩均在线时长")
    private Double pileAvgDuration;

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

    /**
     * 订单总时长
     */
    @ApiModelProperty(value = "订单总时长")
    private Double totalDuration;

    /**
     * 电枪总时间
     */
    @ApiModelProperty(value = "电枪总时间")
    private Double gunTotalDuration;

    /**
     * 站点下电桩在线总时长
     */
    @ApiModelProperty(value = "站点下电桩在线总时长")
    private Double pileOnlineDuration;

    /**
     * 站点累计电量(充电 + 放电)
     */
    @ApiModelProperty(value = "站点累计电量(充电 + 放电)")
    private Double siteSumQt;

    /**
     * 电桩累计额定功率之和 * 24
     */
    @ApiModelProperty(value = "电桩累计额定功率之和 * 24")
    private Double sumPwr;

}
