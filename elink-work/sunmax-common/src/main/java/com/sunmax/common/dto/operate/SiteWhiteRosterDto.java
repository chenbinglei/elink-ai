package com.sunmax.common.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点白名单信息返回实体类")
public class SiteWhiteRosterDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

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
