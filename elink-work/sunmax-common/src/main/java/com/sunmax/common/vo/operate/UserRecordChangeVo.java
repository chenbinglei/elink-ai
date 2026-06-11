package com.sunmax.common.vo.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户记录编辑信息参数")
public class UserRecordChangeVo {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 所属订单记录id
     */
    @Schema(description = "所属订单记录id")
    private String orderId;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 企业账户
     */
    @Schema(description = "企业账户")
    private String enterpriseAccount;

    /**
     * 城市
     */
    @Schema(description = "城市")
    private String city;

    /**
     * 车队名称
     */
    @Schema(description = "车队名称")
    private String fleetName;

    /**
     * 点卡ID
     */
    @Schema(description = "点卡ID")
    private String electCardId;

    /**
     * 卡面号
     */
    @Schema(description = "卡面号")
    private String cardNumber;

    /**
     * 开票状态 1-已开发票 2-未开发票
     */
    @Schema(description = "开票状态 1-已开发票 2-未开发票")
    private Integer invoicingState;
}
