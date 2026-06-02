package com.sunmax.common.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "OccupyPilePriceDto", description = "占桩价格信息返回实体类")
public class OccupyPilePriceDto {

    /**
     * 直流计费
     */
    @ApiModelProperty(value = "直流计费")
    private OccupyPilePriceDto.PriceInfoData dcPriceInfoData;

    /**
     * 交流计费
     */
    @ApiModelProperty(value = "交流计费")
    private OccupyPilePriceDto.PriceInfoData acPriceInfoData;

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
