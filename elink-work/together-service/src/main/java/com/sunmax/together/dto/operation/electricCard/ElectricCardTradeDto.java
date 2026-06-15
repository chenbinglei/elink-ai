package com.sunmax.together.dto.operation.electricCard;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "交易详情返回实体类")
public class ElectricCardTradeDto {
    /**
     * 交易单号
     */
    @Schema(description = "交易单号")
    private String id;

    /**
     * 电卡号
     */
    @Schema(description = "电卡号")
    private String carId;

    /**
     * 交易类型
     */
    @Schema(description = "交易类型")
    private Integer tradeType;

    /**
     * 交易金额
     */
    @Schema(description = "交易金额")
    private double tradeBalance;;

    /**
     * 变动后余额
     */
    @Schema(description = "变动后余额")
    private double afterTradeBalance;


    /**
     * 交易时间
     */
    @Schema(description = "交易时间")
    private String tradeTime;

}
