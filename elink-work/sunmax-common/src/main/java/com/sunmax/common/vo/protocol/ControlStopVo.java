package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ControlStopVo", description = "需求响应终止命令参数实体类")
public class ControlStopVo {

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
     * 事件状态
     */
    @ApiModelProperty(value = "事件状态")
    private String sjzt;

}
