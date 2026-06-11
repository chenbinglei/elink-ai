package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "充电桩日志查询参数实体类")
public class PileLogQueryVo {

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
     * 日志类型 1-日志 2-告警记录
     */
    @Schema(description = "日志类型 1-日志 2-告警记录")
    private Integer logType;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

}
