package com.sunmax.device.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型事件查询参数")
public class ModelEventQueryVo {

    /**
     * 模型表id
     */
    @Schema(description = "模型表id")
    private String modelId;

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
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
