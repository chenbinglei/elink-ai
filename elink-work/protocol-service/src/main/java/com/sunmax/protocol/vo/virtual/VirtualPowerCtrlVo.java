package com.sunmax.protocol.vo.virtual;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PowerCtrlVo", description = "虚拟平台功率控制参数")
public class VirtualPowerCtrlVo {

    /**
     * 充电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 充电枪编号
     */
    @ApiModelProperty(value = "充电枪编号", required = true)
    private Integer gunCode;

    /**
     * 运行模式 0-充电模式  1-放电模式
     */
    @ApiModelProperty(value = "运行模式 0-充电模式  1-放电模式", required = true)
    private Integer runMode;

    /**
     * 控制类型 0-绝对控制 1-相对控制
     */
    @ApiModelProperty(value = "控制类型 0-绝对控制(实际功率) 1-相对控制(百分比)", required = true)
    private Integer ctrlType;

    /**
     * 输出功率
     */
    @ApiModelProperty(value = "输出功率", required = true)
    private Double outPower;

}
