package com.sunmax.common.vo.webapp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 申请开票输入实体类
 * 用于接收前端传入的开票请求参数，包括可选的抬头模板ID以及自定义抬头信息，
 * 以及需要开票的订单号列表等
 */
@Data
@Schema(description = "申请开票入参参数实体类")
public class OrderInvoicesVo {

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 发票类型 1-普通发票 2-专用发票
     */
    @Schema(description = "发票类型 1-普通发票 2-专用发票")
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
     * 纳税人识别号,单位税号
     */
    @Schema(description = "单位税号")
    private String taxNumber;

    /**
     * 注册地址
     */
    @Schema(description = "注册地址")
    private String registeredAddress;

    /**
     * 注册电话
     */
    @Schema(description = "注册电话")
    private String registeredPhone;

    /**
     * 开户银行
     */
    @Schema(description = "开户银行")
    private String bankName;

    /**
     * 银行账号
     */
    @Schema(description = "银行账号")
    private String bankAccount;

    /**
     * 收票人邮箱，发送电子发票时使用
     */
    @Schema(description = "收票人邮箱")
    private String receiptEmail;

    /**
     * 多个订单id
     */
    @Schema(description = "多个订单id 例如[订单主键id1,订单主键id2]")
    private String orderIds;
}
