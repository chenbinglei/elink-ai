package com.sunmax.common.vo.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.18 CMD_AuthenticationRequest,//鉴权请求 18
 * 发送方向：前置服务--->平台服务
 */
@Data
public class AuthenticationReqVo {

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
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号", required = true)
    private UserAccount userAccount;

    /**
     * 鉴权密码
     */
    @ApiModelProperty(value = "鉴权密码", required = true)
    private Integer authPwd;


}
