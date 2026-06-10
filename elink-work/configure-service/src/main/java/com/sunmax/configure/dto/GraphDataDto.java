package com.sunmax.configure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "图表数据返回实体类")
public class GraphDataDto {

    /**
     * 发布文件数据
     */
    @Schema(description = "发布文件路径")
    private String publicFilePath;

    /**
     * 数据源数据
     */
    @Schema(description = "数据源数据")
    private List<DataSource> dataSourceList = Lists.newArrayList();

    @Data
    public static class DataSource {

        /**
         * 通信方式 1-websocket 2-http 3-mqtt
         */
        @Schema(description = "通信方式 1-websocket 2-http 3-mqtt")
        private Integer type;

        /**
         * url地址
         */
        @Schema(description = "url地址")
        private String url;

        /**
         * 动态字段
         */
        @Schema(description = "动态字段")
        private String dynamicField;

        /**
         * 请求参数
         */
        @Schema(description = "请求参数")
        private String requestKey;

        /**
         * 请求参数Value
         */
        @Schema(description = "请求参数Value")
        private String requestValue;

        /**
         * 响应参数Value
         */
        @Schema(description = "响应参数Value")
        private String responseValue;

        /**
         * 变量数据列表
         */
        @Schema(description = "变量数据列表")
        private List<Variable> variableList = Lists.newArrayList();
    }


    @Data
    @Schema(description = "变量数据")
    public static class Variable {

        /**
         * 变量名
         */
        @Schema(description = "变量名")
        private String name;

        /**
         * 变量类型
         */
        @Schema(description = "变量类型")
        private String type;

        /**
         * 数据对象
         */
        @Schema(description = "数据对象")
        private String dataObject;

        /**
         * 数据点
         */
        @Schema(description = "数据点")
        private String dataPoint;

        /**
         * 数据点下标
         */
        @Schema(description = "数据点下标")
        private String dataPointIndex;

    }

}
