package com.sunmax.common.vo.protocol.mqtt.web.command;

import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 电桩费率下发参数
 */
@Data
@ApiModel("PileRateSetPublishVo")
public class PileRateSetPublishVo {
    /**
     * 充电桩编号
     */
    @ApiModelProperty("充电桩编号")
    private String pilesCode;

    /**
     * 费率类型  0 充电费率模型  1 放电费率模型
     * */
    @ApiModelProperty("0 充电费率模型  1 放电费率模型")
    private int type;

    /**
     * 费率模型ID
     */
    @ApiModelProperty("费率模型ID")
    private String rateId;

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
