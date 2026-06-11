package com.sunmax.common.dto.operate;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "站点费率信息返回实体类")
public class SiteRateInfoDto {

    /**
     * 充电直流价格配置列表
     */
    @Schema(description = "充电直流价格配置列表")
    private List<SiteRateInfoDto.PriceConfig> chargeDcPriceList = Lists.newArrayList();

    /**
     * 充电交流价格配置列表
     */
    @Schema(description = "充电交流价格配置列表")
    private List<SiteRateInfoDto.PriceConfig> chargeAcPriceList = Lists.newArrayList();

    /**
     * 放电直流价格配置列表
     */
    @Schema(description = "放电直流价格配置列表")
    private List<SiteRateInfoDto.PriceConfig> dischargeDcPriceList = Lists.newArrayList();

    /**
     * 价格配置信息
     */
    @Data
    public static class PriceConfig {

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
}
