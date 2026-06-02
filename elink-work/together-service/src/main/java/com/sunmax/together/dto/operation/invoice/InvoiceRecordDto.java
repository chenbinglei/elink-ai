package com.sunmax.together.dto.operation.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "InvoiceRecordDto", description = "操作记录列表返回实体类")
public class InvoiceRecordDto {

    /**
     * 发票申请单号
     */
    @ApiModelProperty(value = "发票申请单号")
    private String invoiceId;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNum;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private String mchId;

    /**
     * 商户名称
     */
    @ApiModelProperty(value = "商户名称")
    private String mchName;

    /**
     * 操作类型 1-受理 2-开票
     */
    @ApiModelProperty(value = "操作类型 1-受理 2-开票")
    private Integer operationType;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 平台用户名称
     */
    @ApiModelProperty(value = "平台用户名称")
    private String userName;
}
