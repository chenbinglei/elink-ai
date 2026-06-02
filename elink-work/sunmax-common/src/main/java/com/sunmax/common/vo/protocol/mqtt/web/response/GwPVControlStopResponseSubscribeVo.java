package com.sunmax.common.vo.protocol.mqtt.web.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 调控需求终止下发响应
 */
@Data
@ApiModel(value = "GwPVControlStopResponseSubscribeVo")
public class GwPVControlStopResponseSubscribeVo {

    /**
     * 事件编号
     */
    @ApiModelProperty(value = "事件编号", required = true)
    private String sjbh;

    /**
     * 事件状态
     */
    @ApiModelProperty(value = "事件状态", required = true)
    private String sjzt;

    /**
     * 操作结果 1-成功 2-失败
     */
    @ApiModelProperty(value = "操作结果 1-成功 2-失败")
    private Integer result;

}
