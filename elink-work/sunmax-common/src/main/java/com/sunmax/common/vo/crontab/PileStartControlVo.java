package com.sunmax.common.vo.crontab;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PileStartControlVo {

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "枪编号")
    private String gunCode;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 运行模式 -1-未知 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "运行模式 -1-未知 0-充电模式 1-放电模式", required = true)
    private Integer runMode;

    /**
     * 执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
     */
    @ApiModelProperty(value = "执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错")
    private Integer result;

    /**
     * 启动时间
     */
    @ApiModelProperty(value = "启动时间")
    private String startTime;

    /**
     * 是否更新
     */
    @ApiModelProperty(value = "是否更新")
    private Boolean isUpdate;

}
