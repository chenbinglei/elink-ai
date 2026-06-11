package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "开票记录详情返回实体类")
public class AppInvoiceDetailDto {

    /**
     * 发票申请单号
     */
    @Schema(description = "发票申请单号")
    private String id;

    /**
     * 发票总金额
     */
    @Schema(description = "发票总金额")
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

    /**
     * 收票邮箱
     */
    @Schema(description = "收票邮箱")
    private String receiptEmail;

    /**
     * 发票文件路径(pdf格式)
     */
    @Schema(description = "发票文件路径(pdf格式)")
    private String invoiceFilePath;

    /**
     * 关联的订单明细列表
     */
    @Schema(description = "关联的订单明细列表")
    private List<InvoiceOrderDto> invoiceOrderList = Lists.newArrayList();

    @Data
    public static class InvoiceOrderDto {

        /**
         * 订单id
         */
        @Schema(description = "订单id")
        private String orderId;

        /**
         * 订单编号
         */
        @Schema(description = "订单编号")
        private String orderNum;

        /**
         * 实付金额
         */
        @Schema(description = "实付金额")
        private BigDecimal actualTotalCost;

        /**
         * 充电电量
         */
        @Schema(description = "充电电量")
        private Double chargeQt;

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;

        /**
         * 运营商名称
         */
        @Schema(description = "运营商名称")
        private String operatorName;

    }
}
