package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.30 CMD_PileDataRequest,//电桩运行数据请求  30
 * 发送方向：前置服务<---平台服务
 */
@Data
public class PileDataReqDto {

    /**
     * 桩编码
     */
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "枪编号", required = true)
    private Integer gunCode;

}
