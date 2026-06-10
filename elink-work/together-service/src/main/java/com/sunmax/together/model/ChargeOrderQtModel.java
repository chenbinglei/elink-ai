package com.sunmax.together.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChargeOrderQtModel {

    /**
     * 数据日期
     */
    @Schema(description = "数据日期")
    private String dataTime;

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
