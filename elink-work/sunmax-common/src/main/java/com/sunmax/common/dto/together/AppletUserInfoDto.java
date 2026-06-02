package com.sunmax.common.dto.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppletUserInfoDto", description = "小程序用户返回实体类")
public class AppletUserInfoDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 平台类型 1-微信小程序 2-支付宝
     */
    @ApiModelProperty(value = "平台类型 1-微信小程序 2-支付宝")
    private Integer platformType;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String phoneNum;

    /**
     * 用户分组id
     */
    @ApiModelProperty(value = "用户分组id")
    private String groupId;

    /**
     * 用户分组名称
     */
    @ApiModelProperty(value = "用户分组名称")
    private String groupName;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String mailbox;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;

    /**
     * 用户状态 1-正常 2-冻结 3-注销
     */
    @ApiModelProperty(value = "用户状态 1-正常 2-冻结 3-注销")
    private Integer userState;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 用户在普通商户AppID下的唯一标识
     */
    @ApiModelProperty(value = "用户在普通商户AppID下的唯一标识")
    private String openid;

    /**
     * 小程序主键id
     */
    @ApiModelProperty(value = "小程序主键id")
    private String appletId;

    /**
     * 小程序标识
     */
    @ApiModelProperty(value = "小程序标识")
    private String appletCode;
}
