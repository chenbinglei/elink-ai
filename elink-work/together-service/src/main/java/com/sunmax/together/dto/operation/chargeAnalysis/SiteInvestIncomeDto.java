package com.sunmax.together.dto.operation.chargeAnalysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "站点投资收益概况数据返回实体类")
public class SiteInvestIncomeDto {

    /**
     * 累计收益(元)
     */
    @Schema(description = "累计收益(元)")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    /**
     * 日均收益(元/日)
     */
    @Schema(description = "日均收益(元/日)")
    private BigDecimal dailyIncome = BigDecimal.ZERO;

    /**
     * 月均收益(元/月)
     */
    @Schema(description = "月均收益(元/月)")
    private BigDecimal monthlyIncome = BigDecimal.ZERO;

    /**
     * 年均收益(元/年)
     */
    @Schema(description = "年均收益(元/年)")
    private BigDecimal annualIncome = BigDecimal.ZERO;

    /**
     * 年化收益率(%)
     */
    @Schema(description = "年化收益率(%)")
    private Double annualYield = 0.0;

    /**
     * 投资回收周期(年)
     */
    @Schema(description = "投资回收周期(年)")
    private Double recoveryPeriod = 0.0;

}
