package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteRosterModeDto", description = "白名单模式返回实体类")
public class SiteRosterModeDto {

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
     * 名单模式 1-仅白名单用户可用 2-白名单用户免费充电
     */
    @ApiModelProperty(value = "名单模式 1-仅白名单用户可用 2-白名单用户免费充电")
    private Integer rosterMode;
}
