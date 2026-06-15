package com.sunmax.together.dto.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "平台明细返回实体类")
public class PlatformDetailsDto {

    /**
     * 平台名称
     */
    @Schema(description = "平台名称")
    private String platformName;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 充电订单数量
     */
    @Schema(description = "充电订单数量")
    private Integer chargeOrderNum = 0;

    /**
     * 充电量
     */
    @Schema(description = "充电量")
    private Double chargeQt = 0.0;

    /**
     * 充电时长
     */
    @Schema(description = "充电时长")
    private Double chargeDuration = 0.0;

    /**
     * 订单量占比
     */
    @Schema(description = "订单量占比")
    private Double orderNumRatio;

    /**
     * 订单总金额
     */
    @Schema(description = "订单总金额")
    private BigDecimal orderAmount = new BigDecimal("0.0");

    /**
     * 充电电费
     */
    @Schema(description = "充电电费")
    private BigDecimal chargeElecMony = new BigDecimal("0.0");

    /**
     * 充电服务费
     */
    @Schema(description = "充电服务费")
    private BigDecimal chargeServiceMony = new BigDecimal("0.0");

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private String countDate;
}
