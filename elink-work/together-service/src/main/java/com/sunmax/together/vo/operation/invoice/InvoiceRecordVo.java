package com.sunmax.together.vo.operation.invoice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InvoiceRecordQueryVo", description = "发票操作记录查询实体类")
public class InvoiceRecordVo {

    /**
     * 发票申请单号
     */
    @ApiModelProperty(value = "发票申请单号")
    private String invoiceId;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private String mchId;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNum;

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

}

