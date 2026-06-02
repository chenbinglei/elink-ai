package com.sunmax.together.dto.operation.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("充放电记录半小时数据实体类")
public class ChargerHalfHourTimeDto {

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private String time;

    /**
     * 电量
     */
    @ApiModelProperty("电量")
    private Double qtValue;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private BigDecimal moneyValue;

}
