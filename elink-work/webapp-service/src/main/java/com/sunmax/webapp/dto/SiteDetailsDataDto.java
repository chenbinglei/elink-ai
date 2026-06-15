package com.sunmax.webapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "站点详情实体类")
public class SiteDetailsDataDto {

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
     * 站点位置信息
     */
    @Schema(description = "站点位置信息")
    private String location;

    /**
     * 停车费类型 0-免费停车,1-停车收费,2-限时免费,3-充电限免
     */
    @Schema(description = "停车费类型 0-免费停车,1-停车收费,2-限时免费,3-充电限免")
    private Integer parkCostType;

    /**
     * 当前充电价格
     */
    @Schema(description = "当前充电价格")
    private SiteDetailsDataDto.ChargerPrice chargerPrice;

    /**
     * 价格详情列表
     */
    @Schema(description = "价格详情列表")
    private List<SiteDetailsDataDto.ChargerPrice> chargerPriceList;

    @Data
    public static class ChargerPrice {

        /**
         * 是否当前充电时段
         */
        @Schema(description = "是否当前充电时段 1-是")
        private int isChargeCurrent;

        /**
         * 时段开始时间
         */
        @Schema(description = "时段开始时间")
        private String startTime;

        /**
         * 时段结束时间
         */
        @Schema(description = "时段结束时间")
        private String endTime;

        /**
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天
         */
        @Schema(description = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天")
        private Integer periodType;

        /**
         * 电费
         */
        @Schema(description = "电费")
        private BigDecimal electMoney;

        /**
         * 服务费
         */
        @Schema(description = "服务费")
        private BigDecimal serviceMoney;

    }

    /**
     * 营业时间
     */
    @Schema(description = "营业时间")
    private String busineHours;

    /**
     * 运营商
     */
    @Schema(description = "运营商")
    private String operatorName;

    /**
     * 服务电话
     */
    @Schema(description = "服务电话")
    private String serviceTel;

    /**
     * 站点照片
     */
    @Schema(description = "站点照片")
    private String image;
}
