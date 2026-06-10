package com.sunmax.together.dto.monitor.assetOverview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "充电资产统计信息返回实体类")
public class ChargeAssetCountDto {

    /**
     * 装机量
     */
    @Schema(description = "装机量")
    private Double capacity;

    /**
     * 电桩数量
     */
    @Schema(description = "电桩数量")
    private Integer pileNum = 0;

    /**
     * 电枪数量
     */
    @Schema(description = "电枪数量")
    private Integer gunNum = 0;

    /**
     * 站点数量
     */
    @Schema(description = "站点数量")
    private Integer siteNum;

    /**
     * 充电功率
     */
    @Schema(description = "充电功率")
    private Double chargePower;

    /**
     * V2G功率
     */
    @Schema(description = "V2G功率")
    private Double v2gPower;

    /**
     * 今日统计数据
     */
    @Schema(description = "今日统计数据")
    private ChargeAssetCountDto.CountData dayCountData;

    /**
     * 昨日统计数据
     */
    @Schema(description = "昨日统计数据")
    private ChargeAssetCountDto.CountData lastDayCountData;

    /**
     * 本月统计数据
     */
    @Schema(description = "本月统计数据")
    private ChargeAssetCountDto.CountData monthCountData;

    /**
     * 累计统计数据
     */
    @Schema(description = "累计统计数据")
    private ChargeAssetCountDto.CountData sumCountData;

    /**
     * 统计数据
     */
    @Data
    public static class CountData {

        /**
         * 充电电量
         */
        @Schema(description = "充电电量")
        private Double chargeQt;

        /**
         * V2G电量
         */
        @Schema(description = "V2G电量")
        private Double v2gQt;

        /**
         * 枪均充电量
         */
        @Schema(description = "枪均充电量")
        private Double gunAvgQt = 0.0;

        /**
         * 一次充电成功率(%)
         */
        @Schema(description = "一次充电成功率(%)")
        private Double chargeSuccessRatio;
    }
}
