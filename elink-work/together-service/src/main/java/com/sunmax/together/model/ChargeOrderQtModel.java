package com.sunmax.together.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChargeOrderQtModel {

    /**
     * 数据日期
     */
    @ApiModelProperty(value = "数据日期")
    private String dataTime;

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
