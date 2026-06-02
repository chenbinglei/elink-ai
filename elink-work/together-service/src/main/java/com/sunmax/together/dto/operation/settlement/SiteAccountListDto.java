package com.sunmax.together.dto.operation.settlement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteAccountListDto", description = "站点账户列表返回实体类")
public class SiteAccountListDto {

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
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 权限 1-只读 2-读写
     */
    @ApiModelProperty(value = "权限 1-只读 2-读写")
    private Integer authority;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 所在省份
     */
    @ApiModelProperty(value = "所在省份")
    private String province;

    /**
     * 所在市
     */
    @ApiModelProperty(value = "所在市")
    private String city;

    /**
     * 收款主键id
     */
    @ApiModelProperty(value = "收款主键id")
    private String recId;

    /**
     * 收款商户名称
     */
    @ApiModelProperty(value = "收款商户名称")
    private String recMchName;

    /**
     * 收款商户id
     */
    @ApiModelProperty(value = "收款商户id")
    private String recMchId;

    /**
     * 付款主键id
     */
    @ApiModelProperty(value = "付款主键id")
    private String payId;

    /**
     * 付款商户名称
     */
    @ApiModelProperty(value = "付款商户名称")
    private String payMchName;

    /**
     * 付款商户id
     */
    @ApiModelProperty(value = "付款商户id")
    private String payMchId;

}
