package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppletChangeVo", description = "小程序编辑参数实体类")
public class AppletChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 小程序名称
     */
    @ApiModelProperty(value = "小程序名称", required = true)
    private String appletName;

    /**
     * 小程序id
     */
    @ApiModelProperty(value = "小程序id", required = true)
    private String appletCode;

    /**
     * 小程序密钥
     */
    @ApiModelProperty(value = "小程序密钥", required = true)
    private String appletSecret;

    /**
     * 小程序类型 1-微信 2-支付宝
     */
    @ApiModelProperty(value = "小程序类型 1-微信 2-支付宝", required = true)
    private Integer appletType;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 公众号名称
     */
    @ApiModelProperty(value = "公众号名称")
    private String tencentName;

    /**
     * 公众号id
     */
    @ApiModelProperty(value = "公众号id")
    private String tencentCode;

    /**
     * 公众号密钥
     */
    @ApiModelProperty(value = "公众号密钥")
    private String tencentSecret;

    /**
     * 绑定多个租户id
     */
    @ApiModelProperty(value = "绑定多个租户id")
    private String tenantIds;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

}
