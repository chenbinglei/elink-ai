package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设置充电桩二维码信息参数实体类")
public class PileSetQrVo {

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 二维码前缀内容
     */
    @Schema(description = "二维码前缀内容")
    private String qrStr;

}
