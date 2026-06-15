package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小程序用户编辑参数")
public class AppletUserChangeVo {

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
}
