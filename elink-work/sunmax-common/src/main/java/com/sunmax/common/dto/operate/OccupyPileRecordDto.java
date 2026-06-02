package com.sunmax.common.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "OccupyPileRecordDto", description = "占桩订单记录返回实体类")
public class OccupyPileRecordDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 占桩订单号
     */
    @ApiModelProperty(value = "占桩订单号")
    private String occupyNum;

    /**
     * 运营商名称
     */
    @ApiModelProperty(value = "运营商名称")
    private String operateUnitName;

    /**
     * 运营商id
     */
    @ApiModelProperty(value = "运营商id")
    private String operateUnitId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 所属订单记录id
     */
    @ApiModelProperty(value = "所属订单记录id")
    private String orderId;

    /**
     * 充电桩编号
     */
    @ApiModelProperty(value = "充电桩编号")
    private String pileCode;

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

    /**
     * 时长 秒值
     */
    @ApiModelProperty(value = "时长 秒值")
    private Long duration;

    /**
     * 占位订单金额
     */
    @ApiModelProperty(value = "占位订单金额")
    private BigDecimal orderMoney;

    /**
     * 占位实付金额
     */
    @ApiModelProperty(value = "占位实付金额")
    private BigDecimal paidMoney;

    /**
     * 订单状态 1-在途 2-待支付 3-已完成 9-异常
     */
    @ApiModelProperty(value = "订单状态 1-在途 2-待支付 3-已完成 9-异常")
    private Integer occupyState;

    /**
     * 订单支付状态 1-已支付 2-未支付
     */
    @ApiModelProperty(value = "订单支付状态 1-已支付 2-未支付")
    private Integer payState;

    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private String payTime;
}
