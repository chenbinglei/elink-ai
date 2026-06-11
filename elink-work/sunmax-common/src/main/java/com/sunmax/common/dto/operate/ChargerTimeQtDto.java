package com.sunmax.common.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: yqz
 * @Date: 2023/7/413:41
 * @version: 1.0
 * @注释: 充放电时段电量返回实体类
 */
@Data
@Schema(description = "chargerTimeQtDto")
public class ChargerTimeQtDto {

    /**
     * 电量日期
     */
    @Schema(description = "电量日期")
    private String qtDate;

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 终端编号
     */
    @Schema(description = "终端编号")
    private Integer gunCode;

    /**
     * 电量类型 0-充电模式 1-放电模式
     */
    @Schema(description = "电量类型 0-充电模式 1-放电模式")
    private Integer qtType;

    /**
     * 电量值
     */
    @Schema(description = "电量值")
    private Double qtValue = 0.0;

    /**
     * 金额值
     */
    @Schema(description = "金额值")
    private BigDecimal moneyValue = new BigDecimal("0.0");

    /**
     * 电费值
     */
    @Schema(description = "电费值")
    private BigDecimal eleFeeValue = new BigDecimal("0.0");

    /**
     * 服务费值
     */
    @Schema(description = "服务费值")
    private BigDecimal serviceFeeValue = new BigDecimal("0.0");

    /**
     * 时长值
     */
    @Schema(description = "时长值")
    private Double durationValue = 0.0;

    /**
     * 来源平台标识
     */
    @Schema(description = "来源平台标识")
    private String platformLogo;
}
