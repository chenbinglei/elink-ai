package com.sunmax.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@ApiModel(value = "InvoiceTitleVo", description = "发票抬头入参实体类")
public class InvoiceTitleVo {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 小程序用户id
     */
    @ApiModelProperty(value = "小程序用户id", required = true)
    private String appletUserId;

    /**
     * 发票类型 1-普通发票 2-专用发票
     */
    @ApiModelProperty(value = "发票类型 1-普通 2-专用", required = true)
    private Integer invoiceType;

    /**
     * 抬头类型 1-个人 2-单位
     */
    @ApiModelProperty(value = "抬头类型 1-个人 2-单位", required = true)
    private Integer titleType;

    /**
     * 发票抬头名称
     */
    @ApiModelProperty(value = "发票抬头名称", required = true)
    private String invoiceTitle;

    /**
     * 纳税人识别号
     */
    @ApiModelProperty(value = "纳税人识别号", required = true)
    private String taxNumber;

    /**
     * 注册地址
     */
    @ApiModelProperty(value ="注册地址")
    private String registeredAddress;

    /**
     * 注册电话
     */
    @ApiModelProperty(value ="注册电话")
    private String registeredPhone;

    /**
     * 开户银行
     */
    @ApiModelProperty(value ="开户银行")
    private String bankName;

    /**
     * 银行账号
     */
    @ApiModelProperty(value ="银行账号")
    private String bankAccount;

    /**
     * 是否设为默认 1-是 2-否
     */
    @ApiModelProperty(value ="是否设为默认 1-是 2-否", required = true)
    private Integer isDefault;

}
