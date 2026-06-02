package com.sunmax.common.vo.webapp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppOrderQueryVo", description = "小程序订单列表查询参数")
public class AppOrderListQueryVo {

    /**
     * 小程序用户id
     */
    @ApiModelProperty("小程序用户id")
    private String appletUserId;

    /**
     * 订单类型 0-充电订单 1-放电订单
     */
    @ApiModelProperty("订单类型 0-充电订单 1-放电订单")
    private Integer orderType;

    /**
     * 订单标识 1.进行中；2.已完成
     */
    @ApiModelProperty("订单标识 1.进行中；2.已完成")
    private Integer orderLogo;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private String endTime;

    /**
     * 当前页
     */
    @ApiModelProperty("当前页")
    private int page;

    /**
     * 当前页条数
     */
    @ApiModelProperty("当前页条数")
    private int size;

    /**
     * 登录标识
     */
    @ApiModelProperty(value = "登录标识", required = true)
    private String appletKey;
}
