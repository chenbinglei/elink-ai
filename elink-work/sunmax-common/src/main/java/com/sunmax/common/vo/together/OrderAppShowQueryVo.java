package com.sunmax.common.vo.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OrderAppShowQueryVo", description = "订单查询参数实体类")
public class OrderAppShowQueryVo {

    /**
     * 小程序用户id
     */
    @ApiModelProperty(value = "小程序用户id")
    private String appletUserId;

    /**
     * 查询日期(yyyy-MM)
     */
    @ApiModelProperty(value = "查询日期(yyyy-MM)")
    private String queryDate;

    /**
     * 申请单号id
     */
    @ApiModelProperty(value = "申请单号id")
    private String invoiceId;

    /**
     * 查询类型 1-全部订单 2-未开票订单
     */
    @ApiModelProperty(value = "查询类型 1-全部订单 2-未开票订单", required = true)
    private Integer queryType;

    /**
     * 运行模式 0-充电订单 1,2-放电订单
     */
    @ApiModelProperty(value = "运行模式 0-充电订单 1,2-放电订单")
    private Integer runMode;

}
