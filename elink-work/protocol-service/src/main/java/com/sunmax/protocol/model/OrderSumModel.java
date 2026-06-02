package com.sunmax.protocol.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderSumModel {

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
     * 累计金额
     */
    @ApiModelProperty(value = "累计金额")
    private Double totalCost;

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
