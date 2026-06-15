package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "用户钱包信息返回实体类")
public class UserDisWalletDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 账户id
     */
    @Schema(description = "账户id")
    private String accountId;

    /**
     * 商户id
     */
    @Schema(description = "商户id")
    private String mchId;

    /**
     * 商户名称
     */
    @Schema(description = "商户名称")
    private String mchName;

    /**
     * 余额
     */
    @Schema(description = "账户余额")
    private BigDecimal balance = new BigDecimal("0.0");

    /**
     * 冻结余额
     */
    @Schema(description = "账户冻结余额")
    private BigDecimal freezeBalance = new BigDecimal("0.0");

}
