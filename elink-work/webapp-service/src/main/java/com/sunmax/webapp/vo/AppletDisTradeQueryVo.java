package com.sunmax.webapp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppletDisTradeQueryVo", description = "小程序放电交易查询参数实体类")
public class AppletDisTradeQueryVo {

    @ApiModelProperty(value = "关键词类型 1-订单号 2-手机号 3-用户名 4-商户ID")
    private Integer keywordType;

    @ApiModelProperty(value = "关键词")
    private String keyword;

    @ApiModelProperty(value = "交易类型 1-V2G收益存入 2-余额提现")
    private Integer tradeType;

    @ApiModelProperty(value = "商户账号id")
    private String accountId;

    @ApiModelProperty(value = "创建开始时间")
    private String startTime;

    @ApiModelProperty(value = "创建结束时间")
    private String endTime;

    @ApiModelProperty(value = "小程序用户id")
    private String appletUserId;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "当前页条数")
    private Integer size;

}
