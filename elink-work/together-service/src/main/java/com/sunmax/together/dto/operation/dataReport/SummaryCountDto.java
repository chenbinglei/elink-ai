package com.sunmax.together.dto.operation.dataReport;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "渠道汇总返回实体类")
public class SummaryCountDto {

    /**
     * 各平台充电量数据
     */
    @Schema(description = "各平台充电量数据")
    private List<Double> eachPlatformChargeQtList = Lists.newArrayList();

    /**
     * 各平台充电金额数据
     */
    @Schema(description = "各平台充电金额数据")
    private List<BigDecimal> eachPlatformChargeMoneyList = Lists.newArrayList();

    /**
     * 各平台订单数量数据
     */
    @Schema(description = "各平台订单数量数据")
    private List<Integer> eachPlatformChargeNumList = Lists.newArrayList();

    /**
     * 平台xAXis轴
     */
    @Schema(description = "平台xAXis轴")
    private List<String> platformXaxisList = Lists.newArrayList();

    /**
     * 第三方平台电量对比列表
     */
    @Schema(description = "总充电量列表")
    private List<SummaryCountDto.PlatformQtInfo> platformQtInfoList = Lists.newArrayList();

    /**
     * 第三方平台电量xXAisList列表
     */
    @Schema(description = "第三方平台电量xXAisList列表")
    private List<String> xAXisList = Lists.newArrayList();

    /**
     * 数据报表分页数据
     */
    @Schema(description = "数据报表分页数据")
    private PageDto<SummaryCountDto.ChargeSummary> dataReportInfoPage;

    /**
     * 数据报表列表数据
     */
    @Schema(description = "数据报表列表数据")
    private List<SummaryCountDto.ChargeSummary> dataReportInfoList;

    /**
     * 平台电量信息
     */
    @Data
    public static class PlatformQtInfo {

        /**
         * 平台名称
         */
        @Schema(description = "平台名称")
        private String platformName;

        /**
         * 平台电量列表
         */
        @Schema(description = "平台电量列表")
        private List<Double> platformQtList = Lists.newArrayList();
    }

    /**
     * 充电汇总信息
     */
    @Data
    public static class ChargeSummary {

        /**
         * 平台名称
         */
        @Schema(description = "平台名称")
        private String platformName;

        /**
         * 站点id
         */
        @Schema(description = "站点id")
        private String siteId;

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;

        /**
         * 充电订单数量
         */
        @Schema(description = "充电订单数量")
        private Integer chargeOrderNum = 0;

        /**
         * 充电量
         */
        @Schema(description = "充电量")
        private Double chargeQt = 0.0;

        /**
         * 充电时长
         */
        @Schema(description = "充电时长")
        private Double chargeDuration = 0.0;

        /**
         * 订单量占比
         */
        @Schema(description = "订单量占比")
        private Double orderNumRatio;

        /**
         * 订单总金额
         */
        @Schema(description = "订单总金额")
        private BigDecimal orderAmount = new BigDecimal("0.0");

        /**
         * 充电电费
         */
        @Schema(description = "充电电费")
        private BigDecimal chargeElecMony = new BigDecimal("0.0");

        /**
         * 充电服务费
         */
        @Schema(description = "充电服务费")
        private BigDecimal chargeServiceMony = new BigDecimal("0.0");
    }

}
