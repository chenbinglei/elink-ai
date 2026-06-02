package com.sunmax.common.dto.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩
     */
    @ApiModelProperty(value = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩", required = true)
    private Integer starter;

    /**
     * 运行模式 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "运行模式 0-充电模式 1-放电模式", required = true)
    private Integer runMode;

    /**
     * 充值类型 1-金额 2-电量
     */
    @ApiModelProperty(value = "充值类型 1-金额 2-电量", required = true)
    private Integer reChargeType;

    /**
     * 充值余额
     * 1-金额 精度0.001元
     * 2-电量 精度0.001kW·h
     */
    @ApiModelProperty(value = "充值余额", required = true)
    private Integer payValue;

    /**
     * 用户账户
     */
    @ApiModelProperty(value = "用户账户", required = true)
    private UserAccount userAccount;

    /**
     * 策略
     */
    @ApiModelProperty(value = "策略", required = true)
    private Strategy strategy;

    /**
     * 停止充放电密码
     */
    @ApiModelProperty(value = "停止充放电密码", required = true)
    private String stopPwd;

    /**
     * 交易号
     */
    @ApiModelProperty(value = "交易号", required = true)
    private String recordId;

}
