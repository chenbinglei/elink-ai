package com.sunmax.together.vo.operation.invoice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OrderInvoiceVo", description = "订单开票平台实体类")
public class InvoiceQueryVo {

    /**
     * 发票申请单号
     */
    @ApiModelProperty(value = "发票申请单号")
    private String id;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private String mchId;

    /**
     * 发票抬头名称
     */
    @ApiModelProperty(value = "发票抬头名称")
    private String invoiceTitle;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNum;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**
     * 发票状态 1-待开票 2-开票中 3-已开票 4-已撤销
     */
    @ApiModelProperty(value = "发票状态 1-待开票 2-开票中 3-已开票 4-已撤销")
    private Integer invoiceStatus;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

}
