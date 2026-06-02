package com.sunmax.common.vo.protocol.mqtt.web.response;

import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import lombok.Data;

/**
 * 鉴权响应 发布实体类
 */
@Data
public class EventAuthResponsePublicVo {

    /**
     * 电桩编号
     */
    private String pilesCode;

    /**
     *枪标识
     */
    private Integer gunCode;

    /**
     *帐号余额类型 1 金额; 2 电量
     */
    private Integer balanceType;

    /**
     * 失败原因  0 成功；1 帐号非法；2 账号锁定；3 密码错误； 255 其他错误。
     */
    private Integer failReason;

    /**
     * 账户余额（电量,金额）
     * 余额类型 1：金额
     * 余额类型 2：电量
     */
    private Double balanceNum;

    /**
     *用户帐号
     */
    private UserAccount userAccount;

}
