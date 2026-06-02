package com.sunmax.common.vo.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserRecordChangeVo", description = "用户记录编辑信息参数")
public class UserRecordChangeVo {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private String id;

    /**
     * 所属订单记录id
     */
    @ApiModelProperty("所属订单记录id")
    private String orderId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String userId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String userName;

    /**
     * 企业账户
     */
    @ApiModelProperty("企业账户")
    private String enterpriseAccount;

    /**
     * 城市
     */
    @ApiModelProperty("城市")
    private String city;

    /**
     * 车队名称
     */
    @ApiModelProperty("车队名称")
    private String fleetName;

    /**
     * 点卡ID
     */
    @ApiModelProperty("点卡ID")
    private String electCardId;

    /**
     * 卡面号
     */
    @ApiModelProperty("卡面号")
    private String cardNumber;

    /**
     * 开票状态 1-已开发票 2-未开发票
     */
    @ApiModelProperty("开票状态 1-已开发票 2-未开发票")
    private Integer invoicingState;
}
