package com.sunmax.configure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "图形数据源返回实体类")
public class GraphSourceDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联图模id
     */
    @Schema(description = "关联图模id")
    private String graphId;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 数据源id
     */
    @Schema(description = "数据源id")
    private String dataSourceId;

    /**
     * 数据源名称
     */
    @Schema(description = "数据源名称")
    private String dataSourceName;

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
     * 请求参数Value
     */
    @Schema(description = "请求参数Value")
    private String requestValue;

    /**
     * 响应参数Value
     */
    @Schema(description = "响应参数Value")
    private String responseValue;

}
