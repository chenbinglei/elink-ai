package com.sunmax.common.dto.protocol.mqtt.web.model;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "账户类型 1-充/放电卡 2-VIN 3-手机号")
    private Integer accountType;

    /**
     * 账号数据
     */
    @Schema(description = "账号数据")
    private String accountData;

}
