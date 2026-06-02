package com.sunmax.together.dto.monitor.centralMonitor;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@ApiModel(value = "SiteTopCurveDto", description = "站点拓扑节点曲线数据返回实体类")
public class SiteTopCurveDto {

    /**
     * 日期列表
     */
    @ApiModelProperty(value = "日期列表")
    private Set<String> dateList;

    /**
     * 曲线数据对象
     * 数据类型(0-关口表 1-光伏 2-储能 3-电桩 6-换电 7-负荷) -> 数据列表
     */
    @ApiModelProperty(value = "曲线数据对象 (数据类型(0-关口表 1-光伏 2-储能 3-电桩 6-换电 7-负荷) -> 曲线数据列表)")
    private Map<Integer, List<CurveData>> curveDataMap = Maps.newHashMap();


    @Data
    @ApiModel(value = "CurveData", description = "曲线数据")
    public static class CurveData {

        /**
         * 数据类型 1-关口表 2-光伏 3-储能 4-电桩 5-换电 6-负荷
         */
        @ApiModelProperty(value = "数据类型 1-关口表 2-光伏 3-储能 4-电桩 5-换电 6-负荷")
        private Integer dataType;

        /**
         * 曲线编号
         */
        @ApiModelProperty(value = "曲线编号")
        private String curveCode;

        /**
         * 曲线名称
         */
        @ApiModelProperty(value = "曲线名称")
        private String curveName;

        /**
         * 曲线数据列表
         */
        @ApiModelProperty(value = "曲线数据列表")
        private List<Double> curveList;

    }

}
