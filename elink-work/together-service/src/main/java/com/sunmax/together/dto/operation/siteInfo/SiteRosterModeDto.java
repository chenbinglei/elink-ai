package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "白名单模式返回实体类")
public class SiteRosterModeDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 名单模式 1-仅白名单用户可用 2-白名单用户免费充电
     */
    @Schema(description = "名单模式 1-仅白名单用户可用 2-白名单用户免费充电")
    private Integer rosterMode;
}
