package com.sunmax.together.dto.operation.dataReport;

import com.sunmax.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "电站报表返回实体类")
public class SiteReportDto {

    /**
     * 订单总数量
     */
    @Schema(description = "订单总数量")
    private Integer orderTotalCount = 0;

    /**
     * 总电量
     */
    @Schema(description = "总电量")
    private Double totalQt = 0.0;

    /**
     * 尖时总电量
     */
    @Schema(description = "尖时总电量")
    private Double jTotalQt = 0.0;

    /**
     * 峰时总电量
     */
    @Schema(description = "峰时总电量")
    private Double fTotalQt = 0.0;

    /**
     * 平时总电量
     */
    @Schema(description = "平时总电量")
    private Double pTotalQt = 0.0;

    /**
     * 谷时总电量
     */
    @Schema(description = "谷时总电量")
    private Double gTotalQt = 0.0;

    /**
     * 深谷时总电量
     */
    @Schema(description = "深谷时总电量")
    private Double fukayaTotalQt = 0.0;

    /**
     * 数据报表分页数据
     */
    @Schema(description = "数据报表分页数据")
    private PageDto<SiteReportDto.DataReportInfo> dataReportInfoPage;

    /**
     * 数据报表列表数据
     */
    @Schema(description = "数据报表列表数据")
    private List<SiteReportDto.DataReportInfo> dataReportInfoList;

    @Data
    @Schema(description = "数据报表信息")
    public static class DataReportInfo {

        /**
         * 站点id
         */
        @Schema(description = "站点id")
        private String siteId;

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;

        /**
         * 所在省份
         */
        @Schema(description = "所在省份")
        private String province;

        /**
         * 所在市
         */
        @Schema(description = "所在市")
        private String city;

        /**
         * 所在区县
         */
        @Schema(description = "所在区县")
        private String county;

        /**
         * 商户id
         */
        @Schema(description = "商户id")
        private String accountId;

        /**
         * 商户名称
         */
        @Schema(description = "商户名称")
        private String accountName;

        /**
         * 交流桩数量
         */
        @Schema(description = "交流桩数量")
        private Integer acPileNum = 0;

        /**
         * 交流枪数量
         */
        @Schema(description = "交流枪数量")
        private Integer acGunNum = 0;

        /**
         * 交流额定功率
         */
        @Schema(description = "交流额定功率")
        private Double acRatedPower = 0.0;

        /**
         * 交流电量
         */
        @Schema(description = "交流电量")
        private Double acTotalQt = 0.0;

        /**
         * 直流桩数量
         */
        @Schema(description = "直流桩数量")
        private Integer dcPileNum = 0;

        /**
         * 直流枪数量
         */
        @Schema(description = "直流枪数量")
        private Integer dcGunNum = 0;

        /**
         * 直流额定功率
         */
        @Schema(description = "直流额定功率")
        private Double dcRatedPower = 0.0;

        /**
         * 直流电量
         */
        @Schema(description = "直流电量")
        private Double dcTotalQt = 0.0;

        /**
         * v2g桩数量
         */
        @Schema(description = "v2g桩数量")
        private Integer v2gPileNum = 0;

        /**
         * v2g枪数量
         */
        @Schema(description = "v2g数量")
        private Integer v2gGunNum = 0;

        /**
         * v2g额定功率
         */
        @Schema(description = "v2g额定功率")
        private Double v2gRatedPower = 0.0;

        /**
         * v2g电量
         */
        @Schema(description = "v2g电量")
        private Double v2gTotalQt = 0.0;

        /**
         * 总电量
         */
        @Schema(description = "总电量")
        private Double totalQt = 0.0;

        /**
         * 尖时电量
         */
        @Schema(description = "尖时电量")
        private Double jQt = 0.0;

        /**
         * 峰时电量
         */
        @Schema(description = "峰时电量")
        private Double fQt = 0.0;

        /**
         * 平时电量
         */
        @Schema(description = "平时电量")
        private Double pQt = 0.0;

        /**
         * 谷时电量
         */
        @Schema(description = "谷时电量")
        private Double gQt = 0.0;

        /**
         * 深谷时电量
         */
        @Schema(description = "深谷时电量")
        private Double fukayaQt = 0.0;

        /**
         * 总时长
         */
        @Schema(description = "总时长")
        private Double totalDuration = 0.0;

        /**
         * 订单数量
         */
        @Schema(description = "订单数量")
        private Integer orderCount = 0;
    }
}
