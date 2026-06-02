package com.sunmax.common.vo.webapp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 申请开票输入实体类
 * 用于接收前端传入的开票请求参数，包括可选的抬头模板ID以及自定义抬头信息，
 * 以及需要开票的订单号列表等
 */
@Data
@ApiModel(value = "OrderInvoicesVo", description = "申请开票入参参数实体类")
public class OrderInvoicesVo {

    /**
     * 小程序用户id
     */
    @ApiModelProperty(value = "小程序用户id", required = true)
    private String appletUserId;

    /**
     * 发票类型 1-普通发票 2-专用发票
     */
    @ApiModelProperty(value = "发票类型 1-普通发票 2-专用发票", required = true)
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
     * 纳税人识别号,单位税号
     */
    @ApiModelProperty(value = "单位税号", required = true)
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
     * 收票人邮箱，发送电子发票时使用
     */
    @ApiModelProperty(value = "收票人邮箱", required = true)
    private String receiptEmail;

    /**
     * 多个订单id
     */
    @ApiModelProperty(value = "多个订单id 例如[订单主键id1,订单主键id2]", required = true)
    private String orderIds;
}
