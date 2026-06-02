package com.sunmax.configure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "GraphDataDto", description = "图表数据返回实体类")
public class GraphDataDto {

    /**
     * 发布文件数据
     */
    @ApiModelProperty("发布文件路径")
    private String publicFilePath;

    /**
     * 数据源数据
     */
    @ApiModelProperty("数据源数据")
    private List<DataSource> dataSourceList = Lists.newArrayList();

    @Data
    public static class DataSource {

        /**
         * 通信方式 1-websocket 2-http 3-mqtt
         */
        @ApiModelProperty(value = "通信方式 1-websocket 2-http 3-mqtt", required = true)
        private Integer type;

        /**
         * url地址
         */
        @ApiModelProperty(value = "url地址", required = true)
        private String url;

        /**
         * 动态字段
         */
        @ApiModelProperty(value = "动态字段", required = true)
        private String dynamicField;

        /**
         * 请求参数
         */
        @ApiModelProperty(value = "请求参数")
        private String requestKey;

        /**
         * 请求参数Value
         */
        @ApiModelProperty(value = "请求参数Value")
        private String requestValue;

        /**
         * 响应参数Value
         */
        @ApiModelProperty(value = "响应参数Value")
        private String responseValue;

        /**
         * 变量数据列表
         */
        @ApiModelProperty(value = "变量数据列表")
        private List<Variable> variableList = Lists.newArrayList();
    }


    @Data
    @ApiModel(value = "Variable", description = "变量数据")
    public static class Variable {

        /**
         * 变量名
         */
        @ApiModelProperty(value = "变量名")
        private String name;

        /**
         * 变量类型
         */
        @ApiModelProperty(value = "变量类型")
        private String type;

        /**
         * 数据对象
         */
        @ApiModelProperty(value = "数据对象")
        private String dataObject;

        /**
         * 数据点
         */
        @ApiModelProperty(value = "数据点")
        private String dataPoint;

        /**
         * 数据点下标
         */
        @ApiModelProperty(value = "数据点下标")
        private String dataPointIndex;

    }

}
