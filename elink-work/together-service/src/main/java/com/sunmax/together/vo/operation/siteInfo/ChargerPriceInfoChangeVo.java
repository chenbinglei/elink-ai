package com.sunmax.together.vo.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "ChargerPriceInfoChangeVo", description = "充放电价格编辑参数")
public class ChargerPriceInfoChangeVo {

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id", required = true)
    private String siteId;

    /**
     * 生效类型 1-立即生效 2-自定义时间
     */
    @ApiModelProperty(value = "生效类型 1-立即生效 2-自定义时间", required = true)
    private Integer takeType;

    /**
     * 生效时间
     */
    @ApiModelProperty(value = "生效时间")
    private String takeTime;

    /**
     * 设备类型 1-直流 2-交流 3-全部
     */
    @ApiModelProperty(value = "设备类型 1-直流 2-交流 3-全部", required = true)
    private Integer deviceType;

    /**
     * 定价类型 1-全天同价 2-分时段定价
     */
    @ApiModelProperty(value = "定价类型 1-全天同价 2-分时段定价", required = true)
    private Integer fixedType;

    /**
     * 价格类型 1-充电 2-放电
     */
    @ApiModelProperty(value = "价格类型 1-充电 2-放电", required = true)
    private Integer priceType;

    /**
     * 价格配置数据对象
     */
    @ApiModelProperty(value = "价格配置数据对象")
    private String ChargerPriceDtos;

    /**
     * 充放电价格配置
     */
    @Data
    public static class ChargerPriceDto {

        /**
         * 时段开始时间
         */
        @ApiModelProperty(value = "时段开始时间")
        private String startTime;

        /**
         * 时段结束时间
         */
        @ApiModelProperty(value = "时段结束时间")
        private String endTime;

        /**
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天
         */
        @ApiModelProperty(value = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天")
        private Integer periodType;

        /**
         * 电费
         */
        @ApiModelProperty(value = "电费")
        private BigDecimal electMoney;

        /**
         * 服务费
         */
        @ApiModelProperty(value = "服务费")
        private BigDecimal serviceMoney;
    }
}
