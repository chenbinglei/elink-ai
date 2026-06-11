package com.sunmax.together.vo.operation.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "发票操作记录查询实体类")
public class InvoiceRecordVo {

    /**
     * 发票申请单号
     */
    @Schema(description = "发票申请单号")
    private String invoiceId;

    /**
     * 商户id
     */
    @Schema(description = "商户id")
    private String mchId;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phoneNum;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}

