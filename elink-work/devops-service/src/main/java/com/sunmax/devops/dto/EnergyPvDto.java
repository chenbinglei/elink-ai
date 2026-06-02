package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "EnergyPvDto", description = "电能趋势-光伏返回实体类")
public class EnergyPvDto {

    /**
     * 光伏发电量
     */
    @ApiModelProperty(value = "光伏发电量")
    private Double generateQt = 0.0;

    /**
     * 光伏上网电量
     */
    @ApiModelProperty(value = "光伏上网电量")
    private Double netQt = 0.0;

    /**
     * 光伏消纳电量
     */
    @ApiModelProperty(value = "光伏消纳电量")
    private Double consumeQt = 0.0;

    /**
     * 光伏等效发电时长(小时)
     */
    @ApiModelProperty(value = "光伏等效发电时长(小时)")
    private Double effectiveTime = 0.0;

    /**
     * 光伏曲线数据1 日-(前一日功率) 月、年、总 -(光伏实际发电量)
     */
    @ApiModelProperty(value = "光伏曲线数据1 日-(前一日功率) 月、年、总-(光伏实际发电量)")
    private List<Double> curve1List = Lists.newArrayList();

    /**
     * 光伏曲线数据2 日-(当日功率) 月、年、总 -(光伏理论发电量)
     */
    @ApiModelProperty(value = "光伏曲线数据2 日-(当日功率) 月、年、总-(光伏理论发电量)")
    private List<Double> curve2List = Lists.newArrayList();

    /**
     * 时间列表
     */
    @ApiModelProperty(value = "时间列表")
    private List<String> timeList = Lists.newArrayList();

}
