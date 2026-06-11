package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "需求响应终止命令参数实体类")
public class ControlStopVo {

    /**
     * 场站id
     */
    @Schema(description = "场站id")
    private Long stationId;

    /**
     * 事件编号
     */
    @Schema(description = "事件编号")
    private String sjbh;

    /**
     * 事件状态
     */
    @Schema(description = "事件状态")
    private String sjzt;

}
