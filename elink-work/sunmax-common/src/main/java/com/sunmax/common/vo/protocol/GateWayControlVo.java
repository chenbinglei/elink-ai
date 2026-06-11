package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "GateWayControlVo")
public class GateWayControlVo {

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceCode;

    /**
     * 控制类型 0-不设置参数(只做查询) 1-是否启用自动功率控制 2-允许的关口总负荷最大值 3-允许的关口总负荷最大值容差 4-单桩允许的充电功率最大值 5-监视周期
     */
    @Schema(description = "控制类型 0-不设置参数(只做查询) 1-是否启用自动功率控制 2-允许的关口总负荷最大值 3-允许的关口总负荷最大值容差 4-单桩允许的充电功率最大值 5-监视周期")
    private Integer controlType;

    /**
     * 控制值
     */
    @Schema(description = "控制值")
    private String controlValue;

}
