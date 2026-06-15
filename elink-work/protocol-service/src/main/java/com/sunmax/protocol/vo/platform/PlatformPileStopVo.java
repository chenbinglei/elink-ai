package com.sunmax.protocol.vo.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩停止参数实体类")
public class PlatformPileStopVo {

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @Schema(description = "充电枪编号")
    private Integer gunCode;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号")
    private String serialNum;

    /**
     * 停止方式 1-停止充/放电 2-取消预约
     */
    @Schema(description = "停止方式 1-停止充/放电 2-取消预约")
    private Integer type;

}
