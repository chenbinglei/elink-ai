package com.sunmax.common.vo.webapp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小程序订单列表查询参数")
public class AppOrderListQueryVo {

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 订单类型 0-充电订单 1-放电订单
     */
    @Schema(description = "订单类型 0-充电订单 1-放电订单")
    private Integer orderType;

    /**
     * 订单标识 1.进行中；2.已完成
     */
    @Schema(description = "订单标识 1.进行中；2.已完成")
    private Integer orderLogo;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private int page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private int size;

    /**
     * 登录标识
     */
    @Schema(description = "登录标识")
    private String appletKey;
}
