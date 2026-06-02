package com.sunmax.webapp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "ChargeStartVo", description = "充放电启动参数")
public class ChargeStartVo extends AppletChargeStartVo {

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNum;


}
