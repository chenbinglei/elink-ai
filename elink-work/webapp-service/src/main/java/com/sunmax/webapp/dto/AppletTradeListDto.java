package com.sunmax.webapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "AppletTradeListDto", description = "小程序交易明细返回实体类")
public class AppletTradeListDto {

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private String date;

    /**
     * 收入金额
     */
    @ApiModelProperty(value = "收入金额")
    private BigDecimal incomeMoney = new BigDecimal("0.0");

    /**
     * 支出金额
     */
    @ApiModelProperty(value = "支出金额")
    private BigDecimal outcomeMoney = new BigDecimal("0.0");

    /**
     * 交易明细列表
     */
    @ApiModelProperty(value = "交易明细列表")
    private List<TradeDetail> tradeDetailList;

    @Data
    public static class TradeDetail {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 交易类型 1-V2G收益存入 2-余额提现
         */
        @ApiModelProperty(value = "交易类型 1-V2G收益存入 2-余额提现")
        private Integer tradeType;

        /**
         * 交易金额
         */
        @ApiModelProperty(value = "交易金额")
        private BigDecimal tradeMoney = new BigDecimal("0.0");

        /**
         * 交易后用户余额(小程序交易明细里面用到)
         */
        @ApiModelProperty(value = "交易后用户余额")
        private BigDecimal tradeBalance = new BigDecimal("0.0");

        /**
         * 创建时间
         */
        @ApiModelProperty(value = "创建时间")
        private String createTime;

    }

}
