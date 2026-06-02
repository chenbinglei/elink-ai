package com.sunmax.common.vo.webapp;

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
@ApiModel(value = "AppletChargeRefundVo", description = "小程序充电退款参数实体类")
public class AppletChargeRefundVo {

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNum;

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "电桩编号")
    private String pileCode;

    /**
     * 退款金额
     */
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundMoney;

    /**
     * 平台类型 1-微信 2-支付宝 3-银联
     */
    @ApiModelProperty(value = "平台类型 1-微信 2-支付宝 3-银联")
    private Integer platformType;

    /**
     * 类型 1-启动失败退款 2-充电完成退款 3-平台人工退款
     */
    @ApiModelProperty(value = "类型 1-启动失败退款 2-充电完成退款 3-平台人工退款")
    private Integer type;

    /**
     * 退款操作人
     */
    @ApiModelProperty(value = "退款操作人")
    private String refundOperator;

}
