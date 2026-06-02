package com.sunmax.common.dto.configure;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PowerControlResDto", description = "功率控制响应返回实体类")
public class PowerControlResDto {

    /**
     * 电桩编码
     */
    @ApiModelProperty(value = "电桩编码")
    private String pileCode;

    /**
     * 电枪编码
     */
    @ApiModelProperty(value = "电枪编码")
    private Integer gunCode;

    /**
     * 返回结果 0-接收下发 1-下发失败
     */
    @ApiModelProperty(value = "返回结果 0-接收下发 1-下发失败")
    private Integer status;
}
