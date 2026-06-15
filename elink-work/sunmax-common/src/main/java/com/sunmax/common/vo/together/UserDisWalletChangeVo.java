package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "小程序用户编辑参数")
public class UserDisWalletChangeVo {

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 交易金额
     */
    @Schema(description = "交易金额")
    private BigDecimal tradeMoney;

    /**
     * 交易类型 1-放电收益 2-V2G提现中(冻结金额) 3-V2G提现成功(解冻金额) 4-V2G提现失败(解冻金额，冻结余额返回到余额里面)
     */
    @Schema(description = "交易类型")
    private Integer tradeType;

    /**
     * 账户id
     */
    @Schema(description = "账户id")
    private String accountId;

}
