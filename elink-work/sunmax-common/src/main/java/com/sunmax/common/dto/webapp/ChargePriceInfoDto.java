package com.sunmax.common.dto.webapp;

import com.sunmax.common.dto.operate.OccupyPilePriceDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "ChargePriceInfoDto", description = "充放电价格详情实体类")
public class ChargePriceInfoDto {

    /**
     * 当前充放电价格
     */
    @ApiModelProperty("当前充放电价格")
    private ChargePriceInfoDto.ChargerPrice chargerPrice;

    /**
     * 充放电价格详情列表
     */
    @ApiModelProperty("充放电价格详情列表")
    private List<ChargePriceInfoDto.ChargerPrice> chargerPriceList;

    @Data
    public static class ChargerPrice {

        /**
         * 时段开始时间
         */
        @ApiModelProperty("时段开始时间")
        private String startTime;

        /**
         * 时段结束时间
         */
        @ApiModelProperty("时段结束时间")
        private String endTime;

        /**
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天
         */
        @ApiModelProperty("时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天")
        private Integer periodType;

        /**
         * 电费
         */
        @ApiModelProperty("电费")
        private BigDecimal electMoney;

        /**
         * 服务费
         */
        @ApiModelProperty("服务费")
        private BigDecimal serviceMoney;
    }

    /**
     * 占桩计费
     */
    @ApiModelProperty(value = "占桩计费")
    private ChargePriceInfoDto.PriceInfoData occupyPriceInfoData;

    /**
     * 价格详情信息
     */
    @Data
    public static class PriceInfoData {

        /**
         * 唯一id
         */
        @ApiModelProperty(value = "唯一id")
        private String id;

        /**
         * 定价类型 1-全天
         */
        @ApiModelProperty(value = "定价类型 1-全天")
        private Integer fixedType;

        /**
         * 部分时段价格信息
         */
        @ApiModelProperty(value = "部分时段价格信息")
        private String partPeriodInfo;

        /**
         * 设备类型 1-直流 2-交流
         */
        @ApiModelProperty(value = "设备类型 1-直流 2-交流")
        private Integer deviceType;

        /**
         * 免占桩时长(分钟)
         */
        @ApiModelProperty(value = "免占桩时长(分钟)")
        private Integer avoidDuration;

        /**
         * 配置类型 1-固定价格 2-阶梯价格
         */
        @ApiModelProperty(value = "配置类型 1-固定价格 2-阶梯价格")
        private Integer configType;

        /**
         * 配置价格信息字符串
         */
        @ApiModelProperty(value = "配置价格信息字符串")
        private String configPriceInfoStr;

        /**
         * 配置价格信息对象数组
         */
        @ApiModelProperty(value = "配置价格信息对象数组")
        private List<OccupyPilePriceDto.ConfigPriceInfo> configPriceInfoList;
    }

    /**
     * 配置价格信息对象
     */
    @Data
    public static class ConfigPriceInfo {

        /**
         * 超时时长
         */
        @ApiModelProperty(value = "超时时长")
        private Integer timeoutDuration;

        /**
         * 收费类型 1-分钟计费 2-固定金额
         */
        @ApiModelProperty(value = "收费类型 1-分钟计费 2-固定金额")
        private Integer chargeType;

        /**
         * 收费价格
         */
        @ApiModelProperty(value = "收费价格")
        private BigDecimal chargePrice;
    }
}
