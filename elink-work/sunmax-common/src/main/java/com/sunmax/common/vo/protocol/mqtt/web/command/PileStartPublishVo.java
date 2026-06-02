package com.sunmax.common.vo.protocol.mqtt.web.command;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import lombok.Data;

/**
 * 电桩启动命令主题实体类
 */
@Data
public class PileStartPublishVo {

    /**
     * 充电桩编号
     */
    private String pilesCode;

    /**
     * 充电枪编号
     */
    private Integer gunCode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制
     */
    private Integer starter;

    /**
     * 运行模式 0 充电 1 放电模式
     */
    private Integer runMode;

    /**
     * 充值类型
     * 1：金额 精度0.001 元
     * 2：电量 精度0.001kW·h
     */
    private Integer reChargeType;

    /**
     * 充值金额
     */
    private Integer payValue;

    /**
     * 用户账户
     */
    private UserAccount userAccount;

    /**
     * 充放电策略
     */
    private Strategy strategy;

    /**
     * 停止充放电密码
     */
    private String stopPwd;

    /**
     * 交易记录号
     */
    private String recordId;

}
