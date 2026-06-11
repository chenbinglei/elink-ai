package com.sunmax.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
@Schema(description = "发票抬头入参实体类")
public class InvoiceTitleVo {
    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 发票类型 1-普通发票 2-专用发票
     */
    @Schema(description = "发票类型 1-普通 2-专用")
    private Integer invoiceType;

    /**
     * 抬头类型 1-个人 2-单位
     */
    @Schema(description = "抬头类型 1-个人 2-单位")
    private Integer titleType;

    /**
     * 发票抬头名称
     */
    @Schema(description = "发票抬头名称")
    private String invoiceTitle;

    /**
     * 纳税人识别号
     */
    @Schema(description = "纳税人识别号")
    private String taxNumber;

    /**
     * 注册地址
     */
    @Schema(description ="注册地址")
    private String registeredAddress;

    /**
     * 注册电话
     */
    @Schema(description ="注册电话")
    private String registeredPhone;

    /**
     * 开户银行
     */
    @Schema(description ="开户银行")
    private String bankName;

    /**
     * 银行账号
     */
    @Schema(description ="银行账号")
    private String bankAccount;

    /**
     * 是否设为默认 1-是 2-否
     */
    @Schema(description ="是否设为默认 1-是 2-否")
    private Integer isDefault;

}
