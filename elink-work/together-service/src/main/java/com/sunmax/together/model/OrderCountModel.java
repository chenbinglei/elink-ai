package com.sunmax.together.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCountModel {

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
     * 运行模式 0-充电 1-放电
     */
    @ApiModelProperty(value = "运行模式 0-充电 1-放电 2-放电")
    private Integer runMode;

    /**
     * 累计次数
     */
    @ApiModelProperty(value = "累计次数")
    private Integer totalCount;

    /**
     * 累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private Double totalQt;

    /**
     * 累计充电金额
     */
    @ApiModelProperty(value = "累计充电金额")
    private BigDecimal totalMoney;

}
