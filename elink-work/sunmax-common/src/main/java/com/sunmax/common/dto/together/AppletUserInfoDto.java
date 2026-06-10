package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小程序用户返回实体类")
public class AppletUserInfoDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 平台类型 1-微信小程序 2-支付宝
     */
    @Schema(description = "平台类型 1-微信小程序 2-支付宝")
    private Integer platformType;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phoneNum;

    /**
     * 用户分组id
     */
    @Schema(description = "用户分组id")
    private String groupId;

    /**
     * 用户分组名称
     */
    @Schema(description = "用户分组名称")
    private String groupName;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String mailbox;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;

    /**
     * 用户状态 1-正常 2-冻结 3-注销
     */
    @Schema(description = "用户状态 1-正常 2-冻结 3-注销")
    private Integer userState;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 用户在普通商户AppID下的唯一标识
     */
    @Schema(description = "用户在普通商户AppID下的唯一标识")
    private String openid;

    /**
     * 小程序主键id
     */
    @Schema(description = "小程序主键id")
    private String appletId;

    /**
     * 小程序标识
     */
    @Schema(description = "小程序标识")
    private String appletCode;
}
