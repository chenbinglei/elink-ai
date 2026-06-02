package com.sunmax.common.dto.protocol.mqtt.web.model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 充/放电用户帐号
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    /**
     * 账户类型 1-充/放电卡 2-VIN 3-手机号
     */
    @ApiModelProperty(value = "账户类型 1-充/放电卡 2-VIN 3-手机号", required = true)
    private Integer accountType;

    /**
     * 账号数据
     */
    @ApiModelProperty(value = "账号数据", required = true)
    private String accountData;

}
