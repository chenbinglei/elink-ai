package com.sunmax.common.vo.protocol.mqtt.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 调控需求终止下发响应
 */
@Data
@Schema(description = "GwPVControlStopResponseSubscribeVo")
public class GwPVControlStopResponseSubscribeVo {

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

    /**
     * 操作结果 1-成功 2-失败
     */
    @Schema(description = "操作结果 1-成功 2-失败")
    private Integer result;

}
