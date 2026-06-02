package com.sunmax.crontab.dto;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "SiteDeviceDataDto", description = "站点设备数据")
public class SiteDeviceDataDto {

    /**
     * 日期列表
     */
    @ApiModelProperty(value = "日期列表")
    private List<String> dateList;

    /**
     * 设备数据对象
     * 设备id -> 数据列表
     */
    @ApiModelProperty(value = "设备数据对象")
    private Map<String, List<DeviceData>> deviceDataMap = Maps.newHashMap();


    @Data
    public static class DeviceData {

        /**
         * 设备数据标识
         */
        @ApiModelProperty(value = "设备数据标识")
        private String dataCode;

        /**
         * 设备数据名称
         */
        @ApiModelProperty(value = "设备数据名称")
        private String chName;

        /**
         * 设备数据类型 1-系统变量 2-设备功能点
         */
        @ApiModelProperty(value = "设备数据类型 1-系统变量 2-设备功能点")
        private Integer dataType;

        /**
         * 变量数据列表
         */
        @ApiModelProperty(value = "变量数据列表")
        private List<Object> dataList = Lists.newArrayList();

    }

}
