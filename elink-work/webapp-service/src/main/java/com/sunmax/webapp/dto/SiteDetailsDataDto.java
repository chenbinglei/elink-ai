package com.sunmax.webapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "SiteDetailsDataDto", description = "站点详情实体类")
public class SiteDetailsDataDto {

    /**
     * 站点id
     */
    @ApiModelProperty("站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty("站点名称")
    private String siteName;

    /**
     * 站点位置信息
     */
    @ApiModelProperty("站点位置信息")
    private String location;

    /**
     * 停车费类型 0-免费停车,1-停车收费,2-限时免费,3-充电限免
     */
    @ApiModelProperty("停车费类型 0-免费停车,1-停车收费,2-限时免费,3-充电限免")
    private Integer parkCostType;

    /**
     * 当前充电价格
     */
    @ApiModelProperty("当前充电价格")
    private SiteDetailsDataDto.ChargerPrice chargerPrice;

    /**
     * 价格详情列表
     */
    @ApiModelProperty("价格详情列表")
    private List<SiteDetailsDataDto.ChargerPrice> chargerPriceList;

    @Data
    public static class ChargerPrice {

        /**
         * 是否当前充电时段
         */
        @ApiModelProperty("是否当前充电时段 1-是")
        private int isChargeCurrent;

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

    /**
     * 营业时间
     */
    @ApiModelProperty("营业时间")
    private String busineHours;

    /**
     * 运营商
     */
    @ApiModelProperty("运营商")
    private String operatorName;

    /**
     * 服务电话
     */
    @ApiModelProperty("服务电话")
    private String serviceTel;

    /**
     * 站点照片
     */
    @ApiModelProperty("站点照片")
    private String image;
}
