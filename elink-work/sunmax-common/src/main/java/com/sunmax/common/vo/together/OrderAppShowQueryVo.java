package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单查询参数实体类")
public class OrderAppShowQueryVo {

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 查询日期(yyyy-MM)
     */
    @Schema(description = "查询日期(yyyy-MM)")
    private String queryDate;

    /**
     * 申请单号id
     */
    @Schema(description = "申请单号id")
    private String invoiceId;

    /**
     * 查询类型 1-全部订单 2-未开票订单
     */
    @Schema(description = "查询类型 1-全部订单 2-未开票订单")
    private Integer queryType;

    /**
     * 运行模式 0-充电订单 1,2-放电订单
     */
    @Schema(description = "运行模式 0-充电订单 1,2-放电订单")
    private Integer runMode;

}
