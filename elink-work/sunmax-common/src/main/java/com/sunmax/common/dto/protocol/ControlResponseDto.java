package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ControlResponseDto", description = "需求响应命令返回实体类")
public class ControlResponseDto {

    /**
     * 场站id
     */
    @ApiModelProperty(value = "场站id", required = true)
    private Long stationId;

    /**
     * 事件编号
     */
    @ApiModelProperty(value = "事件编号", required = true)
    private String sjbh;

    /**
     * 操作结果 0-成功 1-失败
     */
    @ApiModelProperty(value = "操作结果 0-成功 1-失败")
    private Integer result;

}
