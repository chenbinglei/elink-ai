package com.sunmax.common.dto.webapp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "申请开票订单展示列表返回实体类")
public class OrderAppShowDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNum;

    /**
     * 充电电量
     */
    @Schema(description = "充电电量")
    private Double chargeQt;

    /**
     * 实付金额
     */
    @Schema(description = "实付金额")
    private BigDecimal actualTotalCost;

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

}

