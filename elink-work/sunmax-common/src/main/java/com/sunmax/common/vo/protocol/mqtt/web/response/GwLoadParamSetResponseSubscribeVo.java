package com.sunmax.common.vo.protocol.mqtt.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 网关参数设置参数
 */
@Data
@Schema(description = "GwLoadParamSetResponseSubscribeVo")
public class GwLoadParamSetResponseSubscribeVo {
    /**
     * 最大负荷
     */
    @Schema(description = "最大负荷")
    private Double max_load; //

    /**
     * 电桩最大输出功率
     */
    @Schema(description = "电桩最大输出功率")
    private Integer pile_maxPower;

    /**
     * 桩波动数据类型
     */
    @Schema(description = "桩波动数据类型 1-绝对值  2-百分比")
    private Integer pileWaveType;

    /**
     * 电桩输出上浮参数
     */
    @Schema(description = "电桩输出上浮参数")
    private Double pileWaveUpCfg;

    /**
     * 电桩输出下浮参数
     */
    @Schema(description = "电桩输出下浮参数")
    private Double pileWaveDownCfg;

    /**
     * 负载数据浮动数据类型
     */
    @Schema(description = "负载数据浮动数据类型")
    private Integer loadWaveType;

    /**
     * 负载上浮参数
     */
    @Schema(description = "负载上浮参数")
    private Integer loadWaveUpCfg;

    /**
     * 负载下浮参数
     */
    @Schema(description = "负载下浮参数")
    private Integer loadWaveDownCfg;
    /**
     * 使能端
     */
    @Schema(description = "使能端 0-关   1-开")
    private Integer enable;

    /**
     * 监视周期
     */
    @Schema(description = "监视周期")
    private Integer monitorPeriod;
}
