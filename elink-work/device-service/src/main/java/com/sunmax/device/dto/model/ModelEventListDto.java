package com.sunmax.device.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型事件列表返回实体类")
public class ModelEventListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 事件名称
     */
    @Schema(description = "事件名称")
    private String eventName;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @Schema(description = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 多个功能点名称
     */
    @Schema(description = "多个功能点名称")
    private String functionNames;

    /**
     * 事件描述
     */
    @Schema(description = "事件描述")
    private String eventDesc;

}
