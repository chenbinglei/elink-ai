package com.sunmax.together.dto.operation.storageCount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "ElectTypeDto", description = "电价类型返回实体类")
public class ElectTypeDto {

    /**
     * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天
     */
    @ApiModelProperty(value = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天")
    private Integer periodType;

    /**
     * 电费
     */
    @ApiModelProperty(value = "电费")
    private BigDecimal electMoney;

}
