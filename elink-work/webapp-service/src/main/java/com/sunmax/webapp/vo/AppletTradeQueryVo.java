package com.sunmax.webapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小程序交易查询参数实体类")
public class AppletTradeQueryVo {

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 小程序放电钱包id
     */
    @Schema(description = "小程序放电钱包id")
    private String disWalletId;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private String startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private String endDate;

    /**
     * 交易类型 1-V2G收益存入 2-余额提现
     */
    @Schema(description = "交易类型 1-V2G收益存入 2-余额提现")
    private Integer tradeType;

}
