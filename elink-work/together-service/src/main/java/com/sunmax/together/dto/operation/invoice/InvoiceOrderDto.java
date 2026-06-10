package com.sunmax.together.dto.operation.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "发票订单详情返回实体类")
public class InvoiceOrderDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 总金额
     */
    @Schema(description = "总金额")
    private BigDecimal totalMoney = new BigDecimal("0.0");

    /**
     * 总电费
     */
    @Schema(description = "总电费")
    private BigDecimal totalElect = new BigDecimal("0.0");

    /**
     * 总服务费
     */
    @Schema(description = "总服务费")
    private BigDecimal totalFee = new BigDecimal("0.0");

    /**
     * 总电量
     */
    @Schema(description = "总电量")
    private Double totalQt = 0.0;

    /**
     * 订单列表
     */
    @Schema(description = "订单列表")
    private List<OrderDto> orderList = Lists.newArrayList();

    @Data
    @Schema(description = "订单详情返回实体类")
    public static class OrderDto {

        /**
         * 订单编号
         */
        @Schema(description = "订单编号")
        private String orderNum;

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;

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
         * 总电量
         */
        @Schema(description = "总电量")
        private Double totalQt = 0.0;

        /**
         * 实付金额
         */
        @Schema(description = "实付金额")
        private BigDecimal actualTotalCost = new BigDecimal("0.0");

        /**
         * 实收电费
         */
        @Schema(description = "实收电费")
        private BigDecimal actualTotalElect = new BigDecimal("0.0");

        /**
         * 实收服务费
         */
        @Schema(description = "实收服务费")
        private BigDecimal actualTotalFee = new BigDecimal("0.0");

    }
}
