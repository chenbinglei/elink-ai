package com.sunmax.webapp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppletTradeQueryVo", description = "小程序交易查询参数实体类")
public class AppletTradeQueryVo {

    /**
     * 小程序用户id
     */
    @ApiModelProperty(value = "小程序用户id", required = true)
    private String appletUserId;

    /**
     * 小程序放电钱包id
     */
    @ApiModelProperty(value = "小程序放电钱包id", required = true)
    private String disWalletId;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private String endDate;

    /**
     * 交易类型 1-V2G收益存入 2-余额提现
     */
    @ApiModelProperty(value = "交易类型 1-V2G收益存入 2-余额提现")
    private Integer tradeType;

}
