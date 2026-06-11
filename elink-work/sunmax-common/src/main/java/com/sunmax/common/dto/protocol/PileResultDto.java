package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 电桩响应返回实体类
 */
@Data
@Schema(description = "电桩响应返回实体类")
public class PileResultDto {

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
     * 类型 0-充电 1-放电
     */
    @Schema(description = "类型 0-充电 1-放电")
    private Integer type;

    /**
     * 执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
     */
    @Schema(description = "执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错")
    private Integer result = 1;

    /**
     * 失败详细原因
     */
    @Schema(description = "失败详细原因")
    private String failDetailReason;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号")
    private String serialNum;

}
