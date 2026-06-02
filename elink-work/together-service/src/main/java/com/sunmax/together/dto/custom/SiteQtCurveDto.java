package com.sunmax.together.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "SiteQtCurveDto", description = "电站电量统计曲线返回实体类")
public class SiteQtCurveDto {

    /**
     * 日期列表
     */
    @ApiModelProperty(value = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 曲线1列表(关口-正向电量 直流母线-正向电量 光伏-发电量 储能-充电量 负载-用电量)
     */
    @ApiModelProperty(value = "曲线1列表(关口-正向电量 直流母线-正向电量 光伏-发电量 储能-充电量 负载-用电量)")
    private List<Double> curve1List = Lists.newArrayList();

    /**
     * 曲线2列表(关口-正向电流 直流母线-正向电量 储能-放电量)
     */
    @ApiModelProperty(value = "曲线2列表(关口-正向电量 直流母线-正向电量 储能-放电量)")
    private List<Double> curve2List = Lists.newArrayList();

}
