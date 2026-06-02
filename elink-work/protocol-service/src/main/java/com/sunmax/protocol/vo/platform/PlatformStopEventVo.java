package com.sunmax.protocol.vo.platform;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StopEventVo", description = "电桩启动事件实体类")
public class PlatformStopEventVo {

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "充电桩枪编号", required = true)
    private Integer gunCode;

    /**
     * 停止详细原因
     */
    @ApiModelProperty(value = "停止详细原因", required = true)
    private Integer failDetailReason;

    /**
     * 停止原因
     */
    @ApiModelProperty(value = "停止原因", required = true)
    private Integer failReason;

    /**
     * 结束充/放电时间
     */
    @ApiModelProperty(value = "结束充/放电时间", required = true)
    private String endTime;

    /**
     * 交易流水号
     */
    @ApiModelProperty(value = "交易流水号", required = true)
    private String serialNum;

}
