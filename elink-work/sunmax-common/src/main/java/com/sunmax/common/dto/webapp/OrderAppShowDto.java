package com.sunmax.common.dto.webapp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "OrderAppShowDto", description = "申请开票订单展示列表返回实体类")
public class OrderAppShowDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNum;

    /**
     * 充电电量
     */
    @ApiModelProperty(value = "充电电量")
    private Double chargeQt;

    /**
     * 实付金额
     */
    @ApiModelProperty(value = "实付金额")
    private BigDecimal actualTotalCost;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

}

