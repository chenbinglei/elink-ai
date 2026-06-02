package com.sunmax.common.vo.webapp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RechargeTradeQueryVo", description = "充电交易查询参数实体类")
public class DischargeTradeQueryVo {

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "关键词类型 1-订单号 2-手机号 3-站点名称")
    private Integer keywordType;

    @ApiModelProperty(value = "关键词")
    private String keyword;

    @ApiModelProperty(value = "交易类型 1-V2G收益存入 2-余额提现")
    private Integer tradeType;

    @ApiModelProperty(value = "交易状态 1-处理中 2-处理成功 3-处理失败")
    private Integer tradeStatus;

    @ApiModelProperty(value = "交易方式 1-微信 2-支付宝 3-银联商户")
    private Integer tradeWay;

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
