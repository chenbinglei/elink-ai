package com.sunmax.together.dto.operation.settlement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteAccountDetailDto", description = "站点账户详情数据")
public class SiteAccountDetailDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /**
     * 支付平台 1-微信
     */
    @ApiModelProperty(value = "支付平台 1-微信")
    private Integer payPlatform;

    /**
     * 租户账号id
     */
    @ApiModelProperty(value = "租户账号id")
    private String accountId;

    /**
     * 商户名称
     */
    @ApiModelProperty(value = "商户名称")
    private String mchName;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private String mchId;

    /**
     * 类型 1-收款账户 2-付款账户 3-分帐账户
     */
    @ApiModelProperty(value = "类型 1-收款账户 2-付款账户 3-分帐账户")
    private Integer type;

    /**
     * 比例(%)
     */
    @ApiModelProperty(value = "比例(%)")
    private Double ratio;

}
