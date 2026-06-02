package com.sunmax.together.dto.operation.invoice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "OrderInvoiceDetailDto", description = "订单开票详情返回实体类")
public class InvoiceDetailDto {

    /**
     * 发票申请单号
     */
    @ApiModelProperty(value = "发票申请单号")
    private String id;

    /**
     * 发票类型 1-普通发票 2-专用发票
     */
    @ApiModelProperty(value = "发票类型 1-普通发票 2-专用发票")
    private Integer invoiceType;

    /**
     * 抬头类型 1-个人 2-单位
     */
    @ApiModelProperty(value = "抬头类型 1-个人 2-单位")
    private Integer titleType;

    /**
     * 发票抬头名称
     */
    @ApiModelProperty(value = "发票抬头名称")
    private String invoiceTitle;

    /**
     * 纳税人识别号
     */
    @ApiModelProperty(value = "纳税人识别号")
    private String taxNumber;

    /**
     * 注册地址
     */
    @ApiModelProperty(value = "注册地址")
    private String registeredAddress;

    /**
     * 注册电话
     */
    @ApiModelProperty(value = "注册电话")
    private String registeredPhone;

    /**
     * 开户银行
     */
    @ApiModelProperty(value = "开户银行")
    private String bankName;

    /**
     * 银行账号
     */
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    /**
     * 发票金额
     */
    @ApiModelProperty(value = "发票金额")
    private BigDecimal invoiceAmount = new BigDecimal("0.0");

    /**
     * 收票人邮箱
     */
    @ApiModelProperty(value = "收票人邮箱")
    private String receiptEmail;

    /**
     * 发票文件路径
     */
    @ApiModelProperty(value = "发票文件路径")
    private String invoiceFilePath;

}
