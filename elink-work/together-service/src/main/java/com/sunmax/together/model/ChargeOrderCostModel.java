package com.sunmax.together.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeOrderCostModel {

    /**
     * 数据日期
     */
    @ApiModelProperty(value = "数据日期")
    private String dataTime;

    /**
     * 运行模式 0-充电 1-放电 部分接口数据返回为空
     */
    @ApiModelProperty(value = "运行模式 0-充电 1-放电 2-放电 部分接口数据返回为空")
    private Integer runMode;

    /**
     * 累计次数
     */
    @ApiModelProperty(value = "累计次数")
    private Integer totalCount;

    /**
     * 累计金额
     */
    @ApiModelProperty(value = "累计金额")
    private BigDecimal totalCost;

    /**
     * 累计电量
     */
    @ApiModelProperty(value = "累计电量")
    private Double totalQt;

}
