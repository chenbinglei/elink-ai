package com.sunmax.common.vo.protocol.mqtt.web.request;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import lombok.Data;

/**
 * 策略设置 订阅实体类
 */
@Data
public class EventStrategySettingSubscribeVo {

    /**
     * 电桩编号
     */
    private String pilesCode;

    /**
     * 枪编号
     */
    private Integer gunCode;

    /**
     *充/放电接口运行模式
     * 0 充电模式；1 放电模式
     */
    private Integer runMode;

    /**
     * 保留字节
     */
    private String reserve;

    /**
     * 用户帐号
     */
    public UserAccount userAccount;

    /**
     *充/放电策略
     */
    public Strategy strategy;

}
