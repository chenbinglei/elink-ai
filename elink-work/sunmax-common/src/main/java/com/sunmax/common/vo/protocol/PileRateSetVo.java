package com.sunmax.common.vo.protocol;

import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 电桩费率下发参数
 */
@Data
@ApiModel("PileRateSetVo")
public class PileRateSetVo {

    /**
     * 充电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 费率类型 0-充电费率模型 1-放电费率模型
     * */
    @ApiModelProperty(value = "费率类型 0-充电费率模型 1-放电费率模型", required = true)
    private Integer type;

    /**
     * 费率模版id
     */
    @ApiModelProperty(value = "费率模型id", required = true)
    private String templateId;

    /**
     * 时段数量 N 范围 1～48
     */
    @ApiModelProperty("时段数量 N 范围 1～48")
    private Integer timeFrameNum;

    /**
     * 时段费率
     */
    @ApiModelProperty("时段费率")
    private List<CostFormat> timeFrameRates;

}
