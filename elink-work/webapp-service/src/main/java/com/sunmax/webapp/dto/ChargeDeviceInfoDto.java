package com.sunmax.webapp.dto;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 充放电设备详情实体类
 */
@Data
@ApiModel(value = "ChargeDeviceInfoDto", description = "充放电设备详情实体类")
public class ChargeDeviceInfoDto {

    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private String deviceId;

    /**
     * 所属站点id
     */
    @ApiModelProperty("所属站点id")
    private String siteId;

    /**
     * 所属站点名称
     */
    @ApiModelProperty("所属站点名称")
    private String siteName;

    /**
     * 全天开放 0-否 1-是
     */
    @ApiModelProperty(value = "全天开放 0-否 1-是")
    private Integer openAllDay;

    /**
     * 所属站点地址
     */
    @ApiModelProperty("所属站点地址")
    private String siteAddress;

    /**
     * 充电桩类型 29-直流 28-交流
     */
    @ApiModelProperty("充电桩类型 29-直流 28-交流")
    private Integer pileTypeId;

    /**
     * 电桩编号
     */
    @ApiModelProperty("电桩编号")
    private String pileCode;

    /**
     * 电桩名称
     */
    @ApiModelProperty("电桩名称")
    private String pileName;

    /**
     * 是否要付费 0-否 1-是
     */
    @ApiModelProperty("是否要付费 0-否 1-是")
    private Integer isPay;

    /**
     * 充电枪信息
     */
    @ApiModelProperty("充电枪信息")
    private List<ChargeDeviceInfoDto.GunData> gunDataList = Lists.newArrayList();

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
