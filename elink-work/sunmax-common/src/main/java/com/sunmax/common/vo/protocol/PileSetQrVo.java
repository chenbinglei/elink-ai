package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileSetQrVo", description = "设置充电桩二维码信息参数实体类")
public class PileSetQrVo {

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "电桩编号", required = true)
    private String pileCode;

    /**
     * 二维码前缀内容
     */
    @ApiModelProperty(value = "二维码前缀内容", required = true)
    private String qrStr;

}
