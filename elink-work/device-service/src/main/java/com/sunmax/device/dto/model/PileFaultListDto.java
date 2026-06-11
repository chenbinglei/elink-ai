package com.sunmax.device.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型电桩告警列表返回实体类")
public class PileFaultListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 故障编码
     */
    @Schema(description = "故障编码")
    private Integer faultCode;

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
     * 关联模型表id
     */
    @Schema(description = "关联模型表id")
    private String modelId;

}
