package com.sunmax.together.dto.operation.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "返回实体类")
public class InvoiceListDto {

    /**
     * 发票申请单号
     */
    @Schema(description = "发票申请单号")
    private String id;

    /**
     * 发票抬头名称
     */
    @Schema(description = "发票抬头名称")
    private String invoiceTitle;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phoneNum;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 发票状态 1-待开票 2-开票中 3-已开票 4-已撤销
     */
    @Schema(description = "发票状态 1-待开票 2-开票中 3-已开票 4-已撤销")
    private Integer invoiceStatus;

    /**
     * 发票金额（单位：元）
     */
    @Schema(description = "发票金额")
    private BigDecimal invoiceAmount = new BigDecimal("0.0");

    /**
     * 商户id
     */
    @Schema(description = "商户id")
    private String mchId;

    /**
     * 商户名称
     */
    @Schema(description = "商户名称")
    private String mchName;

}
