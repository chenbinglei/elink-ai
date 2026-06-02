package com.sunmax.together.dto.operation.electricCard;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ElectricCardTradeDto", description = "交易详情返回实体类")
public class ElectricCardTradeDto {
    /**
     * 交易单号
     */
    @ApiModelProperty(value = "交易单号")
    private String id;

    /**
     * 电卡号
     */
    @ApiModelProperty(value = "电卡号")
    private String carId;

    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型")
    private Integer tradeType;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private double tradeBalance;;

    /**
     * 变动后余额
     */
    @ApiModelProperty(value = "变动后余额")
    private double afterTradeBalance;


    /**
     * 交易时间
     */
    @ApiModelProperty(value = "交易时间")
    private String tradeTime;

}
