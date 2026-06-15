package com.sunmax.together.dto.websocket;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "大屏静态页面websocket返回实体类")
public class StaticWebSocketDto {

    /**
     * 资产总览数据
     */
    @Schema(description = "资产总览数据")
    private AssetOverviewDto assetOverview;

    /**
     * 地图站点数据
     */
    @Schema(description = "地图站点数据")
    private MapSiteDto mapSite;

    /**
     * 设备告警数据
     */
    @Schema(description = "设备告警数据")
    private DeviceAlarmDto deviceAlarm;


    @Data
    @Schema(description = "资产总览数据实体类")
    public static class AssetOverviewDto {

        /**
         * 总装机容量
         */
        @Schema(description = "总装机容量")
        private Double totalCapacity;

        /**
         * 光伏装机容量
         */
        @Schema(description = "光伏装机容量")
        private Double pvCapacity;

        /**
         * 储能装机容量
         */
        @Schema(description = "储能装机容量")
        private Double storageCapacity;

        /**
         * 充电桩装机容量
         */
        @Schema(description = "充电桩装机容量")
        private Double pileCapacity;

        /**
         * 换电装机容量
         */
        @Schema(description = "换电装机容量")
        private Double changeCapacity;

    }

    @Data
    @Schema(description = "地图站点数据实体类")
    public static class MapSiteDto {

        /**
         * 站点数量
         */
        @Schema(description = "站点数量")
        private Integer siteCount;

        /**
         * 站点数量数据 (场站类型(0-一体化电站 1-光伏电站 2-储能电站 3-充电桩电站 4-换电电站) -> 数量)
         */
        @Schema(description = "站点数量数据 (场站类型(0-一体化电站 1-光伏电站 2-储能电站 3-充电桩电站 4-换电电站) -> 数量)")
        private Map<String, Long> siteCountMap = Maps.newTreeMap();

        /**
         * 站点数据列表
         */
        @Schema(description = "站点数据列表")
        private List<SiteDto> siteList = Lists.newArrayList();

    }

    @Data
    @Schema(description = "场站统计返回实体类")
    public static class SiteDto {

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
         * 位置
         */
        @Schema(description = "位置")
        private String location;

        /**
         * 站点类型 0-一体化电站 1-光伏电站 2-储能电站 3-充电桩电站 4-换电电站
         */
        @Schema(description = "站点类型 0-一体化电站 1-光伏电站 2-储能电站 3-充电桩电站 4-换电电站")
        private Integer siteType;

        /**
         * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)
         */
        @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)")
        private String scenarioTypes;

        /**
         * 光伏装机容量
         */
        @Schema(description = "光伏装机容量")
        private Double pvCapacity = 0.0;

        /**
         * 光伏日发电量
         */
        @Schema(description = "光伏日发电量")
        private Double pvDayQt = 0.0;

        /**
         * 储能装机容量
         */
        @Schema(description = "储能装机容量")
        private Double storageCapacity = 0.0;

        /**
         * 储能今日充/放电量
         */
        @Schema(description = "储能今日充/放电量")
        private String storageChargeQt;

        /**
         * 充电桩装机容量
         */
        @Schema(description = "充电桩装机容量")
        private Double pileCapacity = 0.0;

        /**
         * 充电今日充/放电量
         */
        @Schema(description = "充电今日充/放电量")
        private String pileChargeQt;

        /**
         * 换电装机容量
         */
        @Schema(description = "换电装机容量")
        private Double changeCapacity = 0.0;

        /**
         * 换电今日充电量
         */
        @Schema(description = "换电今日充/耗电量")
        private String changeChargeQt;

    }

    @Data
    @Schema(description = "设备告警返回实体类")
    public static class DeviceAlarmDto {

        /**
         * 今日告警数量
         */
        @Schema(description = "今日告警数量")
        private Integer dayAlarmNum = 0;

        /**
         * 已修复告警数量
         */
        @Schema(description = "已修复告警数量")
        private Integer fixAlarmNum = 0;

        /**
         * 已修复占比率
         */
        @Schema(description = "已修复占比率")
        private Double fixAlarmRate = 100.0;

    }

}
