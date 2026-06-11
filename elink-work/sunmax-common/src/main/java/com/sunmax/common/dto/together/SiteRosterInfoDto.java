package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "站点白名单信息返回实体类")
public class SiteRosterInfoDto {

    /**
     * 名单模式 1-仅白名单用户可用 2-白名单用户免费充电
     */
    @Schema(description = "名单模式 1-仅白名单用户可用 2-白名单用户免费充电")
    private Integer rosterMode;

    /**
     * 白名单列表
     */
    @Schema(description = "白名单列表")
    private List<SiteRosterInfoDto.WhiteRosterInfo> whiteRosterInfoList;

    @Data
    public static class WhiteRosterInfo {

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
}
