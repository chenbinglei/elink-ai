package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileStopVo", description = "电桩停止参数实体类")
public class PileStopVo {

    /**
     * 充电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 充电枪编号
     */
    @ApiModelProperty(value = "充电枪编号", required = true)
    private String gunCode;

    /**
     * 交易流水号
     */
    @ApiModelProperty(value = "交易流水号", required = true)
    private String serialNum;

    /**
     * 停止方式 1-停止充/放电 2-取消预约
     */
    @ApiModelProperty(value = "停止方式 1-停止充/放电 2-取消预约", required = true)
    private Integer type;

    /**
     * 是否存控制记录
     */
    @ApiModelProperty(value = "是否存控制记录")
    private Boolean isStore = false;

}
