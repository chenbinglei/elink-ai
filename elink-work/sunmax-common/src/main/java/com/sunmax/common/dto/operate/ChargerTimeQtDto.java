package com.sunmax.common.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: yqz
 * @Date: 2023/7/413:41
 * @version: 1.0
 * @注释: 充放电时段电量返回实体类
 */
@Data
@ApiModel("chargerTimeQtDto")
public class ChargerTimeQtDto {

    /**
     * 电量日期
     */
    @ApiModelProperty("电量日期")
    private String qtDate;

    /**
     * 电桩编号
     */
    @ApiModelProperty("电桩编号")
    private String pileCode;

    /**
     * 终端编号
     */
    @ApiModelProperty("终端编号")
    private Integer gunCode;

    /**
     * 电量类型 0-充电模式 1-放电模式
     */
    @ApiModelProperty("电量类型 0-充电模式 1-放电模式")
    private Integer qtType;

    /**
     * 电量值
     */
    @ApiModelProperty("电量值")
    private Double qtValue = 0.0;

    /**
     * 金额值
     */
    @ApiModelProperty("金额值")
    private BigDecimal moneyValue = new BigDecimal("0.0");

    /**
     * 电费值
     */
    @ApiModelProperty("电费值")
    private BigDecimal eleFeeValue = new BigDecimal("0.0");

    /**
     * 服务费值
     */
    @ApiModelProperty("服务费值")
    private BigDecimal serviceFeeValue = new BigDecimal("0.0");

    /**
     * 时长值
     */
    @ApiModelProperty("时长值")
    private Double durationValue = 0.0;

    /**
     * 来源平台标识
     */
    @ApiModelProperty("来源平台标识")
    private String platformLogo;
}
