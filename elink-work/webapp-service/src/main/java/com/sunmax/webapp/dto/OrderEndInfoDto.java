package com.sunmax.webapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单结束界面详情实体类")
public class OrderEndInfoDto {

    /**
     * 订单类型 1-充电订单 2-放电订单
     */
    @Schema(description = "订单类型 1-充电订单 2-放电订单")
    private Integer orderType;

    /**
     * 使用时长
     */
    @Schema(description = "使用时长")
    private String useTime;

    /**
     * 订单金额
     */
    @Schema(description = "订单金额")
    private BigDecimal orderMoney;

    /**
     * 减免金额
     */
    @Schema(description = "减免金额")
    private BigDecimal reliefMoney;

    /**
     * 退回金额
     */
    @Schema(description = "订单金额")
    private BigDecimal returnMoney;

    /**
     * 充放电量
     */
    @Schema(description = "充放电量")
    private Double chargeQt;
}
