package com.sunmax.protocol.dto.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 电桩启动响应返回实体类
 */
@Data
@Schema(description = "电桩启动响应返回实体类")
public class PlatformPileStartDto {

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
     * 失败原因 -1-响应超时 0-启动成功 1-启动失败 2-预约成功 3-预约失败 255-其他原因 500-平台处理报错
     */
    @Schema(description = "失败原因 -1-响应超时 0-下发成功 1-下发失败 2-预约成功 3-预约失败 255-其他原因 500-平台处理报错")
    private Integer failReason = 1;

    /**
     * 启动失败详细原因
     */
    @Schema(description = "启动失败详细原因")
    private Integer failDetailReason;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号")
    private String serialNum;

}
