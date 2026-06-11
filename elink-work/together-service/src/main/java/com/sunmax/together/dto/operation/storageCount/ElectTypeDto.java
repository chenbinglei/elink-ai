package com.sunmax.together.dto.operation.storageCount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "电价类型返回实体类")
public class ElectTypeDto {

    /**
     * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天
     */
    @Schema(description = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天")
    private Integer periodType;

    /**
     * 电费
     */
    @Schema(description = "电费")
    private BigDecimal electMoney;

}
