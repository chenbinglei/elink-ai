package com.sunmax.common.dto.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.5 CMD_START,//启动命令 5
 * 发送方向：前置服务<---平台服务
 */
@Data
public class StartCmdDto {

    /**
     * 桩编码
     */
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @Schema(description = "枪标识")
    private Integer gunCode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩
     */
    @Schema(description = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩")
    private Integer starter;

    /**
     * 运行模式 0-充电模式 1-放电模式
     */
    @Schema(description = "运行模式 0-充电模式 1-放电模式")
    private Integer runMode;

    /**
     * 充值类型 1-金额 2-电量
     */
    @Schema(description = "充值类型 1-金额 2-电量")
    private Integer reChargeType;

    /**
     * 充值余额
     * 1-金额 精度0.001元
     * 2-电量 精度0.001kW·h
     */
    @Schema(description = "充值余额")
    private Integer payValue;

    /**
     * 用户账户
     */
    @Schema(description = "用户账户")
    private UserAccount userAccount;

    /**
     * 策略
     */
    @Schema(description = "策略")
    private Strategy strategy;

    /**
     * 停止充放电密码
     */
    @Schema(description = "停止充放电密码")
    private String stopPwd;

    /**
     * 交易号
     */
    @Schema(description = "交易号")
    private String recordId;

}
