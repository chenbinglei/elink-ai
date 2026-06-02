package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网关参数设置参数
 */
@Data
@ApiModel("GwLoadParamSetPublishVo")
public class GwLoadParamSetPublishVo {
    /**
     * 最大负荷
     */
    @ApiModelProperty("最大负荷")
    private Double max_load; //

    /**
     * 电桩最大输出功率
     */
    @ApiModelProperty("电桩最大输出功率")
    private Double pile_maxPower;

    /**
     * 桩波动数据类型
     */
    @ApiModelProperty("桩波动数据类型 1-绝对值  2-百分比")
    private Integer pileWaveType;

    /**
     * 电桩输出上浮参数
     */
    @ApiModelProperty("电桩输出上浮参数")
    private Double pileWaveUpCfg;

    /**
     * 电桩输出下浮参数
     */
    @ApiModelProperty("电桩输出下浮参数")
    private Double pileWaveDownCfg;

    /**
     * 负载数据浮动数据类型
     */
    @ApiModelProperty("负载数据浮动数据类型")
    private Integer loadWaveType;

    /**
     * 负载上浮参数
     */
    @ApiModelProperty("负载上浮参数")
    private Double loadWaveUpCfg;

    /**
     * 负载下浮参数
     */
    @ApiModelProperty("负载下浮参数")
    private Double loadWaveDownCfg;
    /**
     * 使能端
     */
    @ApiModelProperty("使能端 0-关   1-开")
    private Integer enable;

    /**
     * 监视周期
     */
    @ApiModelProperty("监视周期")
    private Integer monitorPeriod;
}
