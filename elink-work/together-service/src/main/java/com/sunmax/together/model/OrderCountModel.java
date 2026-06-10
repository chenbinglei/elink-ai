package com.sunmax.together.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCountModel {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 运行模式 0-充电 1-放电
     */
    @Schema(description = "运行模式 0-充电 1-放电 2-放电")
    private Integer runMode;

    /**
     * 累计次数
     */
    @Schema(description = "累计次数")
    private Integer totalCount;

    /**
     * 累计充电量
     */
    @Schema(description = "累计充电量")
    private Double totalQt;

    /**
     * 累计充电金额
     */
    @Schema(description = "累计充电金额")
    private BigDecimal totalMoney;

}
