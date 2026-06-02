package com.sunmax.common.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "ChargerPriceRateDto", description = "充放电价格费率返回实体类")
public class ChargerPriceRateDto {

    /**
     * 价格id
     */
    @ApiModelProperty("价格id")
    private String priceId;

    /**
     * 时段开始时间
     */
    @ApiModelProperty("时段开始时间")
    private String startTime;

    /**
     * 时段结束时间
     */
    @ApiModelProperty("时段结束时间")
    private String endTime;

    /**
     * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天
     */
    @ApiModelProperty("时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天")
    private Integer periodType;

    /**
     * 电费
     */
    @ApiModelProperty("电费")
    private BigDecimal electMoney;

    /**
     * 服务费
     */
    @ApiModelProperty("服务费")
    private BigDecimal serviceMoney;
}
