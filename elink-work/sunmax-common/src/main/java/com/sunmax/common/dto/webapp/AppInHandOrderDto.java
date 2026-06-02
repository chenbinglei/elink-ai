package com.sunmax.common.dto.webapp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 小程序进行中的订单返回实体类
 */
@Data
@ApiModel(value = "AppInHandOrderDto", description = "进行中订单实体类")
public class AppInHandOrderDto {

    /**
     * 小程序用户id
     */
    @ApiModelProperty("小程序用户id")
    private String appletUserId;

    /**
     * 订单编号
     */
    @ApiModelProperty("订单编号")
    private String orderNum;

    /**
     * 电桩编号
     */
    @ApiModelProperty("电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty("枪编号")
    private Integer gunCode;

    /**
     * 订单类型 0-充电订单 1-放电订单 2-占用订单
     */
    @ApiModelProperty("订单类型 0-充电订单 1-放电订单 2-占用订单")
    private Integer orderType;

    /**
     * 订单状态 1-在途 2-预约;占用订单状态 1-在途 2-待支付
     */
    @ApiModelProperty("订单状态 1-在途 2-预约; 占用订单状态 1-在途 2-待支付")
    private Integer orderState;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;
}
