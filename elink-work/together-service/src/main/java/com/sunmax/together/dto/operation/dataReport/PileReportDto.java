package com.sunmax.together.dto.operation.dataReport;

import com.sunmax.common.dto.PageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "PileReportDto", description = "电桩报表返回实体类")
public class PileReportDto {

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
    private PageDto<PileReportDto.DataReportInfo> dataReportInfoPage;

    /**
     * 数据报表列表数据
     */
    @ApiModelProperty(value = "数据报表列表数据")
    private List<PileReportDto.DataReportInfo> dataReportInfoList;

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
         * 电桩编码
         */
        @ApiModelProperty(value = "电桩编码")
        private String pileCode;

        /**
         * 电枪编码
         */
        @ApiModelProperty(value = "电枪编码")
        private Integer gunCode;

        /**
         * 电桩类型 28-交流 29-直流 30-V2G
         */
        @ApiModelProperty(value = "电桩类型 28-交流 29-直流 30-V2G")
        private String typeTd;

        /**
         * 电桩额定功率
         */
        @ApiModelProperty(value = "电桩额定功率")
        private Double ratedPower = 0.0;

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
