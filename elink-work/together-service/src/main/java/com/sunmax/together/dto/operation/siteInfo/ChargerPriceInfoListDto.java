package com.sunmax.together.dto.operation.siteInfo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "ChargerPriceInfoListDto", description = "充放电价格信息列表返回实体类")
public class ChargerPriceInfoListDto {

    /**
     * 定价记录列表
     */
    @ApiModelProperty(value = "定价记录列表")
    private List<FixPriceRecord> fixPriceRecordList = Lists.newArrayList();

    /**
     * 生效中直流价格配置列表
     */
    @ApiModelProperty(value = "生效中直流价格配置列表")
    private List<PriceConfig> dcPriceConfigList = Lists.newArrayList();

    /**
     * 生效中交流价格配置列表
     */
    @ApiModelProperty(value = "生效中交流价格配置列表")
    private List<PriceConfig> acPriceConfigList = Lists.newArrayList();

    /**
     * 定价记录信息
     */
    @Data
    public static class FixPriceRecord {

        /**
         * 唯一id
         */
        @ApiModelProperty("唯一id")
        private String id;

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
         * 价格状态 1-生效中 2-待生效 3-已失效
         */
        @ApiModelProperty("价格状态 1-生效中 2-待生效 3-已失效")
        private Integer priceState;

        /**
         * 创建时间
         */
        @ApiModelProperty("创建时间")
        private LocalDateTime createTime;

    }

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
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天
         */
        @ApiModelProperty("时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天")
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
}
