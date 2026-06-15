package com.sunmax.common.vo.protocol;

import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 电桩费率下发参数
 */
@Data
@Schema(description = "PileRateSetVo")
public class PileRateSetVo {

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 费率类型 0-充电费率模型 1-放电费率模型
     * */
    @Schema(description = "费率类型 0-充电费率模型 1-放电费率模型")
    private Integer type;

    /**
     * 费率模版id
     */
    @Schema(description = "费率模型id")
    private String templateId;

    /**
     * 时段数量 N 范围 1～48
     */
    @Schema(description = "时段数量 N 范围 1～48")
    private Integer timeFrameNum;

    /**
     * 时段费率
     */
    @Schema(description = "时段费率")
    private List<CostFormat> timeFrameRates;

}
