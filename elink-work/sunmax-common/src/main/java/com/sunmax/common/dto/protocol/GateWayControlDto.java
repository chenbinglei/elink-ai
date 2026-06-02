package com.sunmax.common.dto.protocol;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网关控制参数返回实体类
 */
@Data
@ApiModel("GateWayControlDto")
public class GateWayControlDto {

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String deviceCode;

    /**
     * 控制参数返回数据列表
     */
    @ApiModelProperty("控制参数返回数据列表")
    private List<ControlValue> controlValueList = Lists.newArrayList();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ControlValue {

        /**
         * 控制类型 0-不设置参数 1-是否启用自动功率控制 2-允许的关口总负荷最大值 3-允许的关口总负荷最大值容差 4-单桩允许的充电功率最大值 5-监视周期
         */
        @ApiModelProperty("控制类型 1-是否启用自动功率控制 2-允许的关口总负荷最大值 3-允许的关口总负荷最大值容差 4-单桩允许的充电功率最大值 5-监视周期")
        private Integer controlType;

        /**
         * 控制值
         */
        @ApiModelProperty("控制值")
        private String controlValue;

    }

}
