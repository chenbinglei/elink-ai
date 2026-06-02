package com.sunmax.together.dto.operation.electricCard;

import com.sunmax.common.dto.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ElectricCardTradeSummaryDto {
    /**
     * 累计充值
     **/
    @ApiModelProperty(value = "累计充值")
    private Double totalRecharge;
    /**
     * 累计消费
     **/
    @ApiModelProperty(value = "累计消费")
    private Double totalConsume;
    /**
     * 累计退款
     **/
    @ApiModelProperty(value = "累计退款")
    private Double totalRefund;
    /**
     * 分页数据
     **/
    @ApiModelProperty(value = "分页数据")
    private PageDto<ElectricCardTradeDto> pageDto;
}

