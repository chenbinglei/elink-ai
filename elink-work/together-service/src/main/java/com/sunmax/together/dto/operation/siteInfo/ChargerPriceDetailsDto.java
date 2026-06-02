package com.sunmax.together.dto.operation.siteInfo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "ChargerPriceDetailsDto", description = "充放电价格详情返回实体类")
public class ChargerPriceDetailsDto {

    /**
     * 生效时间
     */
    @ApiModelProperty("生效时间")
    private String takeTime;

    /**
     * 设备类型 1-直流 2-交流
     */
    @ApiModelProperty("设备类型 1-直流 2-交流")
    private Integer deviceType;

    /**
     * 定价类型 1-全天同价 2-分时段定价
     */
    @ApiModelProperty("定价类型 1-全天同价 2-分时段定价")
    private Integer fixedType;

    /**
     * 计费策略
     */
    @ApiModelProperty(value = "计费策略")
    private List<PriceConfig> priceConfigList = Lists.newArrayList();

    /**
     * 应用范围
     */
    @ApiModelProperty(value = "应用范围")
    private List<PirceAppliedRange> pirceAppliedRangeList = Lists.newArrayList();

    /**
     * 价格配置信息
     */
    @Data
    public static class PriceConfig {

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
     * 价格配置信息
     */
    @Data
    public static class PirceAppliedRange {

        /**
         * 电桩编码
         */
        @ApiModelProperty("电桩编码")
        private String pileCode;

        /**
         * 电桩名称
         */
        @ApiModelProperty("电桩名称")
        private String pileName;

        /**
         * 生效结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
         */
        @ApiModelProperty("生效结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错")
        private Integer takeResult;
    }
}
