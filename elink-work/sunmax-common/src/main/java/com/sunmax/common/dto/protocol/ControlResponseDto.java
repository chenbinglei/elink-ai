package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "需求响应命令返回实体类")
public class ControlResponseDto {

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
     * 操作结果 0-成功 1-失败
     */
    @Schema(description = "操作结果 0-成功 1-失败")
    private Integer result;

}
