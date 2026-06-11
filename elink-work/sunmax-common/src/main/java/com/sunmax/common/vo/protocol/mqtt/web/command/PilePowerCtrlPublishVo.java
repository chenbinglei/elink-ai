package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 充电桩功率控制参数
 */
@Data
@Schema(description = "stopParamVo")
public class PilePowerCtrlPublishVo {
    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pilesCode;

    /**
     * 充电枪编号
     */
    @Schema(description = "充电枪编号")
    private Integer gunCode;

    /**
     * 运行模式户id
     */
    @Schema(description = "运行模式 0-充电模式  1-放电模式")
    private Integer runMode;

    /**
     * 控制类型
     */
    @Schema(description = "控制类型 0-绝对控制 1-相对控制")
    private Integer ctrlType;

    /**
     * 输出功率
     */
    @Schema(description = "输出功率")
    private Double out;
}
