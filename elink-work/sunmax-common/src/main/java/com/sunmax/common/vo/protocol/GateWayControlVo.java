package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("GateWayControlVo")
public class GateWayControlVo {

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String deviceCode;

    /**
     * 控制类型 0-不设置参数(只做查询) 1-是否启用自动功率控制 2-允许的关口总负荷最大值 3-允许的关口总负荷最大值容差 4-单桩允许的充电功率最大值 5-监视周期
     */
    @ApiModelProperty("控制类型 0-不设置参数(只做查询) 1-是否启用自动功率控制 2-允许的关口总负荷最大值 3-允许的关口总负荷最大值容差 4-单桩允许的充电功率最大值 5-监视周期")
    private Integer controlType;

    /**
     * 控制值
     */
    @ApiModelProperty("控制值")
    private String controlValue;

}
