package com.sunmax.together.vo.operation.electricCard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "交易查询参数")
public class ElectricCardTradeVo {
    /**
     * 交易类型
     */
    @Schema(description = "交易类型")
    private Integer tradeType;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startDate;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endDate;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
