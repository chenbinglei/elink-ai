package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 电桩响应返回实体类
 */
@Data
@ApiModel(value = "PileResultDto", description = "电桩响应返回实体类")
public class PileResultDto {

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
     * 类型 0-充电 1-放电
     */
    @ApiModelProperty(value = "类型 0-充电 1-放电")
    private Integer type;

    /**
     * 执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
     */
    @ApiModelProperty(value = "执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错")
    private Integer result = 1;

    /**
     * 失败详细原因
     */
    @ApiModelProperty(value = "失败详细原因")
    private String failDetailReason;

    /**
     * 交易流水号
     */
    @ApiModelProperty(value = "交易流水号")
    private String serialNum;

}
