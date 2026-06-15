package com.sunmax.protocol.vo.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩启动事件实体类")
public class PlatformStopEventVo {

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
     * 停止详细原因
     */
    @Schema(description = "停止详细原因")
    private Integer failDetailReason;

    /**
     * 停止原因
     */
    @Schema(description = "停止原因")
    private Integer failReason;

    /**
     * 结束充/放电时间
     */
    @Schema(description = "结束充/放电时间")
    private String endTime;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号")
    private String serialNum;

}
