package com.sunmax.together.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AcGGDDeviceDto", description = "交流配电柜数据返回实体类")
public class AcGGDDeviceDto {

    /**
     * 有功功率(kW)
     */
    @ApiModelProperty(value = "有功功率(kW)")
    private Double power;

    /**
     * A相电流(A)
     */
    @ApiModelProperty(value = "A相电流(A)")
    private Double currentA;

    /**
     * B相电流(A)
     */
    @ApiModelProperty(value = "B相电流(A)")
    private Double currentB;

    /**
     * C相电流(A)
     */
    @ApiModelProperty(value = "C相电流(A)")
    private Double currentC;

}
