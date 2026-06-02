package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileLogQueryVo", description = "充电桩日志查询参数实体类")
public class PileLogQueryVo {

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "充电桩枪编号", required = true)
    private String gunCode;

    /**
     * 日志类型 1-日志 2-告警记录
     */
    @ApiModelProperty(value = "日志类型 1-日志 2-告警记录", required = true)
    private Integer logType;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private String endTime;

}
