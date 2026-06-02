package com.sunmax.webapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SiteDetailsDataDto", description = "站点详情实体类")
public class SitePileDetailsDto {

    /**
     * 快充空闲数量
     */
    @ApiModelProperty("快充空闲数量")
    private Integer fastIdleNum;

    /**
     * 快充总数量
     */
    @ApiModelProperty("快充总数量")
    private Integer fastTotalNum;

    /**
     * 慢充空闲数量
     */
    @ApiModelProperty("慢充空闲数量")
    private Integer slowIdleNum;

    /**
     * 慢充总数量
     */
    @ApiModelProperty("慢充总数量")
    private Integer slowTotalNum;

    /**
     * 直流充电桩信息
     */
    @ApiModelProperty("直流充电桩信息")
    private List<SitePileDetailsDto.GunData> dcGunDataList;

    /**
     * 交流充电桩信息
     */
    @ApiModelProperty("交流充电桩信息")
    private List<SitePileDetailsDto.GunData> acGunDataList;

    @Data
    public static class GunData {

        /**
         * 枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
         */
        @ApiModelProperty("枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
        private Integer gunWorkState;

        /**
         * 电桩设备id
         */
        @ApiModelProperty("电桩设备id")
        private String pileDeviceId;

        /**
         * 当前soc
         */
        @ApiModelProperty("当前soc")
        private Integer batterySOC;

        /**
         * 充电桩类型 29-直流 28-交流 30-V2G
         */
        @ApiModelProperty("充电桩类型 29-直流 28-交流 30-V2G")
        private Integer pileTypeId;

        /**
         * 电桩编号
         */
        @ApiModelProperty("电桩编号")
        private String pileCode;

        /**
         * 电枪编号
         */
        @ApiModelProperty("电枪编号")
        private String gunCode;

        /**
         * 电枪名称
         */
        @ApiModelProperty("电枪名称")
        private String gunName;

        /**
         * 额定功率
         */
        @ApiModelProperty("额定功率")
        private Double power;

        /**
         * 额定电流
         */
        @ApiModelProperty("额定电流")
        private Integer ratedCurrent;

        /**
         * 额定电压上限 单位V
         */
        @ApiModelProperty("额定电压上限 单位V")
        private Integer voltageUpperLimits;

        /**
         * 额定电压下限 单位V
         */
        @ApiModelProperty("额定电压下限 单位V")
        private Integer voltageLowerLimits;

        /**
         * 国家标准 1:2011 2:2015
         */
        @ApiModelProperty("国家标准 1:2011 2:2015")
        private Integer nationalStandard;
    }
}
