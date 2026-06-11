package com.sunmax.common.dto.system;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Schema(description = "小程序信息返回实体类")
public class AppletDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 小程序名称
     */
    @Schema(description = "小程序名称")
    private String appletName;

    /**
     * 小程序id
     */
    @Schema(description = "小程序id")
    private String appletCode;

    /**
     * 小程序密钥
     */
    @Schema(description = "小程序密钥")
    private String appletSecret;

    /**
     * 小程序类型 1-微信 2-支付宝
     */
    @Schema(description = "小程序类型 1-微信 2-支付宝")
    private Integer appletType;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 小程序logo
     */
    @Schema(description = "小程序logo")
    private String appletLogo;

    /**
     * 公众号名称
     */
    @Schema(description = "公众号名称")
    private String tencentName;

    /**
     * 公众号id
     */
    @Schema(description = "公众号id")
    private String tencentCode;

    /**
     * 公众号密钥
     */
    @Schema(description = "公众号密钥")
    private String tencentSecret;

    /**
     * 公众号二维码图片
     */
    @Schema(description = "公众号二维码图片")
    private String tencentImage;

    /**
     * 绑定多个租户id 租户id -> 多个站点id
     */
    @Schema(description = "绑定多个租户id")
    private Map<String, Set<String>> tenantSiteIdMap = Maps.newHashMap();

}
