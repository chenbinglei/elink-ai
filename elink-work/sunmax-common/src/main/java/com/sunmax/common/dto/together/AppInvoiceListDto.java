package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "发票开票记录列表返回实体类")
public class AppInvoiceListDto {

    /**
     * 发票申请单号
     */
    @Schema(description = "发票申请单号")
    private String id;

    /**
     * 发票金额
     */
    @Schema(description = "发票金额")
    private BigDecimal invoiceAmount;

    /**
     * 发票抬头名称
     */
    @Schema(description = "发票抬头名称")
    private String invoiceTitle;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 发票状态 1-待开票 2-开票中 3-已开票 4-已撤销
     */
    @Schema(description = "发票状态 1-待开票 2-开票中 3-已开票 4-已撤销")
    private Integer invoiceStatus;
}
