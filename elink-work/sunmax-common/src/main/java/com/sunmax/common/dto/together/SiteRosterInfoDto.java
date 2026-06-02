package com.sunmax.common.dto.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SiteWhiteRosterDto", description = "站点白名单信息返回实体类")
public class SiteRosterInfoDto {

    /**
     * 名单模式 1-仅白名单用户可用 2-白名单用户免费充电
     */
    @ApiModelProperty(value = "名单模式 1-仅白名单用户可用 2-白名单用户免费充电")
    private Integer rosterMode;

    /**
     * 白名单列表
     */
    @ApiModelProperty(value = "白名单列表")
    private List<SiteRosterInfoDto.WhiteRosterInfo> whiteRosterInfoList;

    @Data
    public static class WhiteRosterInfo {

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
}
