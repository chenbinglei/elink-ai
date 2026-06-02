package com.sunmax.common.dto.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 4.26 CMD_RATE_SET,//费率下发 26
 * 发送方向：前置服务<---平台服务
 */
@Data
public class RateSetCmdDto {

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
     * 费率模型Id
     */
    @ApiModelProperty(value = "费率模型Id", required = true)
    private String rateId;

    /**
     * 时段数量
     */
    @ApiModelProperty(value = "时段数量", required = true)
    private Integer timeFrameNum;

    /**
     * 充/放电费率
     */
    @ApiModelProperty(value = "充/放电费率", required = true)
    private List<CostFormat> timeFrameRates;

}
