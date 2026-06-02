package com.sunmax.common.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "OrderCountDto", description = "订单统计返回实体类")
public class OrderCountDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "电桩编号")
    private String pileCode;

    /**
     * 累计充电次数
     */
    @ApiModelProperty(value = "累计充电次数")
    private Integer chargeCount = 0;

    /**
     * 站点累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private Double chargeQt = 0.0;

    /**
     * 累计充电金额
     */
    @ApiModelProperty(value = "累计充电金额")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 站点累计放电次数
     */
    @ApiModelProperty(value = "累计放电次数")
    private Integer dischargeCount = 0;

    /**
     * 累计放电量
     */
    @ApiModelProperty(value = "累计放电量")
    private Double dischargeQt = 0.0;

    /**
     * 累计放电金额
     */
    @ApiModelProperty(value = "累计放电金额")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

}
