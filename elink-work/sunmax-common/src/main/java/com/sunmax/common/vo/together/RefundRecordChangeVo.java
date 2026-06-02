package com.sunmax.common.vo.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "RefundRecordChangeVo", description = "退款记录编辑参数")
public class RefundRecordChangeVo {

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", required = true)
    private String orderNum;

    /**
     * 退款金额
     */
    @ApiModelProperty(value = "退款金额", required = true)
    private BigDecimal refundAmount;

    /**
     * 退款操作人
     */
    @ApiModelProperty(value = "退款操作人")
    private String refundOperator;
}
