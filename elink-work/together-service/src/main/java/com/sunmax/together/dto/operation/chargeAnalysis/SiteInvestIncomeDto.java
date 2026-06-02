package com.sunmax.together.dto.operation.chargeAnalysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "SiteInvestIncomeDto", description = "站点投资收益概况数据返回实体类")
public class SiteInvestIncomeDto {

    /**
     * 累计收益(元)
     */
    @ApiModelProperty(value = "累计收益(元)")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    /**
     * 日均收益(元/日)
     */
    @ApiModelProperty(value = "日均收益(元/日)")
    private BigDecimal dailyIncome = BigDecimal.ZERO;

    /**
     * 月均收益(元/月)
     */
    @ApiModelProperty(value = "月均收益(元/月)")
    private BigDecimal monthlyIncome = BigDecimal.ZERO;

    /**
     * 年均收益(元/年)
     */
    @ApiModelProperty(value = "年均收益(元/年)")
    private BigDecimal annualIncome = BigDecimal.ZERO;

    /**
     * 年化收益率(%)
     */
    @ApiModelProperty(value = "年化收益率(%)")
    private Double annualYield = 0.0;

    /**
     * 投资回收周期(年)
     */
    @ApiModelProperty(value = "投资回收周期(年)")
    private Double recoveryPeriod = 0.0;

}
