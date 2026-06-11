package com.sunmax.crontab.vo;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Schema(description = "站点设备数据查询条件")
public class SiteDeviceQueryVo {

    /**
     * 查询时间类型 0-当日 1-昨日 2-近七天 3-近30天 4-当月 5-上月 6-本年
     */
    @Schema(description = "查询时间类型 0-当日 1-昨日 2-近七天 3-近30天 4-当月 5-上月 6-本年")
    private Integer dateType;

    /**
     * 时间间隔 m-分钟 h-小时 d-天 n-月 y-年
     */
    @Schema(description = "时间间隔 m-分钟 h-小时 d-天 n-月 y-年")
    private String timeInterval;

    /**
     * 设备变量数据
     */
    @Schema(description = "设备变量数据")
    private Map<String, VariableData> variableDataMap = Maps.newHashMap();

    @Data
    public static class VariableData {

        /**
         * 节点变量
         */
        @Schema(description = "节点变量")
        private Set<String> nodeList;

        /**
         * 功能点数据
         */
        @Schema(description = "功能点数据")
        private Set<String> functionList;

    }

}
