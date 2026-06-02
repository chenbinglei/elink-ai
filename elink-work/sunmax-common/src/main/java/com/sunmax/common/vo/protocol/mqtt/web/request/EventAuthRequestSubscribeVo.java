package com.sunmax.common.vo.protocol.mqtt.web.request;

import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import lombok.Data;

/**
 * 鉴权请求 订阅实体类
 */
@Data
public class EventAuthRequestSubscribeVo {

    /**
     * 电桩编号
     */
    private String pilesCode;

    /**
     * 枪编号
     */
    private Integer gunCode;

    /**
     * 保留字节
     */
    private String reserve;

    /**
     * 用户账号
     */
    private UserAccount userAccount;

    /**
     * 鉴权密码
     */
    private String authPwd;

}
