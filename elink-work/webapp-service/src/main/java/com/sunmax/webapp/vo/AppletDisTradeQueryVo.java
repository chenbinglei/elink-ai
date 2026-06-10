package com.sunmax.webapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小程序放电交易查询参数实体类")
public class AppletDisTradeQueryVo {

    @Schema(description = "关键词类型 1-订单号 2-手机号 3-用户名 4-商户ID")
    private Integer keywordType;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "交易类型 1-V2G收益存入 2-余额提现")
    private Integer tradeType;

    @Schema(description = "商户账号id")
    private String accountId;

    @Schema(description = "创建开始时间")
    private String startTime;

    @Schema(description = "创建结束时间")
    private String endTime;

    @Schema(description = "小程序用户id")
    private String appletUserId;

    @Schema(description = "当前页")
    private Integer page;

    @Schema(description = "当前页条数")
    private Integer size;

}
