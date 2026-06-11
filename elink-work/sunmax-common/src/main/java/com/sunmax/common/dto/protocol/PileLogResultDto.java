package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩日志结果返回实体类")
public class PileLogResultDto {

    /**
     * 电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "充电桩枪编号")
    private String gunCode;

    /**
     * 序号
     */
    @Schema(description = "序号")
    private Integer logNum;

    /**
     * 日志类型 1-日志 2-告警记录
     */
    @Schema(description = "日志类型 1-日志 2-告警记录")
    private Integer logType;

    /**
     * 日志发生时间
     */
    @Schema(description = "日志发生时间")
    private String logTime;

    /**
     * 日志内容
     */
    @Schema(description = "日志内容")
    private String logContent;

}
