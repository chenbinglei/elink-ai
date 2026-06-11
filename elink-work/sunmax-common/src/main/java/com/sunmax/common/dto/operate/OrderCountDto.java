package com.sunmax.common.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单统计返回实体类")
public class OrderCountDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 累计充电次数
     */
    @Schema(description = "累计充电次数")
    private Integer chargeCount = 0;

    /**
     * 站点累计充电量
     */
    @Schema(description = "累计充电量")
    private Double chargeQt = 0.0;

    /**
     * 累计充电金额
     */
    @Schema(description = "累计充电金额")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 站点累计放电次数
     */
    @Schema(description = "累计放电次数")
    private Integer dischargeCount = 0;

    /**
     * 累计放电量
     */
    @Schema(description = "累计放电量")
    private Double dischargeQt = 0.0;

    /**
     * 累计放电金额
     */
    @Schema(description = "累计放电金额")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

}
