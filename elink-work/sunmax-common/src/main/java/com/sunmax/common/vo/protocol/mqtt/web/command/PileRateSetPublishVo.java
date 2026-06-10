package com.sunmax.common.vo.protocol.mqtt.web.command;

import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 电桩费率下发参数
 */
@Data
@Schema(description = "PileRateSetPublishVo")
public class PileRateSetPublishVo {
    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pilesCode;

    /**
     * 费率类型  0 充电费率模型  1 放电费率模型
     * */
    @Schema(description = "0 充电费率模型  1 放电费率模型")
    private int type;

    /**
     * 费率模型ID
     */
    @Schema(description = "费率模型ID")
    private String rateId;

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
