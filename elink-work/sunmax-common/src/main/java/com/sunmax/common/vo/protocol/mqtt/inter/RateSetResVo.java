package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.27 CMD_RATE_SET_RES,//费率下发响应  27
 * 发送方向：前置服务--->平台服务
 */
@Data
public class RateSetResVo {

    /**
     * 桩编码
     */
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 费率类型 0-充电费率 1-放电费率
     */
    @ApiModelProperty(value = "费率类型", required = true)
    private Integer type;

    /**
     * 费率模型ID
     */
    @ApiModelProperty(value = "费率模型ID", required = true)
    private String rateId;

}
