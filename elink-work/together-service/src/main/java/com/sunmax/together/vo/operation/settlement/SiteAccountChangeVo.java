package com.sunmax.together.vo.operation.settlement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteAccountChangeVo", description = "站点账户编辑参数")
public class SiteAccountChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", required = true)
    private String tenantId;

    /**
     * 支付平台 1-微信
     */
    @ApiModelProperty(value = "支付平台 1-微信", required = true)
    private Integer payPlatform;

    /**
     * 租户账号id
     */
    @ApiModelProperty(value = "租户账号id", required = true)
    private String accountId;

    /**
     * 类型 1-收款账户 2-付款账户 3-分帐账户
     */
    @ApiModelProperty(value = "类型 1-收款账户 2-付款账户 3-分帐账户", required = true)
    private Integer type;

    /**
     * 比例(%)
     */
    @ApiModelProperty(value = "比例(%)")
    private Double ratio;

}
