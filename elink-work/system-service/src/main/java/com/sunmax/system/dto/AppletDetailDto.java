package com.sunmax.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "AppletDetailDto", description = "小程序详情返回实体类")
public class AppletDetailDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 小程序名称
     */
    @ApiModelProperty(value = "小程序名称")
    private String appletName;

    /**
     * 小程序id
     */
    @ApiModelProperty(value = "小程序id")
    private String appletCode;

    /**
     * 小程序密钥
     */
    @ApiModelProperty(value = "小程序密钥")
    private String appletSecret;

    /**
     * 小程序类型 1-微信 2-支付宝
     */
    @ApiModelProperty(value = "小程序类型 1-微信 2-支付宝")
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
     * 小程序logo
     */
    @ApiModelProperty(value = "小程序logo")
    private String appletLogo;

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
     * 公众号二维码图片
     */
    @ApiModelProperty(value = "公众号二维码图片")
    private String tencentImage;

    /**
     * 绑定多个租户id
     */
    @ApiModelProperty(value = "绑定多个租户id")
    private String tenantIds;

    /**
     * 多个租户名称
     */
    @ApiModelProperty(value = "多个租户名称")
    private String tenantNames;

    /**
     * 创建人名称
     */
    @ApiModelProperty("创建人名称")
    private String createName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改人名称
     */
    @ApiModelProperty("修改人名称")
    private String updateName;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
