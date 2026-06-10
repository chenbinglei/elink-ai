package com.sunmax.common.dto.webapp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 小程序进行中的订单返回实体类
 */
@Data
@Schema(description = "进行中订单实体类")
public class AppInHandOrderDto {

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNum;

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private Integer gunCode;

    /**
     * 订单类型 0-充电订单 1-放电订单 2-占用订单
     */
    @Schema(description = "订单类型 0-充电订单 1-放电订单 2-占用订单")
    private Integer orderType;

    /**
     * 订单状态 1-在途 2-预约;占用订单状态 1-在途 2-待支付
     */
    @Schema(description = "订单状态 1-在途 2-预约; 占用订单状态 1-在途 2-待支付")
    private Integer orderState;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;
}
