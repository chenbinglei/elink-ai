package com.sunmax.protocol.dto.virtual;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 电桩功率控制返回实体类
 */
@Data
@ApiModel(value = "PowerCtrlDto", description = "电桩功率控制返回实体类")
public class VirtualPowerCtrlDto {

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
     * 失败原因 -1-响应超时 0-调控成功 1-下发失败 255-其他原因 500-平台处理报错
     */
    @ApiModelProperty(value = "失败原因 -1-响应超时 0-调控成功 1-下发失败 255-其他原因 500-平台处理报错", required = true)
    private Integer failReason = -1;

}
