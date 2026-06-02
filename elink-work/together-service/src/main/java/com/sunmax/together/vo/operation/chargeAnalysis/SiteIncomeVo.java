package com.sunmax.together.vo.operation.chargeAnalysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "SiteIncomeVo", description = "站点收益入参参数实体类")
public class SiteIncomeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 收益模型id(目前默认展示工商业V2G)
     */
    @ApiModelProperty(value = "收益模型id(目前默认展示工商业V2G)", required = true)
    private String incomeModelId;

    /**
     * 设备总成本(元)
     */
    @ApiModelProperty(value = "设备总成本(元)", required = true)
    private BigDecimal deviceCost;

    /**
     * 施工总费用(元)
     */
    @ApiModelProperty(value = "施工总费用(元)", required = true)
    private BigDecimal constructionCost;

    /**
     * 场地租金(元/月)
     */
    @ApiModelProperty(value = "场地租金(元/月)")
    private BigDecimal siteRent;

    /**
     * 场地租金开始时间
     */
    @ApiModelProperty(value = "场地租金开始时间")
    private String rentStartDate;

    /**
     * 场地租金结束时间
     */
    @ApiModelProperty(value = "场地租金结束时间")
    private String rentEndDate;

    /**
     * 充电运营补贴(元/度)
     */
    @ApiModelProperty(value = "充电运营补贴(元/度)")
    private BigDecimal operationSubsidy;

    /**
     * 充电运营补贴开始时间
     */
    @ApiModelProperty(value = "充电运营补贴开始时间")
    private String subsidyStartDate;

    /**
     * 充电运营补贴结束时间
     */
    @ApiModelProperty(value = "充电运营补贴结束时间")
    private String subsidyEndDate;

    /**
     * 建设补贴(元)
     */
    @ApiModelProperty(value = "建设补贴(元)")
    private BigDecimal constructionSubsidy;

    /**
     * 运营成本(元/月)
     */
    @ApiModelProperty(value = "运营成本(元/月)")
    private BigDecimal operationCost;

    /**
     * 运维成本(元/月)
     */
    @ApiModelProperty(value = "运维成本(元/月)")
    private BigDecimal maintainCost;

}
