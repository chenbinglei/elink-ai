package com.sunmax.common.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "OrderQtDto", description = "电桩订单电量返回实体类")
public class OrderQtDto {

    /**
     * 时间
     */
    @ApiModelProperty(value = "时间")
    private String dataTime;

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
