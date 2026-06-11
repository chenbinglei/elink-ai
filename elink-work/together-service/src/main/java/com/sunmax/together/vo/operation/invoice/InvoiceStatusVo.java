package com.sunmax.together.vo.operation.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "发票状态修改参数实体类")
public class InvoiceStatusVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 发票申请单号
     */
    @Schema(description = "发票申请单号")
    private String id;

    /**
     * 发票状态 1-待开票 2-开票中 3-已开票 4-已撤销
     */
    @Schema(description = "发票状态 2-开票中 3-已开票")
    private Integer invoiceStatus;

}
