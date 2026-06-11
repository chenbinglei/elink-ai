package com.sunmax.common.vo.webapp;

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
@Schema(description = "小程序充电退款参数实体类")
public class AppletChargeRefundVo {

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNum;

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 退款金额
     */
    @Schema(description = "退款金额")
    private BigDecimal refundMoney;

    /**
     * 平台类型 1-微信 2-支付宝 3-银联
     */
    @Schema(description = "平台类型 1-微信 2-支付宝 3-银联")
    private Integer platformType;

    /**
     * 类型 1-启动失败退款 2-充电完成退款 3-平台人工退款
     */
    @Schema(description = "类型 1-启动失败退款 2-充电完成退款 3-平台人工退款")
    private Integer type;

    /**
     * 退款操作人
     */
    @Schema(description = "退款操作人")
    private String refundOperator;

}
