package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 调控需求终止下发
 */
@Data
@Schema(description = "GwPVControlStopPublishVo")
public class GwPVControlStopPublishVo {

    /**
     * 需求响应事件编号
     */
    @Schema(description = "事件编号")
    private String sjbh;

    /**
     * 事件状态
     */
    @Schema(description = "事件状态")
    private String sjzt;

}
