package com.sunmax.together.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "交流配电柜数据返回实体类")
public class AcGGDDeviceDto {

    /**
     * 有功功率(kW)
     */
    @Schema(description = "有功功率(kW)")
    private Double power;

    /**
     * A相电流(A)
     */
    @Schema(description = "A相电流(A)")
    private Double currentA;

    /**
     * B相电流(A)
     */
    @Schema(description = "B相电流(A)")
    private Double currentB;

    /**
     * C相电流(A)
     */
    @Schema(description = "C相电流(A)")
    private Double currentC;

}
