package com.sunmax.together.vo.operation.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单开票平台实体类")
public class InvoiceQueryVo {

    /**
     * 发票申请单号
     */
    @Schema(description = "发票申请单号")
    private String id;

    /**
     * 商户id
     */
    @Schema(description = "商户id")
    private String mchId;

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
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 发票状态 1-待开票 2-开票中 3-已开票 4-已撤销
     */
    @Schema(description = "发票状态 1-待开票 2-开票中 3-已开票 4-已撤销")
    private Integer invoiceStatus;

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

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
