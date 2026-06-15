package com.sunmax.together.dto.monitor.assetOverview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光伏资产统计信息返回实体类")
public class PvAssetCountDto {

    /**
     * 装机量
     */
    @Schema(description = "装机量")
    private Double capacity;

    /**
     * 逆变器数量
     */
    @Schema(description = "逆变器数量")
    private Integer inverterNum;

    /**
     * 站点数量
     */
    @Schema(description = "站点数量")
    private Integer siteNum;

    /**
     * 实时出力
     */
    @Schema(description = "实时出力")
    private Double realOutput;

    /**
     * 运行效率
     */
    @Schema(description = "运行效率")
    private Double runEfficiency;

    /**
     * 今日统计数据
     */
    @Schema(description = "今日统计数据")
    private PvAssetCountDto.CountData dayCountData;

    /**
     * 昨日统计数据
     */
    @Schema(description = "昨日统计数据")
    private PvAssetCountDto.CountData lastDayCountData;

    /**
     * 本月统计数据
     */
    @Schema(description = "本月统计数据")
    private PvAssetCountDto.CountData monthCountData;

    /**
     * 累计统计数据
     */
    @Schema(description = "累计统计数据")
    private PvAssetCountDto.CountData sumCountData;

    /**
     * 统计数据
     */
    @Data
    public static class CountData {

        /**
         * 今日发电量
         */
        @Schema(description = "今日发电量")
        private Double dayQt = 0.0;

        /**
         * 总发电量
         */
        @Schema(description = "总发电量")
        private Double totalQt = 0.0;

        /**
         * 上网电量
         */
        @Schema(description = "上网电量")
        private Double internetQt = 0.0;

        /**
         * 消纳电量
         */
        @Schema(description = "消纳电量")
        private Double absorptiveQt = 0.0;

        /**
         * 消纳率
         */
        @Schema(description = "消纳率")
        private Double absorptiveRate;

        /**
         * 等待发电小时
         */
        @Schema(description = "等待发电小时")
        private Double waitOutHour;
    }
}
