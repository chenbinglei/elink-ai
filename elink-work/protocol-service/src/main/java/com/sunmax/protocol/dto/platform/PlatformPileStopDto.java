package com.sunmax.protocol.dto.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 电桩启动响应返回实体类
 */
@Data
@Schema(description = "电桩停止响应返回实体类")
public class PlatformPileStopDto {

    /**
     * 电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "充电桩枪编号")
    private Integer gunCode;

    /**
     * 失败原因 0-停止成功 1-停止失败 255-其他原因 500-平台处理报错
     */
    @Schema(description = "失败原因 0-停止成功 1-停止失败 255-其他原因 500-平台处理报错")
    private Integer failReason = 1;

    /**
     * 启动失败详细原因
     */
    @Schema(description = "启动失败详细原因")
    private Integer failDetailReason = 0;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号")
    private String serialNum;

}
