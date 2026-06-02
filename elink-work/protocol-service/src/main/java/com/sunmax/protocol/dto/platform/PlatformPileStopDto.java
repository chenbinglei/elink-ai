package com.sunmax.protocol.dto.platform;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 电桩启动响应返回实体类
 */
@Data
@ApiModel(value = "PileStopDto", description = "电桩停止响应返回实体类")
public class PlatformPileStopDto {

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
     * 失败原因 0-停止成功 1-停止失败 255-其他原因 500-平台处理报错
     */
    @ApiModelProperty(value = "失败原因 0-停止成功 1-停止失败 255-其他原因 500-平台处理报错", required = true)
    private Integer failReason = 1;

    /**
     * 启动失败详细原因
     */
    @ApiModelProperty(value = "启动失败详细原因", required = true)
    private Integer failDetailReason = 0;

    /**
     * 交易流水号
     */
    @ApiModelProperty(value = "交易流水号", required = true)
    private String serialNum;

}
