package com.sunmax.together.dto.monitor.assetOverview;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StorageAssetCountDto", description = "储能资产统计信息返回实体类")
public class StorageAssetCountDto {

    /**
     * 电池簇总额定容量
     */
    @ApiModelProperty(value = "电池簇总额定容量")
    private Double capacity;

    /**
     * pcs总额定功率
     */
    @ApiModelProperty(value = "pcs总额定功率")
    private Double stationRatedpower;

    /**
     * pcs设备数量
     */
    @ApiModelProperty(value = "pcs设备数量")
    private Integer pcsDeviceNum;

    /**
     * 光伏设备数量
     */
    @ApiModelProperty(value = "光伏设备数量")
    private Integer pvDeviceNum;

    /**
     * 站点数量
     */
    @ApiModelProperty(value = "站点数量")
    private Integer siteNum;

    /**
     * 充电功率
     */
    @ApiModelProperty(value = "充电功率")
    private Double chargePower;

    /**
     * 放电功率
     */
    @ApiModelProperty(value = "放电功率")
    private Double dischargePower;

    /**
     * 今日统计数据
     */
    @ApiModelProperty(value = "今日统计数据")
    private StorageAssetCountDto.CountData dayCountData;

    /**
     * 昨日统计数据
     */
    @ApiModelProperty(value = "昨日统计数据")
    private StorageAssetCountDto.CountData lastDayCountData;

    /**
     * 本月统计数据
     */
    @ApiModelProperty(value = "本月统计数据")
    private StorageAssetCountDto.CountData monthCountData;

    /**
     * 累计统计数据
     */
    @ApiModelProperty(value = "累计统计数据")
    private StorageAssetCountDto.CountData sumCountData;

    /**
     * 统计数据
     */
    @Data
    public static class CountData {

        /**
         * 储能充电量
         */
        @ApiModelProperty(value = "储能充电量")
        private Double storageChargeQt = 0.0;

        /**
         * 储能放电量
         */
        @ApiModelProperty(value = "储能放电量")
        private Double storageDischargeQt = 0.0;

        /**
         * 系统充放电循环效率
         */
        @ApiModelProperty(value = "系统充放电循环效率")
        private Double systemEfficiency;
    }
}
