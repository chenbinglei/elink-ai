package com.sunmax.webapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "充放电启动参数")
public class ChargeStartVo extends AppletChargeStartVo {

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNum;


}
