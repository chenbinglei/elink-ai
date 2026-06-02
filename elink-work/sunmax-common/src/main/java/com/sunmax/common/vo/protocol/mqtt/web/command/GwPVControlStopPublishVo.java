package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 调控需求终止下发
 */
@Data
@ApiModel(value = "GwPVControlStopPublishVo")
public class GwPVControlStopPublishVo {

    /**
     * 需求响应事件编号
     */
    @ApiModelProperty(value = "事件编号", required = true)
    private String sjbh;

    /**
     * 事件状态
     */
    @ApiModelProperty(value = "事件状态", required = true)
    private String sjzt;

}
