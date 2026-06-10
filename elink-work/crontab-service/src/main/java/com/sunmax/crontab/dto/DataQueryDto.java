package com.sunmax.crontab.dto;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 数据查询返回实体类
 */
@Data
@Schema(description = "数据查询返回实体类")
public class DataQueryDto {

    /**
     * 时间轴
     */
    @Schema(description = "时间轴")
    private List<String> xAXisList = Lists.newArrayList();

    /**
     * 数据
     */
    @Schema(description = "数据")
    private List<DataQueryDto.DataInfo> dataInfoList = Lists.newArrayList();

    /**
     * 数据信息
     */
    @Data
    public static class DataInfo {

        /**
         * 名称
         */
        @Schema(description = "名称")
        private String name;

        /**
         * 数据
         */
        @Schema(description = "数据")
        private List<Double> dataList = Lists.newArrayList();
    }
}
