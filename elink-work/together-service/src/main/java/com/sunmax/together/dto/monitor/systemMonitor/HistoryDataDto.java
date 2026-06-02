package com.sunmax.together.dto.monitor.systemMonitor;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "HistoryDataDto", description = "历史数据列表返回实体类")
public class HistoryDataDto {

    /**
     * 时间列表
     */
    @ApiModelProperty(value = "时间列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 数据信息列表
     */
    @ApiModelProperty(value = "数据信息列表")
    private List<DataInfo> dataInfoList = Lists.newArrayList();

    /**
     * 数据信息
     */
    @Data
    public static class DataInfo {

        /**
         * 设备名称
         */
        @ApiModelProperty(value = "设备名称")
        private String deviceName;

        /**
         * 字段编码
         */
        @ApiModelProperty(value = "字段编码")
        private String fieldCode;

        /**
         * 字段名称
         */
        @ApiModelProperty(value = "字段名称")
        private String fieldName;

        /**
         * 数据
         */
        @ApiModelProperty(value = "数据列表", required = true)
        private List<Object> dataList = Lists.newArrayList();
    }

}
