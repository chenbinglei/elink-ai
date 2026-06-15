package com.sunmax.together.dto.monitor.systemMonitor;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "历史数据列表返回实体类")
public class HistoryDataDto {

    /**
     * 时间列表
     */
    @Schema(description = "时间列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 数据信息列表
     */
    @Schema(description = "数据信息列表")
    private List<DataInfo> dataInfoList = Lists.newArrayList();

    /**
     * 数据信息
     */
    @Data
    public static class DataInfo {

        /**
         * 设备名称
         */
        @Schema(description = "设备名称")
        private String deviceName;

        /**
         * 字段编码
         */
        @Schema(description = "字段编码")
        private String fieldCode;

        /**
         * 字段名称
         */
        @Schema(description = "字段名称")
        private String fieldName;

        /**
         * 数据
         */
        @Schema(description = "数据列表")
        private List<Object> dataList = Lists.newArrayList();
    }

}
