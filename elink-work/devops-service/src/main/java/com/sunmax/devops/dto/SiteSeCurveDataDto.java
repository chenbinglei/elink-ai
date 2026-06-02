package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "SiteSeCurveDataDto",description = "站点储能概览曲线数据返回实体类")
public class SiteSeCurveDataDto {

    /**
     * 储能充电量
     */
    @ApiModelProperty(value = "储能充电量")
    private Double chargeQt = 0.0;

    /**
     * 储能放电量
     */
    @ApiModelProperty(value = "储能放电量")
    private Double dischargeQt = 0.0;

    /**
     * 储能收益
     */
    @ApiModelProperty(value = "储能收益")
    private BigDecimal income;

    /**
     * 储能循环次数(累计放电量 / 装机容量)
     */
    @ApiModelProperty(value = "储能循环次数")
    private Double cycleNum = 0.0;

    /**
     * 储能曲线数据1 日-(前一日功率) 月、年、总 -(储能充电量)
     */
    @ApiModelProperty(value = "储能曲线数据1 日-(前一日功率) 月、年、总 -(储能充电量)")
    private List<Double> curve1List = Lists.newArrayList();

    /**
     * 储能曲线数据2 日-(当日功率) 月、年、总 -(储能放电量)
     */
    @ApiModelProperty(value = "储能曲线数据2 日-(当日功率) 月、年、总 -(储能放电量)")
    private List<Double> curve2List = Lists.newArrayList();

    /**
     * 时间列表
     */
    @ApiModelProperty(value = "时间列表")
    private List<String> timeList = Lists.newArrayList();

    /**
     * 储能购电量(总)
     */
    @ApiModelProperty(value = "储能购电量(总)")
    public Double supKwh;

    /**
     * 储能购电量(尖时)
     */
    @ApiModelProperty(value = "储能购电量(尖)")
    public Double topSupKwh;

    /**
     * 储能购电量(峰时)
     */
    @ApiModelProperty(value = "储能购电量(峰)")
    public Double peakSupKwh;

    /**
     * 储能购电量(平时)
     */
    @ApiModelProperty(value = "储能购电量(平)")
    public Double plainSupKwh;

    /**
     * 储能购电量(谷时)
     */
    @ApiModelProperty(value = "储能购电量(谷)")
    public Double valleySupKwh;

    /**
     * 储能购电量(谷时)
     */
    @ApiModelProperty(value = "储能购电量(深谷)")
    public Double deepSupKwh;

    /**
     * 储能售电量(总)
     */
    @ApiModelProperty(value = "储能售电量(总)")
    public Double revKwh;

    /**
     * 储能售电量(尖时)
     */
    @ApiModelProperty(value = "储能售电量(尖)")
    public Double topRevKwh;

    /**
     * 储能售电量(峰时)
     */
    @ApiModelProperty(value = "储能售电量(峰)")
    public Double peakRevKwh;

    /**
     * 储能售电量(平时)
     */
    @ApiModelProperty(value = "储能售电量(平)")
    public Double plainRevKwh;

    /**
     * 储能售电量(谷时)
     */
    @ApiModelProperty(value = "储能售电量(谷)")
    public Double valleyRevKwh;

    /**
     * 储能售电量(谷时)
     */
    @ApiModelProperty(value = "储能售电量(深谷)")
    public Double deepRevKwh;

}
