package com.sunmax.webapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "AppletDisWalletDto", description = "小程序用户钱包信息返回实体类")
public class AppletDisWalletDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 小程序用户id
     */
    @ApiModelProperty(value = "小程序用户id")
    private String appletUserId;

    /**
     * 账户id
     */
    @ApiModelProperty(value = "账户id")
    private String accountId;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private String mchId;

    /**
     * 商户名称
     */
    @ApiModelProperty(value = "商户名称")
    private String mchName;

    /**
     * 余额
     */
    @ApiModelProperty(value = "账户余额")
    private BigDecimal balance = new BigDecimal("0.0");

    /**
     * 冻结余额
     */
    @ApiModelProperty(value = "账户冻结余额")
    private BigDecimal freezeBalance = new BigDecimal("0.0");
}
