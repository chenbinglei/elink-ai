package com.sunmax.configure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "数据源列表返回实体类")
public class DataSourceListDto {

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
