package com.sunmax.together.dto.operation.electricCard;

import com.sunmax.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ElectricCardTradeSummaryDto {
    /**
     * 累计充值
     **/
    @Schema(description = "累计充值")
    private Double totalRecharge;
    /**
     * 累计消费
     **/
    @Schema(description = "累计消费")
    private Double totalConsume;
    /**
     * 累计退款
     **/
    @Schema(description = "累计退款")
    private Double totalRefund;
    /**
     * 分页数据
     **/
    @Schema(description = "分页数据")
    private PageDto<ElectricCardTradeDto> pageDto;
}

