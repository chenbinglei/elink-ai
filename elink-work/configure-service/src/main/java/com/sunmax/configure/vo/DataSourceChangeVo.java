package com.sunmax.configure.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据源编辑参数实体类")
public class DataSourceChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 数据源名称
     */
    @Schema(description = "数据源名称")
    private String name;

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
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 动态字段
     */
    @Schema(description = "动态字段")
    private String dynamicField;

    /**
     * 请求参数Key
     */
    @Schema(description = "请求参数Key")
    private String requestKey;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
