package com.sunmax.together.vo.operation.siteInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点白名单编辑参数")
public class SiteWhiteRosterChangeVo {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 鉴权类型 1-用户 2-车辆
     */
    @Schema(description = "鉴权类型 1-用户 2-车辆")
    private Integer authorityType;

    /**
     * 鉴权账户
     */
    @Schema(description = "鉴权账户")
    private String authorityAccount;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String notes;
}
