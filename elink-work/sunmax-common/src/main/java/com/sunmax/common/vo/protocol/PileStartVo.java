package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "电桩启动参数实体类")
public class PileStartVo {

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "充电桩枪编号")
    private String gunCode;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号")
    private String serialNum;

//    /**
//     * 平台标识
//     */
//    @Schema(description = "平台标识")
//    private String platformLogo;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制
     */
    @Schema(description = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制")
    private Integer starter;

    /**
     * 充电方式 0-立即充电 1-定时充电 2-自动充电
     */
    @Schema(description = "充电方式 0-立即充电 1-定时充电 2-自动充电")
    private Integer type;

    /**
     * 预约时间
     */
    @Schema(description = "预约时间")
    private String clockingTime;

    /**
     * 充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
     */
    @Schema(description = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量")
    private Integer strategy;

    /**
     * 充放电策略参数 金额 电量
     */
    @Schema(description = "充放电策略参数")
    private Double strategyCfg;

    /**
     * 运行模式 -1-未知 0-充电模式 1-放电模式
     */
    @Schema(description = "运行模式 -1-未知 0-充电模式 1-放电模式")
    private Integer runMode;

    /**
     * 账户类型 1-充/放电卡 2-VIN码 3-手机号
     */
    @Schema(description = "账户类型 1-充/放电卡 2-VIN码 3-手机号")
    private Integer accountType;

    /**
     * 账户数据
     */
    @Schema(description = "账户数据")
    private String accountData;

    /**
     * 预付金额
     */
    @Schema(description = "预付金额")
    private BigDecimal prepayMoney;

    /**
     * 是否存控制记录
     */
    @Schema(description = "是否存控制记录")
    private Boolean isStore = false;

}
