package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 充电桩功率控制参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("PilePowerCtrlVo")
public class PilePowerCtrlVo {

    /**
     * 充电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 充电枪编号
     */
    @ApiModelProperty(value = "充电枪编号", required = true)
    private String gunCode;

    /**
     * 运行模式 0-充电模式  1-放电模式
     */
    @ApiModelProperty(value = "运行模式 0-充电模式  1-放电模式", required = true)
    private Integer runMode;

    /**
     * 控制类型 0-绝对控制 1-相对控制
     */
    @ApiModelProperty(value = "控制类型 0-绝对控制 1-相对控制", required = true)
    private Integer ctrlType;

    /**
     * 输出功率
     */
    @ApiModelProperty(value = "输出功率", required = true)
    private Double outPower;

}
