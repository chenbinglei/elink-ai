package com.sunmax.protocol.vo.platform;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StartEventVo", description = "电桩启动事件实体类")
public class PlatformStartEventVo {

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
     * 失败原因 0-成功 1-启动失败 2-预约成功 3-预约失败 255-其他原因
     */
    @ApiModelProperty(value = "失败原因 0-成功 1-启动失败 2-预约成功 3-预约失败 255-其他原因", required = true)
    private Integer failReason;

    /**
     * 启动失败详细原因
     */
    @ApiModelProperty(value = "启动失败详细原因(参考故障码分类)", required = true)
    private Integer failDetailReason;

    /**
     * 结束详细描述
     */
    @ApiModelProperty(value = "结束详细描述", required = true)
    private String stopDetail;

    /**
     * 开始充/放电时间
     */
    @ApiModelProperty(value = "开始充/放电时间", required = true)
    private String startTime;

    /**
     * 交易流水号
     */
    @ApiModelProperty(value = "交易流水号", required = true)
    private String serialNum;

}
