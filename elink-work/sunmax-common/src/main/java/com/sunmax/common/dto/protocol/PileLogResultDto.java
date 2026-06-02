package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileLogResultDto", description = "电桩日志结果返回实体类")
public class PileLogResultDto {

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "充电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "充电桩枪编号")
    private String gunCode;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private Integer logNum;

    /**
     * 日志类型 1-日志 2-告警记录
     */
    @ApiModelProperty(value = "日志类型 1-日志 2-告警记录")
    private Integer logType;

    /**
     * 日志发生时间
     */
    @ApiModelProperty(value = "日志发生时间")
    private String logTime;

    /**
     * 日志内容
     */
    @ApiModelProperty(value = "日志内容")
    private String logContent;

}
