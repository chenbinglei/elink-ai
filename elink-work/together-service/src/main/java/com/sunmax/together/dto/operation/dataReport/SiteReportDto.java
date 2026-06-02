package com.sunmax.together.dto.operation.dataReport;

import com.sunmax.common.dto.PageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SiteReportDto", description = "电站报表返回实体类")
public class SiteReportDto {

    /**
     * 订单总数量
     */
    @ApiModelProperty(value = "订单总数量")
    private Integer orderTotalCount = 0;

    /**
     * 总电量
     */
    @ApiModelProperty(value = "总电量")
    private Double totalQt = 0.0;

    /**
     * 尖时总电量
     */
    @ApiModelProperty(value = "尖时总电量")
    private Double jTotalQt = 0.0;

    /**
     * 峰时总电量
     */
    @ApiModelProperty(value = "峰时总电量")
    private Double fTotalQt = 0.0;

    /**
     * 平时总电量
     */
    @ApiModelProperty(value = "平时总电量")
    private Double pTotalQt = 0.0;

    /**
     * 谷时总电量
     */
    @ApiModelProperty(value = "谷时总电量")
    private Double gTotalQt = 0.0;

    /**
     * 深谷时总电量
     */
    @ApiModelProperty(value = "深谷时总电量")
    private Double fukayaTotalQt = 0.0;

    /**
     * 数据报表分页数据
     */
    @ApiModelProperty(value = "数据报表分页数据")
    private PageDto<SiteReportDto.DataReportInfo> dataReportInfoPage;

    /**
     * 数据报表列表数据
     */
    @ApiModelProperty(value = "数据报表列表数据")
    private List<SiteReportDto.DataReportInfo> dataReportInfoList;

    @Data
    @ApiModel(value = "DataReportInfo", description = "数据报表信息")
    public static class DataReportInfo {

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
         * 所在省份
         */
        @ApiModelProperty(value = "所在省份")
        private String province;

        /**
         * 所在市
         */
        @ApiModelProperty(value = "所在市")
        private String city;

        /**
         * 所在区县
         */
        @ApiModelProperty(value = "所在区县")
        private String county;

        /**
         * 商户id
         */
        @ApiModelProperty(value = "商户id")
        private String accountId;

        /**
         * 商户名称
         */
        @ApiModelProperty(value = "商户名称")
        private String accountName;

        /**
         * 交流桩数量
         */
        @ApiModelProperty(value = "交流桩数量")
        private Integer acPileNum = 0;

        /**
         * 交流枪数量
         */
        @ApiModelProperty(value = "交流枪数量")
        private Integer acGunNum = 0;

        /**
         * 交流额定功率
         */
        @ApiModelProperty(value = "交流额定功率")
        private Double acRatedPower = 0.0;

        /**
         * 交流电量
         */
        @ApiModelProperty(value = "交流电量")
        private Double acTotalQt = 0.0;

        /**
         * 直流桩数量
         */
        @ApiModelProperty(value = "直流桩数量")
        private Integer dcPileNum = 0;

        /**
         * 直流枪数量
         */
        @ApiModelProperty(value = "直流枪数量")
        private Integer dcGunNum = 0;

        /**
         * 直流额定功率
         */
        @ApiModelProperty(value = "直流额定功率")
        private Double dcRatedPower = 0.0;

        /**
         * 直流电量
         */
        @ApiModelProperty(value = "直流电量")
        private Double dcTotalQt = 0.0;

        /**
         * v2g桩数量
         */
        @ApiModelProperty(value = "v2g桩数量")
        private Integer v2gPileNum = 0;

        /**
         * v2g枪数量
         */
        @ApiModelProperty(value = "v2g数量")
        private Integer v2gGunNum = 0;

        /**
         * v2g额定功率
         */
        @ApiModelProperty(value = "v2g额定功率")
        private Double v2gRatedPower = 0.0;

        /**
         * v2g电量
         */
        @ApiModelProperty(value = "v2g电量")
        private Double v2gTotalQt = 0.0;

        /**
         * 总电量
         */
        @ApiModelProperty(value = "总电量")
        private Double totalQt = 0.0;

        /**
         * 尖时电量
         */
        @ApiModelProperty(value = "尖时电量")
        private Double jQt = 0.0;

        /**
         * 峰时电量
         */
        @ApiModelProperty(value = "峰时电量")
        private Double fQt = 0.0;

        /**
         * 平时电量
         */
        @ApiModelProperty(value = "平时电量")
        private Double pQt = 0.0;

        /**
         * 谷时电量
         */
        @ApiModelProperty(value = "谷时电量")
        private Double gQt = 0.0;

        /**
         * 深谷时电量
         */
        @ApiModelProperty(value = "深谷时电量")
        private Double fukayaQt = 0.0;

        /**
         * 总时长
         */
        @ApiModelProperty(value = "总时长")
        private Double totalDuration = 0.0;

        /**
         * 订单数量
         */
        @ApiModelProperty(value = "订单数量")
        private Integer orderCount = 0;
    }
}
