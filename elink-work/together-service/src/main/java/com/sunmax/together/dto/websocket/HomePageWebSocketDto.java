package com.sunmax.together.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "首页WebSocket返回实体类")
public class HomePageWebSocketDto {

    /**
     * 光伏数据
     */
    @Schema(description = "光伏数据")
    private PvDataDto pvData = new PvDataDto();

    /**
     * 储能数据
     */
    @Schema(description = "储能数据")
    private StorageDataDto storageData = new StorageDataDto();

    /**
     * 充电桩数据
     */
    @Schema(description = "充电桩数据")
    private PileDataDto pileData = new PileDataDto();

    /**
     * 换电数据
     */
    @Schema(description = "换电数据")
    private ChangeDataDto changeData = new ChangeDataDto();

    @Data
    @Schema(description = "光伏数据返回实体类")
    public static class PvDataDto {

        /**
         * 光伏装机容量(kWp)
         */
        @Schema(description = "光伏装机容量(kWp)")
        private Double pvCapacity = 0.0;

        /**
         * 光伏场站数量
         */
        @Schema(description = "光伏场站数量")
        private Integer siteNum = 0;

        /**
         * 逆变器数量
         */
        @Schema(description = "逆变器数量")
        private Integer inverterNum = 0;

        /**
         * 累计发电量
         */
        @Schema(description = "累计发电量")
        private Double totalQt = 0.0;

        /**
         * 场站列表
         */
        @Schema(description = "场站列表")
        private List<SiteInfoDto> siteList;

    }

    @Data
    @Schema(description = "储能数据返回实体类")
    public static class StorageDataDto {

        /**
         * 储能PCS额定功率(单位kW)
         */
        @Schema(description = "储能PCS额定功率(单位kW)")
        private Double pcsRatedPower = 0.0;

        /**
         * 储能电池簇额定容量(单位kWh)
         */
        @Schema(description = "储能电池簇额定容量(单位kWh)")
        private Double batteryRatedCapacity = 0.0;

        /**
         * 储能场站数量
         */
        @Schema(description = "储能场站数量")
        private Integer siteNum = 0;

        /**
         * 储能柜数量(取PCS的数量)
         */
        @Schema(description = "储能柜数量")
        private Integer storageNum = 0;

        /**
         * 累计充电量
         */
        @Schema(description = "累计充电量")
        private Double totalChargeQt = 0.0;

        /**
         * 累计放电量
         */
        @Schema(description = "累计放电量")
        private Double totalDisChargeQt = 0.0;

        /**
         * 场站列表
         */
        @Schema(description = "场站列表")
        private List<SiteInfoDto> siteList;

    }

    @Data
    @Schema(description = "充电桩数据返回实体类")
    public static class PileDataDto {

        /**
         * 场站数量
         */
        @Schema(description = "场站数量")
        private Integer siteNum = 0;

        /**
         * 电桩数量
         */
        @Schema(description = "场站桩数量")
        private Integer pileNum = 0;

        /**
         * 累计充电量
         */
        @Schema(description = "累计充电量")
        private Double totalChargeQt = 0.0;

        /**
         * 累计充电次数
         */
        @Schema(description = "累计充电次数")
        private Integer totalChargeNum = 0;

        /**
         * 累计V2G电量
         */
        @Schema(description = "累计V2G电量")
        private Double totalDisChargeQt = 0.0;

        /**
         * 累计V2G次数
         */
        @Schema(description = "累计V2G次数")
        private Integer totalDisChargeNum = 0;

        /**
         * 场站列表
         */
        @Schema(description = "场站列表")
        private List<SiteInfoDto> siteList;

    }

    @Data
    @Schema(description = "换电数据返回实体类")
    public static class ChangeDataDto {

        /**
         * 换电站数量
         */
        @Schema(description = "换电站数量")
        private Integer changeSiteNum = 0;

        /**
         * 换电总装机容量
         */
        @Schema(description = "换电站总装机容量")
        private Double changeCapacity = 0.0;

        /**
         * 累计换电量
         */
        @Schema(description = "累计换电量")
        private Double totalChangeQt = 0.0;

        /**
         * 累计换电次数
         */
        @Schema(description = "累计换电次数")
        private Integer totalChangeNum = 0;

        /**
         * 场站列表
         */
        @Schema(description = "场站列表")
        private List<SiteInfoDto> siteList;

    }

    @Data
    @Schema(description = "场站信息返回实体类")
    public static class SiteInfoDto {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 场站名称
         */
        @Schema(description = "场站名称")
        private String siteName;

        /**
         * 场站类型 光伏类型(0-户用 1-工商业分布式 2-集中式) 储能类型(0-户用 1-用户侧工商业 2-集中式) 充电桩类型(0-充电场站 1-V2G场站 2-超充场站) 换电站类型(0-换电站)
         */
        @Schema(description = "场站类型 光伏类型(0-户用 1-工商业分布式 2-集中式) 储能类型(0-户用 1-用户侧工商业 2-集中式) 充电桩类型(0-充电场站 1-V2G场站 2-超充场站) 换电站类型(0-换电站)")
        private Integer siteType;

        /**
         * 经纬度
         */
        @Schema(description = "经纬度")
        private String coordinate;

        /**
         * 场景类型
         */
        @Schema(description = "场景类型")
        private String scenarioTypes;

        /**
         * 地址
         */
        @Schema(description = "地址")
        private String address;
    }

}
