package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "PileStartVo", description = "电桩启动参数实体类")
public class PileStartVo {

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id", required = true)
    private String siteId;

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "充电桩枪编号", required = true)
    private String gunCode;

    /**
     * 交易流水号
     */
    @ApiModelProperty(value = "交易流水号")
    private String serialNum;

//    /**
//     * 平台标识
//     */
//    @ApiModelProperty(value = "平台标识", required = true)
//    private String platformLogo;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制
     */
    @ApiModelProperty(value = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制", required = true)
    private Integer starter;

    /**
     * 充电方式 0-立即充电 1-定时充电 2-自动充电
     */
    @ApiModelProperty(value = "充电方式 0-立即充电 1-定时充电 2-自动充电", required = true)
    private Integer type;

    /**
     * 预约时间
     */
    @ApiModelProperty(value = "预约时间")
    private String clockingTime;

    /**
     * 充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
     */
    @ApiModelProperty(value = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量", required = true)
    private Integer strategy;

    /**
     * 充放电策略参数 金额 电量
     */
    @ApiModelProperty(value = "充放电策略参数", required = true)
    private Double strategyCfg;

    /**
     * 运行模式 -1-未知 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "运行模式 -1-未知 0-充电模式 1-放电模式", required = true)
    private Integer runMode;

    /**
     * 账户类型 1-充/放电卡 2-VIN码 3-手机号
     */
    @ApiModelProperty(value = "账户类型 1-充/放电卡 2-VIN码 3-手机号", required = true)
    private Integer accountType;

    /**
     * 账户数据
     */
    @ApiModelProperty(value = "账户数据", required = true)
    private String accountData;

    /**
     * 预付金额
     */
    @ApiModelProperty(value = "预付金额", required = true)
    private BigDecimal prepayMoney;

    /**
     * 是否存控制记录
     */
    @ApiModelProperty(value = "是否存控制记录")
    private Boolean isStore = false;

}
