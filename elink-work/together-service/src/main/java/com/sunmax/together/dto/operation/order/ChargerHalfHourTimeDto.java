package com.sunmax.together.dto.operation.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "充放电记录半小时数据实体类")
public class ChargerHalfHourTimeDto {

    /**
     * 时间
     */
    @Schema(description = "时间")
    private String time;

    /**
     * 电量
     */
    @Schema(description = "电量")
    private Double qtValue;

    /**
     * 金额
     */
    @Schema(description = "金额")
    private BigDecimal moneyValue;

}
