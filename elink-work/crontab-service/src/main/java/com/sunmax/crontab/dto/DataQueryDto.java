package com.sunmax.crontab.dto;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 数据查询返回实体类
 */
@Data
@ApiModel(value = "DataQueryDto", description = "数据查询返回实体类")
public class DataQueryDto {

    /**
     * 时间轴
     */
    @ApiModelProperty("时间轴")
    private List<String> xAXisList = Lists.newArrayList();

    /**
     * 数据
     */
    @ApiModelProperty("数据")
    private List<DataQueryDto.DataInfo> dataInfoList = Lists.newArrayList();

    /**
     * 数据信息
     */
    @Data
    public static class DataInfo {

        /**
         * 名称
         */
        @ApiModelProperty("名称")
        private String name;

        /**
         * 数据
         */
        @ApiModelProperty("数据")
        private List<Double> dataList = Lists.newArrayList();
    }
}
