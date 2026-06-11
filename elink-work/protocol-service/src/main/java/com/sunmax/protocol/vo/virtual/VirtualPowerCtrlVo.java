package com.sunmax.protocol.vo.virtual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "虚拟平台功率控制参数")
public class VirtualPowerCtrlVo {

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @Schema(description = "充电枪编号")
    private Integer gunCode;

    /**
     * 运行模式 0-充电模式  1-放电模式
     */
    @Schema(description = "运行模式 0-充电模式  1-放电模式")
    private Integer runMode;

    /**
     * 控制类型 0-绝对控制 1-相对控制
     */
    @Schema(description = "控制类型 0-绝对控制(实际功率) 1-相对控制(百分比)")
    private Integer ctrlType;

    /**
     * 输出功率
     */
    @Schema(description = "输出功率")
    private Double outPower;

}
