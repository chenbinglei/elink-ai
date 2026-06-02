package com.sunmax.common.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteWhiteRosterDto", description = "站点白名单信息返回实体类")
public class SiteWhiteRosterDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 鉴权类型 1-用户 2-车辆
     */
    @ApiModelProperty(value = "鉴权类型 1-用户 2-车辆")
    private Integer authorityType;

    /**
     * 鉴权账户
     */
    @ApiModelProperty(value = "鉴权账户")
    private String authorityAccount;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String notes;
}
