package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 充电桩功率控制参数
 */
@Data
@ApiModel("stopParamVo")
public class PilePowerCtrlPublishVo {
    /**
     * 充电桩编号
     */
    @ApiModelProperty("充电桩编号")
    private String pilesCode;

    /**
     * 充电枪编号
     */
    @ApiModelProperty("充电枪编号")
    private Integer gunCode;

    /**
     * 运行模式户id
     */
    @ApiModelProperty("运行模式 0-充电模式  1-放电模式")
    private Integer runMode;

    /**
     * 控制类型
     */
    @ApiModelProperty("控制类型 0-绝对控制 1-相对控制")
    private Integer ctrlType;

    /**
     * 输出功率
     */
    @ApiModelProperty("输出功率")
    private Double out;
}
