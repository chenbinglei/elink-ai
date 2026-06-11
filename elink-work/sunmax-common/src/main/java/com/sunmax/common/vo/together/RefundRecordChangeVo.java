package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "退款记录编辑参数")
public class RefundRecordChangeVo {

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNum;

    /**
     * 退款金额
     */
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    /**
     * 退款操作人
     */
    @Schema(description = "退款操作人")
    private String refundOperator;
}
