package com.sunmax.together.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderSumDataModel {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "电桩编号")
    private String pileCode;

    /**
     * 电枪编码
     */
    @ApiModelProperty(value = "电枪编码")
    private Integer gunCode;

    /**
     * 累计次数
     */
    @ApiModelProperty(value = "累计次数")
    private Integer totalCount;

    /**
     * 累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private Double totalQt;

    /**
     * 累计充电时长(单位：秒)
     */
    @ApiModelProperty(value = "累计充电时长(单位：秒)")
    private Integer chargeDuration;

    /**
     * 尖时电量
     */
    @ApiModelProperty(value = "尖时电量")
    private Double jQt;

    /**
     * 峰时电量
     */
    @ApiModelProperty(value = "峰时电量")
    private Double fQt;

    /**
     * 平时电量
     */
    @ApiModelProperty(value = "平时电量")
    private Double pQt;

    /**
     * 谷时电量
     */
    @ApiModelProperty(value = "谷时电量")
    private Double gQt;
}
