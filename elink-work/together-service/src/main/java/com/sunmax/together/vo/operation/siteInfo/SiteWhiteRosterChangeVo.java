package com.sunmax.together.vo.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteWhiteRosterChangeVo", description = "站点白名单编辑参数")
public class SiteWhiteRosterChangeVo {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id", required = true)
    private String siteId;

    /**
     * 鉴权类型 1-用户 2-车辆
     */
    @ApiModelProperty(value = "鉴权类型 1-用户 2-车辆", required = true)
    private Integer authorityType;

    /**
     * 鉴权账户
     */
    @ApiModelProperty(value = "鉴权账户", required = true)
    private String authorityAccount;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String notes;
}
