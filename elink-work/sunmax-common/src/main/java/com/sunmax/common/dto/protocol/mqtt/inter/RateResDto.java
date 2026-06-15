package com.sunmax.common.dto.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 4.2 CMD_RATE_RES,//费率响应 CMD=2
 * 发送方向：前置服务<---平台服务
 */
@Data
public class RateResDto {

    /**
     * 桩编码
     */
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 充电费率id
     */
    @Schema(description = "充电费率id")
    private String cRateId;

    /**
     * 充电费率时段时段数量
     */
    @Schema(description = "充电费率时段时段数量")
    private Integer cTimeFrameNum;

    /**
     * 充电费率
     */
    @Schema(description = "充电费率")
    private List<CostFormat> cTimeFrameRate;

    /**
     * 放电费率id
     */
    @Schema(description = "放电费率id")
    private String dRateId;

    /**
     * 放电费率时段时段数量
     */
    @Schema(description = "放电费率时段时段数量")
    private Integer dTimeFrameNum;

    /**
     * 放电费率
     */
    @Schema(description = "放电费率")
    private List<CostFormat> dTimeFrameRate;

}
