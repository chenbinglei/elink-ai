package com.sunmax.together.dto.monitor.assetOverview;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 数据查询返回实体类
 */
@Data
@Schema(description = "电桩功能点曲线数据返回实体类")
public class PileFunCurveDto {

    /**
     * 时间轴
     */
    @JsonProperty("xAxisList")
    @Schema(description = "时间轴")
    private List<String> xAXisList = Lists.newArrayList();

    /**
     * 数据
     */
    @Schema(description = "数据")
    private List<PileFunCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

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
         * 英文名称
         */
        @Schema(description = "英文名称")
        private String eName;

        /**
         * 数据
         */
        @Schema(description = "数据")
        private List<Object> dataList = Lists.newArrayList();
    }
}
