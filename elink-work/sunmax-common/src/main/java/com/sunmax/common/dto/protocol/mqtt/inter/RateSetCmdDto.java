package com.sunmax.common.dto.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 费率类型 0-充电费率 1-放电费率
     */
    @Schema(description = "费率类型")
    private Integer type;

    /**
     * 费率模型Id
     */
    @Schema(description = "费率模型Id")
    private String rateId;

    /**
     * 时段数量
     */
    @Schema(description = "时段数量")
    private Integer timeFrameNum;

    /**
     * 充/放电费率
     */
    @Schema(description = "充/放电费率")
    private List<CostFormat> timeFrameRates;

}
