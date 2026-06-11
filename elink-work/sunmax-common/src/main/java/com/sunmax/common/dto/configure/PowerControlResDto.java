package com.sunmax.common.dto.configure;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "功率控制响应返回实体类")
public class PowerControlResDto {

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
     * 返回结果 0-接收下发 1-下发失败
     */
    @Schema(description = "返回结果 0-接收下发 1-下发失败")
    private Integer status;
}
