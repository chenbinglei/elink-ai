package com.sunmax.common.vo.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "功率控制参数实体类")
public class PowerControlParamVo {

    /**
     * 电桩编码
     */
    @Schema(description = "电桩编码")
    private String pileCode;

    /**
     * 电枪编码
     */
    @Schema(description = "电枪编码")
    private Integer gunCode;

    /**
     * 输出功率
     */
    @Schema(description = "输出功率")
    private Double outPower;

    /**
     * 下发控制时长 单位：分
     */
    @Schema(description = "下发控制时长 单位：分")
    private Integer controlDurationValue;
}
