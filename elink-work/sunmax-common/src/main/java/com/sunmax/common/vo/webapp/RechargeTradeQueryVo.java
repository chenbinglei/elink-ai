package com.sunmax.common.vo.webapp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "充电交易查询参数实体类")
public class RechargeTradeQueryVo {

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "关键词类型 1-订单号 2-交易流水号 3-手机号 4-站点名称")
    private Integer keywordType;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "交易类型 1-充电预付 2-充电退款")
    private Integer tradeType;

    @Schema(description = "交易状态 1-处理中 2-处理成功 3-处理失败")
    private Integer tradeStatus;

    @Schema(description = "交易方式 1-微信 2-支付宝 3-银联商户")
    private Integer tradeWay;

    @Schema(description = "商户账号id")
    private String accountId;

    @Schema(description = "支付开始时间")
    private String startTime;

    @Schema(description = "支付结束时间")
    private String endTime;

    @Schema(description = "当前页")
    private Integer page;

    @Schema(description = "当前页条数")
    private Integer size;

}
