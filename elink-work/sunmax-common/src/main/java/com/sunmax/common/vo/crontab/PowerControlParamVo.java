package com.sunmax.common.vo.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PowerControlParamVo", description = "功率控制参数实体类")
public class PowerControlParamVo {

    /**
     * 电桩编码
     */
    @ApiModelProperty(value = "电桩编码", required = true)
    private String pileCode;

    /**
     * 电枪编码
     */
    @ApiModelProperty(value = "电枪编码", required = true)
    private Integer gunCode;

    /**
     * 输出功率
     */
    @ApiModelProperty(value = "输出功率", required = true)
    private Double outPower;

    /**
     * 下发控制时长 单位：分
     */
    @ApiModelProperty(value = "下发控制时长 单位：分")
    private Integer controlDurationValue;
}
