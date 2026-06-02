package com.sunmax.together.dto.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "PlatformDetailsDto", description = "平台明细返回实体类")
public class PlatformDetailsDto {

    /**
     * 平台名称
     */
    @ApiModelProperty(value = "平台名称")
    private String platformName;

    /**
     * 站点id
     */
    @ApiModelProperty("站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty("站点名称")
    private String siteName;

    /**
     * 充电订单数量
     */
    @ApiModelProperty("充电订单数量")
    private Integer chargeOrderNum = 0;

    /**
     * 充电量
     */
    @ApiModelProperty("充电量")
    private Double chargeQt = 0.0;

    /**
     * 充电时长
     */
    @ApiModelProperty("充电时长")
    private Double chargeDuration = 0.0;

    /**
     * 订单量占比
     */
    @ApiModelProperty("订单量占比")
    private Double orderNumRatio;

    /**
     * 订单总金额
     */
    @ApiModelProperty("订单总金额")
    private BigDecimal orderAmount = new BigDecimal("0.0");

    /**
     * 充电电费
     */
    @ApiModelProperty("充电电费")
    private BigDecimal chargeElecMony = new BigDecimal("0.0");

    /**
     * 充电服务费
     */
    @ApiModelProperty("充电服务费")
    private BigDecimal chargeServiceMony = new BigDecimal("0.0");

    /**
     * 统计时间
     */
    @ApiModelProperty("统计时间")
    private String countDate;
}
