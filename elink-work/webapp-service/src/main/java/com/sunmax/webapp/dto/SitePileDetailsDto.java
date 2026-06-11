package com.sunmax.webapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "站点详情实体类")
public class SitePileDetailsDto {

    /**
     * 快充空闲数量
     */
    @Schema(description = "快充空闲数量")
    private Integer fastIdleNum;

    /**
     * 快充总数量
     */
    @Schema(description = "快充总数量")
    private Integer fastTotalNum;

    /**
     * 慢充空闲数量
     */
    @Schema(description = "慢充空闲数量")
    private Integer slowIdleNum;

    /**
     * 慢充总数量
     */
    @Schema(description = "慢充总数量")
    private Integer slowTotalNum;

    /**
     * 直流充电桩信息
     */
    @Schema(description = "直流充电桩信息")
    private List<SitePileDetailsDto.GunData> dcGunDataList;

    /**
     * 交流充电桩信息
     */
    @Schema(description = "交流充电桩信息")
    private List<SitePileDetailsDto.GunData> acGunDataList;

    @Data
    public static class GunData {

        /**
         * 枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
         */
        @Schema(description = "枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
        private Integer gunWorkState;

        /**
         * 电桩设备id
         */
        @Schema(description = "电桩设备id")
        private String pileDeviceId;

        /**
         * 当前soc
         */
        @Schema(description = "当前soc")
        private Integer batterySOC;

        /**
         * 充电桩类型 29-直流 28-交流 30-V2G
         */
        @Schema(description = "充电桩类型 29-直流 28-交流 30-V2G")
        private Integer pileTypeId;

        /**
         * 电桩编号
         */
        @Schema(description = "电桩编号")
        private String pileCode;

        /**
         * 电枪编号
         */
        @Schema(description = "电枪编号")
        private String gunCode;

        /**
         * 电枪名称
         */
        @Schema(description = "电枪名称")
        private String gunName;

        /**
         * 额定功率
         */
        @Schema(description = "额定功率")
        private Double power;

        /**
         * 额定电流
         */
        @Schema(description = "额定电流")
        private Integer ratedCurrent;

        /**
         * 额定电压上限 单位V
         */
        @Schema(description = "额定电压上限 单位V")
        private Integer voltageUpperLimits;

        /**
         * 额定电压下限 单位V
         */
        @Schema(description = "额定电压下限 单位V")
        private Integer voltageLowerLimits;

        /**
         * 国家标准 1:2011 2:2015
         */
        @Schema(description = "国家标准 1:2011 2:2015")
        private Integer nationalStandard;
    }
}
