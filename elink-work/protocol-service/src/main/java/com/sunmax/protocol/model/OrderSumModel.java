package com.sunmax.protocol.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderSumModel {

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 电枪编码
     */
    @Schema(description = "电枪编码")
    private Integer gunCode;

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
     * 累计金额
     */
    @Schema(description = "累计金额")
    private Double totalCost;

    /**
     * 尖时电量
     */
    @Schema(description = "尖时电量")
    private Double jQt;

    /**
     * 峰时电量
     */
    @Schema(description = "峰时电量")
    private Double fQt;

    /**
     * 平时电量
     */
    @Schema(description = "平时电量")
    private Double pQt;

    /**
     * 谷时电量
     */
    @Schema(description = "谷时电量")
    private Double gQt;

}
