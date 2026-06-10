package com.sunmax.together.vo.operation.chargeAnalysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "站点收益入参参数实体类")
public class SiteIncomeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 收益模型id(目前默认展示工商业V2G)
     */
    @Schema(description = "收益模型id(目前默认展示工商业V2G)")
    private String incomeModelId;

    /**
     * 设备总成本(元)
     */
    @Schema(description = "设备总成本(元)")
    private BigDecimal deviceCost;

    /**
     * 施工总费用(元)
     */
    @Schema(description = "施工总费用(元)")
    private BigDecimal constructionCost;

    /**
     * 场地租金(元/月)
     */
    @Schema(description = "场地租金(元/月)")
    private BigDecimal siteRent;

    /**
     * 场地租金开始时间
     */
    @Schema(description = "场地租金开始时间")
    private String rentStartDate;

    /**
     * 场地租金结束时间
     */
    @Schema(description = "场地租金结束时间")
    private String rentEndDate;

    /**
     * 充电运营补贴(元/度)
     */
    @Schema(description = "充电运营补贴(元/度)")
    private BigDecimal operationSubsidy;

    /**
     * 充电运营补贴开始时间
     */
    @Schema(description = "充电运营补贴开始时间")
    private String subsidyStartDate;

    /**
     * 充电运营补贴结束时间
     */
    @Schema(description = "充电运营补贴结束时间")
    private String subsidyEndDate;

    /**
     * 建设补贴(元)
     */
    @Schema(description = "建设补贴(元)")
    private BigDecimal constructionSubsidy;

    /**
     * 运营成本(元/月)
     */
    @Schema(description = "运营成本(元/月)")
    private BigDecimal operationCost;

    /**
     * 运维成本(元/月)
     */
    @Schema(description = "运维成本(元/月)")
    private BigDecimal maintainCost;

}
