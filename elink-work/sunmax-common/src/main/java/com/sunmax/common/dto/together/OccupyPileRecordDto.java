package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "占桩订单记录返回实体类")
public class OccupyPileRecordDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 占桩订单号
     */
    @Schema(description = "占桩订单号")
    private String occupyNum;

    /**
     * 运营商名称
     */
    @Schema(description = "运营商名称")
    private String operateUnitName;

    /**
     * 运营商id
     */
    @Schema(description = "运营商id")
    private String operateUnitId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 所属订单记录id
     */
    @Schema(description = "所属订单记录id")
    private String orderId;

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

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
     * 时长 秒值
     */
    @Schema(description = "时长 秒值")
    private Long duration;

    /**
     * 占位订单金额
     */
    @Schema(description = "占位订单金额")
    private BigDecimal orderMoney;

    /**
     * 占位实付金额
     */
    @Schema(description = "占位实付金额")
    private BigDecimal paidMoney;

    /**
     * 订单状态 1-在途 2-待支付 3-已完成 9-异常
     */
    @Schema(description = "订单状态 1-在途 2-待支付 3-已完成 9-异常")
    private Integer occupyState;

    /**
     * 订单支付状态 1-已支付 2-未支付
     */
    @Schema(description = "订单支付状态 1-已支付 2-未支付")
    private Integer payState;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    private String payTime;
}
